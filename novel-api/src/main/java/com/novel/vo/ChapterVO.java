package com.novel.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChapterVO {
    private Long id;
    private Long novelId;
    private String title;
    private String content;
    private Integer chapterNum;
    private Integer wordCount;
    private Integer totalPages;
    private Integer currentPage;
    private LocalDateTime createTime;
}
