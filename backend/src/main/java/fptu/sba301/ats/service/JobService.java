package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.JobDTO;
import fptu.sba301.ats.dto.request.CreateJobRequest;
import fptu.sba301.ats.dto.request.UpdateJobRequest;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface JobService {

    JobDTO create(CreateJobRequest request);

    List<JobDTO> listApprovedJobs();

    List<JobDTO> listPendingJobs();

    JobDTO getById(UUID id, Authentication authentication);

    JobDTO update(UUID id, UpdateJobRequest request);

    JobDTO approve(UUID id);

    JobDTO reject(UUID id);
}
