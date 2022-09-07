package me.pulsi_.portalsplus.utils;

import me.pulsi_.portalsplus.PortalsPlus;
import me.pulsi_.portalsplus.enums.Configs;
import me.pulsi_.portalsplus.managers.ConfigManager;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PSMessages {

    private static final HashMap<String, List<String>> messages = new HashMap<>();

    public static void send(Player p, String message, boolean fromString) {
        if (!fromString) {
            send(p, message);
            return;
        }
        p.sendMessage(PSChat.color(message.replace("%prefix%", getPrefix())));
    }

    public static void send(CommandSender p, String message, boolean fromString) {
        if (!fromString) {
            send(p, message);
            return;
        }
        p.sendMessage(PSChat.color(message.replace("%prefix%", getPrefix())));
    }

    public static void send(Player p, String message, List<String> stringsToReplace, boolean fromString) {
        if (!fromString) {
            send(p, message);
            return;
        }
        for (String stringToReplace : stringsToReplace) {
            if (!stringToReplace.contains("$")) continue;
            String oldChar = stringToReplace.split("\\$")[0];
            String replacement = stringToReplace.split("\\$")[1];
            message = message.replace(oldChar, replacement);
        }
        p.sendMessage(PSChat.color(message.replace("%prefix%", getPrefix())));
    }

    public static void send(Player p, String identifier) {
        if (!messages.containsKey(identifier)) return;

        List<String> listOfMessages = messages.get(identifier);
        if (listOfMessages.isEmpty()) {
            PSLogger.error("The message \"" + identifier + "\" is missing in the messages file!");
            return;
        }

        for (String message : listOfMessages) p.sendMessage(PSChat.color(message.replace("%prefix%", getPrefix())));
    }

    public static void send(Player p, String identifier, String... stringsToReplace) {
        if (!messages.containsKey(identifier)) return;

        List<String> listOfMessages = messages.get(identifier);
        if (listOfMessages.isEmpty()) {
            PSLogger.error("The message \"" + identifier + "\" is missing in the messages file!");
            return;
        }

        for (String message : listOfMessages) {
            for (String stringToReplace : stringsToReplace) {
                if (!stringToReplace.contains("$")) continue;
                String oldChar = stringToReplace.split("\\$")[0];
                String replacement = stringToReplace.split("\\$")[1];
                message = message.replace(oldChar, replacement);
            }
            p.sendMessage(PSChat.color(message.replace("%prefix%", getPrefix())));
        }
    }

    public static void send(Player p, String identifier, List<String> stringsToReplace) {
        if (!messages.containsKey(identifier)) return;

        List<String> listOfMessages = messages.get(identifier);
        if (listOfMessages.isEmpty()) {
            PSLogger.error("The message \"" + identifier + "\" is missing in the messages file!");
            return;
        }

        for (String message : listOfMessages) {
            for (String stringToReplace : stringsToReplace) {
                if (!stringToReplace.contains("$")) continue;
                String oldChar = stringToReplace.split("\\$")[0];
                String replacement = stringToReplace.split("\\$")[1];
                message = message.replace(oldChar, replacement);
            }
            p.sendMessage(PSChat.color(message.replace("%prefix%", getPrefix())));
        }
    }

    public static void send(CommandSender s, String identifier) {
        if (!messages.containsKey(identifier)) return;

        List<String> listOfMessages = messages.get(identifier);
        if (listOfMessages.isEmpty()) {
            PSLogger.error("The message \"" + identifier + "\" is missing in the messages file!");
            return;
        }

        for (String message : listOfMessages) s.sendMessage(PSChat.color(message.replace("%prefix%", getPrefix())));
    }

    public static void send(CommandSender s, String identifier, String... stringsToReplace) {
        if (!messages.containsKey(identifier)) return;

        List<String> listOfMessages = messages.get(identifier);
        if (listOfMessages.isEmpty()) {
            PSLogger.error("The message \"" + identifier + "\" is missing in the messages file!");
            return;
        }

        for (String message : listOfMessages) {
            for (String stringToReplace : stringsToReplace) {
                if (!stringToReplace.contains("$")) continue;
                String oldChar = stringToReplace.split("\\$")[0];
                String replacement = stringToReplace.split("\\$")[1];
                message = message.replace(oldChar, replacement);
            }
            s.sendMessage(PSChat.color(message.replace("%prefix%", getPrefix())));
        }
    }

    public static void send(CommandSender s, String identifier, List<String> stringsToReplace) {
        if (!messages.containsKey(identifier)) return;

        List<String> listOfMessages = messages.get(identifier);
        if (listOfMessages.isEmpty()) {
            PSLogger.error("The message \"" + identifier + "\" is missing in the messages file!");
            return;
        }

        for (String message : listOfMessages) {
            for (String stringToReplace : stringsToReplace) {
                if (!stringToReplace.contains("$")) continue;
                String oldChar = stringToReplace.split("\\$")[0];
                String replacement = stringToReplace.split("\\$")[1];
                message = message.replace(oldChar, replacement);
            }
            s.sendMessage(PSChat.color(message.replace("%prefix%", getPrefix())));
        }
    }

    public static void loadMessages() {
        messages.clear();
        FileConfiguration config = PortalsPlus.INSTANCE.getConfigManager().getConfig(Configs.MESSAGES);
        for (String path : config.getConfigurationSection("").getKeys(false)) {
            List<String> listOfMessages = config.getStringList(path);
            if (listOfMessages.isEmpty()) {
                String message = config.getString(path);
                messages.put(path, Collections.singletonList(message));
                continue;
            }
            messages.put(path, config.getStringList(path));
        }
    }

    public static String addPrefix(String message) {
        return PSChat.color(message.replace("%prefix%", getPrefix()));
    }

    private static String getPrefix() {
        if (!messages.containsKey("PREFIX")) return PSChat.prefix;
        List<String> prefix = messages.get("PREFIX");
        if (prefix.isEmpty()) return PSChat.prefix;
        return prefix.get(0);
    }
}