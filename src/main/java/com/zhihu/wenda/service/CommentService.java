package com.zhihu.wenda.service;

import com.zhihu.wenda.dao.CommentDAO;
import com.zhihu.wenda.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentDAO commentDAO;

    public List<Comment> getCommentsByEntity(int entityId, int entityType){
        return commentDAO.selectByEntity(entityId, entityType);
    }

    public int addComment(Comment comment){
        return commentDAO.addComment(comment);
    }

    public void deleteComment(int entityId, int entityType){
        commentDAO.updateStatus(entityId, entityType, 1);
    }

    public int getCommentCount(int entityId, int entityType){
        return commentDAO.getCommentCount(entityId, entityType);
    }

    public Comment getCommentById(int id){
        return commentDAO.getCommentById(id);
    }

    public int getUserCommentCount(int userId){
        return commentDAO.getUserCommentCount(userId);
    }
}

