package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.request.DepartmentRequestDTO;
import fptu.sba301.ats.dto.response.DepartmentResponseDTO;
import fptu.sba301.ats.entity.Department;
import fptu.sba301.ats.repository.DepartmentRepository;
import fptu.sba301.ats.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department department;
    private UUID deptId;

    @BeforeEach
    void setUp() {
        deptId = UUID.randomUUID();
        department = new Department();
        department.setId(deptId);
        department.setName("Engineering");
        department.setDescription("Tech Department");
    }

    @Test
    void testGetDepartmentById_Success() {
        when(departmentRepository.findById(deptId)).thenReturn(Optional.of(department));
        DepartmentResponseDTO result = departmentService.getDepartmentById(deptId);
        assertNotNull(result);
        assertEquals("Engineering", result.getName());
        assertEquals("Tech Department", result.getDescription());
    }

    @Test
    void testCreateDepartment_Success() {
        DepartmentRequestDTO req = new DepartmentRequestDTO();
        req.setName("HR");
        req.setDescription("Human Resources");

        when(departmentRepository.save(any(Department.class))).thenAnswer(inv -> {
            Department d = inv.getArgument(0);
            d.setId(UUID.randomUUID());
            return d;
        });

        DepartmentResponseDTO result = departmentService.createDepartment(req);
        assertNotNull(result);
        assertEquals("HR", result.getName());
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    void testDeleteDepartment_Success() {
        doNothing().when(departmentRepository).deleteById(deptId);
        departmentService.deleteDepartment(deptId);
        verify(departmentRepository, times(1)).deleteById(deptId);
    }
}
