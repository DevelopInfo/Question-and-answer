package com.zhihu.wenda.async;

import java.util.List;

public interface EventHandler {
    void doHandle(EventModel eventModel);

    // 获取EventHandler支持哪些类
    List<EventType> getSupportEventTypes();
}
