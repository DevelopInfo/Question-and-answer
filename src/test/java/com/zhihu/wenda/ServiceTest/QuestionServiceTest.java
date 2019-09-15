package com.zhihu.wenda.ServiceTest;

import com.zhihu.wenda.WendaApplication;
import com.zhihu.wenda.model.Question;
import com.zhihu.wenda.service.QuestionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
public class QuestionServiceTest {

    @Autowired
    QuestionService questionService;

    @Test
    public void test(){
        int userId = 0;
        int offset = 0;
        int limit = 10;
        List<Question> questions = questionService.getLatestQuestions(userId, offset, limit);
        for(Question q : questions){
            System.out.println(q);
        }
    }
}
