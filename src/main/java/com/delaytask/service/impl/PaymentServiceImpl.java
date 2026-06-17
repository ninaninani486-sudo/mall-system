package com.delaytask.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.delaytask.entity.*;
import com.delaytask.mapper.*;
import com.delaytask.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment>
        implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private AccountTransactionMapper transactionMapper;

    @Override
    @Transactional
    public Payment payOrder(Long userId, String orderNo, String payType) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }
        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不允许支付");
        }

        UserAccount account = userAccountMapper.selectByUserId(userId);
        if (account == null) {
            throw new RuntimeException("账户不存在");
        }

        if ("BALANCE".equals(payType)) {
            if (account.getBalance().compareTo(order.getPayAmount()) < 0) {
                throw new RuntimeException("余额不足");
            }

            BigDecimal beforeBalance = account.getBalance();
            int rows = userAccountMapper.updateBalance(userId, order.getPayAmount().negate(), account.getVersion());
            if (rows == 0) {
                throw new RuntimeException("支付失败，请重试");
            }

            AccountTransaction transaction = new AccountTransaction();
            transaction.setUserId(userId);
            transaction.setType("PAY");
            transaction.setAmount(order.getPayAmount().negate());
            transaction.setBalanceBefore(beforeBalance);
            transaction.setBalanceAfter(beforeBalance.subtract(order.getPayAmount()));
            transaction.setOrderNo(orderNo);
            transaction.setRemark("订单支付");
            transactionMapper.insert(transaction);
        } else {
            log.info("模拟第三方支付成功, orderNo={}, payType={}", orderNo, payType);
        }

        Payment payment = new Payment();
        payment.setPaymentNo("PAY" + UUID.randomUUID().toString().substring(0, 16).toUpperCase());
        payment.setOrderNo(orderNo);
        payment.setUserId(userId);
        payment.setAmount(order.getPayAmount());
        payment.setPayType(payType);
        payment.setStatus("SUCCESS");
        paymentMapper.insert(payment);

        order.setStatus("PAID");
        order.setPayType(payType);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);

        log.info("订单支付成功, orderNo={}, amount={}, payType={}", orderNo, order.getPayAmount(), payType);
        return payment;
    }

    @Override
    @Transactional
    public void handleOrderTimeout(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            log.warn("订单不存在, orderNo={}", orderNo);
            return;
        }
        if (!"PENDING".equals(order.getStatus())) {
            log.info("订单状态已变更, orderNo={}, status={}", orderNo, order.getStatus());
            return;
        }

        log.info("订单超时自动取消, orderNo={}", orderNo);

        List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
        for (OrderItem item : items) {
            productMapper.restoreStock(item.getProductId(), item.getQuantity());

            LambdaQueryWrapper<Cart> cartWrapper = new LambdaQueryWrapper<>();
            cartWrapper.eq(Cart::getUserId, order.getUserId())
                    .eq(Cart::getProductId, item.getProductId());
            Cart existingCart = cartMapper.selectOne(cartWrapper);

            if (existingCart != null) {
                existingCart.setQuantity(existingCart.getQuantity() + item.getQuantity());
                existingCart.setChecked(1);
                cartMapper.updateById(existingCart);
            } else {
                Cart cart = new Cart();
                cart.setUserId(order.getUserId());
                cart.setProductId(item.getProductId());
                cart.setQuantity(item.getQuantity());
                cart.setChecked(1);
                cartMapper.insert(cart);
            }
        }

        order.setStatus("CANCELLED");
        orderMapper.updateById(order);

        log.info("订单超时取消完成, 商品已返回购物车, orderNo={}", orderNo);
    }

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CartMapper cartMapper;
}
