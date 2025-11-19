package com.novel.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.novel.common.Result;
import com.novel.service.UserService;
import com.novel.vo.AppUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    
    private final UserService userService;
    
    /**
     * 设置用户试看权限
     */
    @PutMapping("/{id}/trial-settings")
    public Result<Void> setTrialSettings(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        try {
            Integer isTrialEnabled = params.get("isTrialEnabled");
            if (isTrialEnabled == null) {
                return Result.error("参数错误");
            }
            
            userService.setTrialSettings(id, isTrialEnabled);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户信息（管理员查看）
     */
    @GetMapping("/{id}")
    public Result<AppUserVO> getUserInfo(@PathVariable Long id) {
        try {
            AppUserVO user = userService.getUserInfo(id);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
