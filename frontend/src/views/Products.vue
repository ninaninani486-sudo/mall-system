<template>
  <div class="products">
    <div class="page-header">
      <h2>商品中心</h2>
      <p>发现优质好物</p>
    </div>
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索你想要的商品..." clearable @clear="loadProducts" @keyup.enter="loadProducts" size="large" class="search-input" prefix-icon="Search" />
      <el-select v-model="category" placeholder="全部分类" clearable @change="loadProducts" size="large" class="category-select">
        <el-option label="手机" value="Phone" />
        <el-option label="电脑" value="Computer" />
        <el-option label="平板" value="Tablet" />
        <el-option label="配件" value="Accessory" />
        <el-option label="手表" value="Watch" />
        <el-option label="游戏" value="Gaming" />
      </el-select>
      <el-button type="primary" @click="loadProducts" size="large" class="search-btn">搜索</el-button>
    </div>
    <div class="product-grid">
      <div v-for="product in products" :key="product.id" class="product-card" @click="$router.push(`/product/${product.id}`)">
        <div class="product-image-wrapper">
          <img :src="proxyImage(product.image)" class="product-image" />
          <div class="product-badge" v-if="product.stock < 10">仅剩{{ product.stock }}件</div>
        </div>
        <div class="product-info">
          <h3 class="product-name">{{ product.name }}</h3>
          <p class="product-desc">{{ product.description }}</p>
          <div class="product-bottom">
            <span class="price">¥{{ product.price }}</span>
            <span class="sales">已售{{ product.sales }}件</span>
          </div>
        </div>
      </div>
    </div>
    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination v-model:current-page="page" :page-size="size" :total="total" layout="prev, pager, next" @current-change="loadProducts" />
    </div>
    <el-empty v-if="products.length === 0" description="暂无商品" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api, { proxyImage } from '../api'

const products = ref([])
const page = ref(1)
const size = ref(12)
const total = ref(0)
const keyword = ref('')
const category = ref('')

const loadProducts = async () => {
  const params = { page: page.value, size: size.value }
  if (keyword.value) params.keyword = keyword.value
  if (category.value) params.category = category.value
  const res = await api.get('/product/list', { params })
  products.value = res.data.records
  total.value = res.data.total
}

onMounted(() => loadProducts())
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
.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 28px;
}
.search-input {
  flex: 1;
  max-width: 400px;
}
.search-input :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}
.category-select {
  width: 150px;
}
.search-btn {
  border-radius: 10px;
  padding: 0 24px;
}
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}
.product-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(0, 0, 0, 0.04);
}
.product-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}
.product-image-wrapper {
  position: relative;
  overflow: hidden;
  aspect-ratio: 1;
}
.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}
.product-card:hover .product-image {
  transform: scale(1.08);
}
.product-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 4px 10px;
  background: linear-gradient(135deg, #f5576c, #ff6b6b);
  color: white;
  font-size: 11px;
  font-weight: 600;
  border-radius: 20px;
}
.product-info {
  padding: 16px;
}
.product-name {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.product-desc {
  font-size: 12px;
  color: #909399;
  margin-bottom: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.price {
  font-size: 20px;
  font-weight: 700;
  color: #f56c6c;
}
.sales {
  font-size: 12px;
  color: #c0c4cc;
}
.pagination-wrapper {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}
</style>
