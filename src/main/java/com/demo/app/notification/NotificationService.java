package com.demo.app.notification;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.demo.app.comment.Comment;
import com.demo.app.user.User;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(NotificationService.class);

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void addNotification(Comment comment) {
        UUID id = Uuids.timeBased();
        // TODO: limit content size
        String content = "%s commented on your post %s".formatted(comment.getUser().getName(),
                comment.getPost().getTitle());
        String sender = comment.getUser().getName();
        String receiver = comment.getPost().getUser().getName();
        Notification notification = new Notification(id, "comment", content, comment.getCreatedAt(),
                sender, receiver);
        Notification saved = notificationRepository.save(notification);
        logger.info("Saved notification: {}", saved);
    }

    public List<Notification> getNotifications(String receiver) {
        return notificationRepository.findByKeyReceiver(receiver);
    }

    public void deleteNotification(UUID id, User user) {
        NotificationPrimaryKey key = new NotificationPrimaryKey(user.getName(), id);
        notificationRepository.deleteById(key);
    }
}
