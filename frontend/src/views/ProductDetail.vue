<template>
  <div class="product-detail" v-if="product">
    <div class="back-btn" @click="$router.back()">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20"><polyline points="15 18 9 12 15 6"/></svg>
      返回
    </div>
    <div class="detail-card">
      <div class="detail-left">
        <div class="image-wrapper">
          <img :src="proxyImage(product.image)" class="detail-image" />
        </div>
      </div>
      <div class="detail-right">
        <div class="product-tag" v-if="product.category">{{ product.category }}</div>
        <h1 class="product-title">{{ product.name }}</h1>
        <p class="product-desc">{{ product.description }}</p>
        <div class="price-section">
          <span class="price">¥{{ product.price }}</span>
          <span class="stock-info">库存 {{ product.stock }} 件</span>
        </div>
        <div class="sales-info">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><path d="M20.59 13.41l-7.17 7.17a2 2 0 01-2.83 0L2 12V2h10l8.59 8.59a2 2 0 010 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/></svg>
          已售 {{ product.sales }} 件
        </div>
        <div class="action-section">
          <div class="quantity-control">
            <span class="label">数量</span>
            <el-input-number v-model="quantity" :min="1" :max="product.stock" size="large" />
          </div>
          <div class="buttons">
            <el-button type="primary" size="large" @click="addToCart" :disabled="product.stock === 0" class="add-cart-btn">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18"><circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.68 13.39a2 2 0 002 1.61h9.72a2 2 0 002-1.61L23 6H6"/></svg>
              加入购物车
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import api, { proxyImage } from '../api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const product = ref(null)
const quantity = ref(1)

const loadProduct = async () => {
  const res = await api.get(`/product/detail/${route.params.id}`)
  product.value = res.data
}

const addToCart = async () => {
  await api.post('/cart/add', { productId: product.value.id, quantity: quantity.value })
  ElMessage.success('已加入购物车')
}

onMounted(() => loadProduct())
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
  display: flex;
  gap: 48px;
  background: white;
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}
.detail-left {
  flex: 0 0 420px;
}
.image-wrapper {
  border-radius: 16px;
  overflow: hidden;
  aspect-ratio: 1;
  background: #f5f7fa;
}
.detail-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.detail-right {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.product-tag {
  display: inline-block;
  padding: 4px 12px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  font-size: 12px;
  font-weight: 600;
  border-radius: 20px;
  width: fit-content;
  margin-bottom: 12px;
}
.product-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 8px;
}
.product-desc {
  color: #909399;
  font-size: 15px;
  line-height: 1.6;
  margin-bottom: 24px;
}
.price-section {
  display: flex;
  align-items: baseline;
  gap: 16px;
  margin-bottom: 12px;
}
.price {
  font-size: 32px;
  font-weight: 800;
  color: #f56c6c;
}
.stock-info {
  font-size: 14px;
  color: #c0c4cc;
}
.sales-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #909399;
  font-size: 14px;
  margin-bottom: 32px;
}
.action-section {
  margin-top: auto;
}
.quantity-control {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}
.label {
  font-size: 14px;
  color: #606266;
}
.buttons {
  display: flex;
  gap: 12px;
}
.add-cart-btn {
  flex: 1;
  height: 48px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}
.add-cart-btn:hover {
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
}
</style>
