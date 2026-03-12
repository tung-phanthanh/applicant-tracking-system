package fptu.sba301.ats.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateDocumentDto {
    private UUID id;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSizeBytes;
    private LocalDateTime uploadedAt;
}
