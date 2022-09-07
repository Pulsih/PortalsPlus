package me.pulsi_.portalsplus.managers;

import me.pulsi_.portalsplus.commands.MainCmd;
import me.pulsi_.portalsplus.events.Listeners;
import me.pulsi_.portalsplus.PortalsPlus;
import me.pulsi_.portalsplus.utils.PSLogger;
import me.pulsi_.portalsplus.utils.PSMessages;
import me.pulsi_.portalsplus.values.Values;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class DataManager {

    private final PortalsPlus plugin;

    public DataManager(PortalsPlus plugin) {
        this.plugin = plugin;
    }

    public void setupPlugin() {
        long time = System.currentTimeMillis();

        Values.CONFIG.setupValues();
        PSMessages.loadMessages();
        registerCommands();
        registerEvents();

        Bukkit.getScheduler().runTaskLater(PortalsPlus.INSTANCE, () -> new PortalsManager().loadPortals(), 20L);

        if (Values.CONFIG.isSilentStartup()) return;
        PSLogger.log(
                "&d__________              __         .__         &6__________.__",
                "&d\\______   \\____________/  |______  |  |   _____&6\\______   \\  |  __ __  ______",
                "&d |     ___/  _ \\_  __ \\   __\\__  \\ |  |  /  ___/&6|     ___/  | |  |  \\/  ___/",
                "&d |    |  (  <_> )  | \\/|  |  / __ \\|  |__\\___ \\ &6|    |   |  |_|  |  /\\___ \\ ",
                "&d |____|   \\____/|__|   |__| (____  /____/____  >|____&6|   |____/____//____  >",
                "&d                                 \\/          \\/                          &6\\/"
        );
        PSLogger.log("");
        PSLogger.info("Plugin successfully enabled! &8(&6Took %t&8)".replace("%t", (System.currentTimeMillis() - time) + "ms"));
    }

    public void shutdownPlugin() {
        if (Values.CONFIG.isSilentStartup()) return;
        PSLogger.log(
                "&d__________              __         .__         &6__________.__",
                "&d\\______   \\____________/  |______  |  |   _____&6\\______   \\  |  __ __  ______",
                "&d |     ___/  _ \\_  __ \\   __\\__  \\ |  |  /  ___/&6|     ___/  | |  |  \\/  ___/",
                "&d |    |  (  <_> )  | \\/|  |  / __ \\|  |__\\___ \\ &6|    |   |  |_|  |  /\\___ \\ ",
                "&d |____|   \\____/|__|   |__| (____  /____/____  >|____&6|   |____/____//____  >",
                "&d                                 \\/          \\/                          &6\\/"
        );
        PSLogger.log("");
        PSLogger.info("Shutting down the plugin, bye!");
    }

    private void registerEvents() {
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(new Listeners(), plugin);
    }

    private void registerCommands() {
        plugin.getCommand("portalsplus").setExecutor(new MainCmd());
        plugin.getCommand("portalsplus").setTabCompleter(new MainCmd());
    }
}
