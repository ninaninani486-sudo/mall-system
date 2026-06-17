package com.delaytask.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.delaytask.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    @Select("SELECT * FROM cart WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Cart> selectByUserId(@Param("userId") Long userId);
}
