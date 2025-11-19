package com.novel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novel.dto.ChapterDTO;
import com.novel.entity.Chapter;
import com.novel.entity.Novel;
import com.novel.mapper.ChapterMapper;
import com.novel.mapper.NovelMapper;
import com.novel.vo.ChapterListVO;
import com.novel.vo.ChapterVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChapterService {
    
    private final ChapterMapper chapterMapper;
    private final NovelMapper novelMapper;
    private static final int PAGE_SIZE = 2000; // 每页2000字符
    
    /**
     * 获取章节列表
     */
    public List<ChapterListVO> getChapterList(Long novelId) {
        List<Chapter> chapters = chapterMapper.selectList(
            new LambdaQueryWrapper<Chapter>()
                .eq(Chapter::getNovelId, novelId)
                .orderByAsc(Chapter::getChapterNum)
        );
        
        return chapters.stream().map(chapter -> {
            ChapterListVO vo = new ChapterListVO();
            BeanUtils.copyProperties(chapter, vo);
            return vo;
        }).collect(Collectors.toList());
    }
    
    /**
     * 获取章节内容（分页）
     */
    public ChapterVO getChapterContent(Long chapterId, Integer page) {
        Chapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null) {
            throw new RuntimeException("章节不存在");
        }
        
        // 检查小说是否已审核
        Novel novel = novelMapper.selectById(chapter.getNovelId());
        if (novel == null || !"APPROVED".equals(novel.getStatus())) {
            throw new RuntimeException("小说未审核通过");
        }
        
        String content = chapter.getContent();
        int totalPages = (int) Math.ceil((double) content.length() / PAGE_SIZE);
        
        if (page == null || page < 1) {
            page = 1;
        }
        if (page > totalPages) {
            page = totalPages;
        }
        
        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, content.length());
        String pageContent = content.substring(start, end);
        
        ChapterVO vo = new ChapterVO();
        BeanUtils.copyProperties(chapter, vo);
        vo.setContent(pageContent);
        vo.setTotalPages(totalPages);
        vo.setCurrentPage(page);
        
        return vo;
    }
    
    /**
     * 创建章节
     */
    @Transactional
    public ChapterVO createChapter(ChapterDTO chapterDTO, Long authorId) {
        // 检查小说是否存在且属于当前作者
        Novel novel = novelMapper.selectById(chapterDTO.getNovelId());
        if (novel == null) {
            throw new RuntimeException("小说不存在");
        }
        
        if (!novel.getAuthorId().equals(authorId)) {
            throw new RuntimeException("无权在此小说下添加章节");
        }
        
        // 检查章节序号是否重复
        Chapter existChapter = chapterMapper.selectOne(
            new LambdaQueryWrapper<Chapter>()
                .eq(Chapter::getNovelId, chapterDTO.getNovelId())
                .eq(Chapter::getChapterNum, chapterDTO.getChapterNum())
        );
        
        if (existChapter != null) {
            throw new RuntimeException("章节序号已存在");
        }
        
        Chapter chapter = new Chapter();
        BeanUtils.copyProperties(chapterDTO, chapter);
        chapter.setWordCount(chapterDTO.getContent().length());
        
        chapterMapper.insert(chapter);
        
        ChapterVO vo = new ChapterVO();
        BeanUtils.copyProperties(chapter, vo);
        return vo;
    }
    
    /**
     * 更新章节
     */
    @Transactional
    public ChapterVO updateChapter(Long id, ChapterDTO chapterDTO, Long authorId) {
        Chapter chapter = chapterMapper.selectById(id);
        if (chapter == null) {
            throw new RuntimeException("章节不存在");
        }
        
        // 检查小说是否属于当前作者
        Novel novel = novelMapper.selectById(chapter.getNovelId());
        if (novel == null || !novel.getAuthorId().equals(authorId)) {
            throw new RuntimeException("无权编辑此章节");
        }
        
        BeanUtils.copyProperties(chapterDTO, chapter, "id", "novelId");
        chapter.setWordCount(chapterDTO.getContent().length());
        
        chapterMapper.updateById(chapter);
        
        ChapterVO vo = new ChapterVO();
        BeanUtils.copyProperties(chapter, vo);
        return vo;
    }
    
    /**
     * 删除章节
     */
    @Transactional
    public void deleteChapter(Long id, Long authorId) {
        Chapter chapter = chapterMapper.selectById(id);
        if (chapter == null) {
            throw new RuntimeException("章节不存在");
        }
        
        Novel novel = novelMapper.selectById(chapter.getNovelId());
        if (novel == null || !novel.getAuthorId().equals(authorId)) {
            throw new RuntimeException("无权删除此章节");
        }
        
        chapterMapper.deleteById(id);
    }
}
