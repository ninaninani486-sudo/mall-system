<template>
  <div class="cart">
    <div class="page-header">
      <h2>购物车</h2>
      <p v-if="cartList.length > 0">共 {{ cartList.length }} 件商品</p>
    </div>
    <div class="cart-content" v-if="cartList.length > 0">
      <div class="cart-list">
        <div v-for="item in cartList" :key="item.productId" class="cart-item">
          <div class="item-image">
            <img :src="proxyImage(item.product?.image)" />
          </div>
          <div class="item-info">
            <h4 class="item-name">{{ item.product?.name }}</h4>
            <p class="item-price">¥{{ item.product?.price }}</p>
          </div>
          <div class="item-quantity">
            <el-input-number v-model="item.quantity" :min="1" @change="updateQuantity(item)" size="default" />
          </div>
          <div class="item-subtotal">
            ¥{{ (item.product?.price * item.quantity).toFixed(2) }}
          </div>
          <div class="item-action">
            <el-button type="danger" text @click="removeItem(item.productId)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/></svg>
            </el-button>
          </div>
        </div>
      </div>
      <div class="cart-summary">
        <div class="summary-card">
          <h3>订单摘要</h3>
          <div class="summary-row">
            <span>商品数量</span>
            <span>{{ cartList.reduce((s, i) => s + i.quantity, 0) }} 件</span>
          </div>
          <div class="summary-row total">
            <span>合计</span>
            <span class="total-price">¥{{ totalAmount }}</span>
          </div>
          <el-button type="primary" size="large" @click="goCheckout" class="checkout-btn">去结算</el-button>
        </div>
      </div>
    </div>
    <el-empty v-else description="购物车是空的">
      <el-button type="primary" @click="$router.push('/products')">去逛逛</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api, { proxyImage } from '../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const cartList = ref([])

const totalAmount = computed(() => {
  return cartList.value.reduce((sum, item) => sum + (item.product?.price || 0) * item.quantity, 0).toFixed(2)
})

const loadCart = async () => {
  const res = await api.get('/cart/list')
  cartList.value = res.data
}

const updateQuantity = async (row) => {
  await api.put('/cart/update', { productId: row.productId, quantity: row.quantity })
}

const removeItem = async (productId) => {
  await api.delete(`/cart/remove/${productId}`)
  ElMessage.success('已删除')
  loadCart()
}

const goCheckout = async () => {
  try {
    const items = cartList.value.map(item => ({
      productId: item.productId,
      quantity: item.quantity
    }))
    const res = await api.post('/order/create', {
      address: '默认地址',
      remark: '',
      items: items
    })
    await api.delete('/cart/clear')
    ElMessage.success('下单成功')
    router.push(`/pay/${res.data.orderNo}`)
  } catch (e) {
    // error handled by interceptor
  }
}

onMounted(() => loadCart())
</script>

<style scoped>
.page-header {
  margin-bottom: 24px;
}
.page-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a2e;
}
.page-header p {
  color: #909399;
  font-size: 14px;
  margin-top: 4px;
}
.cart-content {
  display: flex;
  gap: 24px;
}
.cart-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.cart-item {
  display: flex;
  align-items: center;
  gap: 20px;
  background: white;
  padding: 20px;
  border-radius: 14px;
  border: 1px solid rgba(0, 0, 0, 0.04);
  transition: box-shadow 0.2s;
}
.cart-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}
.item-image {
  width: 90px;
  height: 90px;
  border-radius: 10px;
  overflow: hidden;
  flex-shrink: 0;
  background: #f5f7fa;
}
.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.item-info {
  flex: 1;
  min-width: 0;
}
.item-name {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.item-price {
  color: #f56c6c;
  font-size: 16px;
  font-weight: 600;
}
.item-quantity {
  flex-shrink: 0;
}
.item-subtotal {
  font-size: 16px;
  font-weight: 700;
  color: #f56c6c;
  min-width: 100px;
  text-align: right;
}
.item-action {
  flex-shrink: 0;
}
.cart-summary {
  flex: 0 0 300px;
}
.summary-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  position: sticky;
  top: 88px;
  border: 1px solid rgba(0, 0, 0, 0.04);
}
.summary-card h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}
.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-size: 14px;
  color: #606266;
}
.summary-row.total {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  font-size: 16px;
  font-weight: 600;
}
.total-price {
  font-size: 24px;
  font-weight: 800;
  color: #f56c6c;
}
.checkout-btn {
  width: 100%;
  height: 48px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  margin-top: 20px;
}
.checkout-btn:hover {
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
}
</style>
