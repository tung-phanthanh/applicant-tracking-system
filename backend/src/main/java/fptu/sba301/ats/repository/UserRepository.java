package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailAndDeletedFalse(String email);
    List<User> findAllByDeletedFalse();
    Optional<User> findByResetTokenAndDeletedFalse(String resetToken);
    Optional<User> findByIdAndDeletedFalse(UUID id);
    boolean existsByEmailAndDeletedFalse(String email);
    Optional<User> findByActivationTokenAndDeletedFalse(String activationToken);

    Optional<User> findByEmail(String email);
}

