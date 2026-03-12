export interface Job {
  id: number;
  title: string;
  department?: string;
  location?: string;
  description?: string;
  status?: string;
  postedDate?: string;
}

export interface JobRequest {
  title: string;
  department?: string;
  location?: string;
  description?: string;
  status?: string;
}
