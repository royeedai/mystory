package com.novel.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChapterListVO {
    private Long id;
    private String title;
    private Integer chapterNum;
    private Integer wordCount;
    private LocalDateTime createTime;
}
