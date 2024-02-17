package dev.wplugins.waze.gerementions.commands.cmd;


import dev.wplugins.waze.gerementions.Main;
import dev.wplugins.waze.gerementions.commands.Commands;
import dev.wplugins.waze.gerementions.database.MySQLDatabase;
import dev.wplugins.waze.gerementions.enums.punish.PunishType;
import dev.wplugins.waze.gerementions.enums.reason.Reason;
import dev.wplugins.waze.gerementions.punish.Punish;
import dev.wplugins.waze.gerementions.punish.dao.PunishDao;
import dev.wplugins.waze.gerementions.util.Util;
import dev.wplugins.waze.gerementions.util.Webhook;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.awt.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.stream.Stream;

public class BanCommand extends Commands {
    int sequence = 0;
    public BanCommand() {
        super("ban", "vban", "banip", "ban-ip");
    }

    public static Punish punish;

    //private String webhookURL = "https://discord.com/api/webhooks/1181247723660394546/jif9m6zF5-Gylit7op4vZGX2CyEP0JiBkCKxwOOVjrvv4_JtRZsjPXUjzly6Ffi94KDg";
    private String webhookURL = Main.getInstance().getConfig().getString("WebHookURL");
    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("wpunish.ban")) {
            sender.sendMessage(TextComponent.fromLegacyText("§cVocê não tem autorização para isto."));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUtilize \"/ban <user> [motivo]"));
            return;
        }
        String target = args[0];
        if (impossibleToBan(target)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cVocê não pode banir esse usuário"));
            return;
        }
        ProxiedPlayer target2 = BungeeCord.getInstance().getPlayer(target);
        if (BungeeCord.getInstance().getPlayer(target) != null) {
            if (target2.hasPermission("wpunish.ignorar") && !sender.hasPermission("wpunish.ignorar.bypass") && target2 != null) {
                sender.sendMessage(TextComponent.fromLegacyText("§cEsse jogador tem um nível de permissão maior que o seu."));
                return;
            }
        }

        ProxyServer.getInstance().getPlayers().stream().filter(player -> player.hasPermission("wpunish.veralerta")).forEach(player -> {
            player.sendMessage(TextComponent.fromLegacyText("§c- " + args[0] + " §cfoi banido por " + sender.getName() +
                    "\n§c- Motivo: " + args[1] +
                    "\n§c- Duração: Permanente\n"));
        });
        PunishDao punish = new PunishDao();

        Statement statement3 = null;
        try {
            statement3 = MySQLDatabase.getInstance().getConnection().createStatement();

            ResultSet resultSet3 = statement3.executeQuery("SELECT * FROM wPunish WHERE playerName='" + args[0] + "'");
            apply(punish.createPunish(target, sender.getName(), Reason.VIOLACAO_DAS_DIRETRIZES, null, PunishType.BAN.name(), sequence++, ProxyServer.getInstance().getPlayer(target) != null ? ProxyServer.getInstance().getPlayer(target).getAddress().getAddress().toString() : "NULO"), ProxyServer.getInstance().getPlayer(target), sender.getName());
            Webhook webhook = new Webhook(webhookURL);
            webhook.addEmbed(
                    new Webhook.EmbedObject()
                            .setDescription("Um usuário foi punido do servidor.")
                            .setThumbnail("https://mc-heads.net/avatar/" + target + "/500")
                            .setColor(Color.decode("#00A8FF"))
                            .addField("Usuário:", target, true)
                            .addField("Motivo:", args[1], true)
                            .addField("Duração:", "Eterna", false)
                            .addField("Tipo:", "Banimento", true)
                            .addField("Expira em:", "Nunca", true)
                            .addField("Provas:", "Nenhuma", true)
            );
            try {
                webhook.execute();
            } catch (IOException e) {
                BungeeCord.getInstance().getConsole().sendMessage(e.getLocalizedMessage());
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static void apply(Punish punish, ProxiedPlayer target, String staffer) {
        final String textString;
        final Reason reason = punish.getReason();
        final String proof = (punish.getProof() == null ? "Nenhuma" : punish.getProof());
        String webhookURL = "https://discord.com/api/webhooks/1181247723660394546/jif9m6zF5-Gylit7op4vZGX2CyEP0JiBkCKxwOOVjrvv4_JtRZsjPXUjzly6Ffi94KDg";
        switch (reason.getPunishType()) {
            case BAN:
                textString = "§c* " + punish.getPlayerName() + " §cfoi banido para sempre";

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

        ProxyServer.getInstance().getPlayers().stream().filter(o -> o.hasPermission("wpunish.veralerta")).forEach(o -> {
            o.sendMessage(TextComponent.fromLegacyText(" "));
            o.sendMessage(text);
            o.sendMessage(TextComponent.fromLegacyText(" "));
        });
        SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        if (target != null) {
            target.sendMessage(TextComponent.fromLegacyText(" "));
            target.sendMessage(text);
            target.sendMessage(TextComponent.fromLegacyText(" "));

            if (reason.getPunishType() == PunishType.TEMPBAN) {
                target.disconnect(TextComponent.fromLegacyText(Main.getInstance().getConfig().getString("Prefix").replace("&", "§") + "\n\n§cVocê foi banido da rede\n" +
                        "\n§cMotivo: " + reason.getText() + " - " + proof +
                        "\n§cDuração: " + Util.fromLong(punish.getExpire()) +
                        "\n§cID da punição: §e#" + punish.getId() +
                        "\n\n§cAcha que a punição foi aplicada injustamente?\n§cFaça uma revisão em" + Main.getInstance().getConfig().getString("AppealSite").replace("&", "§")));
                return;
            }
            if (reason.getPunishType() == PunishType.BAN) {
                target.disconnect(TextComponent.fromLegacyText(Main.getInstance().getConfig().getString("Prefix").replace("&", "§") + "\n\n§cVocê foi banido da rede\n" +
                        "\n§cMotivo: " + reason.getText() + " - " + proof +
                        "\n§cDuração: Permanente" +
                        "\n§cID da punição: §e#" + punish.getId() +
                        "\n\n§cAcha que a punição foi aplicada injustamente?\n§cFaça uma revisão em" + Main.getInstance().getConfig().getString("AppealSite").replace("&", "§")));
            }

        }

    }
    private static boolean impossibleToBan(String nickName) {
        return Stream.of(Main.getInstance().getConfig().getStringList("NicksAntiBan")).anyMatch(s -> s.contains(nickName));
    }
}
