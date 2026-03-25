package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.response.ScorecardTemplateResponse;
import java.util.List;

public interface ScorecardTemplateService {
    List<ScorecardTemplateResponse> getAllTemplates();
}
