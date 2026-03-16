package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.request.CreateScorecardTemplateRequest;
import fptu.sba301.ats.dto.response.ScorecardTemplateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ScorecardTemplateService {
    Page<ScorecardTemplateResponse> getAll(Pageable pageable);
    ScorecardTemplateResponse getById(UUID id);
    List<ScorecardTemplateResponse> getByDepartment(UUID departmentId);
    ScorecardTemplateResponse create(CreateScorecardTemplateRequest request);
    ScorecardTemplateResponse update(UUID id, CreateScorecardTemplateRequest request);
    void delete(UUID id);
}
