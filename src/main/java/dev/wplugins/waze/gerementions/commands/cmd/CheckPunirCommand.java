package dev.wplugins.waze.gerementions.commands.cmd;

import dev.wplugins.waze.gerementions.Main;
import dev.wplugins.waze.gerementions.commands.Commands;
import dev.wplugins.waze.gerementions.database.MySQLDatabase;
import dev.wplugins.waze.gerementions.punish.Punish;
import dev.wplugins.waze.gerementions.punish.dao.PunishDao;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CheckPunirCommand extends Commands {
    public CheckPunirCommand() {
        super("despunir", "checkpunir");
    }

    private static PunishDao punishDao;
    private static Punish punish;
    SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm");
    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cApenas jogadores podem utilizar este comando."));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!player.hasPermission("wpunish.checkpunir")) {
            player.sendMessage(TextComponent.fromLegacyText("§cVocê não tem permissão para isto."));
            return;
        }

        if (args.length != 1) {
            player.sendMessage(TextComponent.fromLegacyText("§cUtilize /checkpunir <jogador>."));
            return;
        }
        String target = args[0];
        if (!sender.hasPermission("wpunish.admin")) {
            sender.sendMessage(TextComponent.fromLegacyText("\n§cVocê não tem permissão para despunir pessoas."));
        }
            try {

                Statement statement1 = MySQLDatabase.getInstance().getConnection().createStatement();
                ResultSet resultSet1 = statement1.executeQuery("SELECT * FROM wPunish WHERE playerName='" + args[0] + "'");
                Statement statement2 = MySQLDatabase.getInstance().getConnection().createStatement();
                ResultSet resultSet2 = statement2.executeQuery("SELECT * FROM wPunish2 WHERE playerName='" + args[0] + "'");

                if (resultSet1.next() || resultSet2.next()) {
                    sender.sendMessage(TextComponent.fromLegacyText("\n§a\uD83D\uDFE9 Ativo §c\uD83D\uDFE5 Finalizado \n"));
                    List<String> components = new ArrayList<>();

                    TextComponent component = new TextComponent("§a§l[§a" + SDF.format(resultSet1.getLong("expires")) + "§a§l]");
                    for (BaseComponent baseComponent : TextComponent.fromLegacyText("§a§l[§a" + resultSet1.getString("reason") + "§a§l]")) {
                        component.addExtra(baseComponent);
                        baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§6ID: §7#" + resultSet1.getString("id") + "\n§6Nick: §7" + resultSet1.getString("playerName") + "\n§6Quem puniu: §7" + resultSet1.getString("stafferName") + "\n\n§6Tipo: §7" + resultSet1.getString("type") + "\n§6Data de início: §7" + SDF.format(resultSet1.getLong("date")) + "\n§6Data de fim: §7" + SDF.format(resultSet1.getLong("expires")) + "\n§6Categoria: §7" + resultSet1.getString("reason") + "\n§6Motivo: §7" + resultSet1.getString("reason") + "\n§6Prova: §7" + (resultSet1.getString("proof") == null ? "§7Sem prova" : resultSet1.getString("proof") + "\n§6Punido até: §7" + SDF.format((resultSet1.getLong("expires")))))));
                    }
                    for (BaseComponent baseComponents : TextComponent.fromLegacyText(" §f§l[§fRevogar§f§l]")) {
                        component.addExtra(baseComponents);
                        baseComponents.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§cApenas o autor da punição ou um administrador podem remover esta punição.")));
                        baseComponents.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/revogar " + args[0]));
                    }
                    for (BaseComponent baseComponente : TextComponent.fromLegacyText(" §f§l[§fProva§f§l]")) {
                        component.addExtra(baseComponente);
                        baseComponente.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(resultSet1.getString("proof") == null ? "§fSem prova" : "§fClique para abrir: §7" + resultSet1.getString("proof"))));
                        baseComponente.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,  resultSet1.getString("proof")));
                    }
                    sender.sendMessage(component);
                } else {
                    sender.sendMessage(TextComponent.fromLegacyText("\n§fJogador exemplar! Sem quaisquer punições ativas."));
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        player.sendMessage(TextComponent.fromLegacyText(" "));
    }

    static {
        punishDao = Main.getInstance().getPunishDao();
    }
}

