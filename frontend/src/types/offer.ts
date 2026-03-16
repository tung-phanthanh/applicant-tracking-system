export interface PageResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
    first: boolean;
    last: boolean;
}

export type OfferStatus = "DRAFT" | "PENDING_APPROVAL" | "APPROVED" | "REJECTED" | "SENT";
export type ApprovalStatus = "APPROVED" | "REJECTED";

export interface OfferApproval {
    id: string;
    offerId: string;
    approvedByName: string;
    status: ApprovalStatus;
    comment: string;
    approvedAt: string;
}

export interface Offer {
    id: string;
    applicationId: string;
    candidateName?: string;
    positionTitle: string;
    salary: number;
    equity: number;
    signOnBonus: number;
    startDate: string;
    expiryDate: string;
    status: OfferStatus;
    createdByName: string;
    approvals: OfferApproval[];
}

export interface CreateOfferPayload {
    applicationId: string;
    salary: number;
    equity: number;
    signOnBonus: number;
    positionTitle: string;
    startDate: string;
    expiryDate: string;
}

export interface OfferApprovalPayload {
    status: ApprovalStatus;
    comment: string;
}
