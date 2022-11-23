package com.demo.app.notification;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends CrudRepository<Notification, UUID> {
    List<Notification> findByReceiverId(Long receiverId);

}
