package com.delaytask.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.delaytask.entity.UserAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface UserAccountMapper extends BaseMapper<UserAccount> {

    @Select("SELECT * FROM user_account WHERE user_id = #{userId}")
    UserAccount selectByUserId(@Param("userId") Long userId);

    @Update("UPDATE user_account SET balance = balance + #{amount}, version = version + 1 WHERE user_id = #{userId} AND version = #{version}")
    int updateBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount, @Param("version") Integer version);

    @Update("UPDATE user_account SET frozen_amount = frozen_amount + #{amount} WHERE user_id = #{userId}")
    int freezeAmount(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    @Update("UPDATE user_account SET balance = balance - #{amount}, frozen_amount = frozen_amount - #{amount} WHERE user_id = #{userId} AND frozen_amount >= #{amount}")
    int deductFrozenAmount(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}
