package com.notifications.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.notifications.Model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
