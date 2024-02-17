package dev.wplugins.waze.gerementions.commands.cmd;


import dev.wplugins.waze.gerementions.Main;
import dev.wplugins.waze.gerementions.commands.Commands;
import dev.wplugins.waze.gerementions.punish.Punish;
import dev.wplugins.waze.gerementions.punish.dao.PunishDao;
import dev.wplugins.waze.gerementions.util.Webhook;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.stream.Stream;

public class KickCommand extends Commands {

    public KickCommand() {
        super("kick", "expulsar");
    }

    private static PunishDao punishDao;
    private static Punish punish;
    private String webhookURL = "https://discord.com/api/webhooks/1181247723660394546/jif9m6zF5-Gylit7op4vZGX2CyEP0JiBkCKxwOOVjrvv4_JtRZsjPXUjzly6Ffi94KDg";
    SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @Override
    public void perform(CommandSender sender, String[] args) {
        //ProxiedPlayer player = (ProxiedPlayer) (sender);
        if (!sender.hasPermission("wpunish.kick")) {
            sender.sendMessage(TextComponent.fromLegacyText("§cVocê não tem permissão para isto."));
            return;
        }
        if (args.length == 0) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUtilize \"/kick <player>\"."));
            return;
        }
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(TextComponent.fromLegacyText("§cO jogador " + args[0] + "§c não encontra-se presente."));
            return;
        }

        if (target.hasPermission("wpunish.ignorar") && !sender.hasPermission("wpunish.ignorar.bypass") && target != null) {
            sender.sendMessage(TextComponent.fromLegacyText("§cEsse jogador tem um nível de permissão maior que o seu."));
            return;
        }
        if (impossibleToBan(target.getName())) {
            sender.sendMessage(TextComponent.fromLegacyText("§cVocê não pode kickar este jogador."));
            return;
        }
        if (sender.hasPermission("wpunish.admin") || !sender.hasPermission("wpunish.moderador")) {
            sender.sendMessage(TextComponent.fromLegacyText("§cVocê expulsou o jogador " + target.getName() + "§c."));
            target.disconnect(TextComponent.fromLegacyText(Main.getInstance().getConfig().getString("Prefix").replace("&", "§") +  "\n\n§cVocê foi desconectado do servidor por " + sender + "."));
            String textString = "§c* " + target.getName() + " §cfoi expulso por §c" + sender;
            TextComponent text = new TextComponent(textString);
            Webhook webhook = new Webhook(webhookURL);
            webhook.addEmbed(
                    new Webhook.EmbedObject()
                            .setDescription("Um usuário foi expulso do servidor.")
                            .setThumbnail("https://mc-heads.net/avatar/" + target + "/500")
                            .setColor(Color.decode("#00A8FF"))
                            .addField("Usuário:", target.getName(), true)
                            .addField("Aplicador:", sender.getName(), true)
            );
            try {
                webhook.execute();
            } catch (IOException e) {
                Main.getInstance().getLogger().severe(e.getStackTrace().toString());
            }
            ProxyServer.getInstance().getPlayers().stream().filter(o -> o.hasPermission("wpunish.veralerta")).forEach(o -> {
                o.sendMessage(TextComponent.fromLegacyText(" "));
                o.sendMessage(text);
                o.sendMessage(TextComponent.fromLegacyText(" "));
            });
            if (target != null) {
                target.sendMessage(TextComponent.fromLegacyText(" "));
                target.sendMessage(text);
                target.sendMessage(TextComponent.fromLegacyText(" "));

            }
        } else if (sender.hasPermission("wpunish.moderador") || !sender.hasPermission("wpunish.ajudante")) {
            sender.sendMessage(TextComponent.fromLegacyText("§eExpulsão aplicada com sucesso."));
            target.disconnect(TextComponent.fromLegacyText(Main.getInstance().getConfig().getString("Prefix").replace("&", "§") + "\n\n§cVocê foi expulso da rede\n" +
                    "\n§cAutor: " + sender +
                    "\n\n§cAcha que a punição foi aplicada injustamente?\n§cFaça uma revisão em nosso forum" + Main.getInstance().getConfig().getString("AppealSite").replace("&", "§")));
            String textString = "§c* " + target.getName() + " §cfoi expulso por §c" + sender;
            TextComponent text = new TextComponent(textString);

            ProxyServer.getInstance().getPlayers().stream().filter(o -> o.hasPermission("wpunish.veralerta")).forEach(o -> {
                o.sendMessage(TextComponent.fromLegacyText(" "));
                o.sendMessage(text);
                o.sendMessage(TextComponent.fromLegacyText(" "));
            });
            Webhook webhook = new Webhook(webhookURL);
            webhook.addEmbed(
                    new Webhook.EmbedObject()
                            .setDescription("Um usuário foi punido do servidor.")
                            .setThumbnail("https://mc-heads.net/avatar/" + target + "/500")
                            .setColor(Color.decode("#00A8FF"))
                            .addField("Usuário:", target.getName(), true)
                            .addField("Staff:", sender.getName(), true)
            );
            try {
                webhook.execute();
            } catch (IOException e) {
                Main.getInstance().getLogger().severe(e.getStackTrace().toString());
            }
            return;
        }
    }

    private static boolean impossibleToBan(String nickName) {
        return Stream.of(Main.getInstance().getConfig().getStringList("NicksAntiBan")).anyMatch(s -> s.contains(nickName));
    }

    static {
        punishDao = Main.getInstance().getPunishDao();
    }
}


