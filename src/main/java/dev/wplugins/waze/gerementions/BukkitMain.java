package dev.wplugins.waze.gerementions;

import dev.wplugins.waze.gerementions.database.Database;
import dev.wplugins.waze.gerementions.database.MySQLDatabase;
import dev.wplugins.waze.gerementions.database.PluginInstance;
import dev.wplugins.waze.gerementions.listeners.BukkitListener;
import dev.wplugins.waze.gerementions.punish.dao.PunishDao;
import dev.wplugins.waze.gerementions.thread.PunishThread;
import net.md_5.bungee.config.YamlConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.logging.Level;


public class BukkitMain extends JavaPlugin {
    private Configuration config;
    private static BukkitMain instance;
    private PunishDao punishDao;

    public static BukkitMain getPlugin() {
        return BukkitMain.getPlugin(BukkitMain.class);
    }

    private PunishThread punishThread;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        punishDao = new PunishDao();
        Database.setupDatabase();
        punishDao.loadPunishes();
        instance = this;
        MySQLDatabase.instancia = PluginInstance.SPIGOT;
        this.getLogger().info("§aEste plugin foi ativo com sucesso");
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new BukkitListener(), this);
    }

    public Configuration getConfig2() {
        return config;
    }

    public static BukkitMain getInstance() {
        return instance;
    }




    /**
     * Copia um arquivo a partir de um InputStream.
     *
     * @param input O input para ser copiado.
     * @param out   O arquivo destinario.
     */
    public static void copyFile(InputStream input, File out) {
        FileOutputStream ou = null;
        try {
            ou = new FileOutputStream(out);
            byte[] buff = new byte[1024];
            int len;
            while ((len = input.read(buff)) > 0) {
                ou.write(buff, 0, len);
            }
        } catch (IOException ex) {
            getPlugin().getLogger().log(Level.WARNING, "Failed at copy file " + out.getName() + "!", ex);
        } finally {
            try {
                if (ou != null) {
                    ou.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException ignore) {
            }
        }
    }

    public PunishDao getPunishDao() {
        return punishDao;
    }

    public PunishThread getPunishThread() {
        return punishThread;
    }
}