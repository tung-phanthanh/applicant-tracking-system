export type CandidateStage =
  | "APPLIED"
  | "SCREENING"
  | "INTERVIEW"
  | "OFFER"
  | "HIRED"
  | "REJECTED";

export type CandidateApplicationStatus = "ACTIVE" | "WITHDRAWN" | "REJECTED";

export interface CandidateListItem {
  candidateId: string;
  fullName: string;
  email: string;
  jobTitle: string;
  stage: CandidateStage;
  rating: number | null;
  appliedAt: string;
}

export interface CandidateDocumentItem {
  documentId: string;
  fileName: string;
  fileUrl: string;
  fileType: string;
  fileSizeBytes: number | null;
  uploadedAt: string;
}

export interface CandidateDetailItem {
  candidateId: string;
  fullName: string;
  email: string;
  phone: string;
  currentCompany: string;
  jobTitle: string;
  stage: CandidateStage;
  status: CandidateApplicationStatus;
  rating: number | null;
  appliedAt: string;
  source: string;
  location: string;
  experienceYears: number | null;
  summary: string;
  documents: CandidateDocumentItem[];
}

export interface BulkImportResult {
  totalRows: number;
  successCount: number;
  failCount: number;
  errors: string[];
}
