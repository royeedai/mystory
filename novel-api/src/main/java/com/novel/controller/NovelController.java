package com.novel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.novel.common.Result;
import com.novel.service.NovelService;
import com.novel.vo.NovelVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/novel")
@RequiredArgsConstructor
public class NovelController {
    
    private final NovelService novelService;
    
    /**
     * 获取小说列表（小程序端）
     */
    @GetMapping("/list")
    public Result<Page<NovelVO>> getNovelList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        try {
            Page<NovelVO> page = novelService.getNovelList(pageNum, pageSize, category, keyword);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取小说详情
     */
    @GetMapping("/{id}")
    public Result<NovelVO> getNovelById(@PathVariable Long id) {
        try {
            NovelVO novel = novelService.getNovelById(id);
            return Result.success(novel);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
