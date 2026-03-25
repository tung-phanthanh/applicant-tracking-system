package fptu.sba301.ats.service.impl;

import com.opencsv.CSVWriter;
import fptu.sba301.ats.entity.AuditLog;
import fptu.sba301.ats.entity.User;
import fptu.sba301.ats.repository.AuditLogRepository;
import fptu.sba301.ats.repository.UserRepository;
import fptu.sba301.ats.service.ExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ExportServiceImpl implements ExportService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void exportAuditLogsCsv(OutputStream outputStream) {
        log.info("Exporting Audit Logs to CSV");
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            List<AuditLog> logs = auditLogRepository.findAll();
            
            // Write BOM for Excel compatibility
            outputStream.write(0xEF);
            outputStream.write(0xBB);
            outputStream.write(0xBF);

            String[] header = {"ID", "User ID", "Action", "Entity Type", "Entity ID", "Old Value", "New Value", "IP Address", "User Agent", "Created At"};
            writer.writeNext(header);

            for (AuditLog logRecord : logs) {
                String[] data = {
                        String.valueOf(logRecord.getId()),
                        logRecord.getUserId() != null ? logRecord.getUserId().toString() : "",
                        logRecord.getAction() != null ? logRecord.getAction() : "",
                        logRecord.getEntityType() != null ? logRecord.getEntityType() : "",
                        logRecord.getEntityId() != null ? logRecord.getEntityId() : "",
                        logRecord.getOldValue() != null ? logRecord.getOldValue() : "",
                        logRecord.getNewValue() != null ? logRecord.getNewValue() : "",
                        logRecord.getIpAddress() != null ? logRecord.getIpAddress() : "",
                        logRecord.getUserAgent() != null ? logRecord.getUserAgent() : "",
                        logRecord.getCreatedAt() != null ? logRecord.getCreatedAt().toString() : ""
                };
                writer.writeNext(data);
            }
            log.info("Finished writing Audit Logs to CSV");
        } catch (Exception e) {
            log.error("Error exporting Audit Logs to CSV", e);
            throw new RuntimeException("Error exporting Audit Logs to CSV", e);
        }
    }

    @Override
    public void exportAuditLogsExcel(OutputStream outputStream) {
        log.info("Exporting Audit Logs to Excel");
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Audit Logs");
            List<AuditLog> logs = auditLogRepository.findAll();

            String[] header = {"ID", "User ID", "Action", "Entity Type", "Entity ID", "Old Value", "New Value", "IP Address", "User Agent", "Created At"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < header.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(header[i]);
            }

            int rowNum = 1;
            for (AuditLog logRecord : logs) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(logRecord.getId() != null ? String.valueOf(logRecord.getId()) : "");
                row.createCell(1).setCellValue(logRecord.getUserId() != null ? logRecord.getUserId().toString() : "");
                row.createCell(2).setCellValue(logRecord.getAction() != null ? logRecord.getAction() : "");
                row.createCell(3).setCellValue(logRecord.getEntityType() != null ? logRecord.getEntityType() : "");
                row.createCell(4).setCellValue(logRecord.getEntityId() != null ? logRecord.getEntityId() : "");
                row.createCell(5).setCellValue(logRecord.getOldValue() != null ? logRecord.getOldValue() : "");
                row.createCell(6).setCellValue(logRecord.getNewValue() != null ? logRecord.getNewValue() : "");
                row.createCell(7).setCellValue(logRecord.getIpAddress() != null ? logRecord.getIpAddress() : "");
                row.createCell(8).setCellValue(logRecord.getUserAgent() != null ? logRecord.getUserAgent() : "");
                row.createCell(9).setCellValue(logRecord.getCreatedAt() != null ? logRecord.getCreatedAt().toString() : "");
            }

            log.info("Finished creating Audit Logs Excel workbook, writing to stream");
            workbook.write(outputStream);
            log.info("Finished writing Audit Logs to Excel outputStream");
        } catch (Exception e) {
            log.error("Error exporting Audit Logs to Excel", e);
            throw new RuntimeException("Error exporting Audit Logs to Excel", e);
        }
    }

    @Override
    public void exportUsersCsv(OutputStream outputStream) {
        log.info("Exporting Users to CSV");
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            List<User> users = userRepository.findAll();
            
            // Write BOM for Excel compatibility
            outputStream.write(0xEF);
            outputStream.write(0xBB);
            outputStream.write(0xBF);

            String[] header = {"ID", "Email", "Full Name", "Role", "Department", "Active", "Locked", "Created At"};
            writer.writeNext(header);

            for (User user : users) {
                String[] data = {
                        user.getId() != null ? user.getId().toString() : "",
                        user.getEmail() != null ? user.getEmail() : "",
                        user.getFullName() != null ? user.getFullName() : "",
                        user.getRole() != null ? user.getRole().name() : "",
                        (user.getDepartment() != null && user.getDepartment().getName() != null) ? user.getDepartment().getName() : "",
                        String.valueOf(user.isActive()),
                        String.valueOf(user.isAccountLocked()),
                        user.getCreatedAt() != null ? user.getCreatedAt().toString() : ""
                };
                writer.writeNext(data);
            }
            log.info("Finished writing Users to CSV");
        } catch (Exception e) {
            log.error("Error exporting Users to CSV", e);
            throw new RuntimeException("Error exporting Users to CSV", e);
        }
    }

    @Override
    public void exportUsersExcel(OutputStream outputStream) {
        log.info("Exporting Users to Excel");
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");
            List<User> users = userRepository.findAll();

            String[] header = {"ID", "Email", "Full Name", "Role", "Department", "Active", "Locked", "Created At"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < header.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(header[i]);
            }

            int rowNum = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getId() != null ? user.getId().toString() : "");
                row.createCell(1).setCellValue(user.getEmail() != null ? user.getEmail() : "");
                row.createCell(2).setCellValue(user.getFullName() != null ? user.getFullName() : "");
                row.createCell(3).setCellValue(user.getRole() != null ? user.getRole().name() : "");
                row.createCell(4).setCellValue((user.getDepartment() != null && user.getDepartment().getName() != null) ? user.getDepartment().getName() : "");
                row.createCell(5).setCellValue(user.isActive() ? "Yes" : "No");
                row.createCell(6).setCellValue(user.isAccountLocked() ? "Yes" : "No");
                row.createCell(7).setCellValue(user.getCreatedAt() != null ? user.getCreatedAt().toString() : "");
            }

            log.info("Finished creating Users Excel workbook, writing to stream");
            workbook.write(outputStream);
            log.info("Finished writing Users to Excel outputStream");
        } catch (Exception e) {
            log.error("Error exporting Users to Excel", e);
            throw new RuntimeException("Error exporting Users to Excel", e);
        }
    }

    @Override
    public void exportDatabaseSql(OutputStream outputStream) {
        log.info("Exporting Database SQL Backup");
        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            writer.write("-- Database Backup\n\n");
            
            // Get all tables
            log.info("Querying for tables list");
            List<String> tables = jdbcTemplate.queryForList("SHOW TABLES", String.class);
            log.info("Found {} tables", tables.size());
            
            for (String table : tables) {
                log.info("Getting structure and data for table {}", table);
                writer.write("-- Table structure for table `" + table + "`\n");
                writer.write("DROP TABLE IF EXISTS `" + table + "`;\n");
                
                // Get Create Table statement
                try {
                    Map<String, Object> createTableMap = jdbcTemplate.queryForMap("SHOW CREATE TABLE " + table);
                    // Use case-insensitive search for key
                    String createSyntax = null;
                    for (Map.Entry<String, Object> entry : createTableMap.entrySet()) {
                        if (entry.getKey().equalsIgnoreCase("Create Table")) {
                            createSyntax = (String) entry.getValue();
                            break;
                        }
                    }
                    if (createSyntax != null) {
                        writer.write(createSyntax + ";\n\n");
                    }
                } catch (Exception e) {
                    log.warn("Could not get create syntax for table {}: {}", table, e.getMessage());
                }

                writer.write("-- Dumping data for table `" + table + "`\n");
                log.info("Querying data for table {}", table);
                List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM " + table);
                log.info("Found {} rows for table {}", rows.size(), table);
                for (Map<String, Object> row : rows) {
                    StringBuilder cols = new StringBuilder();
                    StringBuilder vals = new StringBuilder();
                    
                    for (Map.Entry<String, Object> entry : row.entrySet()) {
                        if (cols.length() > 0) {
                            cols.append(", ");
                            vals.append(", ");
                        }
                        cols.append("`").append(entry.getKey()).append("`");
                        
                        Object val = entry.getValue();
                        if (val == null) {
                            vals.append("NULL");
                        } else if (val instanceof Number) {
                            vals.append(val);
                        } else if (val instanceof Boolean) {
                            vals.append(((Boolean) val) ? "1" : "0");
                        } else {
                            String strVal = val.toString().replace("'", "''").replace("\\", "\\\\");
                            vals.append("'").append(strVal).append("'");
                        }
                    }
                    
                    writer.write("INSERT INTO `" + table + "` (" + cols.toString() + ") VALUES (" + vals.toString() + ");\n");
                }
                writer.write("\n");
            }
            writer.flush();
        } catch (Exception e) {
            log.error("Error exporting Database SQL", e);
            throw new RuntimeException("Error exporting Database SQL", e);
        }
    }
}
