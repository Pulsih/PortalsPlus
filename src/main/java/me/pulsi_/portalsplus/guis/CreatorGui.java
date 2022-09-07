package me.pulsi_.portalsplus.guis;

import me.pulsi_.portalsplus.PortalsPlus;
import me.pulsi_.portalsplus.enums.Guis;
import me.pulsi_.portalsplus.getters.PlayersGetter;
import me.pulsi_.portalsplus.managers.PortalsManager;
import me.pulsi_.portalsplus.objects.PSPlayer;
import me.pulsi_.portalsplus.objects.Portal;
import me.pulsi_.portalsplus.objects.PortalEditor;
import me.pulsi_.portalsplus.utils.PSChat;
import me.pulsi_.portalsplus.utils.PSMessages;
import me.pulsi_.portalsplus.utils.PSMethods;
import me.pulsi_.portalsplus.values.Values;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.List;

public class CreatorGui extends PSGui {

    private Inventory gui;

    @Override
    public Guis getType() {
        return Guis.CREATOR_GUI;
    }

    @Override
    public String getTitle() {
        return "&dPortal &6Creator";
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public ItemStack[] getContent() {
        return gui.getContents();
    }

    public void loadCreatorGui() {
        gui = Bukkit.createInventory(null, this.getSize(), "");

        ItemStack filler;
        try {
            filler = new ItemStack(Material.valueOf("STAINED_GLASS_PANE"));
        } catch (IllegalArgumentException e) {
            filler = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        }
        for (int i = 0; i < this.getSize(); i++) gui.setItem(i, filler);

        gui.setItem(10, new ItemBuilder()
                .setType(Material.PAPER)
                .setDisplayname("&d&lCURRENT NAME")
                .setLore(
                        "&7This is the &6name &7of the portal",
                        "&7that you are currently &6editing&7.",
                        "",
                        "&7Current: &6%name%"
                )
                .getItem()
        );

        gui.setItem(11, new ItemBuilder()
                .setType("OAK_SIGN", "SIGN")
                .setDisplayname("&d&lCHANGE DESTINATION")
                .setLore(
                        "&7Set the portal &6destination location&7.",
                        "",
                        "&7This will be the location where",
                        "&7players will be &6teleported&7.",
                        "",
                        "&c&lThe portal destination must be",
                        "&c&lout the portal teleport locations!",
                        "",
                        "&7Current: &6%destination%")
                .getItem()
        );

        gui.setItem(12, new ItemBuilder()
                .setType("REDSTONE_LAMP")
                .setDisplayname("&d&lCHANGE PARTICLES")
                .setLore(
                        "&7Set the portal &6particles&7.",
                        "",
                        "&7Current: &6%particle%"
                )
                .getItem()
        );

        gui.setItem(13, new ItemBuilder()
                .setType(Material.COMPASS)
                .setDisplayname("&d&lCHANGE TELEPORT LOCATIONS")
                .setLore(
                        "&7Set the portal &6teleport locations&7.",
                        "",
                        "&7Basically the &6locations &7where players",
                        "&7will &6enter &7to get &6teleported&7.",
                        "",
                        "&c&lThe portal teleport locations must",
                        "&c&lbe out the portal destination!",
                        "",
                        "&7Current locations: &6x%locations%"
                )
                .getItem()
        );

        gui.setItem(14, new ItemBuilder()
                .setType(Material.SLIME_BALL)
                .setDisplayname("&d&lDONE")
                .setLore("&7Close this gui and &6create &7the portal.")
                .setGlowing()
                .getItem()
        );

        PortalsPlus.INSTANCE.getGuis().put(Guis.CREATOR_GUI, this);
    }

    @Override
    public void processGuiClick(InventoryClickEvent e) {
        Inventory inventory = e.getInventory();
        if (inventory.getHolder() == null || !(inventory.getHolder() instanceof PSGui)) return;
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        PlayersGetter getter = new PlayersGetter(p);
        PSPlayer player = getter.getPSPlayer();
        PortalEditor editor = player.getPortalEditor();
        Portal portal = player.getPortalEditor().getEditingPortal();

        switch (e.getSlot()) {
            case 11: {
                editor.setPlacingDestination(true);
                p.closeInventory();
                PSMessages.send(p, "PLACING_DESTINATION_INFO");
            }
            break;

            case 12: {
                editor.setChangingParticles(true);
                p.closeInventory();
                PSMessages.send(p, PSChat.prefix + " &7Current available particles: &6" + Arrays.toString(Particle.values())
                        .replace("[", "")
                        .replace(", ", "&7, &6")
                        .replace("]", ""), true);
                PSMessages.send(p, PSChat.prefix + " &7To change particles use this format:", true);
                PSMessages.send(p, "  &8&l* &6[Particle-Name] <Amount> <Off-X> <Off-Y> <Off-Z> <Extra>", true);
                PSMessages.send(p, "  &8&l* &7Type &6\"done\" &7to finish.", true);
            }
            break;

            case 13: {
                editor.setPlacingLocations(true);

                Inventory inv = p.getInventory();
                player.setContent(inv.getContents());
                inv.clear();

                Material type;
                try {
                    type = Material.valueOf(Values.CONFIG.getPortalBlockType());
                } catch (IllegalArgumentException ex) {
                    type = Material.STONE;
                }
                inv.setItem(4, new ItemBuilder()
                        .setType(type)
                        .setDisplayname("&d&lPORTAL TELEPORT LOCATION")
                        .setLore(
                                "&7Place the block in the locations",
                                "&7that will &6teleport &7players."
                        )
                        .setGlowing()
                        .getItem());

                BukkitTask task = Bukkit.getScheduler().runTaskTimer(PortalsPlus.INSTANCE, () ->
                        PSMethods.sendActionBar(p, "&d&lPortals&6&lPlus &7Current locations: &6x" + player.getPortalEditor().getPortalLocationsHolder().size()), 0L, 10L);
                player.setActionbarInformer(task);

                p.closeInventory();
                PSMessages.send(p, "PLACING_BLOCKS_INFO");
            }
            break;

            case 14: {
                PSMessages.send(p, "PORTAL_CREATED", "%portal%$" + portal.getIdentifier());
                getter.getPSPlayers().put(p.getUniqueId(), new PSPlayer());
                p.closeInventory();

                new PortalsManager().loadPortals();
            }
        }
    }

    @Override
    public void processGuiOpen(Player p) {
        PlayersGetter getter = new PlayersGetter(p);
        PSPlayer player = getter.getPSPlayer();
        Portal portal = player.getPortalEditor().getEditingPortal();

        Inventory guiInventory = Bukkit.createInventory(new PSGui(), getSize(), PSChat.color(getTitle()));
        guiInventory.setContents(getContent());

        replace(guiInventory.getItem(10), "%name%$" + portal.getIdentifier());
        replace(guiInventory.getItem(11), "%destination%$" + (portal.getDestinationLocation() == null ? "undefined" : PSMethods.getStringLocation(portal.getDestinationLocation())));
        replace(guiInventory.getItem(12), "%particle%$" + (portal.getParticles() == null ? "undefined" : portal.getParticles()));
        replace(guiInventory.getItem(13), "%locations%$" + (portal.getTeleportLocations() == null ? "0" : portal.getTeleportLocations().size()));

        p.openInventory(guiInventory);

        player.setOpenedInventory(guiInventory);
        player.setOpenedGui(this);

        try {
            p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 5, 2);
        } catch (NoSuchFieldError e) {
            p.playSound(p.getLocation(), Sound.valueOf("CHICKEN_EGG_POP"), 5, 2);
        }
    }

    private void replace(ItemStack item, String... replacements) {
        ItemMeta nameMeta = item.getItemMeta();
        if (nameMeta == null) return;

        List<String> lore = nameMeta.getLore();
        if (lore == null) return;

        for (String replacement : replacements) {
            if (!replacement.contains("$")) continue;
            String oldChar = replacement.split("\\$")[0];
            String newChar = replacement.split("\\$")[1];

            lore.replaceAll(s -> s.replace(oldChar, newChar));
        }
        nameMeta.setLore(lore);
        item.setItemMeta(nameMeta);
    }
}