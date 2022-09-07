package me.pulsi_.portalsplus.objects;

import me.pulsi_.portalsplus.PortalsPlus;
import me.pulsi_.portalsplus.utils.PSLogger;
import me.pulsi_.portalsplus.values.Values;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.List;

public class Portal {

    private final String identifier;
    private final File portalFile;
    private final FileConfiguration portalConfig;
    private Location destinationLocation;
    private List<Location> teleportLocations;
    private String particles;
    private BukkitTask particlesTasks;
    private boolean active, isLinked;
    private Portal linkedPortal;

    public Portal(String identifier) {
        this.identifier = identifier;

        portalFile = new File(PortalsPlus.INSTANCE.getDataFolder(), "portals" + File.separator + identifier + ".yml");

        portalFile.getParentFile().mkdir();
        try {
            portalFile.createNewFile();
        } catch (Exception e) {
            PSLogger.error("Could not create the file for the portal \"" + identifier + "\". " + e.getMessage());
        }

        FileConfiguration portalConfig = new YamlConfiguration();
        try {
            portalConfig.load(portalFile);
        } catch (Exception e) {
            PSLogger.error("Could not load the file for the portal \"" + identifier + "\". " + e.getMessage());
        }
        this.portalConfig = portalConfig;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Location getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(Location destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public List<Location> getTeleportLocations() {
        return teleportLocations;
    }

    public void setTeleportLocations(List<Location> teleportLocations) {
        this.teleportLocations = teleportLocations;
    }

    public String getParticles() {
        return particles;
    }

    public void setParticles(String particles) {
        this.particles = particles;
    }

    public BukkitTask getParticlesTasks() {
        return particlesTasks;
    }

    public void setParticlesTasks(BukkitTask particlesTasks) {
        if (this.particlesTasks != null) this.particlesTasks.cancel();
        this.particlesTasks = particlesTasks;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isLinked() {
        return isLinked;
    }

    public void setLinked(boolean linked) {
        isLinked = linked;
    }

    public Portal getLinkedPortal() {
        return linkedPortal;
    }

    public void setLinkedPortal(Portal linkedPortal) {
        this.linkedPortal = linkedPortal;
    }

    public File getPortalFile() {
        return portalFile;
    }

    public FileConfiguration getPortalConfig() {
        return portalConfig;
    }

    public void savePortalFile() {
        PortalsPlus.INSTANCE.getConfigManager().saveConfig(portalConfig, portalFile, Values.CONFIG.isAsyncSave());
    }
}