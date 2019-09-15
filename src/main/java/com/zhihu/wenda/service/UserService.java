package com.zhihu.wenda.service;

import com.zhihu.wenda.dao.LoginTicketDAO;
import com.zhihu.wenda.dao.UserDAO;
import com.zhihu.wenda.model.LoginTicket;
import com.zhihu.wenda.model.User;
import com.zhihu.wenda.utils.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;


@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public User getUser(int id){
        return userDAO.selectById(id);
    }

    public Map<String, Object> register(String username, String password){
        Map<String, Object> map = new HashMap<>();
        if(StringUtils.isEmpty(username)){
            map.put("msg", "用户名不能为空");
            return map;
        }

        if(StringUtils.isEmpty(password)){
            map.put("msg", "密码不能为空！");
            return map;
        }

        if(userDAO.selectByName(username) != null){
            map.put("msg", "用户名已经被注册！");
            return map;
        }

        // 开始注册
        User user = new User(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setPassword(WendaUtil.MD5(password+user.getSalt()));
        user.setHeadUrl(
                String.format("http://images.nowcoder.com/head/%dt.png",
                new Random().nextInt(1000)));
        userDAO.addUser(user);

        // 登录
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);

        return map;
    }

    public Map<String, Object> login(String username, String password){
        Map<String, Object> map = new HashMap<>();
        if(StringUtils.isEmpty(username)){
            map.put("msg", "用户名不能为空！");
            return map;
        }
        if(StringUtils.isEmpty(password)){
            map.put("msg", "密码不能为空！");
            return map;
        }

        User user = userDAO.selectByName(username);
        if(user == null){
            map.put("msg", "用户名不存在！");
            return map;
        }

        if(!WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msg", "密码不正确！");
            return map;
        }

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);

        return map;
    }

    public void logout(String ticket){
        loginTicketDAO.updateStatus(ticket, 1);
    }

    private String addLoginTicket(int userId){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime()+100*3600*24);
        loginTicket.setExpired(date);
        loginTicket.setStatus(0);
        String ticket = UUID.randomUUID().toString().replaceAll("-","");
        loginTicket.setTicket(ticket);

        loginTicketDAO.addLoginTicket(loginTicket);

        return loginTicket.getTicket();
    }

    public User selectByName(String name){
        return userDAO.selectByName(name);
    }
}
