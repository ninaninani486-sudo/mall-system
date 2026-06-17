package com.delaytask.controller;

import com.delaytask.dto.PayDTO;
import com.delaytask.entity.Payment;
import com.delaytask.service.PaymentService;
import com.delaytask.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/payment")
@Tag(name = "支付接口")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay")
    @Operation(summary = "订单支付")
    public Result<?> payOrder(HttpServletRequest request, @RequestBody PayDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            Payment payment = paymentService.payOrder(userId, dto.getOrderNo(), dto.getPayType());
            return Result.success(payment);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
