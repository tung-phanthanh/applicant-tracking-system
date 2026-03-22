package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserId(java.util.UUID userId, Pageable pageable);

    Page<Notification> findByUserIdAndIsRead(java.util.UUID userId, boolean isRead, Pageable pageable);

    List<Notification> findByUserIdAndIsReadFalse(java.util.UUID userId);
}
