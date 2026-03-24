package fptu.sba301.ats.controller;

import fptu.sba301.ats.constant.AdminConstants;
import fptu.sba301.ats.constant.AppConstant;
import fptu.sba301.ats.constant.PermissionConstants;
import fptu.sba301.ats.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(AppConstant.BASE_URL + AdminConstants.SYSTEM_CONFIG_URL)
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    @GetMapping
    @PreAuthorize("hasAuthority('" + PermissionConstants.SYSTEM_CONFIG_MANAGE + "')")
    public ResponseEntity<Map<String, String>> getAllConfigs() {
        return ResponseEntity.ok(systemConfigService.getAllConfigs());
    }

    @PutMapping
    @PreAuthorize("hasAuthority('" + PermissionConstants.SYSTEM_CONFIG_MANAGE + "')")
    public ResponseEntity<Void> updateConfigs(@RequestBody Map<String, String> configs) {
        systemConfigService.updateConfigs(configs);
        return ResponseEntity.ok().build();
    }
}
