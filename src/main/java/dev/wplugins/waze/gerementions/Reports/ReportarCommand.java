package dev.wplugins.waze.gerementions.Reports;

import dev.wplugins.waze.gerementions.Main;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class ReportarCommand extends Commands {

    public ReportarCommand() {
        super("reportar", "report");
    }
    private static List<ProxiedPlayer> cooldown;
    //private static PunishDao punishDao;

    @Override
    public void perform(org.bukkit.command.CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(String.valueOf(TextComponent.fromLegacyText("§cApenas jogadores podem utilizar este comando.")));
            return;
        }


        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length < 1) {
            player.sendMessage(TextComponent.fromLegacyText("§cUtilize /reportar <jogador> para reportar um hacker"));
            return;
        }
        if (args.length == 1) {
            String targetName = args[0];
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
            if (ProxyServer.getInstance().getPlayer(targetName) == null)
                if (player == null || !player.isConnected()) {
                    player.sendMessage(TextComponent.fromLegacyText("§cEste jogador não encontra-se online na rede."));
                    return;
                }
            if (targetName.equals(player.getName())) {
                player.sendMessage(TextComponent.fromLegacyText("§cVocê não pode se denunciar."));
                return;
            }
            if (impossibleToBan(targetName)) {
                player.sendMessage(TextComponent.fromLegacyText("§cUtilize o /reportar somente para denúncia de hackers."));
                return;
            }
            if (cooldown.contains(player)) {
                player.sendMessage(TextComponent.fromLegacyText("§cVocê deve aguardar " + cooldown + "s para denunciar novamente."));
                return;
            }
            if (target == null) {
                player.sendMessage(TextComponent.fromLegacyText("§cEste jogador não encontra-se online na rede."));
                return;
            }

            Main.getInstance().getReportDao().createReport(player.getName(), target.getName(), target.getServer().getInfo());
            ProxyServer.getInstance().getPlayers().stream().filter(op -> op.hasPermission("wpunish.reports")).forEach(op -> {
                TextComponent text = new TextComponent("§eChegou uma nova denúncia para ser analisada. São §b" +/* + (int) Bungee.getInstance().getReportDao().getReportService().getReports().stream().filter(report -> ProxyServer.getInstance().getPlayer(report.getReportedPlayer()) != null).count() + */" §ecasos §epara análise.");

                op.sendMessage(text);
                TextComponent teleport = new TextComponent("§cClique §f§lAQUI §cpara se teleportar até o §cservidor do §cjogador.");
                teleport.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + target.getServer().getInfo().getName()));


                op.sendMessage(TextComponent.fromLegacyText(" "));
                op.sendMessage(TextComponent.fromLegacyText("§c§lREPORT NOVO ENVIADO" +
                        "\n§cUsuário denunciado: §f" + target.getName() +
                        "\n§cDenunciado por: §f" + player.getName() +
                        "\n§cServidor: §f" + Main.getInstance().getProxy().getPlayer(target.getName()).getServer().getInfo().getName()));
                op.sendMessage(teleport);
                op.sendMessage(TextComponent.fromLegacyText(" "));
            });

            player.sendMessage(TextComponent.fromLegacyText(" "));
            player.sendMessage(TextComponent.fromLegacyText("§a* Você reportou o jogador " + target.getName()) +"§a. Um membro de nossa equipe §afoi notificado e o " +
                    "comportamento deste jogador §aserá analisado em breve.\n\n §a* O uso abusivo deste comando poderá §aresultar em punição.");
            player.sendMessage(TextComponent.fromLegacyText(" "));
            cooldown.add(player);
            ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), () -> cooldown.remove(player), 2, TimeUnit.SECONDS);
        }
    }

    static {
        cooldown = new ArrayList<>();
    }

    private static boolean impossibleToBan(String nickName) {
        return Stream.of(Main.getInstance().getConfig().getStringList("NicksAntiBan")).anyMatch(s -> s.contains(nickName));
    }



}