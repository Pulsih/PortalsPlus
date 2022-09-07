package me.pulsi_.portalsplus.guis;

import me.pulsi_.portalsplus.enums.Guis;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class PSGui implements InventoryHolder {

    private Guis type;
    private String title;
    private int size;
    private ItemStack[] content;

    public Guis getType() {
        return type;
    }

    public void setType(Guis type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ItemStack[] getContent() {
        return content;
    }

    public void setContent(ItemStack[] content) {
        this.content = content;
    }

    public void processGuiClick(InventoryClickEvent e) { }

    public void processGuiOpen(Player p) { }

    @Override
    public Inventory getInventory() {
        return null;
    }
}