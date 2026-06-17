<template>
  <div class="order-detail">
    <div class="back-btn" @click="$router.push('/orders')">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20"><polyline points="15 18 9 12 15 6"/></svg>
      返回订单列表
    </div>
    <div class="detail-card" v-if="order">
      <div class="detail-header">
        <div class="status-badge" :class="order.status.toLowerCase()">
          {{ getStatusText(order.status) }}
        </div>
        <h2>订单详情</h2>
      </div>
      <div class="info-grid">
        <div class="info-item">
          <span class="info-label">订单号</span>
          <span class="info-value">{{ order.orderNo }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">下单时间</span>
          <span class="info-value">{{ order.createTime }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">支付方式</span>
          <span class="info-value">{{ order.payType || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">支付时间</span>
          <span class="info-value">{{ order.payTime || '-' }}</span>
        </div>
        <div class="info-item full">
          <span class="info-label">收货地址</span>
          <span class="info-value">{{ order.address }}</span>
        </div>
        <div class="info-item full" v-if="order.remark">
          <span class="info-label">备注</span>
          <span class="info-value">{{ order.remark }}</span>
        </div>
      </div>
      <div class="items-section">
        <h3>商品清单</h3>
        <div class="item-list">
          <div v-for="item in order.items || []" :key="item.id" class="item-row">
              <img :src="proxyImage(item.productImage)" class="item-image" />
            <div class="item-info">
              <span class="item-name">{{ item.productName }}</span>
              <span class="item-price">¥{{ item.price }} x {{ item.quantity }}</span>
            </div>
            <span class="item-subtotal">¥{{ item.totalAmount }}</span>
          </div>
        </div>
      </div>
      <div class="total-section">
        <div class="total-row">
          <span>商品合计</span>
          <span>¥{{ order.totalAmount }}</span>
        </div>
        <div class="total-row final">
          <span>实付金额</span>
          <span class="final-price">¥{{ order.payAmount }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api, { proxyImage } from '../api'

const route = useRoute()
const router = useRouter()
const order = ref(null)

const loadOrder = async () => {
  try {
    const res = await api.get(`/order/detail/${route.params.orderNo}`)
    order.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const getStatusText = (status) => {
  const map = { PENDING: '待支付', PAID: '已支付', CANCELLED: '已取消' }
  return map[status] || status
}

onMounted(() => loadOrder())
</script>

<style scoped>
.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: #606266;
  cursor: pointer;
  font-size: 14px;
  margin-bottom: 20px;
  transition: color 0.2s;
}
.back-btn:hover {
  color: #667eea;
}
.detail-card {
  background: white;
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}
.detail-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 28px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}
.status-badge {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}
.status-badge.pending {
  background: #fdf6ec;
  color: #e6a23c;
}
.status-badge.paid {
  background: #f0f9eb;
  color: #67c23a;
}
.status-badge.cancelled {
  background: #f4f4f5;
  color: #909399;
}
.detail-header h2 {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a2e;
}
.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 28px;
}
.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.info-item.full {
  grid-column: span 2;
}
.info-label {
  font-size: 12px;
  color: #c0c4cc;
}
.info-value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}
.items-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 16px;
}
.item-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
}
.item-row {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px;
  background: #f8f9fc;
  border-radius: 10px;
}
.item-image {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  object-fit: cover;
}
.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.item-name {
  font-weight: 500;
  color: #333;
}
.item-price {
  font-size: 13px;
  color: #909399;
}
.item-subtotal {
  font-weight: 600;
  color: #333;
}
.total-section {
  background: #f8f9fc;
  border-radius: 12px;
  padding: 20px;
}
.total-row {
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
  font-size: 14px;
  color: #606266;
}
.total-row.final {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e8e8e8;
  font-size: 16px;
  font-weight: 600;
}
.final-price {
  font-size: 24px;
  font-weight: 800;
  color: #f56c6c;
}
</style>
