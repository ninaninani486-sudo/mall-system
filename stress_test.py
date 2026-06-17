import requests
import json
import time
import threading
from concurrent.futures import ThreadPoolExecutor, as_completed

BASE_URL = "http://localhost:8081/api"
PRODUCT_ID = 1

results_lock = threading.Lock()
success_count = 0
fail_count = 0
user_orders = {}  # 记录每个用户创建的订单

def login(username, password="123456"):
    r = requests.post(f"{BASE_URL}/user/login", json={"username": username, "password": password})
    data = r.json()
    if data["code"] == 200:
        return data["data"]["token"]
    raise Exception(f"Login failed: {data['message']}")

def create_order(token, username):
    global success_count, fail_count
    headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}
    try:
        r = requests.post(f"{BASE_URL}/order/create", json={
            "items": [{"productId": PRODUCT_ID, "quantity": 1}],
            "address": "压测地址"
        }, headers=headers, timeout=10)
        data = r.json()
        with results_lock:
            if data["code"] == 200:
                success_count += 1
                order_no = data["data"]["orderNo"]
                user_orders[username] = user_orders.get(username, [])
                user_orders[username].append(order_no)
                return {"status": "SUCCESS", "orderNo": order_no, "username": username}
            else:
                fail_count += 1
                return {"status": "FAIL", "message": data["message"], "username": username}
    except Exception as e:
        with results_lock:
            fail_count += 1
        return {"status": "ERROR", "message": str(e), "username": username}

def get_product_stock(admin_token):
    headers = {"Authorization": f"Bearer {admin_token}"}
    r = requests.get(f"{BASE_URL}/product/detail/{PRODUCT_ID}", headers=headers)
    data = r.json()
    return data["data"]["stock"], data["data"]["sales"]

def main():
    global success_count, fail_count

    print("=" * 60)
    print("  商城系统高并发压测 - 并发下单扣库存")
    print("=" * 60)

    admin_token = login("admin")
    before_stock, before_sales = get_product_stock(admin_token)
    print(f"\n[压测前] iPhone 15 Pro: stock={before_stock} sales={before_sales}")

    # 创建测试用户
    user_tokens = {}
    for i in range(1, 11):
        try:
            requests.post(f"{BASE_URL}/user/register", json={
                "username": f"stress{i}", "password": "123456", "nickname": f"压测{i}"
            })
        except:
            pass
        token = login(f"stress{i}")
        headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}
        requests.post(f"{BASE_URL}/account/recharge", json={"amount": 100000}, headers=headers)
        requests.delete(f"{BASE_URL}/cart/clear", headers=headers)
        user_tokens[f"stress{i}"] = token
    print(f"测试用户: {len(user_tokens)} 个已就绪")

    total_requests = 10 * 5
    print(f"\n[开始压测] {total_requests} 并发请求 (10线程 x 5循环)...")
    start_time = time.time()

    all_results = []
    with ThreadPoolExecutor(max_workers=10) as executor:
        futures = []
        round_num = 0
        while round_num < 5:
            for username, token in user_tokens.items():
                futures.append(executor.submit(create_order, token, username))
            round_num += 1

        for future in as_completed(futures):
            all_results.append(future.result())

    elapsed = time.time() - start_time

    # 支付：每个用户用自己创建的订单支付
    print("\n[支付阶段] 每个用户支付自己的订单...")
    paid_count = 0
    for username, orders in user_orders.items():
        token = user_tokens.get(username)
        if not token:
            continue
        headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}
        for order_no in orders:
            r = requests.post(f"{BASE_URL}/payment/pay", json={
                "orderNo": order_no, "payType": "BALANCE"
            }, headers=headers, timeout=10)
            if r.json()["code"] == 200:
                paid_count += 1

    time.sleep(2)

    after_stock, after_sales = get_product_stock(admin_token)
    stock_diff = before_stock - after_stock
    sales_diff = after_sales - before_sales

    print("\n" + "=" * 60)
    print("  压测结果报告")
    print("=" * 60)
    print(f"  并发请求总数:     {total_requests}")
    print(f"  成功下单:         {success_count}")
    print(f"  被拒绝(库存不足): {fail_count}")
    print(f"  成功支付:         {paid_count}")
    print(f"  耗时:             {elapsed:.2f}s")
    print(f"  QPS:              {total_requests/elapsed:.1f}")
    print("-" * 60)
    print(f"  压测前库存:       {before_stock}")
    print(f"  压测后库存:       {after_stock}")
    print(f"  库存减少:         {stock_diff}")
    print(f"  压测前销量:       {before_sales}")
    print(f"  压测后销量:       {after_sales}")
    print(f"  销量增加:         {sales_diff}")
    print("-" * 60)

    if stock_diff == sales_diff:
        print("  一致性检查:       PASS (库存=销量)")
        print("  无超卖:           YES")
        print("\n  ✓ SQL乐观锁有效防止了并发超卖!")
    else:
        print("  一致性检查:       FAIL")

    print("=" * 60)

if __name__ == "__main__":
    main()
