package com.zhihu.wenda.controller;

import com.zhihu.wenda.model.HostHolder;
import com.zhihu.wenda.model.Message;
import com.zhihu.wenda.model.User;
import com.zhihu.wenda.model.ViewObject;
import com.zhihu.wenda.service.MessageService;
import com.zhihu.wenda.service.UserService;
import com.zhihu.wenda.utils.WendaUtil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @RequestMapping(path = "/msg/list")
    public String conversationDetail(Model model){
        try{
            if(hostHolder.getUser() == null){
                return "redirect:/reglogin";
            }
            int localUserId = hostHolder.getUser().getId();
            List<ViewObject> conversations = new ArrayList<>();
            List<Message> conversationList = messageService.getConversationList(
                    localUserId, 0, 10);
            for(Message msg : conversationList){
                ViewObject vo = new ViewObject();
                vo.set("message", msg);
                int targetId = msg.getFromId() == localUserId ? msg.getToId() : msg.getFromId();
                User user = userService.getUser(targetId);
                vo.set("user", user);
                vo.set("unread", messageService.getConversationUnreadCount(
                        localUserId, msg.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations",conversations);
        }catch (Exception e){
            logger.error("获取站内信列表失败！"+e.getMessage());
        }
        return "letter";
    }

    @RequestMapping(path = {"/msg/detail"})
    public String conversationDetail(Model model,
                                     @Param("conversationId") String conversationId){
        try{
            List<ViewObject> messages = new ArrayList<>();
            List<Message> conversationList = messageService.getConversationDetail(
                    conversationId, 0, 10);
            for(Message msg : conversationList){
                ViewObject vo = new ViewObject();
                vo.set("message", msg);

                User user = userService.getUser(msg.getFromId());
                if(user == null){
                    continue;
                }
                vo.set("user", user);
                messages.add(vo);
            }
            model.addAttribute("messages", messages);
        }catch (Exception e){
            logger.error("获取详情信息失败！" + e.getMessage());
        }
        return "letterDetail";
    }

    @RequestMapping(path = {"/msg/addMessage"})
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content){
        try{
            if(hostHolder.getUser() == null){
                return WendaUtil.getJSONString(999, "未登录，请先登录！");
            }
            User fromUser = hostHolder.getUser();
            User toUser = userService.selectByName(toName);
            if(toUser == null){
                return WendaUtil.getJSONString(1, "用户不存在！");
            }
            Message message = new Message();
            message.setContent(content);
            message.setFromId(fromUser.getId());
            message.setToId(toUser.getId());
            message.setCreatedDate(new Date());
            message.setConversationId(fromUser.getId(), toUser.getId());
            messageService.addMessage(message);
            return WendaUtil.getJSONString(0);
        }catch (Exception e){
            logger.error("增加站内信失败！" +e.getMessage());
            return WendaUtil.getJSONString(1, "插入站内信失败！");
        }
    }

}
