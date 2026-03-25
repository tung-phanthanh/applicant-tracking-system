package fptu.sba301.ats.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Aligns legacy {@code departments} tables with {@link fptu.sba301.ats.entity.BaseEntity}
 * when Hibernate ddl-auto did not add columns to an existing table.
 */
@Component
@Order(Integer.MIN_VALUE)
public class DepartmentAuditColumnsMigration implements ApplicationRunner {

    private final DataSource dataSource;

    public DepartmentAuditColumnsMigration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try (Connection c = dataSource.getConnection()) {
            addColumnIgnoreDuplicate(c,
                    "ALTER TABLE departments ADD COLUMN created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)");
            addColumnIgnoreDuplicate(c, "ALTER TABLE departments ADD COLUMN created_by BINARY(16) NULL");
            addColumnIgnoreDuplicate(c,
                    "ALTER TABLE departments ADD COLUMN last_modified_date DATETIME(6) NULL");
            addColumnIgnoreDuplicate(c, "ALTER TABLE departments ADD COLUMN modified_by BINARY(16) NULL");
        }
    }

    private static void addColumnIgnoreDuplicate(Connection c, String sql) throws SQLException {
        try (Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1060 || (e.getMessage() != null && e.getMessage().contains("Duplicate column"))) {
                return;
            }
            throw e;
        }
    }
}
