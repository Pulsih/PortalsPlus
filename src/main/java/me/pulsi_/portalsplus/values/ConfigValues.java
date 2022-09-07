package me.pulsi_.portalsplus.values;

import me.pulsi_.portalsplus.PortalsPlus;
import me.pulsi_.portalsplus.enums.Configs;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigValues {

    private boolean checkUpdates, silentStartup;
    private String portalBlockType, defaultPortalParticles;
    private boolean asyncSave;
    private int particlesSpeed;

    public void setupValues() {
        FileConfiguration config = PortalsPlus.INSTANCE.getConfigManager().getConfig(Configs.CONFIG);

        checkUpdates = config.getBoolean("startup.check-updates");
        silentStartup = config.getBoolean("startup.silent-startup");
        portalBlockType = config.getString("portal-creator.block-type");
        defaultPortalParticles = config.getString("portal-creator.default-particles");
        asyncSave = config.getBoolean("configs.async-save");
        particlesSpeed = config.getInt("portal-settings.particles-speed");
    }

    public boolean isCheckUpdates() {
        return checkUpdates;
    }

    public boolean isSilentStartup() {
        return silentStartup;
    }

    public String getPortalBlockType() {
        return portalBlockType;
    }

    public String getDefaultPortalParticles() {
        return defaultPortalParticles;
    }

    public boolean isAsyncSave() {
        return asyncSave;
    }

    public int getParticlesSpeed() {
        return particlesSpeed;
    }
}