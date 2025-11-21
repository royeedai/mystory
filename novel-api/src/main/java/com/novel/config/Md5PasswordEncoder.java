package com.novel.config;

import com.novel.util.Md5Util;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * MD5密码编码器
 */
public class Md5PasswordEncoder implements PasswordEncoder {
    
    @Override
    public String encode(CharSequence rawPassword) {
        return Md5Util.encode(rawPassword.toString());
    }
    
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return Md5Util.matches(rawPassword.toString(), encodedPassword);
    }
}
