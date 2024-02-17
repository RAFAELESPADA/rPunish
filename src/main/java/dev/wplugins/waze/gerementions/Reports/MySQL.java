package dev.wplugins.waze.gerementions.Reports;

import dev.wplugins.waze.gerementions.Main;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

public class MySQL extends Database{

    private Connection connection;
    private final ExecutorService executor;
    private final String host;
    private final String port;
    private final String database;
    private final String username;
    private final String password;

    public MySQL() {
        this.host = Main.getInstance().getConfig().getString("database.mysql.host");
        this.port = Main.getInstance().getConfig().getString("database.mysql.porta");
        this.database = Main.getInstance().getConfig().getString("database.mysql.nome");
        this.username = Main.getInstance().getConfig().getString("database.mysql.usuario");
        this.password = Main.getInstance().getConfig().getString("database.mysql.senha");

        this.executor = Executors.newCachedThreadPool();
        openConnection();
        update("CREATE TABLE IF NOT EXISTS `wReports` (`id` VARCHAR(6), `reporterPlayer` VARCHAR(16), `reportedPlayer` VARCHAR(16), `reason` VARCHAR(16) , `serverName` VARCHAR(100), `date` BIGINT(100, PRIMARY KEY(`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin;");
    }

    public void openConnection() {
        if (!isConnected()) {
            try {
                boolean bol = connection == null;
                String rei = Main.getInstance().getConfig().getString("database.url");
                // Set URL for data source


                //jdbc:mysql://u1573_o3nKwl3hpf:L%3DoCfWF!%40SY7E64WYOUFp%2BEg@node18.elgaehost.com.br:3306/s1573_PUNISH
                connection = DriverManager.getConnection(rei);
                Main.getInstance().getLogger().log(Level.FINE, "CONNECTION STRING" + connection);
                if (bol) {
                    Main.getInstance().getLogger().info("§aConectado ao MySQL!");
                    return;
                }

                Main.getInstance().getLogger().info("§6Reconectado ao MySQL!");
            } catch (SQLException e) {
                Main.getInstance().getLogger().log(Level.SEVERE, "Could not open MySQL connection: ", e);
            }
        }
    }

    @Override
    public void closeConnection() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                Main.getInstance().getLogger().log(Level.SEVERE, "Could not close MySQL connection: ", e);
            }
        }
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(5);
        } catch (SQLException e) {
            Main.getInstance().getLogger().log(Level.SEVERE, "MySQL error: ", e);
        }

        return false;
    }

    @Override
    public void update(String sql, Object... vars) {
        try {
            PreparedStatement ps = prepareStatement(sql, vars);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            Main.getInstance().getLogger().log(Level.WARNING, "Could not execute SQL: ", e);
        }
    }

    @Override
    public PreparedStatement execute(String sql, Object... vars) {
        executor.execute(() -> {
            update(sql, vars);
        });
        return null;
    }

    public PreparedStatement prepareStatement(String query, Object... vars) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(query);
            for (int i = 0; i < vars.length; i++) {
                ps.setObject(i + 1, vars[i]);
            }
            return ps;
        } catch (SQLException e) {
            Main.getInstance().getLogger().log(Level.WARNING, "Could not Prepare Statement: ", e);
        }

        return null;
    }

    @Override
    public CachedRowSet query(String query, Object... vars) {
        CachedRowSet rowSet = null;
        try {
            Future<CachedRowSet> future = executor.submit(() -> {
                try {
                    PreparedStatement ps = prepareStatement(query, vars);

                    ResultSet rs = ps.executeQuery();
                    CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
                    crs.populate(rs);
                    rs.close();
                    ps.close();

                    if (crs.next()) {
                        return crs;
                    }
                } catch (Exception e) {
                    Main.getInstance().getLogger().log(Level.WARNING, "Could not Execute Query: ", e);
                }

                return null;
            });

            if (future.get() != null) {
                rowSet = future.get();
            }
        } catch (Exception e) {
            Main.getInstance().getLogger().log(Level.WARNING, "Could not Call FutureTask: ", e);
        }

        return rowSet;
    }

    @Override
    public Connection getConnection() {
        if (!isConnected()) {
            openConnection();
        }

        return connection;
    }
    public static boolean checarExistePlayer(String nome) {
        try {
            PreparedStatement ps = Database.getInstance().execute("SELECT * FROM wReports WHERE id = ?");
            ps.setString(1, nome.toLowerCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException var3) {
            var3.printStackTrace();
        }

        return false;
    }

    @Override
    public List<String[]> getUsers(String table, String... columns) {
        return null;
    }
}