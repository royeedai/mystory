package com.novel.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信API工具类
 */
@Slf4j
@Component
public class WxApiUtil {
    
    @Value("${wx.appid:}")
    private String appid;
    
    @Value("${wx.secret:}")
    private String secret;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 通过code获取openid和session_key
     * @param code 微信登录code
     * @return Map包含openid和session_key
     */
    public Map<String, String> code2Session(String code) {
        String url = String.format(
            "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
            appid, secret, code
        );
        
        try {
            String response = restTemplate.getForObject(url, String.class);
            log.info("微信登录响应: {}", response);
            
            JsonNode jsonNode = objectMapper.readTree(response);
            
            if (jsonNode.has("errcode")) {
                int errcode = jsonNode.get("errcode").asInt();
                String errmsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
                log.error("微信登录失败: errcode={}, errmsg={}", errcode, errmsg);
                throw new RuntimeException("微信登录失败: " + errmsg);
            }
            
            Map<String, String> result = new HashMap<>();
            result.put("openid", jsonNode.get("openid").asText());
            if (jsonNode.has("session_key")) {
                result.put("session_key", jsonNode.get("session_key").asText());
            }
            return result;
        } catch (Exception e) {
            log.error("调用微信API失败", e);
            throw new RuntimeException("微信登录失败: " + e.getMessage());
        }
    }
}
