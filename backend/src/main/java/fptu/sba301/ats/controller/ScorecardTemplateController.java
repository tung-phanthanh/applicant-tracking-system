package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.request.CreateScorecardTemplateRequest;
import fptu.sba301.ats.dto.response.ScorecardTemplateResponse;
import fptu.sba301.ats.service.ScorecardTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static fptu.sba301.ats.constant.AppConstant.BASE_URL;
import static fptu.sba301.ats.constant.AppConstant.SCORECARD_TEMPLATE_CONTROLLER_URL;

@RestController
@RequestMapping(BASE_URL + SCORECARD_TEMPLATE_CONTROLLER_URL)
@RequiredArgsConstructor
public class ScorecardTemplateController {

    private final ScorecardTemplateService templateService;

    // GET /scorecard-templates?page=0&size=10  OR  ?departmentId=xxx
    @GetMapping
    @PreAuthorize("hasAnyAuthority('HR', 'HR_MANAGER', 'INTERVIEWER', 'SYSTEM_ADMIN')")
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) UUID departmentId,
            @PageableDefault(size = 20) Pageable pageable) {

        if (departmentId != null) {
            List<ScorecardTemplateResponse> list = templateService.getByDepartment(departmentId);
            return ResponseEntity.ok(list);
        }
        Page<ScorecardTemplateResponse> page = templateService.getAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('HR', 'HR_MANAGER', 'INTERVIEWER', 'SYSTEM_ADMIN')")
    public ResponseEntity<ScorecardTemplateResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(templateService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('HR_MANAGER', 'SYSTEM_ADMIN')")
    public ResponseEntity<ScorecardTemplateResponse> create(
            @Valid @RequestBody CreateScorecardTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(templateService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('HR_MANAGER', 'SYSTEM_ADMIN')")
    public ResponseEntity<ScorecardTemplateResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateScorecardTemplateRequest request) {
        return ResponseEntity.ok(templateService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('HR_MANAGER', 'SYSTEM_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        templateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
