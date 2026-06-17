<template>
  <div class="account">
    <div class="page-header">
      <h2>我的账户</h2>
      <p>管理你的资金</p>
    </div>
    <div class="account-grid">
      <div class="balance-card">
        <div class="balance-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="4" width="22" height="16" rx="2" ry="2"/><line x1="1" y1="10" x2="23" y2="10"/></svg>
        </div>
        <div class="balance-info">
          <span class="balance-label">账户余额</span>
          <span class="balance-amount">¥{{ account.balance || 0 }}</span>
        </div>
        <div class="recharge-form">
          <el-input v-model="rechargeAmount" placeholder="输入充值金额" type="number" size="large" class="recharge-input" />
          <el-button type="primary" size="large" @click="handleRecharge" :loading="loading" class="recharge-btn">充值</el-button>
        </div>
      </div>
      <div class="quick-amounts">
        <span class="amount-tag" @click="rechargeAmount = 100">¥100</span>
        <span class="amount-tag" @click="rechargeAmount = 500">¥500</span>
        <span class="amount-tag" @click="rechargeAmount = 1000">¥1000</span>
        <span class="amount-tag" @click="rechargeAmount = 5000">¥5000</span>
      </div>
    </div>
    <div class="transaction-card">
      <div class="card-header">
        <h3>交易记录</h3>
        <span class="record-count">{{ transactions.length }} 条记录</span>
      </div>
      <el-table :data="transactions" style="width: 100%" :header-cell-style="{ background: '#fafbfc', color: '#606266' }">
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'RECHARGE' ? 'success' : row.type === 'PAY' ? 'warning' : 'info'" effect="plain" round size="small">
              {{ getTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="金额" width="120">
          <template #default="{ row }">
            <span :class="['amount-cell', row.type === 'RECHARGE' ? 'income' : 'expense']">
              {{ row.type === 'RECHARGE' ? '+' : '-' }}¥{{ row.amount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="变动前" width="120">
          <template #default="{ row }">¥{{ row.balanceBefore }}</template>
        </el-table-column>
        <el-table-column label="变动后" width="120">
          <template #default="{ row }">¥{{ row.balanceAfter }}</template>
        </el-table-column>
        <el-table-column label="时间" min-width="160">
          <template #default="{ row }">
            <span class="time-cell">{{ row.createTime }}</span>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="transactions.length === 0" description="暂无交易记录" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api'
import { ElMessage } from 'element-plus'

const account = ref({})
const transactions = ref([])
const rechargeAmount = ref('')
const loading = ref(false)

const loadAccount = async () => {
  try {
    const res = await api.get('/account/info')
    account.value = res.data || {}
  } catch (e) {
    console.error('加载账户信息失败', e)
  }
}

const loadTransactions = async () => {
  try {
    const res = await api.get('/account/transactions')
    transactions.value = res.data || []
  } catch (e) {
    console.error('加载交易记录失败', e)
  }
}

const getTypeText = (type) => {
  const map = { RECHARGE: '充值', PAY: '支付', REFUND: '退款' }
  return map[type] || type
}

const handleRecharge = async () => {
  if (!rechargeAmount.value || rechargeAmount.value <= 0) {
    ElMessage.error('请输入有效金额')
    return
  }
  loading.value = true
  try {
    await api.post('/account/recharge', { amount: parseFloat(rechargeAmount.value) })
    ElMessage.success('充值成功')
    rechargeAmount.value = ''
    loadAccount()
    loadTransactions()
  } catch (e) {
    console.error('充值失败', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadAccount()
  loadTransactions()
})
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
.account-grid {
  margin-bottom: 24px;
}
.balance-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 32px;
  color: white;
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.balance-icon {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.balance-icon svg {
  width: 24px;
  height: 24px;
}
.balance-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.balance-label {
  font-size: 14px;
  opacity: 0.8;
}
.balance-amount {
  font-size: 40px;
  font-weight: 800;
  letter-spacing: 1px;
}
.recharge-form {
  display: flex;
  gap: 12px;
}
.recharge-input {
  flex: 1;
  max-width: 280px;
}
.recharge-input :deep(.el-input__wrapper) {
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: none;
}
.recharge-input :deep(.el-input__wrapper:hover) {
  border-color: rgba(255, 255, 255, 0.4);
}
.recharge-input :deep(.el-input__wrapper.is-focus) {
  border-color: white;
  box-shadow: 0 0 0 3px rgba(255, 255, 255, 0.15);
}
.recharge-input :deep(.el-input__inner) {
  color: white;
}
.recharge-input :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.5);
}
.recharge-btn {
  border-radius: 10px;
  padding: 0 24px;
  font-weight: 600;
  background: white;
  color: #667eea;
  border: none;
}
.recharge-btn:hover {
  background: rgba(255, 255, 255, 0.9);
}
.quick-amounts {
  display: flex;
  gap: 10px;
  margin-top: 16px;
}
.amount-tag {
  padding: 8px 20px;
  background: white;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  color: #667eea;
  cursor: pointer;
  border: 1px solid #e8e8e8;
  transition: all 0.2s;
}
.amount-tag:hover {
  border-color: #667eea;
  background: #f0f2ff;
}
.transaction-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  border: 1px solid rgba(0, 0, 0, 0.04);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.card-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
}
.record-count {
  font-size: 13px;
  color: #c0c4cc;
}
.amount-cell {
  font-weight: 600;
}
.amount-cell.income {
  color: #67c23a;
}
.amount-cell.expense {
  color: #f56c6c;
}
.time-cell {
  font-size: 13px;
  color: #909399;
}
</style>
