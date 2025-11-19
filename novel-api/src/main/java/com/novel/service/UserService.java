package com.novel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novel.common.JwtUtil;
import com.novel.entity.AppUser;
import com.novel.mapper.AppUserMapper;
import com.novel.vo.AppUserVO;
import com.novel.vo.TrialCheckVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final AppUserMapper appUserMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtUtil jwtUtil;
    
    private static final String TRIAL_TIME_KEY = "trial:time:";
    
    /**
     * 获取用户信息
     */
    public AppUserVO getUserInfo(Long userId) {
        AppUser user = appUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 从Redis获取实时试看时间
        Integer remainingTime = getTrialTimeFromRedis(userId);
        if (remainingTime == null) {
            remainingTime = user.getTrialViewTime() != null ? user.getTrialViewTime() : 0;
        }
        
        AppUserVO vo = new AppUserVO();
        BeanUtils.copyProperties(user, vo);
        vo.setTrialViewTime(remainingTime);
        
        return vo;
    }
    
    /**
     * 检查试看权限
     */
    public TrialCheckVO checkTrialPermission(Long userId) {
        AppUser user = appUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 检查是否允许试看
        if (user.getIsTrialEnabled() == null || user.getIsTrialEnabled() == 0) {
            TrialCheckVO vo = new TrialCheckVO();
            vo.setCanRead(false);
            vo.setRemainingTime(0);
            vo.setMessage("您已被禁止试看");
            return vo;
        }
        
        // 从Redis获取剩余时间
        Integer remainingTime = getTrialTimeFromRedis(userId);
        if (remainingTime == null) {
            remainingTime = user.getTrialViewTime() != null ? user.getTrialViewTime() : 0;
            // 同步到Redis
            setTrialTimeToRedis(userId, remainingTime);
        }
        
        TrialCheckVO vo = new TrialCheckVO();
        vo.setRemainingTime(remainingTime);
        
        if (remainingTime > 0) {
            vo.setCanRead(true);
            vo.setMessage("可以阅读");
        } else {
            vo.setCanRead(false);
            vo.setMessage("试看时间不足，请观看广告获取更多时间");
        }
        
        return vo;
    }
    
    /**
     * 扣减试看时间
     */
    @Transactional
    public void consumeTrialTime(Long userId, Integer seconds) {
        Integer remainingTime = getTrialTimeFromRedis(userId);
        if (remainingTime == null) {
            AppUser user = appUserMapper.selectById(userId);
            remainingTime = user.getTrialViewTime() != null ? user.getTrialViewTime() : 0;
        }
        
        remainingTime = Math.max(0, remainingTime - seconds);
        setTrialTimeToRedis(userId, remainingTime);
        
        // 同步到数据库
        AppUser user = appUserMapper.selectById(userId);
        user.setTrialViewTime(remainingTime);
        appUserMapper.updateById(user);
    }
    
    /**
     * 设置用户试看权限（后台）
     */
    @Transactional
    public void setTrialSettings(Long userId, Integer isTrialEnabled) {
        AppUser user = appUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        user.setIsTrialEnabled(isTrialEnabled);
        appUserMapper.updateById(user);
    }
    
    /**
     * 从Redis获取试看时间
     */
    private Integer getTrialTimeFromRedis(Long userId) {
        Object value = redisTemplate.opsForValue().get(TRIAL_TIME_KEY + userId);
        return value != null ? Integer.parseInt(value.toString()) : null;
    }
    
    /**
     * 设置试看时间到Redis
     */
    private void setTrialTimeToRedis(Long userId, Integer seconds) {
        redisTemplate.opsForValue().set(TRIAL_TIME_KEY + userId, seconds, 7, TimeUnit.DAYS);
    }
}
