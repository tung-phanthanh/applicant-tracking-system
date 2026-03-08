import { apiFetch } from "@/lib/api";
import type {
    OfferResponse,
    CreateOfferRequest,
    UpdateOfferRequest,
    ApprovalDecisionRequest,
    OfferStatus,
    Page,
    PageParams,
} from "@/types/models";

export const offerService = {
    getAll(params: PageParams & { status?: OfferStatus } = {}) {
        const { status, ...rest } = params;
        return apiFetch<Page<OfferResponse>>("/offers", {
            params: { page: rest.page ?? 0, size: rest.size ?? 10, status },
        });
    },

    getById(id: number) {
        return apiFetch<OfferResponse>(`/offers/${id}`);
    },

    create(body: CreateOfferRequest) {
        return apiFetch<OfferResponse>("/offers", { method: "POST", body });
    },

    update(id: number, body: UpdateOfferRequest) {
        return apiFetch<OfferResponse>(`/offers/${id}`, { method: "PUT", body });
    },

    submit(offerId: number) {
        return apiFetch<void>(`/offers/${offerId}/submit`, { method: "POST" });
    },

    approve(offerId: number, body: ApprovalDecisionRequest) {
        return apiFetch<void>(`/offers/${offerId}/approve`, {
            method: "POST",
            body,
        });
    },

    reject(offerId: number, body: ApprovalDecisionRequest) {
        return apiFetch<void>(`/offers/${offerId}/reject`, {
            method: "POST",
            body,
        });
    },

    /** Returns PDF as blob URL — used for iframe preview */
    getPreviewUrl(offerId: number): string {
        const token = localStorage.getItem("accessToken");
        // We need to fetch and create object URL
        return `/api/v1/offers/${offerId}/preview?token=${token ?? ""}`;
    },

    async getPreviewBlob(offerId: number): Promise<string> {
        const token = localStorage.getItem("accessToken");
        const response = await fetch(
            `${import.meta.env.VITE_API_URL || "http://localhost:8386/api/v1"}/offers/${offerId}/preview`,
            { headers: { Authorization: `Bearer ${token}` } },
        );
        if (!response.ok) throw new Error("Failed to load PDF");
        const blob = await response.blob();
        return URL.createObjectURL(blob);
    },
};
