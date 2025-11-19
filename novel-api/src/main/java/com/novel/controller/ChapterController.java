package com.novel.controller;

import com.novel.common.Result;
import com.novel.service.ChapterService;
import com.novel.vo.ChapterListVO;
import com.novel.vo.ChapterVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chapter")
@RequiredArgsConstructor
public class ChapterController {
    
    private final ChapterService chapterService;
    
    /**
     * 获取章节列表
     */
    @GetMapping("/novel/{novelId}")
    public Result<List<ChapterListVO>> getChapterList(@PathVariable Long novelId) {
        try {
            List<ChapterListVO> list = chapterService.getChapterList(novelId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取章节内容（分页）
     */
    @GetMapping("/{id}/content")
    public Result<ChapterVO> getChapterContent(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page) {
        try {
            ChapterVO chapter = chapterService.getChapterContent(id, page);
            return Result.success(chapter);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
