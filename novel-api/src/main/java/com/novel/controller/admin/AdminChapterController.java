package com.novel.controller.admin;

import com.novel.common.Result;
import com.novel.common.SecurityUtil;
import com.novel.dto.ChapterDTO;
import com.novel.service.ChapterService;
import com.novel.vo.ChapterVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/chapter")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
public class AdminChapterController {
    
    private final ChapterService chapterService;
    
    /**
     * 添加章节
     */
    @PostMapping("/novel/{novelId}")
    public Result<ChapterVO> createChapter(@PathVariable Long novelId, @Valid @RequestBody ChapterDTO chapterDTO) {
        try {
            Long authorId = SecurityUtil.getCurrentUserId();
            chapterDTO.setNovelId(novelId);
            ChapterVO chapter = chapterService.createChapter(chapterDTO, authorId);
            return Result.success(chapter);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新章节
     */
    @PutMapping("/{id}")
    public Result<ChapterVO> updateChapter(@PathVariable Long id, @Valid @RequestBody ChapterDTO chapterDTO) {
        try {
            Long authorId = SecurityUtil.getCurrentUserId();
            ChapterVO chapter = chapterService.updateChapter(id, chapterDTO, authorId);
            return Result.success(chapter);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除章节
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteChapter(@PathVariable Long id) {
        try {
            Long authorId = SecurityUtil.getCurrentUserId();
            chapterService.deleteChapter(id, authorId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
