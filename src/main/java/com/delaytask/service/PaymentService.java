package com.delaytask.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.delaytask.entity.Payment;

public interface PaymentService extends IService<Payment> {

    Payment payOrder(Long userId, String orderNo, String payType);

    void handleOrderTimeout(String orderNo);
}
