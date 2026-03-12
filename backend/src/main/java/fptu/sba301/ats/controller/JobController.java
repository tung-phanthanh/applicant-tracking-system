package fptu.sba301.ats.controller;

import fptu.sba301.ats.entity.Job;
import fptu.sba301.ats.service.JobService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public List<Job> getAll() {
        return jobService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getById(@PathVariable Long id) {
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
    public ResponseEntity<Job> update(@PathVariable Long id, @RequestBody Job job) {
        try {
            Job updated = jobService.update(id, job);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        jobService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
