package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.response.JobResponse;
import java.util.List;

import fptu.sba301.ats.entity.Job;

import java.util.List;
import java.util.Optional;

public interface JobService {
    List<Job> findAll();
    Optional<Job> findById(java.util.UUID id);
    Job save(Job job);
    Job update(java.util.UUID id, Job job);
    void delete(java.util.UUID id);
      List<JobResponse> getAllJobs();

    JobResponse getJobById(java.util.UUID id);

    long countActiveJobs();
}