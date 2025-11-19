package com.novel.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReadingRecordVO {
    private Long id;
    private Long userId;
    private Long novelId;
    private Long chapterId;
    private Integer pageIndex;
    private LocalDateTime lastReadTime;
}
