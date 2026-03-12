package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.response.JobResponse;
import java.util.List;

import fptu.sba301.ats.entity.Job;

import java.util.List;
import java.util.Optional;

public interface JobService {
    List<Job> findAll();
    Optional<Job> findById(Long id);
    Job save(Job job);
    Job update(Long id, Job job);
    void delete(Long id);
      List<JobResponse> getAllJobs();

    JobResponse getJobById(Long id);

    long countActiveJobs();
}
