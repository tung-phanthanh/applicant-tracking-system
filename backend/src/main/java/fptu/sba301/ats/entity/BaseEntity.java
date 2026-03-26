package fptu.sba301.ats.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "created_by", length = 36)
    private UUID createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "modified_by", length = 36)
    private UUID modifiedBy;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now(APP_ZONE);
        lastModifiedDate = LocalDateTime.now(APP_ZONE);
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedDate = LocalDateTime.now(APP_ZONE);
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(UUID modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
