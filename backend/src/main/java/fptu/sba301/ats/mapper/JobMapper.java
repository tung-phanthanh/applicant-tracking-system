package fptu.sba301.ats.mapper;

import fptu.sba301.ats.dto.JobDTO;
import fptu.sba301.ats.dto.request.CreateJobRequest;
import fptu.sba301.ats.dto.request.UpdateJobRequest;
import fptu.sba301.ats.entity.Department;
import fptu.sba301.ats.entity.Job;
import fptu.sba301.ats.enums.JobStatus;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {

    public JobDTO toDto(Job job) {
        if (job == null) {
            return null;
        }
        Department dept = job.getDepartment();
        return JobDTO.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .salary(job.getSalary())
                .status(toApiStatus(job.getStatus()))
                .createdAt(job.getCreatedAt())
                .departmentId(dept != null ? dept.getId() : null)
                .departmentName(dept != null ? dept.getName() : null)
                .headcount(job.getHeadcount())
                .build();
    }

    public Job toNewEntity(CreateJobRequest request, Department department) {
        Integer headcount = request.getHeadcount() != null && request.getHeadcount() > 0
                ? request.getHeadcount()
                : 1;
        return Job.builder()
                .title(request.getTitle().trim())
                .description(trimToNull(request.getDescription()))
                .location(trimToNull(request.getLocation()))
                .salary(trimToNull(request.getSalary()))
                .department(department)
                .status(JobStatus.PENDING_APPROVAL)
                .headcount(headcount)
                .build();
    }

    public void applyUpdate(Job job, UpdateJobRequest request, Department department) {
        job.setTitle(request.getTitle().trim());
        job.setDescription(trimToNull(request.getDescription()));
        job.setLocation(trimToNull(request.getLocation()));
        job.setSalary(trimToNull(request.getSalary()));
        job.setDepartment(department);
        if (request.getHeadcount() != null && request.getHeadcount() > 0) {
            job.setHeadcount(request.getHeadcount());
        }
    }

    public static String toApiStatus(JobStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case PENDING_APPROVAL -> "PENDING";
            case APPROVED -> "APPROVED";
            case REJECTED -> "REJECTED";
            case DRAFT -> "DRAFT";
            case CLOSED -> "CLOSED";
        };
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }
}
