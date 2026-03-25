package fptu.sba301.ats.controller;

import fptu.sba301.ats.constant.AdminConstants;
import fptu.sba301.ats.constant.AppConstant;
import fptu.sba301.ats.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AppConstant.BASE_URL + AdminConstants.EXPORT_URL)
@RequiredArgsConstructor
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public class ExportController {

    private final ExportService exportService;

    @GetMapping("/audit-logs/csv")
    public void exportAuditLogsCsv(HttpServletResponse response) throws Exception {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"audit_logs.csv\"");
        exportService.exportAuditLogsCsv(response.getOutputStream());
    }

    @GetMapping("/audit-logs/excel")
    public void exportAuditLogsExcel(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"audit_logs.xlsx\"");
        exportService.exportAuditLogsExcel(response.getOutputStream());
    }

    @GetMapping("/users/csv")
    public void exportUsersCsv(HttpServletResponse response) throws Exception {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"users.csv\"");
        exportService.exportUsersCsv(response.getOutputStream());
    }

    @GetMapping("/users/excel")
    public void exportUsersExcel(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"users.xlsx\"");
        exportService.exportUsersExcel(response.getOutputStream());
    }

    @GetMapping("/database/sql")
    public void exportDatabaseSql(HttpServletResponse response) throws Exception {
        response.setContentType("application/sql");
        response.setHeader("Content-Disposition", "attachment; filename=\"database_backup.sql\"");
        exportService.exportDatabaseSql(response.getOutputStream());
    }
}
