package xyz.xiatiao.majiangbiji.controller;


import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.xiatiao.majiangbiji.dto.AccessTokenDTO;
import xyz.xiatiao.majiangbiji.dto.GithubUser;
import xyz.xiatiao.majiangbiji.mapper.UserMapper;
import xyz.xiatiao.majiangbiji.model.User;
import xyz.xiatiao.majiangbiji.provider.GithubProvider;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;
    @Autowired
    private UserMapper userMapper;

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.client.redirect.uri}")
    private String redirectUri;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletRequest request,
                           HttpServletResponse httpServletResponse) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        String strResponse = githubProvider.getAccessToken(accessTokenDTO);//请求token
        String accessToken = strResponse.split("&")[0].split("=")[1];//解析token
        GithubUser githubUser = githubProvider.getGitUserInfo(accessToken);
        if (githubUser != null) {
            //此刻已授权登录拿到user信息
            User user = new User();
            user.setToken(UUID.randomUUID().toString());
            user.setName(githubUser.getName()==null?githubUser.getLogin():githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insertUser(user);
            System.out.println("用户登录成功");
            httpServletResponse.addCookie(new Cookie("token",user.getToken()));
            HttpSession session = request.getSession();
            session.setAttribute("user",githubUser);
            return ("redirect:/");
        } else {
            //此刻未授权成功返回主页执行重新登录
            //TODO 此刻应该让首页提示授权失败
            System.out.println("用户登录失败");
            return ("redirect:/");
        }
    }
}
