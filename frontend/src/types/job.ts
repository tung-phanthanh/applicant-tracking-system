export type JobStatus = "DRAFT" | "PENDING_APPROVAL" | "APPROVED" | "REJECTED" | "CLOSED";

export interface JobOption {
  jobId: string;
  title: string;
  status: JobStatus;
}
