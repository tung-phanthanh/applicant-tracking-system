export type JobApiStatus =
    | "PENDING"
    | "APPROVED"
    | "REJECTED"
    | "DRAFT"
    | "CLOSED";

export interface JobDTO {
    id: string;
    title: string;
    description: string | null;
    location: string | null;
    salary: string | null;
    status: JobApiStatus;
    createdAt: string;
    departmentId: string | null;
    departmentName: string | null;
    headcount: number;
}

export interface CreateJobPayload {
    title: string;
    description?: string;
    location?: string;
    salary?: string;
    departmentId?: string;
    departmentName?: string;
    headcount?: number;
}

export interface UpdateJobPayload {
    title: string;
    description?: string;
    location?: string;
    salary?: string;
    departmentId?: string;
    departmentName?: string;
    headcount?: number;
}

export interface JobFormValues {
    title: string;
    description: string;
    location: string;
    salary: string;
    departmentName: string;
}

export const JOB_DEPARTMENT_OPTIONS = [
    "Engineering",
    "Product",
    "Design",
    "Marketing",
    "Sales",
] as const;

/** Dropdown option for linking a candidate to an approved job posting. */
export interface JobOption {
    jobId: string;
    title: string;
    status: JobApiStatus;
}
