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


public class HackBan implements CommandExecutor, Listener {
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

    public HackBan(BukkitMain main) {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String Label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("hackban")) {
            if (!sender.hasPermission("*")) {
                sender.sendMessage(ChatColor.RED + "Você não tem permissão.");
                return true;
            }
            if (args.length > 0) {

                final Player testando = Bukkit.getPlayer(args[0]);
                Random r = new Random();


                    Database.getInstance().execute("INSERT INTO `wPunish` VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", UUID.randomUUID().toString().substring(0, 6), testando == null ? args[0] : testando.getName(), "CONSOLE", "HACK", "BAN", "Pego pelo AntiCheat", new Date().getTime(), 0, 54, "NULO");
                Database.getInstance().execute("INSERT INTO `wPunish2` VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", UUID.randomUUID().toString().substring(0, 6), testando == null ? args[0] : testando.getName(), "CONSOLE", "HACK", "BAN", "Pego pelo AntiCheat", new Date().getTime(), 0, 54, "NULO");

                if (testando != null) {
                    testando.kickPlayer("Você foi banido por usar trapaças");
                }

                Bukkit.broadcastMessage(ChatColor.RED + args[0] + " foi banido do servidor!");
                Webhook webhook = new Webhook("https://discord.com/api/webhooks/1228785436051378216/Z4VJ9Qamxbhaxr8OnKLevbxrfX1tmvQNOdNavDkaqkJt1Yw-uy4nsjA03S4Rxa_bbQQs");
                webhook.addEmbed(
                        new Webhook.EmbedObject()
                                .setDescription("Um usuário foi punido do servidor.")
                                .setThumbnail("https://mc-heads.net/avatar/" + args[0] + "/500")
                                .setColor(Color.decode("#00A8FF"))
                                .addField("Usuário:", args[0], true)
                                .addField("Motivo:", "HACK (AntiCheat)", true)
                                .addField("Staffer:", sender.getName(), true)
                                .addField("Duração:", "Permanente", false)
                                .addField("Tipo:", "Banimento permanente", true)
                                .addField("Expira em:", "Nunca", true)
                                .addField("Provas:", "Nenhuma", true)
                );
                try {
                    webhook.execute();
                } catch (IOException e) {
                    BukkitMain.getInstance().getLogger().severe(e.getStackTrace().toString());

                }
            } else {
                sender.sendMessage("Utilize /hackban <Nick>");
                return true;
            }

            return false;
        }
        return false;
    }

    private static void applyp(Punish punish, Player target, String staffer){
            final String textString;
            final Reason reason = punish.getReason();
            final String proof = (punish.getProof() == null ? "Nenhuma" : punish.getProof());

            switch (reason.getPunishType()) {
                case BAN:
                    textString = "§c* " + punish.getPlayerName() + " §cfoi banido." +
                            "\n§c* Motivo: " + reason.getText() + " (Permanente)";

                    break;
                case MUTE:
                    textString = "§c* " + punish.getPlayerName() + " §cfoi silenciado por " + staffer +
                            "\n§c* Motivo: " + reason.getText() + " - " + proof +
                            "\n§c* Duração: Permanente\n";
                    break;
                case TEMPBAN:
                    textString = "§c* " + punish.getPlayerName() + " §cfoi banido." +
                            "\n§c* Motivo: " + reason.getText() + " (" + Util.fromLong(punish.getExpire()) + ")";

                    break;
                case TEMPMUTE:
                    textString = "\n§c* " + punish.getPlayerName() + " §cfoi silenciado por " + staffer +
                            "\n§c* Motivo: " + reason.getText() + " - " + proof +
                            "\n§c* Duração: " + Util.fromLong(punish.getExpire());
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + reason.getPunishType());
            }
            final TextComponent text = new TextComponent(textString);
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/checkpunir " + punish.getPlayerName()));
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§fClique para mais informações.")));
for (Player o : Bukkit.getServer().getOnlinePlayers()) {
    o.sendMessage(String.valueOf(TextComponent.fromLegacyText(" ")));
    o.sendMessage(String.valueOf(text));
    o.sendMessage(String.valueOf(TextComponent.fromLegacyText(" ")));
}
            if (target != null) {
                target.sendMessage(String.valueOf(TextComponent.fromLegacyText(" ")));
                target.sendMessage(String.valueOf(text));
                target.sendMessage(String.valueOf(TextComponent.fromLegacyText(" ")));

                if (reason.getPunishType() == PunishType.TEMPBAN) {
                    target.kickPlayer(String.valueOf(TextComponent.fromLegacyText(Main.getInstance().getConfig().getString("Prefix").replace("&", "§") + "\n\n§cVocê foi banido da rede\n" +
                            "\n§cMotivo: " + reason.getText() + " - " + proof +
                            "\n§cDuração: " + Util.fromLong(punish.getExpire()) +
                            "\n§cID da punição: §e#" + punish.getId() +
                            "\n\n§cAcha que a punição foi aplicada injustamente?\n§cFaça uma revisão em" + Main.getInstance().getConfig().getString("AppealSite").replace("&", "§"))));
                    return;
                }
                if (reason.getPunishType() == PunishType.BAN) {
                    target.kickPlayer(String.valueOf(TextComponent.fromLegacyText(Main.getInstance().getConfig().getString("Prefix").replace("&", "§") + "\n\n§cVocê foi banido da rede\n" +
                            "\n§cMotivo: " + reason.getText() + " - " + proof +
                            "\n§cDuração: Permanente" +
                            "\n§cID da punição: §e#" + punish.getId() +
                            "\n\n§cAcha que a punição foi aplicada injustamente?\n§cFaça uma revisão em" + Main.getInstance().getConfig().getString("AppealSite").replace("&", "§"))));
                }
            }
        }}