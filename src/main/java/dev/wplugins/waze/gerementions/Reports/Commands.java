package dev.wplugins.waze.gerementions.Reports;

import dev.wplugins.waze.gerementions.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.logging.Level;


public abstract class Commands extends Command {
    public Commands(String name, String... aliases) {
        super(name);
        this.setAliases(Arrays.asList(aliases));

        try {
            SimpleCommandMap simpleCommandMap = (SimpleCommandMap) Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());
            simpleCommandMap.register(this.getName(), "vulcanthreportes", this);
        } catch (ReflectiveOperationException ex) {
            Main.getInstance().getLogger().log(Level.SEVERE, "Cannot register command: ", ex);
        }
    }
    public abstract void perform(CommandSender sender, String commandLabel, String[] args);

    public boolean execute(final CommandSender sender, String commandLabel, String[] args) {
        this.perform(sender, commandLabel, args);
        return true;
    }

    public static void setupCommands() {
        new ReportesCommand();
    }
}
