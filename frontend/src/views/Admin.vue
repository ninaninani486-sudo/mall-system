<template>
  <div class="admin">
    <div class="page-header">
      <h2>管理后台</h2>
      <p>商品与订单管理</p>
    </div>

    <el-tabs v-model="activeTab" class="admin-tabs">
      <el-tab-pane label="商品管理" name="products">
        <div class="toolbar">
          <el-input v-model="productKeyword" placeholder="搜索商品名称..." clearable @clear="loadProducts" @keyup.enter="loadProducts" class="search-input" prefix-icon="Search" />
        </div>
        <el-table :data="products" style="width: 100%" v-loading="productLoading" stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="name" label="商品名称" min-width="180" />
          <el-table-column prop="category" label="分类" width="100" />
          <el-table-column label="价格" width="120">
            <template #default="{ row }">¥{{ row.price }}</template>
          </el-table-column>
          <el-table-column prop="stock" label="库存" width="80" />
          <el-table-column prop="sales" label="销量" width="80" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" effect="plain" round size="small">
                {{ row.status === 1 ? '上架' : '下架' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{ row }">
              <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="toggleProduct(row)">
                {{ row.status === 1 ? '下架' : '上架' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-wrapper" v-if="productTotal > 0">
          <el-pagination v-model:current-page="productPage" :page-size="productSize" :total="productTotal" layout="prev, pager, next" @current-change="loadProducts" />
        </div>
      </el-tab-pane>

      <el-tab-pane label="订单管理" name="orders">
        <div class="toolbar">
          <el-input v-model="orderKeyword" placeholder="搜索订单号..." clearable @clear="loadOrders" @keyup.enter="loadOrders" class="search-input" prefix-icon="Search" />
          <el-select v-model="orderStatus" placeholder="全部状态" clearable @change="loadOrders" class="status-select">
            <el-option label="待支付" value="PENDING" />
            <el-option label="已支付" value="PAID" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </div>
        <el-table :data="orders" style="width: 100%" v-loading="orderLoading" stripe>
          <el-table-column prop="orderNo" label="订单号" min-width="200" />
          <el-table-column label="金额" width="120">
            <template #default="{ row }">¥{{ row.payAmount }}</template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" effect="dark" round size="small">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="支付方式" width="120">
            <template #default="{ row }">{{ row.payType || '-' }}</template>
          </el-table-column>
          <el-table-column label="下单时间" min-width="160">
            <template #default="{ row }">{{ row.createTime }}</template>
          </el-table-column>
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button size="small" type="primary" link @click="viewOrder(row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-wrapper" v-if="orderTotal > 0">
          <el-pagination v-model:current-page="orderPage" :page-size="orderSize" :total="orderTotal" layout="prev, pager, next" @current-change="loadOrders" />
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="detailVisible" title="订单详情" width="600px">
      <div v-if="currentOrder" class="order-detail">
        <div class="detail-row"><span class="label">订单号：</span><span>{{ currentOrder.orderNo }}</span></div>
        <div class="detail-row"><span class="label">状态：</span><el-tag :type="getStatusType(currentOrder.status)" effect="dark" round size="small">{{ getStatusText(currentOrder.status) }}</el-tag></div>
        <div class="detail-row"><span class="label">金额：</span><span>¥{{ currentOrder.payAmount }}</span></div>
        <div class="detail-row"><span class="label">支付方式：</span><span>{{ currentOrder.payType || '-' }}</span></div>
        <div class="detail-row"><span class="label">下单时间：</span><span>{{ currentOrder.createTime }}</span></div>
        <div class="detail-row"><span class="label">地址：</span><span>{{ currentOrder.address }}</span></div>
        <el-divider />
        <h4>商品清单</h4>
        <div v-for="item in currentOrder.items || []" :key="item.id" class="item-row">
          <img :src="proxyImage(item.productImage)" class="item-img" />
          <span class="item-name">{{ item.productName }}</span>
          <span class="item-sub">¥{{ item.price }} x {{ item.quantity }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api, { proxyImage } from '../api'

const activeTab = ref('products')

const productKeyword = ref('')
const products = ref([])
const productPage = ref(1)
const productSize = ref(10)
const productTotal = ref(0)
const productLoading = ref(false)

const orderKeyword = ref('')
const orderStatus = ref('')
const orders = ref([])
const orderPage = ref(1)
const orderSize = ref(10)
const orderTotal = ref(0)
const orderLoading = ref(false)
const detailVisible = ref(false)
const currentOrder = ref(null)

const loadProducts = async () => {
  productLoading.value = true
  try {
    const params = { page: productPage.value, size: productSize.value }
    if (productKeyword.value) params.keyword = productKeyword.value
    const res = await api.get('/admin/product/list', { params })
    products.value = res.data.records
    productTotal.value = res.data.total
  } finally {
    productLoading.value = false
  }
}

const toggleProduct = async (row) => {
  await api.post(`/admin/product/${row.id}/toggle`)
  loadProducts()
}

const loadOrders = async () => {
  orderLoading.value = true
  try {
    const params = { page: orderPage.value, size: orderSize.value }
    if (orderKeyword.value) params.orderNo = orderKeyword.value
    if (orderStatus.value) params.status = orderStatus.value
    const res = await api.get('/admin/order/list', { params })
    orders.value = res.data.records
    orderTotal.value = res.data.total
  } finally {
    orderLoading.value = false
  }
}

const viewOrder = (row) => {
  currentOrder.value = row
  detailVisible.value = true
}

const getStatusType = (status) => {
  const map = { PENDING: 'warning', PAID: 'success', CANCELLED: 'info' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { PENDING: '待支付', PAID: '已支付', CANCELLED: '已取消' }
  return map[status] || status
}

onMounted(() => {
  loadProducts()
  loadOrders()
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
.admin-tabs {
  background: white;
  border-radius: 16px;
  padding: 20px 24px;
  border: 1px solid rgba(0, 0, 0, 0.04);
}
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
.search-input {
  width: 300px;
}
.status-select {
  width: 140px;
}
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
.detail-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 0;
  font-size: 14px;
}
.detail-row .label {
  color: #909399;
  width: 80px;
  text-align: right;
}
.item-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}
.item-img {
  width: 48px;
  height: 48px;
  border-radius: 6px;
  object-fit: cover;
  background: #f5f7fa;
}
.item-name {
  flex: 1;
  font-size: 14px;
}
.item-sub {
  font-size: 13px;
  color: #909399;
}
</style>
