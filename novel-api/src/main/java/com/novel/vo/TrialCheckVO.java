package com.novel.vo;

import lombok.Data;

@Data
public class TrialCheckVO {
    private Boolean canRead;
    private Integer remainingTime; // 剩余试看时间（秒）
    private String message;
}
