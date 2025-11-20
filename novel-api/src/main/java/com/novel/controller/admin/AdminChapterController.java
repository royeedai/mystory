package com.novel.controller.admin;

import com.novel.common.Result;
import com.novel.common.SecurityUtil;
import com.novel.dto.ChapterDTO;
import com.novel.service.ChapterService;
import com.novel.vo.ChapterListVO;
import com.novel.vo.ChapterVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/chapter")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
public class AdminChapterController {
    
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
     * 获取章节详情
     */
    @GetMapping("/{id}")
    public Result<ChapterVO> getChapter(@PathVariable Long id) {
        try {
            ChapterVO chapter = chapterService.getChapterById(id);
            return Result.success(chapter);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 添加章节
     */
    @PostMapping("/novel/{novelId}")
    public Result<ChapterVO> createChapter(@PathVariable Long novelId, @Valid @RequestBody ChapterDTO chapterDTO) {
        try {
            Long authorId = SecurityUtil.getCurrentUserId();
            String role = SecurityUtil.getCurrentUserRole();
            chapterDTO.setNovelId(novelId);
            ChapterVO chapter = chapterService.createChapter(chapterDTO, authorId, role);
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
            String role = SecurityUtil.getCurrentUserRole();
            ChapterVO chapter = chapterService.updateChapter(id, chapterDTO, authorId, role);
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
            String role = SecurityUtil.getCurrentUserRole();
            chapterService.deleteChapter(id, authorId, role);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
