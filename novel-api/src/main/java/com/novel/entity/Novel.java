package com.novel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("novel")
public class Novel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private Long authorId;
    private String cover;
    private String description;
    private String category;
    private String status; // DRAFT, PENDING, APPROVED, REJECTED
    private Long auditUserId;
    private LocalDateTime auditTime;
    private String auditRemark;
    private Integer sortOrder;
    private Integer viewCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
