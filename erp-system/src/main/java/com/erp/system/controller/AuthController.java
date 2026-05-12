package com.erp.system.controller;

import com.erp.common.response.Result;
import com.erp.common.util.JwtUtil;
import com.erp.system.dto.LoginDTO;
import com.erp.system.dto.UserVO;
import com.erp.system.entity.SysUser;
import com.erp.system.service.SysMenuService;
import com.erp.system.service.SysUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService userService;
    private final SysMenuService menuService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        SysUser user = userService.getByUsername(dto.getUsername());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (user == null || !encoder.matches(dto.getPassword(), user.getPassword())) {
            return Result.fail(401, "用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            return Result.fail(403, "账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setPermissions(menuService.getPermsByUserId(user.getId()));

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", vo);
        return Result.ok(result);
    }

    @GetMapping("/info")
    public Result<UserVO> info(@RequestAttribute("userId") Long userId) {
        SysUser user = userService.getById(userId);
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setAvatar(user.getAvatar());
        vo.setPermissions(menuService.getPermsByUserId(userId));
        return Result.ok(vo);
    }
}
