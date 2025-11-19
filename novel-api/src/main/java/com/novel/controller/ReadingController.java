package com.novel.controller;

import com.novel.common.Result;
import com.novel.common.SecurityUtil;
import com.novel.service.ReadingService;
import com.novel.service.UserService;
import com.novel.vo.ReadingRecordVO;
import com.novel.vo.TrialCheckVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reading")
@RequiredArgsConstructor
public class ReadingController {
    
    private final ReadingService readingService;
    private final UserService userService;
    
    /**
     * 获取用户阅读记录
     */
    @GetMapping("/record")
    public Result<List<ReadingRecordVO>> getReadingRecords() {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            if (userId == null) {
                return Result.error(401, "未登录");
            }
            
            List<ReadingRecordVO> records = readingService.getUserReadingRecords(userId);
            return Result.success(records);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 保存阅读进度
     */
    @PostMapping("/record")
    public Result<Void> saveReadingRecord(@RequestBody Map<String, Object> params) {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            if (userId == null) {
                return Result.error(401, "未登录");
            }
            
            Long novelId = Long.parseLong(params.get("novelId").toString());
            Long chapterId = Long.parseLong(params.get("chapterId").toString());
            Integer pageIndex = Integer.parseInt(params.get("pageIndex").toString());
            
            readingService.saveReadingRecord(userId, novelId, chapterId, pageIndex);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 检查试看权限
     */
    @GetMapping("/check-trial")
    public Result<TrialCheckVO> checkTrial(@RequestParam Long chapterId) {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            if (userId == null) {
                return Result.error(401, "未登录");
            }
            
            TrialCheckVO vo = userService.checkTrialPermission(userId);
            
            // 如果允许阅读，扣减试看时间（每次阅读扣减10秒）
            if (vo.getCanRead()) {
                userService.consumeTrialTime(userId, 10);
                // 重新获取剩余时间
                vo = userService.checkTrialPermission(userId);
            }
            
            return Result.success(vo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
