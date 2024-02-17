package dev.wplugins.waze.gerementions.listeners;

import dev.wplugins.waze.gerementions.BukkitMain;
import dev.wplugins.waze.gerementions.Main;
import dev.wplugins.waze.gerementions.database.MySQLDatabase;
import dev.wplugins.waze.gerementions.enums.reason.Reason;
import dev.wplugins.waze.gerementions.punish.Punish;
import dev.wplugins.waze.gerementions.punish.dao.PunishDao;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class BukkitListener implements Listener {
    private PunishDao punishDao;
    public Statement statement2;
    public Statement statement3;
    public ResultSet resultSet2;
    public ResultSet resultSet3;

    public BukkitListener() {
        punishDao = BukkitMain.getPlugin().getPunishDao();
    }

    SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm");
    SimpleDateFormat SDF2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @EventHandler
    public void login(PlayerLoginEvent event) {
        String name = event.getPlayer().getName();
        Bukkit.getScheduler().runTask((Plugin) BukkitMain.getPlugin(), new Runnable() {
            public void run() {
                try {

                    statement3 = MySQLDatabase.getInstance().getConnection().createStatement();
                    resultSet3 = statement3.executeQuery("SELECT * FROM wPunish WHERE playerName='" + name + "'");
                    statement2 = MySQLDatabase.getInstance().getConnection().createStatement();
                    resultSet2 = statement2.executeQuery("SELECT * FROM wPunish WHERE playerName='" + name + "' AND (type='BAN' OR type='Banimento temporário' OR type='TEMPBAN')");
                    Bukkit.getConsoleSender().sendMessage(name + " está entrando na rede!");
                    BukkitMain.getPlugin().getLogger().log(Level.FINE, name + " está entrando na rede!");
                    if (resultSet2.next() && resultSet3.next()) {

                        Reason r = Reason.valueOf(resultSet3.getString("reason"));
                        BukkitMain.getPlugin().getLogger().info(name + " está banido do servidor até " + SDF.format(System.currentTimeMillis() + (resultSet2.getLong("expires"))));
                        String proof = (resultSet2.getString("proof") == null ? "Indisponível" : resultSet2.getString("proof"));
                        String message = "\n[BANIMENTO] \n§cVocê está banido do servidor. \n§cStaffer que te baniu: §e" + resultSet2.getString("stafferName") + "\n§cMotivo: §e" + r.getText() + "\n§cExpira em: §7" + (resultSet2.getLong("expires") == 0 ? "Nunca" : SDF2.format(System.currentTimeMillis() + (resultSet2.getLong("expires"))));

                        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, message);
                        event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                        event.getPlayer().kickPlayer(message);
                    }
resultSet3.close();
                    resultSet2.close();
                } catch (Exception exception) {

                    BukkitMain.getPlugin().getLogger().log(Level.SEVERE, "UM ERRO OCCOREU");
                    exception.printStackTrace();
                    BukkitMain.getPlugin().getLogger().log(Level.SEVERE, "UM ERRO OCCOREU");
                }

            }
        });
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent event) {

        List<String> commands = Arrays.asList("/tell", "/g", "/r", "/c", "/lobby", "/p", "/s");

        String message = event.getMessage();
        if (event.getMessage().startsWith("/")) {
            if (commands.stream().noneMatch(s -> message.startsWith(s) || message.startsWith(s.toUpperCase()) || message.equalsIgnoreCase(s))) {
                event.setCancelled(false);
                return;
            }
            if (message.startsWith("/report") || message.startsWith("/s") || message.startsWith("/c") || message.startsWith("/reportar") ||
                    message.equalsIgnoreCase("/lobby") ||
                    message.startsWith("/logar") || message.startsWith("/login") ||
                    message.startsWith("/registrar") || message.startsWith("/register") || message.equalsIgnoreCase("/rejoin") ||
                    message.equalsIgnoreCase("/reentrar") || message.equalsIgnoreCase("/leave") || message.equalsIgnoreCase("/loja") || message.equalsIgnoreCase("/party aceitar")) {
                event.setCancelled(false);
                return;
            }
            Player player = event.getPlayer();
            BukkitMain.getPlugin().getLogger().log(Level.FINE, event.getPlayer().getName() + " -> " + event.getMessage());
            Bukkit.getConsoleSender().sendMessage(player.getName() + " ESTÁ FALANDO NO CHAT");
            {
                try {
                    statement2 = MySQLDatabase.getInstance().getConnection().createStatement();
                    resultSet2 = statement2.executeQuery("SELECT * FROM wPunish WHERE playerName='" + player.getName() + "' AND (type='MUTE' OR type='Mute temporário' OR type='TEMPMUTE')");

                    if (resultSet2.next()) {

                        Bukkit.getConsoleSender().sendMessage(player.getName() + " ESTÁ mutado via spigot/bukkit");
                        event.setMessage(ChatColor.RED + "[MUTE] Você está mutado! \n§cStaffer que te mutou: §e" + resultSet2.getString("stafferName") + ChatColor.RED + "\n§cExpira em: §7" + (resultSet2.getLong("expires") == 0 ? "Nunca" : SDF2.format(System.currentTimeMillis() + (resultSet2.getLong("expires")))));
                        event.setCancelled(true);
                        {
                            resultSet2.close();


                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                ;
            }

        }

    }
}











