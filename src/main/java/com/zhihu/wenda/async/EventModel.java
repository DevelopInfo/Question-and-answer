package com.zhihu.wenda.async;

import java.util.HashMap;
import java.util.Map;

public class EventModel {
    private EventType eventType;
    private int actorId;
    private int entityType;
    private int entityId;
    private int entityOwnerId;

    private Map<String, Object> exts = new HashMap<>();

    public EventModel(){

    }

    public EventModel(EventType eventType){
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }

    public EventModel setEventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, Object> getExts(String key) {
        return exts;
    }

    public EventModel setExts(String key, Object val) {
        this.exts.put(key, val);
        return this;
    }
}
