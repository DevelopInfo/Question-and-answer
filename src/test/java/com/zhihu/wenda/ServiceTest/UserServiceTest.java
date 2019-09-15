package com.zhihu.wenda.ServiceTest;

import com.zhihu.wenda.WendaApplication;
import com.zhihu.wenda.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
public class UserServiceTest {
    @Autowired
    UserService userService;

    @Test
    public void test(){
        String username = "zhou";
        String password = "pl,okm";
        Map<String, Object> map = userService.register(username, password);
        System.out.println(map);
        userService.logout(map.get("ticket").toString());
    }
}
