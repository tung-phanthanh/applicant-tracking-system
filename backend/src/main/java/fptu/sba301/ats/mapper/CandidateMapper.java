package fptu.sba301.ats.mapper;

import fptu.sba301.ats.dto.response.CandidateListResponse;
import fptu.sba301.ats.dto.response.CandidateProfileResponse;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CandidateMapper {

    CandidateListResponse toCandidateListResponse(
            String id,
            String name,
            String email,
            String appliedFor,
            String stage,
            Double rating,
            java.time.LocalDateTime appliedDate);

    CandidateProfileResponse toCandidateProfileResponse(
            UUID id,
            String name,
            String email,
            String phone,
            String currentCompany,
            String appliedFor,
            String stage,
            String status,
            Double rating,
            LocalDateTime appliedDate);
}
