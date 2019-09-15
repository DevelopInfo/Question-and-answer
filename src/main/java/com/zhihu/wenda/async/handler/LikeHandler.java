package com.zhihu.wenda.async.handler;

import com.zhihu.wenda.async.EventHandler;
import com.zhihu.wenda.async.EventModel;
import com.zhihu.wenda.async.EventType;
import com.zhihu.wenda.model.Message;
import com.zhihu.wenda.model.User;
import com.zhihu.wenda.service.MessageService;
import com.zhihu.wenda.service.UserService;
import com.zhihu.wenda.utils.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class LikeHandler implements EventHandler {
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel eventModel) {
        Message message = new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(eventModel.getEntityOwnerId());
        message.setConversationId(WendaUtil.SYSTEM_USERID, eventModel.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getUser(eventModel.getActorId());
        message.setContent("用户"+user.getName()+
                "赞了你的评论，http://127.0.0.1:8080/question/"+eventModel.getExts("questionId"));
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
