package me.pulsi_.portalsplus;

import me.pulsi_.portalsplus.enums.Guis;
import me.pulsi_.portalsplus.guis.PSGui;
import me.pulsi_.portalsplus.managers.ConfigManager;
import me.pulsi_.portalsplus.managers.DataManager;
import me.pulsi_.portalsplus.managers.GuisManager;
import me.pulsi_.portalsplus.objects.PSPlayer;
import me.pulsi_.portalsplus.objects.Portal;
import me.pulsi_.portalsplus.utils.PSLogger;
import me.pulsi_.portalsplus.utils.PSMethods;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

public final class PortalsPlus extends JavaPlugin {

    private final HashMap<String, Portal> portals = new HashMap<>();
    private final HashMap<UUID, PSPlayer> players = new HashMap<>();
    private final HashMap<Guis, PSGui> guis = new HashMap<>();

    public static String VERSION;
    public static PortalsPlus INSTANCE;
    private ConfigManager configManager;
    private DataManager dataManager;
    private boolean isUpdated;

    @Override
    public void onEnable() {
        INSTANCE = this;
        VERSION = getServer().getClass().getPackage().getName().substring(getServer().getClass().getPackage().getName().lastIndexOf(".") + 1);

        if (PSMethods.isLegacy()) {
            PSLogger.error("This server version is not compatible with PortalsPlus!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.configManager = new ConfigManager(this);
        configManager.createConfigs();

        this.dataManager = new DataManager(this);
        dataManager.setupPlugin();

        new GuisManager().loadGuis();
        new Metrics(this);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> isUpdated = isPluginUpdated(), 0, 1200 * 60);
    }

    @Override
    public void onDisable() {
        dataManager.shutdownPlugin();
    }

    public HashMap<String, Portal> getPortals() {
        return portals;
    }

    public HashMap<UUID, PSPlayer> getPlayers() {
        return players;
    }

    public HashMap<Guis, PSGui> getGuis() {
        return guis;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    private boolean isPluginUpdated() {
        boolean updated;
        try {
            String newVersion = new BufferedReader(new InputStreamReader(new URL("https://api.spigotmc.org/legacy/update.php?resource=105135").openConnection().getInputStream())).readLine();
            updated = getDescription().getVersion().equals(newVersion);
        } catch (Exception e) {
            updated = true;
        }

        if (updated) PSLogger.info("The plugin is updated!");
        else PSLogger.info("The plugin is outdated! Please download the latest version here: ");

        return updated;
    }
}