package com.demo.app.notification;

import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends CassandraRepository<Notification, NotificationPrimaryKey> {
    List<Notification> findByKeyReceiver(String receiver);

    void deleteByKeyId(UUID id);
}
