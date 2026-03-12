package fptu.sba301.ats.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "permissions")
public class Permission {
    @Id
    @Column(name = "key", length = 100, nullable = false)
    private String key;

    @Column(name = "label", length = 255, nullable = false)
    private String label;

    @Column(name = "category", length = 100, nullable = false)
    private String category;
}
