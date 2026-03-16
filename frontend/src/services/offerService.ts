import api from "@/lib/api";
import type { Offer, CreateOfferPayload, OfferApprovalPayload, PageResponse } from "@/types/offer";

export const offerService = {
    async create(payload: CreateOfferPayload): Promise<Offer> {
        const { data } = await api.post<Offer>("/offers", payload);
        return data;
    },

    async update(id: string, payload: CreateOfferPayload): Promise<Offer> {
        const { data } = await api.put<Offer>(`/offers/${id}`, payload);
        return data;
    },

    async getById(id: string): Promise<Offer> {
        const { data } = await api.get<Offer>(`/offers/${id}`);
        return data;
    },

    async getByApplication(applicationId: string): Promise<Offer> {
        const { data } = await api.get<Offer>(`/offers/application/${applicationId}`);
        return data;
    },

    async submitForApproval(id: string): Promise<Offer> {
        const { data } = await api.post<Offer>(`/offers/${id}/submit`);
        return data;
    },

    async getPendingApprovals(page = 0, size = 10): Promise<PageResponse<Offer>> {
        const { data } = await api.get<PageResponse<Offer>>(
            `/offers/pending-approvals?page=${page}&size=${size}`
        );
        return data;
    },

    async processApproval(id: string, payload: OfferApprovalPayload): Promise<Offer> {
        const { data } = await api.post<Offer>(`/offers/${id}/approve`, payload);
        return data;
    },
};
