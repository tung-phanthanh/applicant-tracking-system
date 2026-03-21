package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.response.JobResponse;
import fptu.sba301.ats.entity.Job;
import fptu.sba301.ats.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @GetMapping("/active/count")
    public ResponseEntity<Long> getActiveJobsCount() {
        return ResponseEntity.ok(jobService.countActiveJobs());
    }

    @GetMapping("/entity/all")
    public List<Job> getAll() {
        return jobService.findAll();
    }

    @GetMapping("/entity/{id}")
    public ResponseEntity<Job> getById(@PathVariable java.util.UUID id) {
        return jobService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Job> create(@RequestBody Job job) {
        Job saved = jobService.save(job);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Job> update(@PathVariable java.util.UUID id, @RequestBody Job job) {
        try {
            Job updated = jobService.update(id, job);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable java.util.UUID id) {
        jobService.delete(id);
        return ResponseEntity.noContent().build();
    }
}