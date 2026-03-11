package fptu.sba301.ats.controller;

import fptu.sba301.ats.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/system-config")
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    @GetMapping
    public ResponseEntity<Map<String, String>> getAllConfigs() {
        return ResponseEntity.ok(systemConfigService.getAllConfigs());
    }

    @PutMapping
    public ResponseEntity<Void> updateConfigs(@RequestBody Map<String, String> configs) {
        systemConfigService.updateConfigs(configs);
        return ResponseEntity.ok().build();
    }
}
