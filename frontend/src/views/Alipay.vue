<template>
  <div class="alipay-page">
    <div class="alipay-header">
      <div class="header-content">
        <svg viewBox="0 0 24 24" fill="none" class="alipay-logo"><path d="M21.422 15.358c-1.286-.504-2.636-.922-4.05-1.254.684-1.476 1.224-3.108 1.584-4.86h-3.3v-1.08h4.05v-.81h-4.05V6.3h-1.62v1.05H9.9v.81h4.05v1.08h-3.6v.81h5.4c-.252 1.098-.666 2.142-1.206 3.078-1.368-.378-2.646-.612-3.834-.684a12.16 12.16 0 00-1.35.072c.504-2.7 1.8-4.95 3.6-6.48C13.32 2.7 15.12 1.8 17.1 1.8c2.7 0 4.95 1.8 5.4 4.5.72.18 1.44.486 2.1.918-.36 2.52-2.16 4.68-4.5 5.76.684.864 1.314 1.8 1.8 2.76-.54.18-1.134.324-1.782.42a15.26 15.26 0 01-4.716.864c-1.566 0-3.078-.216-4.5-.594.936-1.278 1.674-2.736 2.16-4.32-1.44.18-2.934.18-4.32-.18.648-1.8 1.53-3.438 2.592-4.86-1.62-.54-3.348-.864-5.13-.864C1.26 3.3 0 5.4 0 7.8c0 1.38.432 2.664 1.17 3.72-.18.54-.36 1.08-.45 1.62.72.36 1.53.63 2.34.81-.18.72-.45 1.35-.72 1.98.9.45 1.89.72 2.88.81.18.72.45 1.35.72 1.98.72.18 1.35.36 2.07.36.54 0 1.08-.09 1.62-.27-.216-.63-.36-1.26-.45-1.89.936-.27 1.8-.63 2.61-1.08.45.18.9.27 1.35.36.54.09 1.08.18 1.62.18 1.44 0 2.79-.36 3.96-.99-.36-.72-.81-1.35-1.26-1.98z" fill="currentColor"/></svg>
        <span>支付宝</span>
      </div>
    </div>
    <div class="pay-container">
      <div class="pay-box" v-if="!paid">
        <div class="pay-title">确认付款</div>
        <div class="pay-amount">
          <span class="currency">¥</span>
          <span class="amount">{{ amount }}</span>
        </div>
        <div class="pay-info">
          <div class="info-row">
            <span>商品</span>
            <span>商城购物</span>
          </div>
          <div class="info-row">
            <span>订单号</span>
            <span>{{ orderNo }}</span>
          </div>
        </div>
        <div class="pay-method">
          <div class="method-item active">
            <svg viewBox="0 0 24 24" fill="none" class="method-icon"><rect x="2" y="4" width="20" height="16" rx="2" fill="#1677FF"/><path d="M6 12h12M6 8h12M6 16h8" stroke="white" stroke-width="1.5"/></svg>
            <span>支付宝</span>
            <span class="check">✓</span>
          </div>
        </div>
        <el-button type="primary" size="large" @click="simulatePay" :loading="loading" class="confirm-btn">
          确认付款 ¥{{ amount }}
        </el-button>
        <p class="tip">提示：点击"确认付款"按钮完成支付</p>
      </div>
      <div class="success-box" v-else>
        <div class="success-icon">✓</div>
        <h2>支付成功</h2>
        <p class="success-amount">¥{{ amount }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const orderNo = route.params.orderNo
const amount = ref(0)
const loading = ref(false)
const paid = ref(false)

const loadOrder = async () => {
  const res = await api.get(`/order/detail/${orderNo}`)
  amount.value = res.data.payAmount
}

const simulatePay = async () => {
  loading.value = true
  try {
    await api.post('/payment/pay', { orderNo, payType: 'ALIPAY' })
    paid.value = true
    loading.value = false
    ElMessage.success('支付成功')
    setTimeout(() => router.push('/orders'), 1500)
  } catch (e) {
    loading.value = false
  }
}

onMounted(() => loadOrder())
</script>

<style scoped>
.alipay-page {
  min-height: 100vh;
  background: #f5f7fa;
}
.alipay-header {
  background: #1677FF;
  padding: 16px 24px;
}
.header-content {
  max-width: 900px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  gap: 10px;
  color: white;
  font-size: 18px;
  font-weight: 600;
}
.alipay-logo {
  width: 28px;
  height: 28px;
}
.pay-container {
  max-width: 440px;
  margin: 40px auto;
}
.pay-box {
  background: white;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}
.pay-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}
.pay-amount {
  text-align: center;
  margin-bottom: 24px;
}
.pay-amount .currency {
  font-size: 20px;
  color: #333;
  vertical-align: top;
}
.pay-amount .amount {
  font-size: 48px;
  font-weight: 800;
  color: #333;
}
.pay-info {
  background: #f8f9fc;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
}
.info-row {
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
  font-size: 14px;
  color: #666;
}
.pay-method {
  margin-bottom: 24px;
}
.method-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  font-size: 14px;
}
.method-item.active {
  border-color: #1677FF;
  background: #f0f7ff;
}
.method-icon {
  width: 24px;
  height: 24px;
}
.check {
  margin-left: auto;
  color: #1677FF;
  font-weight: 600;
}
.confirm-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  background: #1677FF;
  border: none;
  border-radius: 8px;
}
.confirm-btn:hover {
  background: #4096FF;
}
.success-box {
  background: white;
  border-radius: 12px;
  padding: 48px 32px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}
.success-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: #52c41a;
  color: white;
  font-size: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  animation: scaleIn 0.3s ease;
}
@keyframes scaleIn {
  from { transform: scale(0); }
  to { transform: scale(1); }
}
.success-box h2 {
  font-size: 22px;
  color: #333;
  margin-bottom: 8px;
}
.success-amount {
  font-size: 32px;
  font-weight: 700;
  color: #333;
}
.tip {
  color: #c0c4cc;
  font-size: 12px;
  margin-top: 16px;
  text-align: center;
}
</style>
