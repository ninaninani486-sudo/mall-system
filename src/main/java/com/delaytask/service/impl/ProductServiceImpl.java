package com.delaytask.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.delaytask.entity.Product;
import com.delaytask.mapper.ProductMapper;
import com.delaytask.redis.RedisCache;
import com.delaytask.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
        implements ProductService {

    private static final String CACHE_KEY_PREFIX = "product:detail:";
    private static final long CACHE_TTL_MINUTES = 30;

    @Autowired
    private RedisCache redisCache;

    @Override
    public IPage<Product> getProductList(Page<Product> page, String keyword, String category) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getName, keyword);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(Product::getCategory, category);
        }
        wrapper.orderByDesc(Product::getSales);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public Product getProductDetail(Long productId) {
        String cacheKey = CACHE_KEY_PREFIX + productId;
        Product product = redisCache.get(cacheKey, Product.class);
        if (product != null) {
            return product;
        }
        product = baseMapper.selectById(productId);
        if (product != null) {
            redisCache.set(cacheKey, product, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        }
        return product;
    }

    public void clearProductCache(Long productId) {
        redisCache.delete(CACHE_KEY_PREFIX + productId);
    }
}
