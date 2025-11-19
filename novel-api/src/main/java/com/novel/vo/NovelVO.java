package com.novel.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NovelVO {
    private Long id;
    private String title;
    private Long authorId;
    private String authorName;
    private String cover;
    private String description;
    private String category;
    private String status;
    private Integer viewCount;
    private LocalDateTime createTime;
}
