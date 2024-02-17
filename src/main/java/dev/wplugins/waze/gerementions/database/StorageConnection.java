package dev.wplugins.waze.gerementions.database;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StorageConnection implements AutoCloseable {
    private final Connection connection;

    public StorageConnection(Connection connection) {
        this.connection = connection;
    }

    public void close() {
        try {
            if (hasConnection())
                this.connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public boolean hasConnection() {
        try {
            return (this.connection != null && !this.connection.isClosed());
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
    }

    public void execute(String s) throws SQLException {
        if (hasConnection())
            this.connection.prepareStatement(s).execute();
    }

    public ResultSet query(String s) throws SQLException {
        if (!hasConnection())
            throw new SQLException("connection is closed");
        return this.connection.prepareStatement(s).executeQuery();
    }
}

