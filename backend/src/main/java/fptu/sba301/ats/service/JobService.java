package fptu.sba301.ats.service;

import fptu.sba301.ats.entity.Job;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobService {
    List<Job> findAll();
    Optional<Job> findById(UUID id);
    Job save(Job job);
    Job update(UUID id, Job job);
    void delete(UUID id);
}
