package dev.wplugins.waze.gerementions.Reports;


import javax.sql.rowset.CachedRowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public abstract class Database {

    private static Database instance;

    public static void setupDatabase() {
        instance = new MySQL();
    }

    public static Database getInstance() {
        return instance;
    }

    public abstract void closeConnection();

    public abstract void update(String sql, Object... vars);

    public abstract PreparedStatement execute(String sql, Object... vars);

    public abstract CachedRowSet query(String sql, Object... vars);

    public abstract Connection getConnection();

    public abstract List<String[]> getUsers(String table, String... columns);
}
