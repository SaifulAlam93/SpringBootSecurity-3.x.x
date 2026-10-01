package com.abc.SpringBootSecqurityEx.service;

import com.abc.SpringBootSecqurityEx.enums.ERole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
@Order(0)
public class RoleDataMigration implements CommandLineRunner {
    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    public RoleDataMigration(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws SQLException {
        if (!tableExists("userrole")) {
            return;
        }

        List<Map<String, Object>> oldAssignments = jdbcTemplate.queryForList(
                "select user_name, role_name from userrole");
        for (Map<String, Object> assignment : oldAssignments) {
            String username = String.valueOf(assignment.get("user_name"));
            String roleName = String.valueOf(assignment.get("role_name"));
            try {
                ERole.valueOf(roleName);
                jdbcTemplate.update("""
                        insert into user_roles (user_name, role_name)
                        select ?, ?
                        where not exists (
                            select 1 from user_roles where user_name = ? and role_name = ?
                        )
                        """, username, roleName, username, roleName);
            } catch (IllegalArgumentException ignored) {
                // Old custom database roles are omitted; only enum roles are supported now.
            }
        }
    }

    private boolean tableExists(String tableName) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metadata = connection.getMetaData();
            try (ResultSet tables = metadata.getTables(null, null, tableName, new String[]{"TABLE"})) {
                if (tables.next()) {
                    return true;
                }
            }
            try (ResultSet tables = metadata.getTables(null, null, tableName.toUpperCase(), new String[]{"TABLE"})) {
                return tables.next();
            }
        }
    }
}
