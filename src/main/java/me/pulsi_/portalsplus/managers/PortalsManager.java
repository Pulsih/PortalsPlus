package me.pulsi_.portalsplus.managers;

import me.pulsi_.portalsplus.PortalsPlus;
import me.pulsi_.portalsplus.objects.Portal;
import me.pulsi_.portalsplus.utils.PSLogger;
import me.pulsi_.portalsplus.utils.PSMethods;
import me.pulsi_.portalsplus.values.Values;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PortalsManager {

    private final String name;
    private final Portal portal;

    public PortalsManager() {
        this.name = null;
        this.portal = null;
    }

    public PortalsManager(String portalName) {
        this.name = portalName;
        this.portal = PortalsPlus.INSTANCE.getPortals().get(name);
    }

    public void loadPortals() {
        HashMap<String, Portal> portals = PortalsPlus.INSTANCE.getPortals();

        if (!portals.keySet().isEmpty())
            for (String portalIdentifier : portals.keySet())
                new PortalsManager(portalIdentifier).cancelParticlesTask();

        portals.clear();

        File portalsFolder = new File(PortalsPlus.INSTANCE.getDataFolder(), "portals");
        File[] portalFiles = portalsFolder.listFiles();
        if (portalFiles == null || portalFiles.length == 0) return;

        for (File portalFile : portalFiles) {
            FileConfiguration config = new YamlConfiguration();
            try {
                config.load(portalFile);
            } catch (IOException | InvalidConfigurationException e) {
                PSLogger.error("An error has occurred while loading a portal file: " + e.getMessage());
            }
            String id = portalFile.getName().replace(".yml", "");
            new PortalsManager(id).loadPortal();
        }

        for (String name : getPortals()) {
            PortalsManager manager = new PortalsManager(name);
            Portal loadingPortal = manager.getPortal();
            FileConfiguration portalConfig = loadingPortal.getPortalConfig();

            String linkedPortal = portalConfig.getString("linked-portal");
            if (linkedPortal != null && !linkedPortal.equals("undefined")) {
                loadingPortal.setLinkedPortal(new PortalsManager(linkedPortal).getPortal());
                loadingPortal.setLinked(true);
            }
        }
    }

    public void loadPortal() {
        Portal loadingPortal = new Portal(name);
        FileConfiguration portalConfig = loadingPortal.getPortalConfig();

        List<Location> locations = new ArrayList<>();
        for (String loc : portalConfig.getStringList("teleport-locations"))
            locations.add(PSMethods.getLocation(loc));

        loadingPortal.setTeleportLocations(locations);

        String particles = portalConfig.getString("particles");
        if (particles != null && !particles.equals("undefined")) {
            loadingPortal.setParticles(particles);

            loadingPortal.setParticlesTasks(Bukkit.getScheduler().runTaskTimer(PortalsPlus.INSTANCE, () -> {
                for (Location loc : locations) PSMethods.spawnParticle(loc, particles);
            }, 0L, Values.CONFIG.getParticlesSpeed()));
        }

        String destination = portalConfig.getString("destination");
        if (destination != null && !destination.equals("undefined")) loadingPortal.setDestinationLocation(PSMethods.getLocation(destination));

        PortalsPlus.INSTANCE.getPortals().put(name, loadingPortal);
    }

    public Portal getPortal() {
        return portal;
    }

    public void cancelParticlesTask() {
        BukkitTask task = portal.getParticlesTasks();
        if (task != null) task.cancel();
    }

    public boolean exists() {
        return portal != null;
    }

    public void delete() {
        portal.getPortalFile().delete();
        loadPortals();
    }

    public List<String> getPortals() {
        return new ArrayList<>(PortalsPlus.INSTANCE.getPortals().keySet());
    }
}