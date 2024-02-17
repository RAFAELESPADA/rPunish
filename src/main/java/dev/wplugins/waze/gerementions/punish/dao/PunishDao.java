package dev.wplugins.waze.gerementions.punish.dao;



import dev.wplugins.waze.gerementions.BukkitMain;
import dev.wplugins.waze.gerementions.Main;
import dev.wplugins.waze.gerementions.database.Database;
import dev.wplugins.waze.gerementions.database.MySQLDatabase;
import dev.wplugins.waze.gerementions.database.PluginInstance;
import dev.wplugins.waze.gerementions.enums.punish.PunishType;
import dev.wplugins.waze.gerementions.enums.reason.Reason;
import dev.wplugins.waze.gerementions.enums.reason.ReasonSpigot;
import dev.wplugins.waze.gerementions.punish.Punish;
import dev.wplugins.waze.gerementions.punish.service.PunishService;
import dev.wplugins.waze.gerementions.punish.service.PunishServiceSpigot;
import dev.wplugins.waze.gerementions.punish.service.PunishSpigot;
import dev.wplugins.waze.gerementions.punish.service.impl.PunishServiceImpl;
import dev.wplugins.waze.gerementions.punish.service.impl.PunishServiceImpl2;
import dev.wplugins.waze.gerementions.thread.PunishThread;
import lombok.Getter;
import net.md_5.bungee.BungeeCord;
import org.bukkit.entity.Player;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class PunishDao {

    private final PunishThread thread;
    @Getter
    private final PunishService punishService;
    int sequence = 1;
    @Getter
    private final PunishServiceSpigot punishService2;
    @Getter
    private final List<Punish> lastHourPunishes;
    @Getter
    private final List<PunishSpigot> lastHourPunishes2;

    public PunishDao() {
        PunishThread thread1;
        this.punishService = new PunishServiceImpl();
        this.punishService2 = new PunishServiceImpl2();

        this.lastHourPunishes = new ArrayList<>();
        this.lastHourPunishes2 = new ArrayList<>();
        try {
            Class.forName("org.bukkit.Bukkit");
            thread1 = BukkitMain.getPlugin().getPunishThread();
        } catch (ClassNotFoundException e1) {
            thread1 = Main.getInstance().getPunishThread();
        }
        this.thread = thread1;
    }


    public Punish createPunish(String targetName, String stafferName, Reason reason, String proof, String type, int idreal, String ip) {
        Punish punish = Punish.builder().id(UUID.randomUUID().toString().substring(0, 6)).playerName(targetName).stafferName(stafferName).reason(reason).type(type).proof(proof).date(new Date().getTime()).expire((reason.getTime() != 0 ? (System.currentTimeMillis() + reason.getTime()) : 0)).idpenis(punishService.getPunishes().size()).ip(ip).build();
        CompletableFuture.runAsync(() -> {

            while (getPunishService().getPunishes().stream().anyMatch(p -> p.getId().equals(punish.getId()))) {
                punish.setId(UUID.randomUUID().toString().substring(0, 6));

            }
            punish.setIdpenis(punishService.getPunishes().size());

            punishService.getPunishes().add(punish);
            lastHourPunishes.add(punish);
            Database.getInstance().execute("INSERT INTO `wPunish` VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", punish.getId(), punish.getPlayerName(), punish.getStafferName(), punish.getReason().name(), punish.getType(), punish.getProof(), punish.getDate(), punish.getExpire(), punish.getIdpenis(), punish.getIp());
            Main.getInstance().getLogger().info("Nova punição salva " + stafferName + " puniu " + targetName);
            Main.getInstance().getLogger().info("Tipo " + type);
            Main.getInstance().getLogger().info("Motivo " + reason);
            Main.getInstance().getLogger().info("Prova " + proof);
            Main.getInstance().getLogger().info("ID Númerico: " + idreal);
            Main.getInstance().getLogger().info("Punições ativas: " + punishService.getPunishes().size());
        }, thread);
        return punish;
        }

    public PunishSpigot createPunish2(String targetName, String stafferName, ReasonSpigot reason, String proof, String type) {
        PunishSpigot punish = PunishSpigot.builder().id(UUID.randomUUID().toString().substring(0, 6)).playerName(targetName).stafferName(stafferName).reason(reason).type(type).proof(proof).date(new Date().getTime()).expire((reason.getTime() != 0 ? (System.currentTimeMillis() + reason.getTime()) : 0)).build();
        PunishSpigot punish2 = PunishSpigot.builder().id(UUID.randomUUID().toString().substring(0, 6)).playerName(targetName).stafferName(stafferName).reason(ReasonSpigot.AMEACA).type(type).proof(proof).date(new Date().getTime()).expire((reason.getTime() != 0 ? (System.currentTimeMillis() + reason.getTime()) : 0)).build();
        CompletableFuture.runAsync(() -> {

            while (getPunishService2().getPunishes().stream().anyMatch(p -> p.getId().equals(punish2.getId()))) {
                punish2.setId(UUID.randomUUID().toString().substring(0, 6));
            }

            punishService2.create(punish2);

            lastHourPunishes2.add(punish2);
            Database.getInstance().execute("INSERT INTO `wPunish2` VALUES (?, ?, ?, ?, ?, ?, ?, ?)", punish.getId(), punish.getPlayerName(), punish.getStafferName(), punish.getReason().name(), punish.getType(), punish.getProof(), punish.getDate(), punish.getExpire());
            Main.getInstance().getLogger().info("Nova punição salva (2) " + stafferName + " puniu " + targetName);
            Main.getInstance().getLogger().info("Tipo " + type);
            Main.getInstance().getLogger().info("Motivo " + reason);
            Main.getInstance().getLogger().info("Prova " + proof);
            Main.getInstance().getLogger().info("ID " + punish2.getId());
        }, thread);
        return punish2;
    }

    public static int getId(Player player) {
        try {
            Statement statement = MySQLDatabase.getInstance().getConnection().createStatement();
            Statement statement2 = MySQLDatabase.getInstance().getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM wPunish WHERE playername='" + player.getName() + "'");
            ResultSet resultSet2 = statement2.executeQuery("SELECT * FROM wPunish2");
            return resultSet.getInt("idreal");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Reason getReason(String reason) {
        try {
            Statement statement = MySQLDatabase.getInstance().getConnection().createStatement();
            Statement statement2 = MySQLDatabase.getInstance().getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM wPunish WHERE playerName='" + reason + "'");
            return Reason.valueOf(resultSet.getString("reason"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void loadPunishes() {
        try {
            Statement statement = MySQLDatabase.getInstance().getConnection().createStatement();
            Statement statement2 = MySQLDatabase.getInstance().getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM wPunish");
            ResultSet resultSet2 = statement2.executeQuery("SELECT * FROM wPunish2");
            if (MySQLDatabase.instancia == PluginInstance.BUNGEECORD) {
                if (resultSet.next()) {
                    punishService.getPunishes().add(Punish.builder().id(resultSet.getString("id")).playerName(resultSet.getString("playerName")).stafferName(resultSet.getString("stafferName")).reason(Reason.valueOf(resultSet.getString("reason"))).proof(resultSet.getString("proof")).date(resultSet.getLong("date")).expire(resultSet.getLong("expires")).idpenis(resultSet.getInt("idreal")).ip(resultSet.getString("ip")).build());
                    punishService.create(Punish.builder().id(resultSet.getString("id")).playerName(resultSet.getString("playerName")).stafferName(resultSet.getString("stafferName")).reason(Reason.valueOf(resultSet.getString("reason"))).proof(resultSet.getString("proof")).date(resultSet.getLong("date")).expire(resultSet.getLong("expires")).idpenis(resultSet.getInt("idreal")).ip(resultSet.getString("ip")).build());
                }
            }
            if (resultSet2.next()) {
                if (MySQLDatabase.instancia == PluginInstance.SPIGOT) {
                    punishService2.create(PunishSpigot.builder().id(resultSet2.getString("id")).playerName(resultSet2.getString("playerName")).stafferName(resultSet2.getString("stafferName")).reason(ReasonSpigot.AMEACA).proof(resultSet2.getString("proof")).date(resultSet2.getLong("date")).expire(resultSet2.getLong("expires")).build());
                    BukkitMain.getPlugin().getLogger().info("§9Punições ativas sendo carregadas.");
                }
            }


            if (!statement.isClosed()) {
                statement.close();
            }
            if (MySQLDatabase.instancia == PluginInstance.BUNGEECORD) {
                Main.getInstance().getLogger().info("§ePunições ativa com sucesso.");
            } else {
                BukkitMain.getPlugin().getLogger().info("§9Punições ativa com sucesso.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disablePunish2(String id) {
        CompletableFuture.runAsync(() -> {
            Database.getInstance().execute("DELETE FROM `wPunish2` WHERE id = ?", id);

            punishService2.remove(id);
            if (MySQLDatabase.instancia == PluginInstance.BUNGEECORD) {
                Main.getInstance().getLogger().info("Punish #" + id + " deletado com sucesso");

            }

            if (MySQLDatabase.instancia == PluginInstance.SPIGOT) {
                BukkitMain.getPlugin().getLogger().info("Punish #" + id + " deletado com sucesso");
            }
        }, thread);
    }

    public void disablePunish(String id) {
        CompletableFuture.runAsync(() -> {
            Database.getInstance().execute("DELETE FROM `wPunish` WHERE id = ?", id);
            punishService.remove(id);

            if (MySQLDatabase.instancia == PluginInstance.BUNGEECORD) {
                Main.getInstance().getLogger().info("Punish #" + id + " deletado com sucesso");

            }

            if (MySQLDatabase.instancia == PluginInstance.SPIGOT) {
                BukkitMain.getPlugin().getLogger().info("Punish #" + id + " deletado com sucesso");
            }
        }, thread);
    }

    /*public void unPunish(String id, ReasonRevogar reason) {
        try {
            Statement statement = MySQLDatabase.getInstance().getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT unpunish FROM ZyraPunish");
            while (resultSet.next()) {
                punishService.create(Punish.builder().id(resultSet.getString("id")).playerName(resultSet.getString("playerName")).stafferName(resultSet.getString("stafferName")).reason(Reason.valueOf(resultSet.getString("reason"))).proof(resultSet.getString("proof")).date(resultSet.getLong("date")).expire(resultSet.getLong("expires")).build());
            }
            if (!statement.isClosed()) {
                statement.close();
            }
            Main.getInstance().getLogger().info("§ePunições ativa com sucesso.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/
    public void clearPunishes(String player) {
        getPunishService().getPunishes().stream().filter(punish -> punish.getPlayerName().equals(player)).filter(punish -> punish.getExpire() > 0 && (System.currentTimeMillis() >= punish.getExpire())).forEach(punish -> disablePunish(punish.getId()));
    }

    public void clearPunishes2(String player) {
        getPunishService2().getPunishes().stream().filter(punish -> punish.getPlayerName().equals(player)).filter(punish -> punish.getExpire() > 0 && (System.currentTimeMillis() >= punish.getExpire())).forEach(punish -> disablePunish(punish.getId()));
    }

    public boolean isBanned(String player) {
        Statement statement2 = null;
        try {
            statement2 = MySQLDatabase.getInstance().getConnection().createStatement();
            ResultSet resultSet2 = statement2.executeQuery("SELECT * FROM wPunish WHERE playerName='" + player + "' AND (type='BAN' OR type='TEMPBAN' OR type='Banimento temporário')");

            return resultSet2.next();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public Stream<PunishSpigot> isBannedSpigot(String player) {
        return punishService2.getPunishes().stream().filter(punish -> punish.getPlayerName().equals(player)).filter(punish -> punish.getReason().getPunishType() == PunishType.TEMPBAN || punish.getReason().getPunishType() == PunishType.BAN).filter(PunishSpigot::isLocked);
    }

    public Stream<Punish> isMuted(String player) {
        Statement statement2 = null;
        try {
            statement2 = MySQLDatabase.getInstance().getConnection().createStatement();
            ResultSet resultSet2 = statement2.executeQuery("SELECT * FROM wPunish WHERE playerName='" + player + "' AND (type='TEMPMUTE' OR type='MUTE' OR type='Mute temporário')");
            if (resultSet2.next()) {
                punishService.getPunishes().stream().filter(punish -> {
                    try {

                        BungeeCord.getInstance().getConsole().sendMessage("[MUTES]" + player + " está no meu banco de dados de mutes!");
                        return punish.getPlayerName().equalsIgnoreCase(resultSet2.getString("playerName"));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    public Stream<PunishSpigot> isMutedSpigot(String player) {
        return punishService2.getPunishes().stream().filter(punish -> punish.getPlayerName().equals(player)).filter(punish -> punish.getReason().getPunishType() == PunishType.TEMPMUTE || punish.getReason().getPunishType() == PunishType.MUTE).filter(PunishSpigot::isLocked);
    }
}
