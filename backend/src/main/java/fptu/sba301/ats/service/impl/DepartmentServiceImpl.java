package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.annotation.LogAudit;
import fptu.sba301.ats.dto.request.DepartmentRequestDTO;
import fptu.sba301.ats.dto.response.DepartmentResponseDTO;
import fptu.sba301.ats.entity.Department;
import fptu.sba301.ats.repository.DepartmentRepository;
import fptu.sba301.ats.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import fptu.sba301.ats.exception.BusinessException;

import java.util.List;
import java.util.stream.Collectors;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public List<DepartmentResponseDTO> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentResponseDTO getDepartmentById(UUID id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Department not found", HttpStatus.NOT_FOUND));
        return toDTO(dept);
    }

    @Override
    @LogAudit(action = "CREATE", resource = "Department")
    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO req) {
        if (departmentRepository.existsByName(req.getName())) {
            throw new BusinessException("Department name already exists", HttpStatus.BAD_REQUEST);
        }
        Department dept = Department.builder()
                .name(req.getName())
                .description(req.getDescription())
                .build();
        return toDTO(departmentRepository.save(dept));
    }

    @Override
    @LogAudit(action = "UPDATE", resource = "Department")
    public DepartmentResponseDTO updateDepartment(UUID id, DepartmentRequestDTO req) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Department not found", HttpStatus.NOT_FOUND));
        
        if (!dept.getName().equals(req.getName()) && departmentRepository.existsByName(req.getName())) {
            throw new BusinessException("Department name already exists", HttpStatus.BAD_REQUEST);
        }
        dept.setName(req.getName());
        dept.setDescription(req.getDescription());
        return toDTO(departmentRepository.save(dept));
    }

    @Override
    @LogAudit(action = "TOGGLE_STATUS", resource = "Department")
    public void toggleDepartmentStatus(UUID id, boolean active) {
        // no status field on basic entity – no-op
    }

    @Override
    @LogAudit(action = "DELETE", resource = "Department")
    public void deleteDepartment(UUID id) {
        departmentRepository.deleteById(id);
    }

    // ── mapper ──────────────────────────────────────────────────────────────
    private DepartmentResponseDTO toDTO(Department dept) {
        return DepartmentResponseDTO.builder()
                .id(dept.getId())
                .name(dept.getName())
                .description(dept.getDescription())
                .createdAt(dept.getCreatedAt() != null ? dept.getCreatedAt().toString() : null)
                .updatedAt(dept.getLastModifiedDate() != null ? dept.getLastModifiedDate().toString() : null)
                .build();
    }
}
