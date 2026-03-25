package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.response.ScorecardTemplateResponse;
import fptu.sba301.ats.service.ScorecardTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static fptu.sba301.ats.constant.AppConstant.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final ScorecardTemplateService templateService;

    @GetMapping
//    @PreAuthorize("hasAnyAuthority('INTERVIEWER')")
    public ResponseEntity<List<ScorecardTemplateResponse>> getAllTemplates() {
        return ResponseEntity.ok(templateService.getAllTemplates());
    }
}
