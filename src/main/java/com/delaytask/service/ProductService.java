package com.delaytask.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.delaytask.entity.Product;

public interface ProductService extends IService<Product> {

    IPage<Product> getProductList(Page<Product> page, String keyword, String category);

    Product getProductDetail(Long productId);
}
