package com.delaytask.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.delaytask.entity.Product;
import com.delaytask.mapper.ProductMapper;
import com.delaytask.service.ProductService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
        implements ProductService {

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
    @Cacheable(value = "product:detail", key = "#productId", unless = "#result == null")
    public Product getProductDetail(Long productId) {
        return baseMapper.selectById(productId);
    }

    @Override
    @CacheEvict(value = "product:detail", key = "#product.id")
    public boolean updateById(Product product) {
        return super.updateById(product);
    }
}
