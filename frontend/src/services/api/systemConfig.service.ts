import api from '@/lib/api';


// The backend expects/returns a plain Map<String, String> for now in the `update` endpoint.
// We will shape the data depending on the response.
// Assuming the backend has `GET /system-configs` returning `SystemConfig[]` 
// and `PUT /system-configs` returning the updated records.

export const systemConfigService = {
    getAllConfigs: async (): Promise<{ configKey: string, configValue: string }[]> => {
        const { data } = await api.get('/system-config');
        // data from backend is Map<String, String> like { key: "value" }
        return Object.entries(data).map(([key, value]) => ({
            configKey: key,
            configValue: String(value)
        }));
    },

    updateConfigs: async (configs: Record<string, string>): Promise<void> => {
        await api.put('/system-config', configs);
    }
};
