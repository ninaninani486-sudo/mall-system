package com.delaytask.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.delaytask.entity.AccountTransaction;
import com.delaytask.entity.UserAccount;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService extends IService<UserAccount> {

    UserAccount getAccountByUserId(Long userId);

    void recharge(Long userId, BigDecimal amount);

    List<AccountTransaction> getTransactionList(Long userId);
}
