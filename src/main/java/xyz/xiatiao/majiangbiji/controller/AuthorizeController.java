package xyz.xiatiao.majiangbiji.controller;


import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.xiatiao.majiangbiji.dto.AccessTokenDTO;
import xyz.xiatiao.majiangbiji.provider.GithubProvider;

import java.io.IOException;

@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code")String code,
                           @RequestParam(name = "state")String state) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);
        accessTokenDTO.setRedirect_uri("http://localhost:8080/callback");
        accessTokenDTO.setClient_id("228be2b1fe302327e914");
        accessTokenDTO.setClient_secret("3e71f13bca27ea8c342e8b6a6520bcea3c0c2e01");
        String strResponse = githubProvider.getAccessToken(accessTokenDTO);
        String accessToken = strResponse.split("&")[0].split("=")[1];
        System.out.println("accessToken:"+accessToken);
        GithubProvider gitUser = getGitUserInfo(accessToken);
        System.out.println("userName:"+gitUser);
        return "index";
    }

    public GithubProvider getGitUserInfo(String token){
        String url = "https://api.github.com/user?access_token="+token;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            String strResponse = response.body().string();
            System.out.println("getGitUserInfo："+strResponse);
            GithubProvider githubProvider = JSON.parseObject(strResponse,GithubProvider.class);
            return githubProvider;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
