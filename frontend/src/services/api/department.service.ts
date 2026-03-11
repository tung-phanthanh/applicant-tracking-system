import api from '@/lib/axios';
import type { Department, DepartmentStatus } from '@/types';

type DeptDTO = { id: string, name: string, description?: string, managerName?: string, employeeCount?: number, openPositions?: number, active?: boolean };

const mapDeptDTO = (dto: DeptDTO): Department => ({
    id: dto.id,
    name: dto.name,
    description: dto.description || '',
    head: dto.managerName || '—',
    employeeCount: dto.employeeCount || 0,
    openPositions: dto.openPositions || 0,
    status: (dto.active ? 'active' : 'inactive') as DepartmentStatus,
    createdAt: new Date().toISOString()
});

export const departmentService = {
    getAllDepartments: async (): Promise<Department[]> => {
        const data = await api.get('/departments');
        return (data as unknown as DeptDTO[]).map(mapDeptDTO);
    },

    getDepartmentById: async (id: string): Promise<Department> => {
        const data = await api.get(`/departments/${id}`);
        return mapDeptDTO(data as unknown as DeptDTO);
    },

    createDepartment: async (data: Partial<Department>): Promise<Department> => {
        const payload = {
            name: data.name,
            description: data.description,
            // Assuming no manager UI yet, backend handles missing manager
        };
        const res = await api.post('/departments', payload);
        return mapDeptDTO(res as unknown as DeptDTO);
    },

    updateDepartment: async (id: string, data: Partial<Department>): Promise<Department> => {
        const payload = {
            name: data.name,
            description: data.description,
        };
        const res = await api.put(`/departments/${id}`, payload);
        return mapDeptDTO(res as unknown as DeptDTO);
    },

    toggleStatus: async (id: string, activate: boolean): Promise<Department> => {
        const res = await api.patch(`/departments/${id}/status`, null, { params: { active: activate } });
        return mapDeptDTO(res as unknown as DeptDTO);
    },

    deleteDepartment: async (id: string): Promise<void> => {
        return api.delete(`/departments/${id}`);
    }
};
