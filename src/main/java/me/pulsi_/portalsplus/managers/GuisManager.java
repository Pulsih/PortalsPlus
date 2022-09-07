package me.pulsi_.portalsplus.managers;

import me.pulsi_.portalsplus.PortalsPlus;
import me.pulsi_.portalsplus.enums.Guis;
import me.pulsi_.portalsplus.guis.CreatorGui;
import me.pulsi_.portalsplus.guis.PSGui;
import org.bukkit.entity.Player;

public class GuisManager {

    private final PSGui gui;

    public GuisManager() {
        this.gui = null;
    }

    public GuisManager(Guis gui) {
        this.gui = PortalsPlus.INSTANCE.getGuis().get(gui);
    }

    public void openGui(Player p) {
        gui.processGuiOpen(p);
    }

    public void loadGuis() {
        new CreatorGui().loadCreatorGui();
    }
}