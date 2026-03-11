import axios from "axios";
import type { CandidateListResponse, CandidateProfileResponse } from "@/types/candidate";

const BASE_URL = "http://localhost:8386/api/v1";

const axiosInstance = axios.create({
    baseURL: BASE_URL,
    headers: {
        "Content-Type": "application/json",
    },
});

export const getAllCandidates = async (): Promise<CandidateListResponse[]> => {
    const response = await axiosInstance.get<CandidateListResponse[]>("/candidate");
    return response.data;
};

export const getCandidateById = async (id: string): Promise<CandidateProfileResponse> => {
    const response = await axiosInstance.get<CandidateProfileResponse>(`/candidate/${id}`);
    return response.data;
};

