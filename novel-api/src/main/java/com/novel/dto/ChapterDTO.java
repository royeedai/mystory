package com.novel.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ChapterDTO {
    private Long id;
    
    @NotNull(message = "小说ID不能为空")
    private Long novelId;
    
    @NotBlank(message = "章节标题不能为空")
    private String title;
    
    @NotBlank(message = "章节内容不能为空")
    private String content;
    
    @NotNull(message = "章节序号不能为空")
    private Integer chapterNum;
}
