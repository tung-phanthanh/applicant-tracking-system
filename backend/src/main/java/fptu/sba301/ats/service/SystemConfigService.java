package fptu.sba301.ats.service;

import java.util.Map;

public interface SystemConfigService {
    Map<String, String> getAllConfigs();

    void updateConfigs(Map<String, String> configs);
}
