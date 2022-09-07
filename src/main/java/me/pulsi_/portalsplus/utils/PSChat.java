package me.pulsi_.portalsplus.utils;

import net.md_5.bungee.api.ChatColor;

public class PSChat {

    public static String prefix = "&d&lPortals&6&lPlus";

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}