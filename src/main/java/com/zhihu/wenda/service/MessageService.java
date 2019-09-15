package com.zhihu.wenda.service;

import com.zhihu.wenda.dao.MessageDAO;
import com.zhihu.wenda.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    MessageDAO messageDAO;

    public int addMessage(Message msg){
        return messageDAO.addMessage(msg);
    }

    public List<Message> getConversationDetail(
            String conversationId,
            int offset, int limit){
        return messageDAO.getConversationDetail(conversationId, offset, limit);
    }

    public List<Message> getConversationList(int userId,
                                             int offset,
                                             int limit){
        return messageDAO.getConversationList(userId, offset, limit);
    }

    public void updateConversationUnread(int conversationId){

    }

    public int getConversationUnreadCount(int userId, String conversationId){
        return messageDAO.getConvesationUnreadCount(userId, conversationId);
    }
}
