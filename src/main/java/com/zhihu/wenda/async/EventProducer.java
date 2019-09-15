package com.zhihu.wenda.async;

import com.alibaba.fastjson.JSONObject;
import com.zhihu.wenda.service.JedisAdaptor;
import com.zhihu.wenda.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {

    @Autowired
    JedisAdaptor jedisAdaptor;

    public boolean fireEvent(EventModel eventModel){
        try{
            String json = JSONObject.toJSONString(eventModel);
            String eventKey = RedisKeyUtil.getEventQueueKey();
            jedisAdaptor.lpush(eventKey, json);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
