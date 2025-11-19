package com.novel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novel.entity.ReadingRecord;
import com.novel.mapper.ReadingRecordMapper;
import com.novel.vo.ReadingRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReadingService {
    
    private final ReadingRecordMapper readingRecordMapper;
    
    /**
     * 获取用户阅读记录
     */
    public List<ReadingRecordVO> getUserReadingRecords(Long userId) {
        List<ReadingRecord> records = readingRecordMapper.selectList(
            new LambdaQueryWrapper<ReadingRecord>()
                .eq(ReadingRecord::getUserId, userId)
                .orderByDesc(ReadingRecord::getLastReadTime)
        );
        
        return records.stream().map(record -> {
            ReadingRecordVO vo = new ReadingRecordVO();
            BeanUtils.copyProperties(record, vo);
            return vo;
        }).collect(Collectors.toList());
    }
    
    /**
     * 保存阅读进度
     */
    @Transactional
    public void saveReadingRecord(Long userId, Long novelId, Long chapterId, Integer pageIndex) {
        ReadingRecord record = readingRecordMapper.selectOne(
            new LambdaQueryWrapper<ReadingRecord>()
                .eq(ReadingRecord::getUserId, userId)
                .eq(ReadingRecord::getNovelId, novelId)
        );
        
        if (record == null) {
            record = new ReadingRecord();
            record.setUserId(userId);
            record.setNovelId(novelId);
            record.setChapterId(chapterId);
            record.setPageIndex(pageIndex);
            readingRecordMapper.insert(record);
        } else {
            record.setChapterId(chapterId);
            record.setPageIndex(pageIndex);
            readingRecordMapper.updateById(record);
        }
    }
}
