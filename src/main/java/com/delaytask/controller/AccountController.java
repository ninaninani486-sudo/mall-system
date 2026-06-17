package com.delaytask.controller;

import com.delaytask.entity.AccountTransaction;
import com.delaytask.entity.UserAccount;
import com.delaytask.service.AccountService;
import com.delaytask.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
@Tag(name = "账户接口")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/info")
    @Operation(summary = "账户信息")
    public Result<?> getAccountInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserAccount account = accountService.getAccountByUserId(userId);
        return Result.success(account);
    }

    @PostMapping("/recharge")
    @Operation(summary = "账户充值")
    public Result<?> recharge(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        Long userId = (Long) request.getAttribute("userId");
        BigDecimal amount = new BigDecimal(params.get("amount").toString());
        try {
            accountService.recharge(userId, amount);
            return Result.success("充值成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/transactions")
    @Operation(summary = "交易流水")
    public Result<?> getTransactions(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<AccountTransaction> transactions = accountService.getTransactionList(userId);
        return Result.success(transactions);
    }
}
