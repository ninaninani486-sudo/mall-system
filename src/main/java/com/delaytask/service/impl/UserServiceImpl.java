package com.delaytask.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.delaytask.dto.LoginDTO;
import com.delaytask.dto.RegisterDTO;
import com.delaytask.entity.User;
import com.delaytask.entity.UserAccount;
import com.delaytask.mapper.UserAccountMapper;
import com.delaytask.mapper.UserMapper;
import com.delaytask.service.UserService;
import com.delaytask.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Map<String, Object> login(LoginDTO dto) {
        Map<String, Object> result = new HashMap<>();

        if (!StringUtils.hasText(dto.getUsername()) || !StringUtils.hasText(dto.getPassword())) {
            result.put("code", 400);
            result.put("message", "用户名或密码不能为空");
            return result;
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            result.put("code", 400);
            result.put("message", "用户不存在");
            return result;
        }

        if (user.getStatus() == 0) {
            result.put("code", 400);
            result.put("message", "账号已被禁用");
            return result;
        }

        String md5Password = DigestUtils.md5DigestAsHex(dto.getPassword().getBytes(StandardCharsets.UTF_8));
        if (!md5Password.equals(user.getPassword())) {
            result.put("code", 400);
            result.put("message", "密码错误");
            return result;
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        result.put("code", 200);
        result.put("message", "登录成功");
        result.put("token", token);
        result.put("user", user);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> register(RegisterDTO dto) {
        Map<String, Object> result = new HashMap<>();

        if (!StringUtils.hasText(dto.getUsername()) || !StringUtils.hasText(dto.getPassword())) {
            result.put("code", 400);
            result.put("message", "用户名或密码不能为空");
            return result;
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        Long count = userMapper.selectCount(wrapper);
        if (count > 0) {
            result.put("code", 400);
            result.put("message", "用户名已存在");
            return result;
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(DigestUtils.md5DigestAsHex(dto.getPassword().getBytes(StandardCharsets.UTF_8)));
        user.setNickname(StringUtils.hasText(dto.getNickname()) ? dto.getNickname() : dto.getUsername());
        user.setPhone(dto.getPhone() != null ? dto.getPhone() : "");
        user.setStatus(1);
        userMapper.insert(user);

        UserAccount account = new UserAccount();
        account.setUserId(user.getId());
        account.setBalance(BigDecimal.ZERO);
        account.setFrozenAmount(BigDecimal.ZERO);
        account.setVersion(0);
        userAccountMapper.insert(account);

        result.put("code", 200);
        result.put("message", "注册成功");
        return result;
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }
}
