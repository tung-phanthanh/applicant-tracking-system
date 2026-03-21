import api from '@/lib/api';
import type { Role, PermissionKey } from '@/types';

type RoleDTO = { id: string, name: string, description?: string, isSystemRole?: boolean, permissions?: { key: string }[], userCount?: number };

const mapRoleDTO = (dto: RoleDTO): Role => ({
    id: dto.id,
    name: dto.name,
    description: dto.description || '',
    color: dto.isSystemRole ? 'bg-blue-600' : 'bg-slate-600',
    permissions: (dto.permissions || []).map((p: { key: string }) => p.key as PermissionKey),
    userCount: dto.userCount || 0,
    isSystem: !!dto.isSystemRole,
    createdAt: new Date().toISOString(),
});

export const roleService = {
    getAllRoles: async (): Promise<Role[]> => {
        const { data } = await api.get('/roles');
        return (data as unknown as RoleDTO[]).map(mapRoleDTO);
    },

    createRole: async (payloadData: Partial<Role>): Promise<Role> => {
        const payload = {
            name: payloadData.name,
            description: payloadData.description,
            permissionKeys: payloadData.permissions
        };
        const { data } = await api.post('/roles', payload);
        return mapRoleDTO(data as unknown as RoleDTO);
    },

    updateRole: async (id: string, payloadData: Partial<Role>): Promise<Role> => {
        const payload = {
            name: payloadData.name,
            description: payloadData.description,
            permissionKeys: payloadData.permissions
        };
        const { data } = await api.put(`/roles/${id}`, payload);
        return mapRoleDTO(data as unknown as RoleDTO);
    },

    deleteRole: async (id: string): Promise<void> => {
        return api.delete(`/roles/${id}`);
    }
};
