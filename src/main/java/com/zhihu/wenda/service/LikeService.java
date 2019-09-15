package com.zhihu.wenda.service;

import com.zhihu.wenda.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    JedisAdaptor jedisAdaptor;

    public long addLike(int userId, int entityType, int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdaptor.sadd(likeKey, String.valueOf(userId));

        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdaptor.srem(disLikeKey, String.valueOf(userId));

        return jedisAdaptor.scard(likeKey);
    }

    public long addDislike(int userId, int entityType, int entityId){
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdaptor.sadd(disLikeKey, String.valueOf(userId));

        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdaptor.srem(likeKey, String.valueOf(userId));

        return jedisAdaptor.scard(likeKey);
    }

    public long getLikeCount(int entityType, int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        return jedisAdaptor.scard(likeKey);
    }

    public int getLikeStatus(int userId, int entityType, int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        if(jedisAdaptor.sismember(likeKey, String.valueOf(userId)))
            return 1;
        String dislikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        return jedisAdaptor.sismember(dislikeKey, String.valueOf(userId)) ?
                -1 : 0;

    }
}
