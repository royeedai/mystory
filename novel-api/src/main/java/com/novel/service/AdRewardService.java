package com.novel.service;

import com.novel.entity.AdWatchRecord;
import com.novel.entity.AppUser;
import com.novel.mapper.AdWatchRecordMapper;
import com.novel.mapper.AppUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AdRewardService {
    
    private final AdWatchRecordMapper adWatchRecordMapper;
    private final AppUserMapper appUserMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserService userService;
    
    private static final String TRIAL_TIME_KEY = "trial:time:";
    private static final int REWARD_TIME = 300; // 奖励300秒（5分钟）
    
    /**
     * 广告激励奖励
     */
    @Transactional
    public Integer rewardAdWatch(Long userId, String adType) {
        // 记录观看记录
        AdWatchRecord record = new AdWatchRecord();
        record.setUserId(userId);
        record.setAdType(adType);
        record.setRewardTime(REWARD_TIME);
        record.setWatchTime(LocalDateTime.now());
        adWatchRecordMapper.insert(record);
        
        // 增加试看时间
        AppUser user = appUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        Integer currentTime = user.getTrialViewTime() != null ? user.getTrialViewTime() : 0;
        Integer newTime = currentTime + REWARD_TIME;
        
        // 更新Redis
        redisTemplate.opsForValue().set(TRIAL_TIME_KEY + userId, newTime, 7, TimeUnit.DAYS);
        
        // 更新数据库
        user.setTrialViewTime(newTime);
        appUserMapper.updateById(user);
        
        return REWARD_TIME;
    }
}
