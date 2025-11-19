package com.novel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ad_watch_record")
public class AdWatchRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String adType; // REWARD
    private Integer rewardTime; // 奖励时间（秒）
    private LocalDateTime watchTime;
}
