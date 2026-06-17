package com.delaytask.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.delaytask.dto.LoginDTO;
import com.delaytask.dto.RegisterDTO;
import com.delaytask.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {

    Map<String, Object> login(LoginDTO dto);

    Map<String, Object> register(RegisterDTO dto);

    User getUserById(Long userId);
}
