package com.novel.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.novel.common.Result;
import com.novel.common.SecurityUtil;
import com.novel.dto.AuditDTO;
import com.novel.dto.NovelDTO;
import com.novel.service.NovelService;
import com.novel.vo.NovelVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/novel")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
public class AdminNovelController {
    
    private final NovelService novelService;
    
    /**
     * 获取小说列表（后台）
     */
    @GetMapping("/list")
    public Result<Page<NovelVO>> getNovelList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long authorId) {
        try {
            // 作者只能看自己的小说
            String role = SecurityUtil.getCurrentUserRole();
            if ("AUTHOR".equals(role)) {
                authorId = SecurityUtil.getCurrentUserId();
            }
            
            Page<NovelVO> page = novelService.getAdminNovelList(pageNum, pageSize, status, authorId);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 创建小说
     */
    @PostMapping
    public Result<NovelVO> createNovel(@Valid @RequestBody NovelDTO novelDTO) {
        try {
            Long authorId = SecurityUtil.getCurrentUserId();
            NovelVO novel = novelService.createNovel(novelDTO, authorId);
            return Result.success(novel);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新小说
     */
    @PutMapping("/{id}")
    public Result<NovelVO> updateNovel(@PathVariable Long id, @Valid @RequestBody NovelDTO novelDTO) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            String currentUserRole = SecurityUtil.getCurrentUserRole();
            NovelVO novel = novelService.updateNovel(id, novelDTO, currentUserId, currentUserRole);
            return Result.success(novel);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除小说
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteNovel(@PathVariable Long id) {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            String currentUserRole = SecurityUtil.getCurrentUserRole();
            novelService.deleteNovel(id, currentUserId, currentUserRole);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 提交审核
     */
    @PostMapping("/{id}/submit")
    public Result<Void> submitAudit(@PathVariable Long id) {
        try {
            Long authorId = SecurityUtil.getCurrentUserId();
            novelService.submitAudit(id, authorId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 审核小说（仅管理员）
     */
    @PostMapping("/{id}/audit")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> auditNovel(@PathVariable Long id, @Valid @RequestBody AuditDTO auditDTO) {
        try {
            Long auditUserId = SecurityUtil.getCurrentUserId();
            novelService.auditNovel(id, auditDTO.getStatus(), auditDTO.getRemark(), auditUserId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
