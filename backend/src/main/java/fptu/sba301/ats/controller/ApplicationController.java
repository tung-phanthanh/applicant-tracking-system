package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.response.ApplicationResponse;
import fptu.sba301.ats.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ApplicationResponse>> getRecentApplications(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(applicationService.getRecentApplications(limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplicationById(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }
}
