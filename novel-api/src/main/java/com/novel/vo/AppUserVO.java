package com.novel.vo;

import lombok.Data;

@Data
public class AppUserVO {
    private Long id;
    private String nickname;
    private String avatar;
    private Integer trialViewTime;
    private Integer isTrialEnabled;
}
