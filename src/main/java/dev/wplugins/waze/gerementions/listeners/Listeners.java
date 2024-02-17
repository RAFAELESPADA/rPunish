package dev.wplugins.waze.gerementions.listeners;

import dev.wplugins.waze.gerementions.BukkitMain;
import dev.wplugins.waze.gerementions.Main;
import dev.wplugins.waze.gerementions.database.MySQLDatabase;
import dev.wplugins.waze.gerementions.enums.reason.Reason;
import dev.wplugins.waze.gerementions.punish.dao.PunishDao;
import lombok.SneakyThrows;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class Listeners implements Listener {

    private PunishDao punishDao;

    public Listeners() {
        punishDao = Main.getInstance().getPunishDao();
    }

    SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm");
    SimpleDateFormat SDF2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @EventHandler
    public void login(PreLoginEvent event) {
        String name = event.getConnection().getName();

        InetSocketAddress ip = event.getConnection().getAddress();
        BungeeCord.getInstance().getConsole().sendMessage("Jogador " + name + " está entrando (PRELOGIN)...");


        Main.getInstance().getLogger().log(Level.FINE
                , "Jogador " + name + " está entrando...");
        BungeeCord.getInstance().getScheduler().schedule(Main.getInstance(), new Runnable() {
            public void run() {

                {
                    try {

                        Statement statement3 = MySQLDatabase.getInstance().getConnection().createStatement();
                        ResultSet resultSet3 = statement3.executeQuery("SELECT * FROM wPunish WHERE playerName='" + name + "'");


                        Statement statement4 = MySQLDatabase.getInstance().getConnection().createStatement();


                        Statement statement2 = MySQLDatabase.getInstance().getConnection().createStatement();
                        ResultSet resultSet2 = statement2.executeQuery("SELECT * FROM wPunish WHERE playerName='" + name + "' AND (type='BAN' OR type='TEMPBAN' OR type='Banimento temporário')");
                        ResultSet resultSetIP = statement4.executeQuery("SELECT * FROM wPunish WHERE ip='" + ip.getAddress() + "'");

                        if ((resultSet2.next() && resultSet3.next())) {
                            Reason r = Reason.valueOf(resultSet3.getString("reason"));
                            event.setCancelled(true);
                            BungeeCord.getInstance().getConsole().sendMessage("Jogador " + name + " (" + ip.getAddress() + ") tentou entrar mas está banido");

                            String proof = (resultSet2.getString("proof") == null ? "Indisponível" : resultSet2.getString("proof"));
                            event.setCancelReason(TextComponent.fromLegacyText(Main.getInstance().getConfig().getString("Prefix").replace("&", "§") + "\n\n§cVocê está banido do servidor.\n" +
                                    "\n§cMotivo: " + r.getText() + " - " + proof +
                                    "\n§cAutor da punição: §7" + resultSet2.getString("stafferName") + "\n§cExpira em: §7" + (resultSet2.getLong("expires") == 0 ? "Nunca" : SDF2.format(resultSet2.getLong("expires")) +
                                    "\n§cID da punição: §e#" + resultSet2.getString("id") +
                                    "\n\n§cUse o ID §e#" + resultSet2.getString("id") + " §cpara criar uma revisão em " + Main.getInstance().getConfig().getString("AppealSite").replace("&", "§"))));
                            event.getConnection().disconnect(TextComponent.fromLegacyText(Main.getInstance().getConfig().getString("Prefix").replace("&", "§") + "\n\n§cVocê está banido do servidor.\n" +
                                    "\n§cMotivo: " + r.getText() + " - " + proof +
                                    "\n§cAutor da punição: §7" + resultSet2.getString("stafferName") + "\n§cExpira em: §7" + (resultSet2.getLong("expires") == 0 ? "Nunca" : SDF2.format(resultSet2.getLong("expires")) +
                                    "\n§cID da punição: §e#" + resultSet2.getString("id") +
                                    "\n\n§cUse o ID §e#" + resultSet2.getString("id") + " §cpara criar uma revisão em " + Main.getInstance().getConfig().getString("AppealSite").replace("&", "§"))));
                            return;
                        } else if (resultSetIP.next()) {

                            Reason r = Reason.valueOf(resultSetIP.getString("reason"));
                            event.setCancelled(true);
                            BungeeCord.getInstance().getConsole().sendMessage("Jogador " + name + " (" + ip.getAddress() + ") tentou entrar mas está banido");
                            String proof = (resultSetIP.getString("proof") == null ? "Indisponível" : resultSetIP.getString("proof"));
                            event.setCancelReason(TextComponent.fromLegacyText(Main.getInstance().getConfig().getString("Prefix").replace("&", "§") + "\n\n§eSeu IP está banido do servidor.\n" +
                                    "\n§cMotivo: " + r.getText() + " - " + proof +
                                    "\n§cAutor da punição: §7" + resultSetIP.getString("stafferName") + "\n§cExpira em: §7" + (resultSetIP.getLong("expires") == 0 ? "Nunca" : SDF2.format(resultSetIP.getLong("expires")) +
                                    "\n§cID da punição: §e#" + resultSetIP.getString("id") +
                                    "\n\n§cUse o ID §e#" + resultSetIP.getString("id") + " §cpara criar uma revisão em " + Main.getInstance().getConfig().getString("AppealSite").replace("&", "§"))));
                            event.getConnection().disconnect(TextComponent.fromLegacyText(Main.getInstance().getConfig().getString("Prefix").replace("&", "§") + "\n\n§eSeu IP está banido do servidor.\n" +
                                    "\n§cMotivo: " + r.getText() + " - " + proof +
                                    "\n§cAutor da punição: §7" + resultSetIP.getString("stafferName") + "\n§cExpira em: §7" + (resultSetIP.getLong("expires") == 0 ? "Nunca" : SDF2.format(resultSetIP.getLong("expires")) +
                                    "\n§cID da punição: §e#" + resultSetIP.getString("id") +
                                    "\n\n§cUse o ID §e#" + resultSetIP.getString("id") + " §cpara criar uma revisão em " + Main.getInstance().getConfig().getString("AppealSite").replace("&", "§"))));

                        } else {
                            Main.getInstance().getLogger().log(Level.FINE
                                    , "Jogador " + name + " não está banido");
                            BungeeCord.getInstance().getConsole().sendMessage("Jogador " + name + " não está banido...");
                        }

                        resultSet3.close();
                        resultSetIP.close();
                        resultSet2.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }


                }
                ;
            }

            ;

        }, 1, TimeUnit.MILLISECONDS);
    }


    @EventHandler
    public void login(LoginEvent event) {
        String name = event.getConnection().getName();

        BungeeCord.getInstance().getConsole().sendMessage("Jogador " + name + " está entrando (LOGIN)...");


        InetSocketAddress ip = event.getConnection().getAddress();
        Main.getInstance().getLogger().log(Level.FINE
                , "Jogador " + name + " está entrando...");
        BungeeCord.getInstance().getScheduler().schedule(Main.getInstance(), new Runnable() {
            public void run() {

                {
                    try {


                        Statement statement2 = MySQLDatabase.getInstance().getConnection().createStatement();

                        Statement statement3 = MySQLDatabase.getInstance().getConnection().createStatement();
                        Statement statement4 = MySQLDatabase.getInstance().getConnection().createStatement();

                        ResultSet resultSet3 = statement3.executeQuery("SELECT * FROM wPunish WHERE playerName='" + name + "'");

                        ResultSet resultSetIP = statement4.executeQuery("SELECT * FROM wPunish WHERE ip='" + ip.getAddress() + "'");
                        ResultSet resultSet2 = statement2.executeQuery("SELECT * FROM wPunish WHERE playerName='" + name + "' AND (type='BAN' OR type='TEMPBAN' OR type='Banimento temporário')");

                        if ((resultSet2.next() && resultSet3.next())) {

                            Reason r = Reason.valueOf(resultSet3.getString("reason"));
                            event.setCancelled(true);
                            BungeeCord.getInstance().getConsole().sendMessage("Jogador " + name + " (" + ip.getAddress() + ") tentou entrar mas está banido");
                            String proof = (resultSet2.getString("proof") == null ? "Indisponível" : resultSet2.getString("proof"));
                            event.setCancelReason(TextComponent.fromLegacyText(Main.getInstance().getConfig().getString("Prefix").replace("&", "§") + "\n\n§cVocê está banido do servidor.\n" +
                                    "\n§cMotivo: " + r.getText() + " - " + proof +
                                    "\n§cAutor da punição: §7" + resultSet2.getString("stafferName") + "\n§cExpira em: §7" + (resultSet2.getLong("expires") == 0 ? "Nunca" : SDF2.format(resultSet2.getLong("expires")) +
                                    "\n§cID da punição: §e#" + resultSet2.getString("id") +
                                    "\n\n§cUse o ID §e#" + resultSet2.getString("id") + " §cpara criar uma revisão em " + Main.getInstance().getConfig().getString("AppealSite").replace("&", "§"))));
                            event.getConnection().disconnect(TextComponent.fromLegacyText(Main.getInstance().getConfig().getString("Prefix").replace("&", "§") + "\n\n§cVocê está banido do servidor.\n" +
                                    "\n§cMotivo: " + r.getText() + " - " + proof +
                                    "\n§cAutor da punição: §7" + resultSet2.getString("stafferName") + "\n§cExpira em: §7" + (resultSet2.getLong("expires") == 0 ? "Nunca" : SDF2.format(resultSet2.getLong("expires")) +
                                    "\n§cID da punição: §e#" + resultSet2.getString("id") +
                                    "\n\n§cUse o ID §e#" + resultSet2.getString("id") + " §cpara criar uma revisão em " + Main.getInstance().getConfig().getString("AppealSite").replace("&", "§"))));

                        } else if (resultSetIP.next()) {

                            Reason r = Reason.valueOf(resultSetIP.getString("reason"));
                            event.setCancelled(true);
                            BungeeCord.getInstance().getConsole().sendMessage("Jogador " + name + " (" + ip.getAddress() + ") tentou entrar mas está banido por IP");
                            String proof = (resultSetIP.getString("proof") == null ? "Indisponível" : resultSetIP.getString("proof"));
                            event.setCancelReason(TextComponent.fromLegacyText(Main.getInstance().getConfig().getString("Prefix").replace("&", "§") + "\n\n§eSeu IP está banido do servidor.\n" +
                                    "\n§cMotivo: " + r.getText() + " - " + proof +
                                    "\n§cAutor da punição: §7" + resultSetIP.getString("stafferName") + "\n§cExpira em: §7" + (resultSetIP.getLong("expires") == 0 ? "Nunca" : SDF2.format(resultSetIP.getLong("expires")) +
                                    "\n§cID da punição: §e#" + resultSetIP.getString("id") +
                                    "\n\n§cUse o ID §e#" + resultSetIP.getString("id") + " §cpara criar uma revisão em " + Main.getInstance().getConfig().getString("AppealSite").replace("&", "§"))));
                            event.getConnection().disconnect(TextComponent.fromLegacyText(Main.getInstance().getConfig().getString("Prefix").replace("&", "§") + "\n\n§eSeu IP está banido do servidor.\n" +
                                    "\n§cMotivo: " + r.getText() + " - " + proof +
                                    "\n§cAutor da punição: §7" + resultSetIP.getString("stafferName") + "\n§cExpira em: §7" + (resultSetIP.getLong("expires") == 0 ? "Nunca" : SDF2.format(resultSetIP.getLong("expires")) +
                                    "\n§cID da punição: §e#" + resultSetIP.getString("id") +
                                    "\n\n§cUse o ID §e#" + resultSetIP.getString("id") + " §cpara criar uma revisão em " + Main.getInstance().getConfig().getString("AppealSite").replace("&", "§"))));

                        } else {
                            Main.getInstance().getLogger().log(Level.FINE
                                    , "Jogador " + name + " não está banido");
                            BungeeCord.getInstance().getConsole().sendMessage("Jogador " + name + " não está banido...");
                        }

                        resultSet3.close();
                        resultSetIP.close();
                        resultSet2.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }


                }
            }

            ;

        }, 1, TimeUnit.MILLISECONDS);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @SneakyThrows
    public void login(ChatEvent event) {
        ProxiedPlayer name = (ProxiedPlayer) event.getSender();
if (event.isCancelled()) {
    return;
}



        InetSocketAddress ip = name.getAddress();
        Main.getInstance().getLogger().log(Level.FINE
                , "Jogador " + name + " digitou algo...");



                        Statement statement2 = MySQLDatabase.getInstance().getConnection().createStatement();

                        Statement statement3 = MySQLDatabase.getInstance().getConnection().createStatement();
                        Statement statement4 = MySQLDatabase.getInstance().getConnection().createStatement();

                        ResultSet resultSet3 = statement3.executeQuery("SELECT * FROM wPunish WHERE playerName='" + name + "'");

                        ResultSet resultSetIP = statement4.executeQuery("SELECT * FROM wPunish WHERE ip='" + ip.getAddress() + "'");
                        ResultSet resultSet2 = statement2.executeQuery("SELECT * FROM wPunish WHERE playerName='" + name + "' AND (type='MUTE' OR type='TEMPMUTE' OR type='Mute temporário')");

                        if ((resultSet2.next() && resultSet3.next())) {
if (event.getMessage().startsWith("/tell")) {
   {
        event.setCancelled(true);
        String proof = (resultSet2.getString("proof") == null ? "Indisponível" : resultSet2.getString("proof"));

        Reason r = Reason.valueOf(resultSet3.getString("reason"));
       BungeeCord.getInstance().getConsole().sendMessage("Jogador " + name + " (" + ip.getAddress() + ") tentou falar ( /tell ) mas está mutado");

       name.sendMessage(TextComponent.fromLegacyText(Main.getInstance().getConfig().getString("Prefix").replace("&", "§") + "\n\n§cVocê está silenciado na nossa rede.\n" +
                "\n§cMotivo: " + r.getText() + " - " + proof +
                "\n§cAutor da punição: §7" + resultSet2.getString("stafferName") + "\n§cExpira em: §7" + (resultSet2.getLong("expires") == 0 ? "Nunca" : SDF2.format(resultSet2.getLong("expires")) +
                "\n§cID da punição: §e#" + resultSet2.getString("id") +
                "\n\n§cUse o ID §e#" + resultSet2.getString("id") + " §cpara criar uma revisão em " + Main.getInstance().getConfig().getString("AppealSite").replace("&", "§"))));
    }
    return;
}
                            if (event.getMessage().startsWith("/g") && name.getServer().getInfo().getName().equalsIgnoreCase("rankup")) {
                                {
                                    event.setCancelled(true);
                                    String proof = (resultSet2.getString("proof") == null ? "Indisponível" : resultSet2.getString("proof"));
                                    BungeeCord.getInstance().getConsole().sendMessage("Jogador " + name + " (" + ip.getAddress() + ") tentou falar (/g) mas está mutado");

                                    Reason r = Reason.valueOf(resultSet3.getString("reason"));
                                    name.sendMessage(TextComponent.fromLegacyText(Main.getInstance().getConfig().getString("Prefix").replace("&", "§") + "\n\n§cVocê está silenciado na nossa rede.\n" +
                                            "\n§cMotivo: " + r.getText() + " - " + proof +
                                            "\n§cAutor da punição: §7" + resultSet2.getString("stafferName") + "\n§cExpira em: §7" + (resultSet2.getLong("expires") == 0 ? "Nunca" : SDF2.format(resultSet2.getLong("expires")) +
                                            "\n§cID da punição: §e#" + resultSet2.getString("id") +
                                            "\n\n§cUse o ID §e#" + resultSet2.getString("id") + " §cpara criar uma revisão em " + Main.getInstance().getConfig().getString("AppealSite").replace("&", "§"))));
                                }
                                return;
                            }
                            if (!event.isCommand()) {
                                Reason r = Reason.valueOf(resultSet3.getString("reason"));
                                BungeeCord.getInstance().getConsole().sendMessage("Jogador " + name + " (" + ip.getAddress() + ") tentou falar mas está mutado");
                                String proof = (resultSet2.getString("proof") == null ? "Indisponível" : resultSet2.getString("proof"));
                                name.sendMessage(TextComponent.fromLegacyText(Main.getInstance().getConfig().getString("Prefix").replace("&", "§") + "\n\n§cVocê está silenciado na nossa rede.\n" +
                                        "\n§cMotivo: " + r.getText() + " - " + proof +
                                        "\n§cAutor da punição: §7" + resultSet2.getString("stafferName") + "\n§cExpira em: §7" + (resultSet2.getLong("expires") == 0 ? "Nunca" : SDF2.format(resultSet2.getLong("expires")) +
                                        "\n§cID da punição: §e#" + resultSet2.getString("id") +
                                        "\n\n§cUse o ID §e#" + resultSet2.getString("id") + " §cpara criar uma revisão em " + Main.getInstance().getConfig().getString("AppealSite").replace("&", "§"))));
                                event.setCancelled(true);
                                return;
                            }
                        } else if (resultSetIP.next()) {
                            if (!event.isCommand()) {
                                Reason r = Reason.valueOf(resultSetIP.getString("reason"));

                                BungeeCord.getInstance().getConsole().sendMessage("Jogador " + name + " (" + ip.getAddress() + ") tentou falar mas está mutado por IP.");
                                String proof = (resultSetIP.getString("proof") == null ? "Indisponível" : resultSetIP.getString("proof"));
                                name.sendMessage(TextComponent.fromLegacyText("\n§c* Você está silenciado por IP " + (resultSet2.getLong("expires") > 0 ? "até o dia " + SDF.format(resultSet2.getLong("expires")) : "permanentemente") +
                                        "\n\n§c* Motivo: " + r.getText() + " - " + proof +
                                        "\n§c* Autor: " + resultSet2.getString("stafferName") +
                                        "\n§c* Use o ID §e#" + String.valueOf(resultSet2.getString("id")) + " §cpara criar uma revisão em " + Main.getInstance().getConfig().getString("AppealSite").replace("&", "§") +
                                        "\n"));
                                event.setCancelled(true);
                                return;
                            } else {
                                BungeeCord.getInstance().getConsole().sendMessage("Jogador " + name + " não está mutado...");
                            }
                        }
                        resultSet3.close();
                        resultSetIP.close();
                        resultSet2.close();
                    }




        }
































