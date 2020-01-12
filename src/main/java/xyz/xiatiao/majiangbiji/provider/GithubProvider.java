package xyz.xiatiao.majiangbiji.provider;

import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.springframework.stereotype.Component;
import xyz.xiatiao.majiangbiji.dto.AccessTokenDTO;
import xyz.xiatiao.majiangbiji.dto.GithubUser;

import java.io.IOException;
import java.util.Objects;

@Component
public class GithubProvider {

    public String getAccessToken(AccessTokenDTO accessTokenDTO){
        MediaType mtJSON = MediaType.get("application/json; charset=utf-8");
        String url = "https://github.com/login/oauth/access_token";
        OkHttpClient client = new OkHttpClient();
        String postJsonStr = JSON.toJSONString(accessTokenDTO);
        RequestBody body = RequestBody.create(postJsonStr, mtJSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String strResponse = response.body().string();
            System.out.println("getAccessToken:"+strResponse);
            return strResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public GithubUser getGitUserInfo(String token){
        String url = "https://api.github.com/user?access_token="+token;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            String strResponse = Objects.requireNonNull(response.body()).string();
            System.out.println("getGitUserInfo："+strResponse);
            GithubUser githubUser = JSON.parseObject(strResponse,GithubUser.class);
            return githubUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
