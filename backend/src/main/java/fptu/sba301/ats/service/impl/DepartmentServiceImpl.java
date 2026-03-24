package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.response.DepartmentResponse;
import fptu.sba301.ats.repository.DepartmentRepository;
import fptu.sba301.ats.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(dept -> DepartmentResponse.builder()
                        .id(dept.getId())
                        .name(dept.getName())
                        .description(dept.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}
