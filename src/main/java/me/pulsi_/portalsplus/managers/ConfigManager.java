package me.pulsi_.portalsplus.managers;

import me.pulsi_.portalsplus.PortalsPlus;
import me.pulsi_.portalsplus.enums.Configs;
import me.pulsi_.portalsplus.utils.PSLogger;
import me.pulsi_.portalsplus.values.Values;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConfigManager {

    private final PortalsPlus plugin;
    private File configFile, messagesFile;
    private FileConfiguration config, messagesConfig;

    public ConfigManager(PortalsPlus plugin) {
        this.plugin = plugin;
    }

    public void createConfigs() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!configFile.exists()) plugin.saveResource("config.yml", false);
        if (!messagesFile.exists()) plugin.saveResource("messages.yml", false);

        config = new YamlConfiguration();
        messagesConfig = new YamlConfiguration();

        checkMissingParts("config.yml");
        checkMissingParts("messages.yml");

        reloadConfig(Configs.CONFIG);
        reloadConfig(Configs.MESSAGES);

        //PortalsPlus.INSTANCE.getDataManager().reloadPlugin();
    }

    public FileConfiguration getConfig(Configs type) {
        switch (type) {
            case CONFIG:
                return config;
            case MESSAGES:
                return messagesConfig;
            default:
                return null;
        }
    }

    public boolean reloadConfig(Configs type) {
        switch (type) {
            case CONFIG:
                try {
                    config.load(configFile);
                    return true;
                } catch (IOException | InvalidConfigurationException e) {
                    PSLogger.error(e.getMessage());
                    return false;
                }

            case MESSAGES:
                try {
                    messagesConfig.load(messagesFile);
                    return true;
                } catch (IOException | InvalidConfigurationException e) {
                    PSLogger.error(e.getMessage());
                    return false;
                }
        }
        return false;
    }

    public void saveConfig(Configs type, boolean async) {
        switch (type) {
            case CONFIG:
                saveConfig(config, configFile, async);
                break;

            case MESSAGES:
                saveConfig(messagesConfig, messagesFile, async);
                break;
        }
    }

    public void saveConfig(FileConfiguration config, File file, boolean async) {
        if (!async) {
            try {
                config.save(file);
            } catch (IOException e) {
                PSLogger.warn(e.getMessage());
            }
            return;
        }
        try {
            Bukkit.getScheduler().runTaskAsynchronously(PortalsPlus.INSTANCE, () -> {
                try {
                    config.save(file);
                } catch (Exception e) {
                    Bukkit.getScheduler().runTask(PortalsPlus.INSTANCE, () -> {
                        try {
                            config.save(file);
                        } catch (IOException ex) {
                            PSLogger.warn(ex.getMessage());
                        }
                    });
                }
            });
        } catch (Exception e) {
            try {
                config.save(file);
            } catch (IOException ex) {
                PSLogger.warn(e.getMessage());
            }
        }
    }

    private void checkMissingParts(String config) {
        File serverFile = new File(plugin.getDataFolder(), config);
        YamlConfiguration serverConfig = YamlConfiguration.loadConfiguration(serverFile);

        InputStreamReader pluginStream = new InputStreamReader(PortalsPlus.INSTANCE.getResource(config), StandardCharsets.UTF_8);
        YamlConfiguration pluginConfig = YamlConfiguration.loadConfiguration(pluginStream);

        for (String path : pluginConfig.getKeys(true))
            serverConfig.set(path, serverConfig.get(path) == null ? pluginConfig.get(path) : serverConfig.get(path));

        saveConfig(serverConfig, serverFile, Values.CONFIG.isAsyncSave());
    }
}