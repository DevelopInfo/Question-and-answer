package com.zhihu.wenda.async;

import com.alibaba.fastjson.JSON;
import com.zhihu.wenda.service.JedisAdaptor;
import com.zhihu.wenda.utils.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    // 存储 每个Event 需要哪些 EventHandler 类型的 实例
    private Map<EventType, List<EventHandler>> eventTypeHandlersMap = new HashMap<>();
    private ApplicationContext applicationContext;

    @Autowired
    JedisAdaptor jedisAdaptor;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 获取实现EventHandler接口的所有类
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        // 构造eventTypeHandlersMap
        if(beans != null){
            for(Map.Entry<String, EventHandler> entry: beans.entrySet()){
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();
                for(EventType eventType: eventTypes){
                    if(!eventTypeHandlersMap.containsKey(eventType)){
                        eventTypeHandlersMap.put(eventType, new ArrayList<EventHandler>());
                    }
                    eventTypeHandlersMap.get(eventType).add(entry.getValue());
                }
            }
        }


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    // 取出Event
                    String key = RedisKeyUtil.getEventQueueKey();
                    // 第一个元素为key，第二个元素为event
                    List<String> event = jedisAdaptor.brpop(0, key);
                    EventModel eventModel = JSON.parseObject(event.get(1), EventModel.class);
                    if(!eventTypeHandlersMap.containsKey(eventModel.getEventType())){
                        logger.error("不能识别eventType!");
                    }

                    //开始处理
                    for(EventHandler handler : eventTypeHandlersMap.get(eventModel.getEventType())){
                        handler.doHandle(eventModel);
                    }
                }
            }
        });
        // 线程启动
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
