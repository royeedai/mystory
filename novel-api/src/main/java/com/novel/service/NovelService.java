package com.novel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.novel.dto.NovelDTO;
import com.novel.entity.Novel;
import com.novel.entity.SysUser;
import com.novel.mapper.NovelMapper;
import com.novel.mapper.SysUserMapper;
import com.novel.vo.NovelVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NovelService {
    
    private final NovelMapper novelMapper;
    private final SysUserMapper sysUserMapper;
    
    /**
     * 获取小说列表（小程序端）
     */
    public Page<NovelVO> getNovelList(Integer pageNum, Integer pageSize, String category, String keyword) {
        Page<Novel> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Novel> wrapper = new LambdaQueryWrapper<>();
        
        // 只返回已审核通过的小说
        wrapper.eq(Novel::getStatus, "APPROVED");
        
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Novel::getCategory, category);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Novel::getTitle, keyword)
                    .or().like(Novel::getDescription, keyword));
        }
        
        wrapper.orderByDesc(Novel::getSortOrder, Novel::getCreateTime);
        
        Page<Novel> novelPage = novelMapper.selectPage(page, wrapper);
        
        Page<NovelVO> voPage = new Page<>();
        BeanUtils.copyProperties(novelPage, voPage);
        
        List<NovelVO> voList = novelPage.getRecords().stream().map(novel -> {
            NovelVO vo = new NovelVO();
            BeanUtils.copyProperties(novel, vo);
            
            // 获取作者信息
            SysUser author = sysUserMapper.selectById(novel.getAuthorId());
            if (author != null) {
                vo.setAuthorName(author.getNickname());
            }
            
            return vo;
        }).collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }
    
    /**
     * 获取小说详情
     */
    public NovelVO getNovelById(Long id) {
        Novel novel = novelMapper.selectById(id);
        if (novel == null) {
            throw new RuntimeException("小说不存在");
        }
        
        NovelVO vo = new NovelVO();
        BeanUtils.copyProperties(novel, vo);
        
        SysUser author = sysUserMapper.selectById(novel.getAuthorId());
        if (author != null) {
            vo.setAuthorName(author.getNickname());
        }
        
        // 增加阅读量
        novel.setViewCount(novel.getViewCount() + 1);
        novelMapper.updateById(novel);
        
        return vo;
    }
    
    /**
     * 创建小说（后台）
     */
    @Transactional
    public NovelVO createNovel(NovelDTO novelDTO, Long authorId) {
        Novel novel = new Novel();
        BeanUtils.copyProperties(novelDTO, novel);
        novel.setAuthorId(authorId);
        novel.setStatus("DRAFT");
        novel.setViewCount(0);
        
        novelMapper.insert(novel);
        
        NovelVO vo = new NovelVO();
        BeanUtils.copyProperties(novel, vo);
        return vo;
    }
    
    /**
     * 更新小说（后台）
     */
    @Transactional
    public NovelVO updateNovel(Long id, NovelDTO novelDTO, Long currentUserId, String currentUserRole) {
        Novel novel = novelMapper.selectById(id);
        if (novel == null) {
            throw new RuntimeException("小说不存在");
        }
        
        // 作者只能编辑自己的小说
        if ("AUTHOR".equals(currentUserRole) && !novel.getAuthorId().equals(currentUserId)) {
            throw new RuntimeException("无权编辑此小说");
        }
        
        BeanUtils.copyProperties(novelDTO, novel, "id", "authorId", "status", "viewCount");
        novelMapper.updateById(novel);
        
        NovelVO vo = new NovelVO();
        BeanUtils.copyProperties(novel, vo);
        return vo;
    }
    
    /**
     * 删除小说
     */
    @Transactional
    public void deleteNovel(Long id, Long currentUserId, String currentUserRole) {
        Novel novel = novelMapper.selectById(id);
        if (novel == null) {
            throw new RuntimeException("小说不存在");
        }
        
        // 作者只能删除自己的小说
        if ("AUTHOR".equals(currentUserRole) && !novel.getAuthorId().equals(currentUserId)) {
            throw new RuntimeException("无权删除此小说");
        }
        
        novelMapper.deleteById(id);
    }
    
    /**
     * 提交审核
     */
    @Transactional
    public void submitAudit(Long id, Long authorId) {
        Novel novel = novelMapper.selectById(id);
        if (novel == null) {
            throw new RuntimeException("小说不存在");
        }
        
        if (!novel.getAuthorId().equals(authorId)) {
            throw new RuntimeException("无权操作此小说");
        }
        
        novel.setStatus("PENDING");
        novelMapper.updateById(novel);
    }
    
    /**
     * 审核小说（管理员）
     */
    @Transactional
    public void auditNovel(Long id, String status, String remark, Long auditUserId) {
        Novel novel = novelMapper.selectById(id);
        if (novel == null) {
            throw new RuntimeException("小说不存在");
        }
        
        novel.setStatus(status);
        novel.setAuditUserId(auditUserId);
        novel.setAuditTime(java.time.LocalDateTime.now());
        novel.setAuditRemark(remark);
        
        novelMapper.updateById(novel);
    }
    
    /**
     * 获取后台小说列表
     */
    public Page<NovelVO> getAdminNovelList(Integer pageNum, Integer pageSize, String status, Long authorId) {
        Page<Novel> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Novel> wrapper = new LambdaQueryWrapper<>();
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Novel::getStatus, status);
        }
        
        if (authorId != null) {
            wrapper.eq(Novel::getAuthorId, authorId);
        }
        
        wrapper.orderByDesc(Novel::getCreateTime);
        
        Page<Novel> novelPage = novelMapper.selectPage(page, wrapper);
        
        Page<NovelVO> voPage = new Page<>();
        BeanUtils.copyProperties(novelPage, voPage);
        
        List<NovelVO> voList = novelPage.getRecords().stream().map(novel -> {
            NovelVO vo = new NovelVO();
            BeanUtils.copyProperties(novel, vo);
            
            SysUser author = sysUserMapper.selectById(novel.getAuthorId());
            if (author != null) {
                vo.setAuthorName(author.getNickname());
            }
            
            return vo;
        }).collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }
}
