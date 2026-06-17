<template>
  <div class="wechat-pay">
    <div class="pay-container">
      <div class="pay-header">
        <svg viewBox="0 0 24 24" fill="none" class="wechat-icon"><path d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 01.213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 00.167-.054l1.903-1.114a.864.864 0 01.717-.098 10.16 10.16 0 002.837.403c.276 0 .543-.027.811-.05-.857-2.578.157-4.972 1.932-6.446 1.703-1.415 3.882-1.98 5.853-1.838-.576-3.583-4.196-6.348-8.596-6.348zM12.797 16.043c-1.16 0-2.27-.217-3.296-.598.341-.645.63-1.333.855-2.054.605.235 1.25.376 1.926.376.567 0 1.113-.107 1.623-.282a6.322 6.322 0 01-.563 1.552 7.64 7.64 0 01-2.545.006z" fill="currentColor"/><path d="M23.997 14.638c0-3.37-3.257-6.102-7.275-6.102-4.07 0-7.275 2.732-7.275 6.102 0 3.37 3.206 6.102 7.275 6.102.766 0 1.505-.1 2.198-.29a.68.68 0 01.563.078l1.49.87a.258.258 0 00.131.042.229.229 0 00.228-.232c0-.057-.023-.113-.038-.167l-.306-1.16a.467.467 0 01.168-.522c1.44-1.062 2.363-2.65 2.363-4.441z" fill="currentColor" opacity="0.9"/></svg>
        <h2>微信支付</h2>
      </div>
      <div class="amount-section">
        <span class="label">支付金额</span>
        <span class="amount">¥{{ amount }}</span>
      </div>
      <div class="qr-section" v-if="!paid">
        <div class="qr-code">
          <svg viewBox="0 0 100 100" class="qr-placeholder">
            <rect x="5" y="5" width="25" height="25" fill="#333" rx="2"/>
            <rect x="70" y="5" width="25" height="25" fill="#333" rx="2"/>
            <rect x="5" y="70" width="25" height="25" fill="#333" rx="2"/>
            <rect x="10" y="10" width="15" height="15" fill="white" rx="1"/>
            <rect x="75" y="10" width="15" height="15" fill="white" rx="1"/>
            <rect x="10" y="75" width="15" height="15" fill="white" rx="1"/>
            <rect x="14" y="14" width="7" height="7" fill="#333"/>
            <rect x="79" y="14" width="7" height="7" fill="#333"/>
            <rect x="14" y="79" width="7" height="7" fill="#333"/>
            <rect x="35" y="5" width="5" height="5" fill="#333"/>
            <rect x="45" y="5" width="5" height="5" fill="#333"/>
            <rect x="55" y="5" width="5" height="5" fill="#333"/>
            <rect x="35" y="15" width="5" height="5" fill="#333"/>
            <rect x="50" y="15" width="5" height="5" fill="#333"/>
            <rect x="35" y="35" width="30" height="30" fill="#333" rx="2"/>
            <rect x="40" y="40" width="20" height="20" fill="white" rx="1"/>
            <rect x="45" y="45" width="10" height="10" fill="#07C160" rx="1"/>
            <rect x="5" y="40" width="5" height="5" fill="#333"/>
            <rect x="15" y="40" width="5" height="5" fill="#333"/>
            <rect x="25" y="40" width="5" height="5" fill="#333"/>
            <rect x="70" y="40" width="5" height="5" fill="#333"/>
            <rect x="80" y="40" width="5" height="5" fill="#333"/>
            <rect x="90" y="40" width="5" height="5" fill="#333"/>
            <rect x="35" y="70" width="5" height="5" fill="#333"/>
            <rect x="50" y="70" width="5" height="5" fill="#333"/>
            <rect x="70" y="70" width="5" height="5" fill="#333"/>
            <rect x="80" y="80" width="5" height="5" fill="#333"/>
            <rect x="90" y="70" width="5" height="5" fill="#333"/>
            <rect x="70" y="90" width="5" height="5" fill="#333"/>
            <rect x="80" y="90" width="5" height="5" fill="#333"/>
          </svg>
        </div>
        <p class="qr-tip">请使用微信扫一扫</p>
      </div>
      <div class="success-section" v-else>
        <div class="success-icon">✓</div>
        <p>支付成功</p>
      </div>
      <div class="actions">
        <el-button v-if="!paid" type="success" size="large" @click="simulatePay" :loading="loading" class="pay-btn">
          模拟支付成功
        </el-button>
        <el-button @click="goBack" size="large">返回订单</el-button>
      </div>
      <p class="tip">提示：点击"模拟支付成功"按钮完成支付</p>
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
    await api.post('/payment/pay', { orderNo, payType: 'WECHAT' })
    paid.value = true
    loading.value = false
    ElMessage.success('支付成功')
    setTimeout(() => router.push('/orders'), 1500)
  } catch (e) {
    loading.value = false
  }
}

const goBack = () => router.push('/orders')

onMounted(() => loadOrder())
</script>

<style scoped>
.wechat-pay {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
}
.pay-container {
  background: white;
  border-radius: 16px;
  padding: 40px;
  width: 400px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
}
.pay-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 24px;
}
.wechat-icon {
  width: 40px;
  height: 40px;
  color: #07C160;
}
.pay-header h2 {
  font-size: 22px;
  color: #333;
}
.amount-section {
  margin-bottom: 24px;
}
.amount-section .label {
  color: #999;
  font-size: 14px;
}
.amount-section .amount {
  display: block;
  font-size: 36px;
  font-weight: 800;
  color: #333;
  margin-top: 4px;
}
.qr-section {
  margin-bottom: 24px;
}
.qr-code {
  width: 200px;
  height: 200px;
  margin: 0 auto;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 10px;
  background: white;
}
.qr-placeholder {
  width: 100%;
  height: 100%;
}
.qr-tip {
  color: #999;
  font-size: 13px;
  margin-top: 12px;
}
.success-section {
  margin: 30px 0;
}
.success-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: #07C160;
  color: white;
  font-size: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  animation: scaleIn 0.3s ease;
}
@keyframes scaleIn {
  from { transform: scale(0); }
  to { transform: scale(1); }
}
.success-section p {
  font-size: 18px;
  color: #333;
  font-weight: 600;
}
.actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}
.pay-btn {
  background: #07C160;
  border: none;
  font-weight: 600;
  padding: 0 32px;
}
.pay-btn:hover {
  background: #06AD56;
}
.tip {
  color: #c0c4cc;
  font-size: 12px;
  margin-top: 16px;
}
</style>
