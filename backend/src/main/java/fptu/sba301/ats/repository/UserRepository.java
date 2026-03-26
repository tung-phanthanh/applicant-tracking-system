package fptu.sba301.ats.repository;

import fptu.sba301.ats.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailAndDeletedFalse(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.department WHERE u.email = :email AND u.deleted = false")
    Optional<User> findByEmailAndDeletedFalseWithDepartment(@Param("email") String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.department WHERE u.id = :id AND u.deleted = false")
    Optional<User> findByIdAndDeletedFalseWithDepartment(@Param("id") UUID id);
    List<User> findByDeletedFalse();
    Page<User> findByDeletedFalse(Pageable pageable);
    Page<User> findByDeletedFalseAndDepartmentId(UUID departmentId, Pageable pageable);
    List<User> findByRoleAndDeletedFalse(fptu.sba301.ats.enums.Role role);
    Optional<User> findByResetTokenAndDeletedFalse(String resetToken);
    Optional<User> findByIdAndDeletedFalse(UUID id);
    boolean existsByEmailAndDeletedFalse(String email);
    Optional<User> findByActivationTokenAndDeletedFalse(String activationToken);
    long countByDepartment_IdAndDeletedFalse(UUID departmentId);
    long countByDeletedFalse();
    long countByCreatedAtBetween(Instant startDate, Instant endDate);
}

