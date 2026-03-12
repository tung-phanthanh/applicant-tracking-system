package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.response.ApplicationResponse;
import java.util.List;

public interface ApplicationService {
    List<ApplicationResponse> getAllApplications();

    List<ApplicationResponse> getRecentApplications(int limit);

    ApplicationResponse getApplicationById(Long id);
}
