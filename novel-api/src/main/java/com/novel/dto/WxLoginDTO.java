package com.novel.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class WxLoginDTO {
    @NotBlank(message = "code不能为空")
    private String code;
}
