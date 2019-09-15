package com.zhihu.wenda.controller;

import com.zhihu.wenda.async.EventModel;
import com.zhihu.wenda.async.EventProducer;
import com.zhihu.wenda.async.EventType;
import com.zhihu.wenda.model.Comment;
import com.zhihu.wenda.model.EntityType;
import com.zhihu.wenda.model.HostHolder;
import com.zhihu.wenda.service.CommentService;
import com.zhihu.wenda.service.LikeService;
import com.zhihu.wenda.utils.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {
    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/like"})
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId){
        if(hostHolder.getUser() == null){
            return WendaUtil.getJSONString(999);
        }

        Comment comment = commentService.getCommentById(commentId);

        // 发站内信的事件利用异步队列进行处理
        eventProducer.fireEvent(
                new EventModel(EventType.LIKE)
                        .setActorId(hostHolder.getUser().getId())
                        .setEntityId(commentId)
                        .setEntityType(EntityType.ENTITY_COMMENT)
                        .setEntityOwnerId(comment.getUserId())
                        .setExts("questionId",String.valueOf(comment.getEntityId())));

        long likeCount = likeService.addLike(
                hostHolder.getUser().getId(),
                EntityType.ENTITY_COMMENT,
                commentId);

        return WendaUtil.getJSONString(0, String.valueOf(likeCount));
    }

    @RequestMapping(path = "/dislike")
    @ResponseBody
    public String dislike(@RequestParam("commentId") int commentId){
        if(hostHolder.getUser() == null){
            return  WendaUtil.getJSONString(999);
        }

        long likeCount = likeService.addDislike(
                hostHolder.getUser().getId(),
                EntityType.ENTITY_COMMENT,
                commentId);
        return WendaUtil.getJSONString(0, String.valueOf(likeCount));
    }
}
