package com.novel.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class AuditDTO {
    @NotBlank(message = "审核状态不能为空")
    private String status; // APPROVED, REJECTED
    
    private String remark;
}
