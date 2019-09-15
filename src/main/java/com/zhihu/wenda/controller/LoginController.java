package com.zhihu.wenda.controller;

import com.zhihu.wenda.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController {
    @Autowired
    UserService userService;

    @RequestMapping(path = {"/reglogin"})
    public String regloginPage(
            Model model){
        return "login";
    }

    @RequestMapping(path = {"/reg/"})
    public String reg(
            Model model, HttpServletResponse response,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
            @RequestParam("next") String next){
        try{
            System.out.println("LoginController: reg");
            Map<String, Object> map = userService.register(username, password);
            // 如果注册成功，应该生成了LoginTicket
            if(map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if(rememberme){
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                if(!StringUtils.isEmpty(next)){
                    return "redirect:" + next;
                }
                return "redirect:/";
            }else{
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            model.addAttribute("msg", "服务器错误！");
            return "login";
        }
    }

    @RequestMapping(path = {"/login/"})
    public String login(
            Model model, HttpServletResponse response,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("next") String next,
            @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme){
        try{
            System.out.println("LoginController: login");
            Map<String, Object> map = userService.login(username, password);
            if(map.containsKey("ticket")){
                Cookie cookie  = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if(rememberme){
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);

                if(!StringUtils.isEmpty(next)){
                    return "redirect:"+next;
                }

                return "redirect:/";
            }else{
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            model.addAttribute("msg", "服务器错误！");
            return "login";
        }

    }

    @RequestMapping(path = {"/logout"})
    public String logout(
            Model model,
            @CookieValue("ticket") String ticket){
        try{
            System.out.println("Logincontroller: logout");
            userService.logout(ticket);
            return "redirect:/";
        }catch (Exception e){
            return "redirect:/";
        }
    }

}
