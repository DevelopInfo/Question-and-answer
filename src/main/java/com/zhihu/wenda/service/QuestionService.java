package com.zhihu.wenda.service;

import com.zhihu.wenda.dao.QuestionDAO;
import com.zhihu.wenda.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionDAO questionDAO;

    public Question getById(int id){ return questionDAO.getById(id); }

    public int addQuestion(Question question){
        // 脚本过滤

        // 敏感词过滤

        return questionDAO.addQuestion(question) > 0 ? question.getId() : 0;
    }

    public List<Question> getLatestQuestions(int userId, int offset, int limit){
        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }

    public int updateCommentCount(int id, int count){
        return questionDAO.updateCommentCount(id, count);
    }
}
