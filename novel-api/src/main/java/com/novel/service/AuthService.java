package com.novel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novel.common.JwtUtil;
import com.novel.common.UserDetails;
import com.novel.dto.LoginDTO;
import com.novel.dto.WxLoginDTO;
import com.novel.entity.AppUser;
import com.novel.entity.SysUser;
import com.novel.mapper.AppUserMapper;
import com.novel.mapper.SysUserMapper;
import com.novel.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final SysUserMapper sysUserMapper;
    private final AppUserMapper appUserMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 后台用户登录
     */
    public LoginVO adminLogin(LoginDTO loginDTO) {
        SysUser user = sysUserMapper.selectOne(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, loginDTO.getUsername())
        );
        
        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());
        loginVO.setRole(user.getRole());
        
        return loginVO;
    }
    
    /**
     * 小程序微信登录
     * 注意：这里简化处理，实际应该调用微信API获取openid
     */
    public LoginVO wxLogin(WxLoginDTO wxLoginDTO) {
        // TODO: 实际应该调用微信API: https://api.weixin.qq.com/sns/jscode2session
        // 这里简化处理，使用code作为openid（仅用于测试）
        String openid = wxLoginDTO.getCode(); // 实际应该从微信API获取
        
        AppUser appUser = appUserMapper.selectOne(
            new LambdaQueryWrapper<AppUser>()
                .eq(AppUser::getOpenid, openid)
        );
        
        if (appUser == null) {
            // 新用户，自动注册
            appUser = new AppUser();
            appUser.setOpenid(openid);
            appUser.setNickname("微信用户");
            appUser.setTrialViewTime(0);
            appUser.setIsTrialEnabled(1);
            appUserMapper.insert(appUser);
        }
        
        String token = jwtUtil.generateAppToken(appUser.getId(), appUser.getOpenid());
        
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(appUser.getId());
        loginVO.setNickname(appUser.getNickname());
        loginVO.setRole("USER");
        
        return loginVO;
    }
}
