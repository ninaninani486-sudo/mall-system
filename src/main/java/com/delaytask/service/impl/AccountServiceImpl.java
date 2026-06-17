package com.delaytask.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.delaytask.entity.AccountTransaction;
import com.delaytask.entity.UserAccount;
import com.delaytask.mapper.AccountTransactionMapper;
import com.delaytask.mapper.UserAccountMapper;
import com.delaytask.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount>
        implements AccountService {

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private AccountTransactionMapper transactionMapper;

    @Override
    public UserAccount getAccountByUserId(Long userId) {
        return userAccountMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public void recharge(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("充值金额必须大于0");
        }

        UserAccount account = userAccountMapper.selectByUserId(userId);
        if (account == null) {
            throw new RuntimeException("账户不存在");
        }

        BigDecimal beforeBalance = account.getBalance();
        int rows = userAccountMapper.updateBalance(userId, amount, account.getVersion());
        if (rows == 0) {
            throw new RuntimeException("充值失败，请重试");
        }

        AccountTransaction transaction = new AccountTransaction();
        transaction.setUserId(userId);
        transaction.setType("RECHARGE");
        transaction.setAmount(amount);
        transaction.setBalanceBefore(beforeBalance);
        transaction.setBalanceAfter(beforeBalance.add(amount));
        transaction.setOrderNo("");
        transaction.setRemark("账户充值");
        transactionMapper.insert(transaction);
    }

    @Override
    public List<AccountTransaction> getTransactionList(Long userId) {
        LambdaQueryWrapper<AccountTransaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountTransaction::getUserId, userId)
                .orderByDesc(AccountTransaction::getCreateTime);
        return transactionMapper.selectList(wrapper);
    }
}
