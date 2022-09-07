package me.pulsi_.portalsplus.events;

import me.pulsi_.portalsplus.PortalsPlus;
import me.pulsi_.portalsplus.guis.PSGui;
import me.pulsi_.portalsplus.getters.PlayersGetter;
import me.pulsi_.portalsplus.managers.GuisManager;
import me.pulsi_.portalsplus.managers.PortalsManager;
import me.pulsi_.portalsplus.objects.PSPlayer;
import me.pulsi_.portalsplus.objects.Portal;
import me.pulsi_.portalsplus.objects.PortalEditor;
import me.pulsi_.portalsplus.utils.PSChat;
import me.pulsi_.portalsplus.utils.PSLogger;
import me.pulsi_.portalsplus.utils.PSMessages;
import me.pulsi_.portalsplus.utils.PSMethods;
import me.pulsi_.portalsplus.values.Values;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class Listeners implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        PSPlayer player = new PlayersGetter(p).getPSPlayer();
        Location pLoc = p.getLocation();
        Location fLoc = e.getFrom();

        int x = player.getX(), y = player.getY(), z = player.getZ(), fx = (int) fLoc.getX(), fy = (int) fLoc.getY(), fz = (int) fLoc.getZ();
        if (x == fx && y == fy && z == fz) return;

        int nx = (int) pLoc.getX(), ny = (int) pLoc.getY(), nz = (int) pLoc.getZ();
        player.setX(nx);
        player.setY(ny);
        player.setZ(nz);

        for (String identifier : PortalsPlus.INSTANCE.getPortals().keySet()) {
            Portal portal = new PortalsManager(identifier).getPortal();
            if (!portal.isLinked()) return;

            for (Location loc : portal.getTeleportLocations()) {
                int px = (int) loc.getX(), py = (int) loc.getY(), pz = (int) loc.getZ();
                if (!isIn(nx, px, ny, py, nz, pz)) continue;

                Portal linkedPortal = portal.getLinkedPortal();
                Location destination = linkedPortal.getDestinationLocation();

                Bukkit.getServer().getPluginManager().callEvent(new PSPortalEnterEvent(p, p.getLocation(), destination, portal, linkedPortal));
                if (destination != null) p.teleport(destination);
                return;
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        PSPlayer player = new PlayersGetter(p).getPSPlayer();
        PortalEditor editor = player.getPortalEditor();
        if (editor == null) return;

        if (!editor.isPlacingLocations()) return;
        Portal portal = editor.getEditingPortal();
        Location loc = e.getBlock().getLocation();
        e.setCancelled(true);

        if (portal.getDestinationLocation() != null && isIn(portal.getDestinationLocation(), loc)) {
            PSMessages.send(p, "PLACING_LOCATION_ERROR");
            return;
        }

        Bukkit.getScheduler().runTaskLater(PortalsPlus.INSTANCE, () -> {
            Material material;
            try {
                material = Material.valueOf(Values.CONFIG.getPortalBlockType());
            } catch (IllegalArgumentException ex) {
                material = Material.STONE;
                PSLogger.error("Invalid material type for the portal block! " + ex.getMessage());
            }
            loc.getWorld().getBlockAt(loc).setType(material);
        }, 0L);

        List<Location> locations = editor.getPortalLocationsHolder();
        locations.add(loc.add(0.5, 0.5, 0.5));
        editor.setPortalLocationsHolder(locations);

        BukkitTask particlesTask = Bukkit.getScheduler().runTaskTimer(PortalsPlus.INSTANCE, () -> {
            for (Location location : locations)
                PSMethods.spawnParticle(location, portal.getParticles());
        }, 0, Values.CONFIG.getParticlesSpeed());

        portal.setParticlesTasks(particlesTask);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        PSPlayer player = new PlayersGetter(p).getPSPlayer();
        PortalEditor editor = player.getPortalEditor();
        if (editor == null) return;

        if (!editor.isPlacingLocations() || !e.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;
        Location loc = e.getClickedBlock().getLocation();

        if (!editor.getPortalLocationsHolder().contains(loc.add(0.5, 0.5, 0.5))) return;
        e.setCancelled(true);

        loc.getWorld().getBlockAt(loc).setType(Material.AIR);

        List<Location> locations = editor.getPortalLocationsHolder();
        locations.remove(loc);
        editor.setPortalLocationsHolder(locations);

        Portal portal = editor.getEditingPortal();
        BukkitTask particlesTask = Bukkit.getScheduler().runTaskTimer(PortalsPlus.INSTANCE, () -> {
            for (Location location : locations)
                PSMethods.spawnParticle(location, portal.getParticles());
        }, 0, Values.CONFIG.getParticlesSpeed());

        portal.setParticlesTasks(particlesTask);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        PSPlayer player = new PlayersGetter(p).getPSPlayer();
        PortalEditor editor = player.getPortalEditor();
        if (editor == null || player.getOpenedGui() == null) return;

        GuisManager guisManager = new GuisManager(player.getOpenedGui().getType());
        String message = e.getMessage();

        if (editor.isPlacingLocations()) {
            e.setCancelled(true);

            switch (message.toLowerCase()) {
                case "cancel": {
                    editor.setPlacingLocations(false);

                    Bukkit.getScheduler().runTask(PortalsPlus.INSTANCE, () -> {
                        for (Location loc : editor.getPortalLocationsHolder())
                            loc.getWorld().getBlockAt(loc).setType(Material.AIR);
                        editor.getEditingPortal().setParticlesTasks(null);
                        editor.setPortalLocationsHolder(null);
                        guisManager.openGui(p);
                    });

                    p.getInventory().setContents(player.getContent());
                    player.cancelActionbarInformer();
                    player.setContent(null);

                    PSMessages.send(p, "CANCELLED");
                }
                break;

                case "done": {
                    editor.setPlacingLocations(false);

                    Bukkit.getScheduler().runTask(PortalsPlus.INSTANCE, () -> {
                        for (Location loc : editor.getPortalLocationsHolder())
                            loc.getWorld().getBlockAt(loc).setType(Material.AIR);
                        guisManager.openGui(p);
                        editor.setPortalLocationsHolder(null);
                    });

                    Portal portal = editor.getEditingPortal();
                    portal.setTeleportLocations(editor.getPortalLocationsHolder());

                    FileConfiguration portalConfig = portal.getPortalConfig();
                    List<String> locations = new ArrayList<>();
                    portal.getTeleportLocations().forEach(loc -> locations.add(PSMethods.getStringLocation(loc)));
                    portalConfig.set("teleport-locations", locations);
                    portal.savePortalFile();

                    p.getInventory().setContents(player.getContent());
                    player.cancelActionbarInformer();
                    player.setContent(null);

                    PSMessages.send(p, "PORTAL_LOCATIONS_SET");
                }
                break;

                default:
                    PSMessages.send(p, "UNKNOWN_ACTION");
            }
            return;
        }

        if (editor.isPlacingDestination()) {
            e.setCancelled(true);

            switch (message.toLowerCase()) {
                case "cancel": {
                    editor.setPlacingDestination(false);
                    Bukkit.getScheduler().runTask(PortalsPlus.INSTANCE, () -> guisManager.openGui(p));
                    PSMessages.send(p, "CANCELLED");
                }
                break;

                case "done": {
                    editor.setPlacingDestination(false);
                    Bukkit.getScheduler().runTask(PortalsPlus.INSTANCE, () -> guisManager.openGui(p));
                }
                break;

                case "set": {
                    Portal portal = editor.getEditingPortal();
                    Location loc = p.getLocation();

                    if (portal.getTeleportLocations() != null) {
                        for (Location teleportLocation : portal.getTeleportLocations()) {
                            if (!isIn(teleportLocation, loc)) continue;
                            PSMessages.send(p, "PLACING_LOCATION_ERROR");
                            return;
                        }
                    }

                    FileConfiguration portalConfig = portal.getPortalConfig();
                    portalConfig.set("destination", PSMethods.getStringLocation(loc));
                    portal.setDestinationLocation(loc);
                    portal.savePortalFile();

                    PSMessages.send(p, "PORTAL_DESTINATION_SET");
                }
                break;

                default:
                    PSMessages.send(p, "UNKNOWN_ACTION");
            }
        }

        if (editor.isChangingParticles()) {
            e.setCancelled(true);

            if (message.equalsIgnoreCase("done")) {
                editor.setChangingParticles(false);
                Bukkit.getScheduler().runTask(PortalsPlus.INSTANCE, () -> guisManager.openGui(p));
                return;
            }

            String[] values = message.split(" ");
            switch (values.length) {
                case 0: {
                    PSMessages.send(p, PSChat.prefix + " &7You need to specify at least a particle!", true);
                    return;
                }

                case 1: {
                    try {
                        Particle.valueOf(values[0]);
                    } catch (IllegalArgumentException ex) {
                        PSMessages.send(p, "INVALID_PARTICLE");
                        return;
                    }

                    Portal portal = editor.getEditingPortal();
                    FileConfiguration portalConfig = portal.getPortalConfig();

                    String particle = values[0] + " 1 0.5 0.5 0.5 0";
                    portalConfig.set("particles", particle);
                    portal.setParticles(particle);
                    portal.savePortalFile();
                    PSMessages.send(p, PSChat.prefix + " &7Successfully updated the portal particles!");
                }
                break;

                case 2: {
                    try {
                        Particle.valueOf(values[0]);
                        Integer.parseInt(values[1]);
                    } catch (NumberFormatException ex) {
                        PSMessages.send(p, "INVALID_NUMBER");
                        return;
                    } catch (IllegalArgumentException ex) {
                        PSMessages.send(p, "INVALID_PARTICLE");
                        return;
                    }

                    Portal portal = editor.getEditingPortal();
                    FileConfiguration portalConfig = portal.getPortalConfig();

                    String particle = values[0] + " " + values[1] + " 0.5 0.5 0.5 0";
                    portalConfig.set("particles", particle);
                    portal.setParticles(particle);
                    portal.savePortalFile();
                    PSMessages.send(p, PSChat.prefix + " &7Successfully updated the portal particles!");
                }
                break;

                case 3: {
                    try {
                        Particle.valueOf(values[0]);
                        Integer.parseInt(values[1]);
                        Double.parseDouble(values[2]);
                    } catch (NumberFormatException ex) {
                        PSMessages.send(p, "INVALID_NUMBER");
                        return;
                    } catch (IllegalArgumentException ex) {
                        PSMessages.send(p, "INVALID_PARTICLE");
                        return;
                    }

                    Portal portal = editor.getEditingPortal();
                    FileConfiguration portalConfig = portal.getPortalConfig();

                    String particle = values[0] + " " + values[1] + " " + values[2] + " 0.5 0.5 0";
                    portalConfig.set("particles", particle);
                    portal.setParticles(particle);
                    portal.savePortalFile();
                    PSMessages.send(p, PSChat.prefix + " &7Successfully updated the portal particles!");
                }
                break;

                case 4: {
                    try {
                        Particle.valueOf(values[0]);
                        Integer.parseInt(values[1]);
                        Double.parseDouble(values[2]);
                        Double.parseDouble(values[3]);
                    } catch (NumberFormatException ex) {
                        PSMessages.send(p, "INVALID_NUMBER");
                        return;
                    } catch (IllegalArgumentException ex) {
                        PSMessages.send(p, "INVALID_PARTICLE");
                        return;
                    }

                    Portal portal = editor.getEditingPortal();
                    FileConfiguration portalConfig = portal.getPortalConfig();

                    String particle = values[0] + " " + values[1] + " " + values[2] + " " + values[3] + " 0.5 0";
                    portalConfig.set("particles", particle);
                    portal.setParticles(particle);
                    portal.savePortalFile();
                    PSMessages.send(p, PSChat.prefix + " &7Successfully updated the portal particles!");
                }
                break;

                case 5: {
                    try {
                        Particle.valueOf(values[0]);
                        Integer.parseInt(values[1]);
                        Double.parseDouble(values[2]);
                        Double.parseDouble(values[3]);
                        Double.parseDouble(values[4]);
                    } catch (NumberFormatException ex) {
                        PSMessages.send(p, "INVALID_NUMBER");
                        return;
                    } catch (IllegalArgumentException ex) {
                        PSMessages.send(p, "INVALID_PARTICLE");
                        return;
                    }

                    Portal portal = editor.getEditingPortal();
                    FileConfiguration portalConfig = portal.getPortalConfig();

                    String particle = values[0] + " " + values[1] + " " + values[2] + " " + values[3] + " " + values[4] + " 0";
                    portalConfig.set("particles", particle);
                    portal.setParticles(particle);
                    portal.savePortalFile();
                    PSMessages.send(p, PSChat.prefix + " &7Successfully updated the portal particles!");
                }
                break;

                case 6: {
                    try {
                        Particle.valueOf(values[0]);
                        Integer.parseInt(values[1]);
                        Double.parseDouble(values[2]);
                        Double.parseDouble(values[3]);
                        Double.parseDouble(values[4]);
                        Integer.parseInt(values[5]);
                    } catch (NumberFormatException ex) {
                        PSMessages.send(p, "INVALID_NUMBER");
                        return;
                    } catch (IllegalArgumentException ex) {
                        PSMessages.send(p, "INVALID_PARTICLE");
                        return;
                    }

                    Portal portal = editor.getEditingPortal();
                    FileConfiguration portalConfig = portal.getPortalConfig();

                    String particle = values[0] + " " + values[1] + " " + values[2] + " " + values[3] + " " + values[4] + " " + values[5];
                    portalConfig.set("particles", particle);
                    portal.setParticles(particle);
                    portal.savePortalFile();
                    PSMessages.send(p, PSChat.prefix + " &7Successfully updated the portal particles!");
                }
                break;
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        PSPlayer player = new PlayersGetter(p).getPSPlayer();
        PortalEditor editor = player.getPortalEditor();
        if (editor == null) return;

        if (editor.isEditingPortal()) e.setCancelled(true);

        PSGui gui = player.getOpenedGui();
        if (gui != null) gui.processGuiClick(e);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        PSPlayer player = new PlayersGetter(p).getPSPlayer();
        PortalEditor editor = player.getPortalEditor();
        if (editor == null) return;

        if (editor.isEditingPortal() && !editor.isChangingParticles() && !editor.isPlacingLocations() && !editor.isPlacingDestination()) {
            Bukkit.getScheduler().runTaskLater(PortalsPlus.INSTANCE, () -> p.openInventory(e.getInventory()), 0L);
            return;
        }
        if (!editor.isEditingPortal()) player.setOpenedInventory(null);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        PSPlayer p = new PlayersGetter(e.getPlayer()).getPSPlayer();
        PortalEditor editor = p.getPortalEditor();
        if (editor != null && editor.isEditingPortal()) e.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        PSPlayer p = new PlayersGetter(e.getPlayer()).getPSPlayer();
        PortalEditor editor = p.getPortalEditor();
        if (editor != null && editor.isEditingPortal()) e.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        PSPlayer player = new PlayersGetter(p).getPSPlayer();

        Location pLoc = p.getLocation();
        int x = (int) pLoc.getX(), y = (int) pLoc.getY(), z = (int) pLoc.getZ();
        player.setX(x);
        player.setY(y);
        player.setZ(z);

        if (PortalsPlus.INSTANCE.isUpdated() || !p.isOp()) return;

        TextComponent text = new TextComponent(PSChat.color(PSChat.prefix + " &dNew update available!"));
        TextComponent button = new TextComponent(PSChat.color("&6&l[CLICK HERE]"));
        button.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/portalsplus.105135/"));
        button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to download it!").color(ChatColor.GRAY).create()));
        text.addExtra(" ");
        text.addExtra(button);

        Bukkit.getScheduler().runTaskLater(PortalsPlus.INSTANCE, () -> {
            p.sendMessage("");
            p.spigot().sendMessage(text);
            p.sendMessage("");
        }, 80);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        new PlayersGetter(e.getPlayer()).unregister();
    }

    private boolean isIn(Location loc1, Location loc2) {
        int x1 = (int) loc1.getX(), x2 = (int) loc2.getX(), y1 = (int) loc1.getY(), y2 = (int) loc2.getY(), z1 = (int) loc1.getZ(), z2 = (int) loc2.getZ();
        return (x1 == x2 && y1 == y2 && z1 == z2) || (x1 == x2 && y1 + 1 == y2 && z1 == z2);
    }

    private boolean isIn(int x1, int x2, int y1, int y2, int z1, int z2) {
        return (x1 == x2 && y1 == y2 && z1 == z2) || (x1 == x2 && y1 + 1 == y2 && z1 == z2);
    }
}
