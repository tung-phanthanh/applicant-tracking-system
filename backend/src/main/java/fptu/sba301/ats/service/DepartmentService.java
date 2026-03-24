package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.response.DepartmentResponse;
import java.util.List;

public interface DepartmentService {
    List<DepartmentResponse> getAllDepartments();
}
