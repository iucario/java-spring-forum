package com.demo.app.notification;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table
public class Notification {

    private String type;
    private String content;
    private String createdAt;
    private Long senderId;
    private Long receiverId;
    @PrimaryKey
    private UUID id;

    public Notification() {
    }

    public Notification(UUID id, String type, String content, String createdAt, Long senderId, Long receiverId) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.createdAt = createdAt;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public String toString() {
        return String.format("Notification[id=%s, type=%s, content=%s, createdAt=%s, senderId=%d, receiverId=%d]", id
                , type, content, createdAt, senderId, receiverId);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

}
