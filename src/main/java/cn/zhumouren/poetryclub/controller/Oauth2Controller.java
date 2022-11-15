package cn.zhumouren.poetryclub.controller;

import cn.zhumouren.poetryclub.property.GitHubOauth2Property;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/15 21:36
 **/
@Controller
@Slf4j
public class Oauth2Controller {

    private ObjectMapper objectMapper = new ObjectMapper();

    private final GitHubOauth2Property gitHubOauth2Properties;

    public Oauth2Controller(GitHubOauth2Property gitHubOauth2Properties) {
        this.gitHubOauth2Properties = gitHubOauth2Properties;
    }

    @GetMapping("/oauth2/authorize")
    public String authorize() {
        String url = gitHubOauth2Properties.getAuthorizeUrl() +
                "?client_id=" + gitHubOauth2Properties.getClientId() +
                "&redirect_uri=" + gitHubOauth2Properties.getRedirectUrl();
        log.info("授权url:{}", url);
        return "redirect:" + url;
    }

    @GetMapping("/oauth2/callback")
    public String callback(@RequestParam("code") String code) {
        log.info("code={}", code);
        // code换token
        String accessToken = getAccessToken(code);
        // token换userInfo
        String userInfo = getUserInfo(accessToken);
        return "redirect:/api";
    }

    private String getAccessToken(String code) {
        String url = gitHubOauth2Properties.getAccessTokenUrl() +
                "?client_id=" + gitHubOauth2Properties.getClientId() +
                "&client_secret=" + gitHubOauth2Properties.getClientSecret() +
                "&code=" + code +
                "&grant_type=authorization_code";
        log.info("getAccessToken url:{}", url);
        // 构建请求头
        HttpHeaders requestHeaders = new HttpHeaders();
        // 指定响应返回json格式
        requestHeaders.add("accept", "application/json");
        // 构建请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(requestHeaders);
        RestTemplate restTemplate = new RestTemplate();
        // post 请求方式
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        String responseStr = response.getBody();
        log.info("responseStr={}", responseStr);
        // 解析响应json字符串
        Map<String, String> json;
        try {
            json = objectMapper.readValue(responseStr, HashMap.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String accessToken = json.get("access_token");
        log.info("accessToken={}", accessToken);
        return accessToken;
    }

    private String getUserInfo(String accessToken) {
        String url = gitHubOauth2Properties.getUserInfoUrl();
        log.info("getUserInfo url:{}", url);
        // 构建请求头
        HttpHeaders requestHeaders = new HttpHeaders();
        // 指定响应返回json格式
        requestHeaders.add("accept", "application/json");
        // AccessToken放在请求头中
        requestHeaders.add("Authorization", "token " + accessToken);
        // 构建请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(requestHeaders);
        RestTemplate restTemplate = new RestTemplate();
        // get请求方式
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        String userInfo = response.getBody();
        log.info("userInfo={}", userInfo);
        return userInfo;
    }
}
