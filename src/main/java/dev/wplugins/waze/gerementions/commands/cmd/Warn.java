package dev.wplugins.waze.gerementions.commands.cmd;


import java.awt.*;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;

import dev.wplugins.waze.gerementions.BukkitMain;
import dev.wplugins.waze.gerementions.Main;
import dev.wplugins.waze.gerementions.database.Database;
import dev.wplugins.waze.gerementions.enums.punish.PunishType;
import dev.wplugins.waze.gerementions.enums.reason.Reason;
import dev.wplugins.waze.gerementions.enums.reason.ReasonSpigot;
import dev.wplugins.waze.gerementions.punish.Punish;
import dev.wplugins.waze.gerementions.punish.dao.PunishDao;
import dev.wplugins.waze.gerementions.thread.PunishThread;
import dev.wplugins.waze.gerementions.util.Util;
import dev.wplugins.waze.gerementions.util.Webhook;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class Warn implements CommandExecutor {
    public static HashMap<String, ItemStack[]> saveinv = (HashMap) new HashMap<>();


    public static HashMap<String, ItemStack[]> armadura = (HashMap) new HashMap<>();
    private static PunishDao punishDao;
    private static PunishThread thread;
    int sequence = 0;

    static {

        PunishThread thread1;

        punishDao = new PunishDao();
        try {
            Class.forName("org.bukkit.Bukkit");
            thread1 = BukkitMain.getPlugin().getPunishThread();
        } catch (ClassNotFoundException e1) {
            thread1 = Main.getInstance().getPunishThread();
        }

        thread = thread1;
    }

    public Warn(BukkitMain main) {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String Label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("warn")) {
            if (!sender.hasPermission("wpunish.punir")) {
                sender.sendMessage(ChatColor.RED + "Você não tem permissão.");
                return true;
            }
            if (args.length > 0) {

                final Player testando = Bukkit.getPlayer(args[0]);
                Random r = new Random();


                if (testando != null) {
                    Bukkit.broadcastMessage(ChatColor.RED + args[0] + " foi advertido por um membro da equipe!");
                    testando.sendMessage("§b§kERFG" + ChatColor.YELLOW + "AVISO FORMAL SOBRE MAL COMPORTAMENTO §b§kERFG");

                    testando.sendMessage(ChatColor.RED + "Você foi advertido por um membro da nossa equipe por mau comportamento!");

                    testando.sendMessage(ChatColor.RED + "Você violou nossas regras da comunidade");

                    testando.sendMessage(ChatColor.RED + "Pare agora o que está fazendo, caso contrário estará sujeito a ser banido!");
                    testando.sendMessage(ChatColor.WHITE + "Você foi avisado pelo membro da equipe: " + ChatColor.AQUA + sender.getName());
                }
            } else {
                sender.sendMessage(ChatColor.RED + "O jogador precisa estar online e no mesmo subserver que o seu!");
                return true;
            }
                Webhook webhook = new Webhook("https://discord.com/api/webhooks/1228785436051378216/Z4VJ9Qamxbhaxr8OnKLevbxrfX1tmvQNOdNavDkaqkJt1Yw-uy4nsjA03S4Rxa_bbQQs");
                webhook.addEmbed(
                        new Webhook.EmbedObject()
                                .setDescription("Um usuário foi advertido.")
                                .setThumbnail("https://mc-heads.net/avatar/" + args[0] + "/500")
                                .setColor(Color.decode("#00A8FF"))
                                .addField("Usuário:", args[0], true)
                                .addField("Staffer:", sender.getName(), true)
                                .addField("Duração:", "Permanente", false)
                                .addField("Tipo:", "Aviso", true)
                                .addField("Expira em:", "Nunca", true)
                                .addField("Provas:", "Nenhuma", true)
                );
                try {
                    webhook.execute();
                } catch (IOException e) {
                    BukkitMain.getInstance().getLogger().severe(e.getStackTrace().toString());

                }
            } else {
                sender.sendMessage("Utilize /warn <Nick>");
                return true;
            }

        return false;
        }
    }

