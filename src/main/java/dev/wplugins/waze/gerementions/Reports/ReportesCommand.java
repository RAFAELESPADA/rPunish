package dev.wplugins.waze.gerementions.Reports;

import dev.wplugins.waze.gerementions.Main;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Getter
public class ReportesCommand extends Commands {


    public ReportesCommand() {
        super("reports");
    }

    public static ReportsDao reportDao;
    String id;
    @Override
    public void perform(CommandSender sender, String label, String[] args) {

    }

    @Override
    public boolean execute(CommandSender sender, String commandlabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cApenas jogadores podem utilizar este comando.");
        }
        Player player = (Player) sender;
        if (!player.hasPermission("wpunish.reports")) {
            player.sendMessage("§cVocê não tem permissão.");
            return true;

        }
        if (args.length != 0 && args.length != 1 && args.length <= 2 ) {
            if(args.length == 2) {
                if (args[0].equalsIgnoreCase("limpar")) {
                    if (!player.hasPermission("wpunish.reports.limpar")) {
                        player.sendMessage("§cVocê não tem permissão.");
                        return true;
                    }
                    String nick = args[1];
                    if (!MySQL.checarExistePlayer(nick)) {
                        player.sendMessage("§cEste usuário não existe.");
                        return true;
                    }
                    player.sendMessage("\n§eVocê limpou o histórico de reports do jogador " + args[1] + ".\n");
                    Database.getInstance().execute("DELETE FROM wReports WHERE id = ?", String.valueOf(id));
                }
                return true;
            } else {
                return false;
            }
        } {

            sender.sendMessage("§eBuscando reports...\n§aSucesso!...");
            try {
                PreparedStatement ps = Database.getInstance().getConnection().prepareStatement("SELECT * FROM wReports BY ID;");
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String name = rs.getString("reporterPlayer");
                    String nick = rs.getString("reportedPlayer");
                    HashMap<String, String> map = new HashMap();
                    map.put(name, nick);
                    sender.sendMessage("§cReport #" + rs.getString("id"));
                    sender.sendMessage("§cReporter " + name);
                    sender.sendMessage("§cReportado " + nick);
                }
                else {
                    sender.sendMessage("§cNão existe reports ativos");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return true;
        }
    }
private <List>Player getReportPlayer(String p) {

        PreparedStatement ps = null;
        try {
            ps = Database.getInstance().getConnection().prepareStatement("SELECT * FROM wReports WHERE reportedPlayer=" + p);

            ResultSet rs = ps.executeQuery();
            return Bukkit.getPlayer(rs.getString(0));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


