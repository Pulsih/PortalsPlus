package me.pulsi_.portalsplus.objects;

import me.pulsi_.portalsplus.guis.PSGui;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class PSPlayer {

    private int x, y, z;
    private PortalEditor portalEditor;
    private Inventory openedInventory;
    private PSGui openedGui;
    private ItemStack[] content;
    private BukkitTask actionbarInformer;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public PortalEditor getPortalEditor() {
        return portalEditor;
    }

    public void setPortalEditor(PortalEditor portalEditor) {
        this.portalEditor = portalEditor;
    }

    public Inventory getOpenedInventory() {
        return openedInventory;
    }

    public void setOpenedInventory(Inventory openedInventory) {
        this.openedInventory = openedInventory;
    }

    public PSGui getOpenedGui() {
        return openedGui;
    }

    public void setOpenedGui(PSGui openedGui) {
        this.openedGui = openedGui;
    }

    public ItemStack[] getContent() {
        return content;
    }

    public void setContent(ItemStack[] content) {
        this.content = content;
    }

    public BukkitTask getActionbarInformer() {
        return actionbarInformer;
    }

    public void setActionbarInformer(BukkitTask actionbarInformer) {
        this.actionbarInformer = actionbarInformer;
    }

    public void cancelActionbarInformer() {
        if (this.actionbarInformer != null) this.actionbarInformer.cancel();
    }
}