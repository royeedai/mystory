package com.novel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("app_user")
public class AppUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String openid;
    private String unionid;
    private String nickname;
    private String avatar;
    private Integer trialViewTime; // 试看剩余时间（秒）
    private Integer isTrialEnabled; // 是否允许试看：0-否，1-是
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
