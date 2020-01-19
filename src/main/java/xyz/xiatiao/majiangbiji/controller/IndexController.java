package xyz.xiatiao.majiangbiji.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.xiatiao.majiangbiji.mapper.UserMapper;
import xyz.xiatiao.majiangbiji.model.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class IndexController {

    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/")
    public String index(HttpServletRequest httpServletRequest) {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    User user = userMapper.fromTokenSearchUser(token);
                    if (user != null) {
                        System.out.println("自动登录成功:" + user.toString());
                        HttpSession session = httpServletRequest.getSession();
                        session.setAttribute("user", user);
                    } else {
                        System.out.println("自动登录失败，数据库中不含此token对应的用户");
                    }
                }
            }
        }
        return "index";
    }
}
