package me.pulsi_.portalsplus.utils;

import org.bukkit.Bukkit;

public class PSLogger {

    public static void log(String log) {
        Bukkit.getConsoleSender().sendMessage(PSChat.color(log));
    }

    public static void log(String... logs) {
        for (String log : logs) Bukkit.getConsoleSender().sendMessage(PSChat.color(log));
    }

    public static void error(String error) {
        log(PSChat.prefix + " &8[&cERROR&8] &c" + error);
    }

    public static void warn(Object warn) {
        log(PSChat.prefix + " &8[&eWARN&8] &e" + warn);
    }

    public static void info(Object info) {
        log(PSChat.prefix + " &8[&9INFO&8] &9" + info);
    }
}