package com.novel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("chapter")
public class Chapter {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long novelId;
    private String title;
    private String content;
    private Integer chapterNum;
    private Integer wordCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
