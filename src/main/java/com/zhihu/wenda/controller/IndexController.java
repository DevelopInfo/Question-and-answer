package com.zhihu.wenda.controller;

import com.zhihu.wenda.model.Question;
import com.zhihu.wenda.model.ViewObject;
import com.zhihu.wenda.service.UserService;
import com.zhihu.wenda.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    private List<ViewObject> getQuestions(int userId, int offset, int limit) {
        List<Question> questionList = questionService.getLatestQuestions(
                userId, offset, limit);
        List<ViewObject> vos = new ArrayList();
        for(Question question : questionList){
            ViewObject vo = new ViewObject();
            vo.set("question", question);
            vo.set("user", userService.getUser(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(path = {"/", "/index"})
    public String IndexPage(Model model){
        model.addAttribute("vos", getQuestions(0,0, 10));
        return "index";
    }

    @RequestMapping(path = {"/user/{userId}"})
    public String userIndex(Model model, @PathVariable("userId") int userId){
        model.addAttribute("vos", getQuestions(userId, 0, 10));
        return "index";
    }
}
