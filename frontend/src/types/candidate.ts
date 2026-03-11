export interface CandidateListResponse {
    id: string; // UUID
    name: string;
    email: string;
    appliedFor: string;
    stage: string;
    rating: number | null;
    appliedDate: string;
}

export interface CandidateProfileResponse {
    id: string; // UUID
    name: string;
    email: string;
    phone: string;
    currentCompany: string;
    appliedFor: string;
    stage: string;
    status: string;
    rating: number | null;
    appliedDate: string;
}

