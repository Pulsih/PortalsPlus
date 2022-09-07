package me.pulsi_.portalsplus.guis;

import me.pulsi_.portalsplus.utils.PSChat;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private final ItemStack item = new ItemStack(Material.STONE);

    public ItemBuilder setType(Material type) {
        item.setType(type);
        return this;
    }

    public ItemBuilder setType(String type) {
        Material material;
        try {
            material = Material.valueOf(type);
        } catch (IllegalArgumentException e) {
            material = Material.STONE;
        }
        item.setType(material);
        return this;
    }

    public ItemBuilder setType(String type, String fallBack) {
        Material material;
        try {
            material = Material.valueOf(type);
        } catch (IllegalArgumentException e) {
            material = Material.valueOf(fallBack);
        }
        item.setType(material);
        return this;
    }

    public ItemBuilder setDisplayname(String displayname) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(PSChat.color(displayname));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        ItemMeta meta = item.getItemMeta();

        List<String> newLore = new ArrayList<>();
        for (String line : lore) newLore.add(PSChat.color(line));
        meta.setLore(newLore);

        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder setGlowing() {
        ItemMeta meta = item.getItemMeta();
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return this;
    }

    public ItemStack getItem() {
        return item;
    }
}