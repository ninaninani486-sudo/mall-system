<template>
  <div class="pay">
    <div class="back-btn" @click="$router.push('/orders')">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20"><polyline points="15 18 9 12 15 6"/></svg>
      返回订单
    </div>
    <div class="pay-card" v-if="order">
      <div class="pay-header">
        <div class="check-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="20 6 9 17 4 12"/></svg>
        </div>
        <h2>确认支付</h2>
      </div>
      <div class="pay-info">
        <div class="info-row">
          <span class="label">订单号</span>
          <span class="value">{{ order.orderNo }}</span>
        </div>
        <div class="info-row amount">
          <span class="label">支付金额</span>
          <span class="value price">¥{{ order.payAmount }}</span>
        </div>
      </div>
      <div class="pay-methods">
        <h3>选择支付方式</h3>
        <div class="method-list">
          <div class="method-item" :class="{ active: payType === 'BALANCE' }" @click="payType = 'BALANCE'">
            <div class="method-icon balance-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="4" width="22" height="16" rx="2" ry="2"/><line x1="1" y1="10" x2="23" y2="10"/></svg>
            </div>
            <div class="method-info">
              <span class="method-name">余额支付</span>
              <span class="method-desc">可用余额 ¥{{ balance }}</span>
            </div>
            <div class="method-check">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20" v-if="payType === 'BALANCE'"><polyline points="20 6 9 17 4 12"/></svg>
            </div>
          </div>
          <div class="method-item" :class="{ active: payType === 'WECHAT' }" @click="payType = 'WECHAT'">
            <div class="method-icon wechat-icon">
              <svg viewBox="0 0 24 24" fill="none"><path d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 01.213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 00.167-.054l1.903-1.114a.864.864 0 01.717-.098 10.16 10.16 0 002.837.403c.276 0 .543-.027.811-.05-.857-2.578.157-4.972 1.932-6.446 1.703-1.415 3.882-1.98 5.853-1.838-.576-3.583-4.196-6.348-8.596-6.348z" fill="currentColor"/><path d="M23.997 14.638c0-3.37-3.257-6.102-7.275-6.102-4.07 0-7.275 2.732-7.275 6.102 0 3.37 3.206 6.102 7.275 6.102.766 0 1.505-.1 2.198-.29a.68.68 0 01.563.078l1.49.87a.258.258 0 00.131.042.229.229 0 00.228-.232c0-.057-.023-.113-.038-.167l-.306-1.16a.467.467 0 01.168-.522c1.44-1.062 2.363-2.65 2.363-4.441z" fill="currentColor" opacity="0.9"/></svg>
            </div>
            <div class="method-info">
              <span class="method-name">微信支付</span>
              <span class="method-desc">推荐使用</span>
            </div>
            <div class="method-check">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20" v-if="payType === 'WECHAT'"><polyline points="20 6 9 17 4 12"/></svg>
            </div>
          </div>
          <div class="method-item" :class="{ active: payType === 'ALIPAY' }" @click="payType = 'ALIPAY'">
            <div class="method-icon alipay-icon">
              <svg viewBox="0 0 24 24" fill="none"><rect x="2" y="4" width="20" height="16" rx="2" fill="currentColor"/><path d="M6 12h12M6 8h12M6 16h8" stroke="white" stroke-width="1.5"/></svg>
            </div>
            <div class="method-info">
              <span class="method-name">支付宝</span>
              <span class="method-desc">推荐使用</span>
            </div>
            <div class="method-check">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20" v-if="payType === 'ALIPAY'"><polyline points="20 6 9 17 4 12"/></svg>
            </div>
          </div>
        </div>
      </div>
      <el-button type="primary" size="large" @click="handlePay" :loading="loading" class="pay-btn">
        确认支付 ¥{{ order.payAmount }}
      </el-button>
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
const order = ref(null)
const balance = ref(0)
const payType = ref('BALANCE')
const loading = ref(false)

const loadOrder = async () => {
  const res = await api.get(`/order/detail/${route.params.orderNo}`)
  order.value = res.data
  const accRes = await api.get('/account/info')
  balance.value = accRes.data.balance
}

const handlePay = async () => {
  if (payType.value === 'WECHAT') {
    router.push(`/pay/wechat/${order.value.orderNo}`)
    return
  }
  if (payType.value === 'ALIPAY') {
    router.push(`/pay/alipay/${order.value.orderNo}`)
    return
  }
  loading.value = true
  try {
    await api.post('/payment/pay', { orderNo: order.value.orderNo, payType: payType.value })
    ElMessage.success('支付成功')
    router.push('/orders')
  } finally {
    loading.value = false
  }
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
.pay-card {
  background: white;
  border-radius: 20px;
  padding: 40px;
  max-width: 500px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}
.pay-header {
  text-align: center;
  margin-bottom: 32px;
}
.check-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.check-icon svg {
  width: 32px;
  height: 32px;
  color: white;
}
.pay-header h2 {
  font-size: 22px;
  font-weight: 700;
  color: #1a1a2e;
}
.pay-info {
  background: #f8f9fc;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 28px;
}
.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}
.info-row + .info-row {
  border-top: 1px solid #eef0f5;
}
.info-row .label {
  color: #909399;
  font-size: 14px;
}
.info-row .value {
  color: #333;
  font-weight: 500;
}
.info-row.amount .value.price {
  font-size: 24px;
  font-weight: 800;
  color: #f56c6c;
}
.pay-methods h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 16px;
}
.method-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 28px;
}
.method-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
  border-radius: 12px;
  border: 2px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.2s;
}
.method-item:hover {
  border-color: #667eea;
  background: #f8f9ff;
}
.method-item.active {
  border-color: #667eea;
  background: #f0f2ff;
}
.method-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.method-icon svg {
  width: 24px;
  height: 24px;
}
.balance-icon {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}
.wechat-icon {
  background: #f0f9eb;
  color: #07C160;
}
.alipay-icon {
  background: #e6f4ff;
  color: #1677FF;
}
.method-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.method-name {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}
.method-desc {
  font-size: 12px;
  color: #909399;
}
.method-check {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #667eea;
  display: flex;
  align-items: center;
  justify-content: center;
}
.method-check svg {
  color: white;
}
.pay-btn {
  width: 100%;
  height: 52px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
}
.pay-btn:hover {
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
}
</style>
