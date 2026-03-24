import api from "@/lib/api";
import type { Department, SystemConfig, AuditLog, Notification } from "@/types/admin";

export const adminService = {
    // Departments
    async getDepartments(): Promise<Department[]> {
        const { data } = await api.get<Department[]>("/departments");
        return data;
    },
    async createDepartment(payload: Partial<Department>): Promise<Department> {
        const { data } = await api.post<Department>("/departments", payload);
        return data;
    },
    async updateDepartment(id: string, payload: Partial<Department>): Promise<Department> {
        const { data } = await api.put<Department>(`/departments/${id}`, payload);
        return data;
    },
    async deleteDepartment(id: string): Promise<void> {
        await api.delete(`/departments/${id}`);
    },

    // System Config
    async getConfigs(): Promise<SystemConfig[]> {
        const { data } = await api.get<SystemConfig[]>("/system-configs");
        return data;
    },
    async updateConfig(payload: { configKey: string; value: string }): Promise<SystemConfig> {
        const { data } = await api.post<SystemConfig>("/system-configs", payload);
        return data;
    },

    // Audit Logs
    async getAuditLogs(): Promise<AuditLog[]> {
        const { data } = await api.get<AuditLog[]>("/audit-logs");
        return data;
    },

    // Notifications
    async getNotifications(): Promise<Notification[]> {
        const { data } = await api.get<Notification[]>("/notifications");
        return data;
    },
    async markAsRead(id: string): Promise<void> {
        await api.patch(`/notifications/${id}/read`);
    },
    async markAllAsRead(): Promise<void> {
        await api.patch("/notifications/read-all");
    },
    async broadcastNotification(payload: { title: string; message: string; type?: string }): Promise<void> {
        await api.post("/notifications/broadcast", payload);
    },
    async sendToRole(payload: { role: string; title: string; message: string; type?: string }): Promise<void> {
        await api.post("/notifications/send-to-role", payload);
    },
    async deleteNotification(id: string): Promise<void> {
        await api.delete(`/notifications/${id}`);
    },
};
