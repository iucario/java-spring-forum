package com.demo.app.notification;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table
public class Notification {

    private String type;
    private String content;
    @Column("created_at")
    private Long createdAt;
    private String sender;
    @PrimaryKey
    private NotificationPrimaryKey key;

    public Notification() {
    }

    public Notification(UUID id, String type, String content, Long createdAt, String sender, String receiver) {
        this.key = new NotificationPrimaryKey(receiver, id);
        this.type = type;
        this.content = content;
        this.createdAt = createdAt;
        this.sender = sender;
    }

    public String toString() {
        UUID id = key.getId();
        String receiver = key.getReceiver();
        return String.format("Notification[id=%s, type=%s, content=%s, createdAt=%s, senderId=%s, receiverId=%s]", id
                , type, content, createdAt, sender, receiver);
    }

    public UUID getId() {
        return key.getId();
    }

    public void setId(UUID id) {
        key.setId(id);
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return key.getReceiver();
    }
}
