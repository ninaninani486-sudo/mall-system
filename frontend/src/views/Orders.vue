<template>
  <div class="orders">
    <div class="page-header">
      <h2>我的订单</h2>
      <p>管理你的所有订单</p>
    </div>
    <div class="toolbar" v-if="orders.length > 0">
      <el-input v-model="keyword" placeholder="搜索订单号..." clearable @clear="searchOrders" @keyup.enter="searchOrders" class="search-input" prefix-icon="Search" />
    </div>
    <div class="order-list">
      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <div class="order-info">
            <span class="order-no">{{ order.orderNo }}</span>
            <span class="order-time">{{ order.createTime }}</span>
          </div>
          <el-tag :type="getStatusType(order.status)" effect="dark" round>{{ getStatusText(order.status) }}</el-tag>
        </div>
        <div class="order-items">
          <div v-for="item in order.items" :key="item.id" class="order-item">
            <img :src="proxyImage(item.productImage)" class="item-image" />
            <div class="item-info">
              <span class="item-name">{{ item.productName }}</span>
              <span class="item-spec">x{{ item.quantity }}</span>
            </div>
            <span class="item-price">¥{{ item.price }}</span>
          </div>
        </div>
        <div class="order-footer">
          <div class="order-total">
            共 {{ order.items.reduce((s, i) => s + i.quantity, 0) }} 件商品
            <span class="total-amount">合计: <em>¥{{ order.payAmount }}</em></span>
          </div>
          <div class="order-actions">
            <el-button v-if="order.status === 'PENDING'" type="primary" @click="$router.push(`/pay/${order.orderNo}`)" class="action-btn pay-btn">去支付</el-button>
            <el-button v-if="order.status === 'PENDING'" @click="cancelOrder(order.orderNo)" class="action-btn">取消</el-button>
            <el-button @click="$router.push(`/order/${order.orderNo}`)" class="action-btn">详情</el-button>
          </div>
        </div>
      </div>
    </div>
    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination v-model:current-page="page" :page-size="size" :total="total" layout="prev, pager, next" @current-change="loadOrders" />
    </div>
    <el-empty v-if="orders.length === 0" description="暂无订单">
      <el-button type="primary" @click="$router.push('/products')">去购物</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api, { proxyImage } from '../api'
import { ElMessage } from 'element-plus'

const orders = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const keyword = ref('')

const loadOrders = async () => {
  const params = { page: page.value, size: size.value }
  if (keyword.value) params.keyword = keyword.value
  const res = await api.get('/order/page', { params })
  orders.value = res.data.records
  total.value = res.data.total
}

const searchOrders = () => {
  page.value = 1
  loadOrders()
}

const cancelOrder = async (orderNo) => {
  await api.post(`/order/cancel/${orderNo}`)
  ElMessage.success('已取消')
  loadOrders()
}

const getStatusType = (status) => {
  const map = { PENDING: 'warning', PAID: 'success', CANCELLED: 'info', SHIPPED: 'primary' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { PENDING: '待支付', PAID: '已支付', CANCELLED: '已取消', SHIPPED: '已发货', COMPLETED: '已完成' }
  return map[status] || status
}

onMounted(() => loadOrders())
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
.toolbar {
  margin-bottom: 16px;
}
.search-input {
  max-width: 320px;
}
.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.order-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid rgba(0, 0, 0, 0.04);
  transition: box-shadow 0.2s;
}
.order-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: #fafbfc;
  border-bottom: 1px solid #f0f0f0;
}
.order-info {
  display: flex;
  align-items: center;
  gap: 16px;
}
.order-no {
  font-weight: 600;
  color: #1a1a2e;
}
.order-time {
  font-size: 13px;
  color: #c0c4cc;
}
.order-items {
  padding: 16px 24px;
}
.order-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 10px 0;
}
.order-item + .order-item {
  border-top: 1px solid #f5f5f5;
}
.item-image {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  object-fit: cover;
  background: #f5f7fa;
}
.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.item-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}
.item-spec {
  font-size: 12px;
  color: #909399;
}
.item-price {
  font-weight: 600;
  color: #333;
}
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: #fafbfc;
  border-top: 1px solid #f0f0f0;
}
.order-total {
  font-size: 14px;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 20px;
}
.total-amount {
  font-size: 14px;
}
.total-amount em {
  font-size: 20px;
  font-weight: 700;
  color: #f56c6c;
  font-style: normal;
}
.order-actions {
  display: flex;
  gap: 8px;
}
.action-btn {
  border-radius: 8px;
  font-weight: 500;
}
.pay-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
}
.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
