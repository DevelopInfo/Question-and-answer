package com.zhihu.wenda.controller;

import com.zhihu.wenda.model.*;
import com.zhihu.wenda.service.CommentService;
import com.zhihu.wenda.service.QuestionService;
import com.zhihu.wenda.service.UserService;
import com.zhihu.wenda.utils.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private QuestionService questionService;

    @RequestMapping(path = "/addComment")
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content){
        try{
            // 添加评论
            Comment comment = new Comment();
            if(hostHolder.getUser() == null){
                comment.setUserId(WendaUtil.ANONYMOUS_USERID);
            }else{
                comment.setUserId(hostHolder.getUser().getId());
            }
            comment.setEntityId(questionId);
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setContent(content);// TODO: 脚本与敏感词过滤
            comment.setCreatedDate(new Date());
            comment.setStatus(0);
            commentService.addComment(comment);
            // 更新问题评论数
            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            questionService.updateCommentCount(comment.getEntityId(), count);
            // TODO:如何异步化
        }catch (Exception e){
            logger.error("增加评论失败！" + e.getMessage());
        }
        return "redirect:/question/"+String.valueOf(questionId);
    }
}
