package dev.wplugins.waze.gerementions.commands.cmd;

import dev.wplugins.waze.gerementions.Main;
import dev.wplugins.waze.gerementions.commands.Commands;
import dev.wplugins.waze.gerementions.database.MySQLDatabase;
import dev.wplugins.waze.gerementions.punish.Punish;
import dev.wplugins.waze.gerementions.punish.dao.PunishDao;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RevogarCommand extends Commands {

    public RevogarCommand() {
        super("revogar");
    }

    private static PunishDao punishDao;
    private static Punish punish;

    @Override
    public void perform(CommandSender sender, String[] args) {

        if (!sender.hasPermission("wpunish.despunir")) {
            sender.sendMessage(TextComponent.fromLegacyText("§cVocê não tem permissão para isso."));
            return;
        }
        else if (args.length != 1 || args.length == 0) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUtilize /revogar <jogador>."));
            return;
        }
        String target = args[0];
        try {
            Statement statement2 = MySQLDatabase.getInstance().getConnection().createStatement();
            ResultSet resultSet2 = statement2.executeQuery("SELECT * FROM wPunish WHERE playerName='" + args[0] + "'");
            if (sender.hasPermission("wpunish.admin") || !sender.hasPermission("wpunish.ajudante")) {
                if (resultSet2.next()) {
                    TextComponent text32 = new TextComponent("§bClique aqui para revogar a punição do jogador\n");
                    text32.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/wiwueioa " + args[0]));
                    text32.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/wiwueioa " + args[0]));
                    text32.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Clique para revogar a punição.")));
                    sender.sendMessage(text32);
                } else {
                    sender.sendMessage(TextComponent.fromLegacyText("§fJogador exemplar! Sem quaisquer punições ativas."));
                }
            } else if (resultSet2.getString("stafferName") == sender.getName() || sender.hasPermission("wpunish.checkpunir")) {
                if (resultSet2.next()) {
                     TextComponent text32 = new TextComponent("§bClique aqui para revogar a punição do jogador\n");
                        text32.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/wiwueioa " + args[0]));
                        text32.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/wiwueioa " + args[0]));
                        text32.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Clique para revogar a punição.")));
                        sender.sendMessage(text32);
                    } else {
                        sender.sendMessage(TextComponent.fromLegacyText("§fJogador exemplar! Sem quaisquer punições ativas."));


                    }

            }
        } catch (SQLException e1) {
            throw new RuntimeException(e1);
        }
    }
        static {
            punishDao = Main.getInstance().getPunishDao();
        }
    }
