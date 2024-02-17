package dev.wplugins.waze.gerementions.database;

import dev.wplugins.waze.gerementions.BukkitMain;
import dev.wplugins.waze.gerementions.Main;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

public class MySQLDatabase extends Database {

    private StorageConnection connection;

    private Connection connection2;
    private ExecutorService executor;
    private String host;
    private String port;
    private String database;
    public static PluginInstance instancia;
    private String username;
    private String password;

    public MySQLDatabase() {
        if (instancia == PluginInstance.BUNGEECORD) {
            this.host = Main.getInstance().getConfig().getString("database.mysql.host");
            this.port = Main.getInstance().getConfig().getString("database.mysql.porta");
            this.database = Main.getInstance().getConfig().getString("database.mysql.nome");
            this.username = Main.getInstance().getConfig().getString("database.mysql.user");
            this.password = Main.getInstance().getConfig().getString("database.mysql.senha");
        } else {

        }
        this.executor = Executors.newCachedThreadPool();
        openConnection();
        update("CREATE TABLE IF NOT EXISTS `wPunish` (`id` VARCHAR(6), `playerName` VARCHAR(16) , `idreal` INT(11), `stafferName` VARCHAR(16), `reason` TEXT, `type` TEXT, `proof` TEXT, `date` BIGINT(100), `expires` LONG, PRIMARY KEY(`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin;");
        update("CREATE TABLE IF NOT EXISTS `wPunish2` (`id` VARCHAR(6), `playerName` VARCHAR(16), `stafferName` VARCHAR(16), `reason` TEXT, `type` TEXT, `proof` TEXT, `date` BIGINT(100), `expires` LONG, PRIMARY KEY(`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin;");
    }

    public void openConnection() {
        if (!isConnected()) {
            try {
                boolean bol = connection2 == null;
                String rei;
                if (instancia == PluginInstance.BUNGEECORD) {
                    Main.getInstance().getLogger().info("CONNECTING IN MYSQL...");
                    rei = Main.getInstance().getConfig().getString("database.url");
                    // Set URL for data source
                } else {
                    BukkitMain.getPlugin().getLogger().info("CONECTANDO NO MYSQL...");
                    Class.forName("com.mysql.jdbc.Driver");
                    rei = BukkitMain.getPlugin().getConfig().getString("database.url");
                }

                if (instancia == PluginInstance.BUNGEECORD) {
                    Properties props = new Properties();
                    props.put("autoReconnect", "true");
                    props.put("failOverReadOnly" , "false");
                    props.put("user", Main.getInstance().getConfig().getString("database.user"));
                    props.put("password", Main.getInstance().getConfig().getString("database.senha"));
                    connection2 =  DriverManager.getConnection("jdbc:mysql://" + Main.getInstance().getConfig().getString("database.host") + "/" + Main.getInstance().getConfig().getString("database.nome"), props);
                } else {
                    Properties props = new Properties();
                    props.put("autoReconnect", "true");
                    props.put("failOverReadOnly" , "false");
                    props.put("user", BukkitMain.getPlugin().getConfig().getString("database.user"));
                    props.put("password", BukkitMain.getPlugin().getConfig().getString("database.senha"));
                    connection2 =  DriverManager.getConnection("jdbc:mysql://" + BukkitMain.getPlugin().getConfig().getString("database.host") + "/" + BukkitMain.getPlugin().getConfig().getString("database.nome"), props);

                }

                if (bol) {
                    if (instancia == PluginInstance.BUNGEECORD) {
                        Main.getInstance().getLogger().info("Conectado ao MySQL!");
                    }  else {
                        BukkitMain.getPlugin().getLogger().info("Conectado ao MYSQL!");
                    }

                }

            } catch (SQLException e) {

                if (instancia == PluginInstance.BUNGEECORD) {

                    Main.getInstance().getLogger().log(Level.SEVERE, "Could not open MySQL connection: ", e);


            } else {
                    BukkitMain.getPlugin().getLogger().log(Level.SEVERE, "Could not open MySQL connection: ", e);

                }
                } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
                }




    @Override
    public void closeConnection() {
        if (isConnected()) {
            try {
                connection2.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isConnected() {
        return connection2 != null;

    }

    @Override
    public void update(String sql, Object... vars) {
        try {
            PreparedStatement ps = prepareStatement(sql, vars);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
        }
    }

    @Override
    public void execute(String sql, Object... vars) {
        executor.execute(() -> {
            update(sql, vars);
        });
    }

    public PreparedStatement prepareStatement(String query, Object... vars) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(query);
            for (int i = 0; i < vars.length; i++) {
                ps.setObject(i + 1, vars[i]);
            }
            return ps;
        } catch (SQLException e) {
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
                }

                return null;
            });

            if (future.get() != null) {
                rowSet = future.get();
            }
        } catch (Exception e) {
        }

        return rowSet;
    }

    @Override
    public Connection getConnection() {
        if (!isConnected()) {
            openConnection();
        }

        return connection2;
    }

    @Override
    public List<String[]> getUsers(String table, String... columns) {
        return null;
    }
}
