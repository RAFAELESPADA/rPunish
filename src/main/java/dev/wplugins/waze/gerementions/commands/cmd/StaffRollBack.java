package dev.wplugins.waze.gerementions.commands.cmd;



import dev.wplugins.waze.gerementions.Main;
import dev.wplugins.waze.gerementions.commands.Commands;
import dev.wplugins.waze.gerementions.database.MySQLDatabase;
import dev.wplugins.waze.gerementions.punish.Punish;
import dev.wplugins.waze.gerementions.punish.dao.PunishDao;
import dev.wplugins.waze.gerementions.punish.service.PunishSpigot;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.logging.Level;

public class StaffRollBack extends Commands {

    public StaffRollBack() {
        super("staffrollback", "staffrolled");
    }

    private static PunishDao punishDao;
    SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm");

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cApenas jogadores podem utilizar este comando."));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!player.hasPermission("wpunish.rollback")) {
            player.sendMessage(TextComponent.fromLegacyText("§cVocê não tem autorização para isso."));
            return;
        }

        if (args.length < 1) {
            return;
        }
        String id = args[0];
        Punish punish = punishDao.getPunishService().get(id);
        PunishSpigot punish2 = punishDao.getPunishService2().get(id);
        try {

            Statement   statement1 = MySQLDatabase.getInstance().getConnection().createStatement();
            ResultSet resultSet1 = statement1.executeQuery("SELECT * FROM wPunish WHERE stafferName='" + args[0] + "'");
            Statement statement2 = MySQLDatabase.getInstance().getConnection().createStatement();
            ResultSet resultSet2 = statement2.executeQuery("SELECT * FROM wPunish2 WHERE stafferName='" + args[0] + "'");
            if (resultSet1.next()) {
                statement2.executeUpdate("DELETE FROM wPunish2 WHERE stafferName='" + args[0] + "'");
                TextComponent text = new TextComponent(TextComponent.fromLegacyText("§cO membro da equipe " + args[0] + " §cacabou de ter todas as punições aplicadas por ele zeradas."));


                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§e" + args[0] +
                        "\n\n§bQuantia de Punições removidas: §e#" + resultSet1.getFetchSize())));


                sender.sendMessage(TextComponent.fromLegacyText("§aVocê zerou as punições de " + args[0] + "§a."));
                ProxyServer.getInstance().getPlayers().stream().filter(o -> o.hasPermission("wpunish.veralerta")).forEach(o -> o.sendMessage(text));

                Main.getInstance().getLogger().log(Level.FINE, args[0] + " foi zerado nas punições [1].");
                statement1.executeUpdate("DELETE FROM wPunish WHERE stafferName='" + args[0] + "'");
            } else {
                player.sendMessage("O Jogador não tem punições aplicadas por ele ativa.");
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }



    }  static {
        punishDao = Main.getInstance().getPunishDao();
    }
}

