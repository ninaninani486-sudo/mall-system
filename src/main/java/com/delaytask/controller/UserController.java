package com.delaytask.controller;

import com.delaytask.dto.LoginDTO;
import com.delaytask.dto.RegisterDTO;
import com.delaytask.entity.User;
import com.delaytask.service.UserService;
import com.delaytask.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户接口")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<?> login(@RequestBody LoginDTO dto) {
        Map<String, Object> result = userService.login(dto);
        if ((Integer) result.get("code") == 200) {
            return Result.success(result);
        }
        return Result.error((String) result.get("message"));
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<?> register(@RequestBody RegisterDTO dto) {
        Map<String, Object> result = userService.register(dto);
        if ((Integer) result.get("code") == 200) {
            return Result.success(result.get("message"));
        }
        return Result.error((String) result.get("message"));
    }

    @GetMapping("/info")
    @Operation(summary = "获取用户信息")
    public Result<?> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getUserById(userId);
        return Result.success(user);
    }
}
