package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.request.DepartmentRequestDTO;
import fptu.sba301.ats.dto.response.DepartmentResponseDTO;
import fptu.sba301.ats.entity.Department;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.repository.DepartmentRepository;
import fptu.sba301.ats.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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

    // ── GET ALL ──────────────────────────────────────────────────────────────

    @Test
    void testGetAllDepartments_Success() {
        Department dept2 = new Department();
        dept2.setId(UUID.randomUUID());
        dept2.setName("Marketing");
        dept2.setDescription("Marketing Department");

        when(departmentRepository.findAll()).thenReturn(List.of(department, dept2));

        List<DepartmentResponseDTO> result = departmentService.getAllDepartments();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Engineering", result.get(0).getName());
        assertEquals("Marketing", result.get(1).getName());
    }

    // ── GET BY ID ────────────────────────────────────────────────────────────

    @Test
    void testGetDepartmentById_Success() {
        when(departmentRepository.findById(deptId)).thenReturn(Optional.of(department));

        DepartmentResponseDTO result = departmentService.getDepartmentById(deptId);

        assertNotNull(result);
        assertEquals("Engineering", result.getName());
        assertEquals("Tech Department", result.getDescription());
    }

    @Test
    void testGetDepartmentById_NotFound_ThrowsException() {
        UUID unknownId = UUID.randomUUID();
        when(departmentRepository.findById(unknownId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            departmentService.getDepartmentById(unknownId);
        });

        assertEquals("Department not found", ex.getMessage());
    }

    // ── CREATE ───────────────────────────────────────────────────────────────

    @Test
    void testCreateDepartment_Success() {
        DepartmentRequestDTO req = new DepartmentRequestDTO();
        req.setName("HR");
        req.setDescription("Human Resources");

        when(departmentRepository.existsByName("HR")).thenReturn(false);
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
    void testCreateDepartment_DuplicateName_ThrowsException() {
        DepartmentRequestDTO req = new DepartmentRequestDTO();
        req.setName("Engineering");

        when(departmentRepository.existsByName("Engineering")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            departmentService.createDepartment(req);
        });

        assertEquals("Department name already exists", ex.getMessage());
        verify(departmentRepository, never()).save(any(Department.class));
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    @Test
    void testUpdateDepartment_Success() {
        DepartmentRequestDTO req = new DepartmentRequestDTO();
        req.setName("R&D");
        req.setDescription("Research and Development");

        when(departmentRepository.findById(deptId)).thenReturn(Optional.of(department));
        when(departmentRepository.existsByName("R&D")).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenAnswer(inv -> inv.getArgument(0));

        DepartmentResponseDTO result = departmentService.updateDepartment(deptId, req);

        assertNotNull(result);
        assertEquals("R&D", result.getName());
        assertEquals("Research and Development", result.getDescription());
        verify(departmentRepository, times(1)).save(department);
    }

    @Test
    void testUpdateDepartment_NotFound_ThrowsException() {
        UUID unknownId = UUID.randomUUID();
        DepartmentRequestDTO req = new DepartmentRequestDTO();
        req.setName("Ops");

        when(departmentRepository.findById(unknownId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            departmentService.updateDepartment(unknownId, req);
        });

        assertEquals("Department not found", ex.getMessage());
    }

    @Test
    void testUpdateDepartment_DuplicateName_ThrowsException() {
        DepartmentRequestDTO req = new DepartmentRequestDTO();
        req.setName("Marketing"); // different name, but already exists

        when(departmentRepository.findById(deptId)).thenReturn(Optional.of(department));
        when(departmentRepository.existsByName("Marketing")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            departmentService.updateDepartment(deptId, req);
        });

        assertEquals("Department name already exists", ex.getMessage());
        verify(departmentRepository, never()).save(any(Department.class));
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    @Test
    void testDeleteDepartment_Success() {
        doNothing().when(departmentRepository).deleteById(deptId);

        departmentService.deleteDepartment(deptId);

        verify(departmentRepository, times(1)).deleteById(deptId);
    }
}
