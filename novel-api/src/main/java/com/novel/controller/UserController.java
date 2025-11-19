package com.novel.controller;

import com.novel.common.Result;
import com.novel.common.SecurityUtil;
import com.novel.service.AdRewardService;
import com.novel.service.UserService;
import com.novel.vo.AppUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final AdRewardService adRewardService;
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<AppUserVO> getUserInfo() {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            if (userId == null) {
                return Result.error(401, "未登录");
            }
            
            AppUserVO user = userService.getUserInfo(userId);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 广告激励奖励接口
     */
    @PostMapping("/ad-reward")
    public Result<Map<String, Object>> adReward(@RequestBody Map<String, String> params) {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            if (userId == null) {
                return Result.error(401, "未登录");
            }
            
            String adType = params.getOrDefault("adType", "REWARD");
            Integer rewardTime = adRewardService.rewardAdWatch(userId, adType);
            
            return Result.success(Map.of("rewardTime", rewardTime, "message", "获得" + rewardTime + "秒试看时间"));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
