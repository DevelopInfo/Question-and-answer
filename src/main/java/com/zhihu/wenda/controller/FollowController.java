package com.zhihu.wenda.controller;

import com.zhihu.wenda.async.EventModel;
import com.zhihu.wenda.async.EventProducer;
import com.zhihu.wenda.async.EventType;
import com.zhihu.wenda.model.*;
import com.zhihu.wenda.service.CommentService;
import com.zhihu.wenda.service.FollowService;
import com.zhihu.wenda.service.QuestionService;
import com.zhihu.wenda.service.UserService;
import com.zhihu.wenda.utils.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FollowController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    FollowService followService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/followUser"})
    @ResponseBody
    public String followUser(@RequestParam("userId") int userId){
        if(hostHolder.getUser() == null){
            return WendaUtil.getJSONString(999);
        }
        int hostId = hostHolder.getUser().getId();
        boolean ret = followService.follow(hostId, EntityType.ENTITY_USER, userId);

        // 发站内信的事件进行异步处理
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostId)
                .setEntityType(EntityType.ENTITY_USER)
                .setEntityId(userId)
                .setEntityOwnerId(userId));

        // 返回当前用户hostUser关注的人数
        Long followeeCount = followService.getFolloweesCount(hostId, EntityType.ENTITY_USER);
        return WendaUtil.getJSONString(ret ? 0 : 1, String.valueOf(followeeCount));
    }

    @RequestMapping(path = {"/unfollowUser"})
    @ResponseBody
    public String unfollowUser(@RequestParam("userId") int userId){
        if (hostHolder.getUser() == null){
            return WendaUtil.getJSONString(999);
        }

        int hostId = hostHolder.getUser().getId();
        boolean ret = followService.unfollow(hostId, EntityType.ENTITY_USER, userId);

        // 利用异步队列处理站内信事件
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostId)
                .setEntityType(EntityType.ENTITY_USER)
                .setEntityId(userId)
                .setEntityId(userId));

        // 返回当前用户hostHolder 关注的人数
        Long followeeCount = followService.getFolloweesCount(hostId, EntityType.ENTITY_USER);
        return WendaUtil.getJSONString(
                ret ? 0 : 1,
                String.valueOf(followeeCount));
    }

    @Autowired
    QuestionService questionService;

    @RequestMapping(path = {"/followQuestion"})
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId){
        if(hostHolder.getUser() == null){
            return WendaUtil.getJSONString(999);
        }

        int hostId = hostHolder.getUser().getId();

        Question q = questionService.getById(questionId);
        if(q == null){
            return WendaUtil.getJSONString(1, "问题不存在！");
        }

        boolean ret = followService.follow(hostId, EntityType.ENTITY_QUESTION, questionId);

        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostId)
                .setEntityType(EntityType.ENTITY_QUESTION)
                .setEntityId(questionId)
                .setEntityOwnerId(q.getUserId()));

        Map<String, Object> info = new HashMap<>();
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getHeadUrl());
        info.put("id", hostId);
        info.put("count", followService.getFollowersCount(EntityType.ENTITY_QUESTION, questionId));

        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }

    @RequestMapping(path = {"/unfollowQuestion"})
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId){
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        Question q = questionService.getById(questionId);
        if (q == null) {
            return WendaUtil.getJSONString(1, "问题不存在");
        }

        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);

        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(q.getUserId()));

        Map<String, Object> info = new HashMap<>();
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowersCount(EntityType.ENTITY_QUESTION, questionId));
        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    private List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds) {
        List<ViewObject> userInfos = new ArrayList<ViewObject>();
        for (Integer uid : userIds) {
            User user = userService.getUser(uid);
            if (user == null) {
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            vo.set("commentCount", commentService.getUserCommentCount(uid));
            vo.set("followerCount", followService.getFollowersCount(EntityType.ENTITY_USER, uid));
            vo.set("followeeCount", followService.getFolloweesCount(uid, EntityType.ENTITY_USER));
            if (localUserId != 0) {
                vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, uid));
            } else {
                vo.set("followed", false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }

    @RequestMapping(path = {"/user/{uid}/followers"})
    public String followers(Model model, @PathVariable("uid") int userId){
        List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER, userId, 0, 10);
        if(hostHolder.getUser() == null){
            model.addAttribute(
                    "followers",
                    getUsersInfo(0, followerIds)
            );
        }else{
            model.addAttribute(
                    "followers",
                    getUsersInfo(hostHolder.getUser().getId(), followerIds)
            );
        }

        model.addAttribute(
                "followerCount",
                followService.getFollowersCount(EntityType.ENTITY_USER, userId)
        );
        model.addAttribute(
                "curUser",
                userService.getUser(userId)
        );
        return "followers";
    }

    @RequestMapping(path = "/user/{uid}/followees")
    public String followees(Model model, @PathVariable("uid") int userId){
        List<Integer> followeeIds = followService.getFollowees(userId, EntityType.ENTITY_USER, 0, 10);

        if(hostHolder.getUser() == null){
            model.addAttribute(
                    "followees",
                    getUsersInfo(0, followeeIds)
            );
        }else{
            model.addAttribute(
                    "followees",
                    getUsersInfo(hostHolder.getUser().getId(), followeeIds)
            );
        }

        model.addAttribute(
                "followeeCount",
                followService.getFollowersCount(userId, EntityType.ENTITY_USER)
        );

        model.addAttribute(
                "curUser",
                userService.getUser(userId)
        );
        return "followees";
    }

}
