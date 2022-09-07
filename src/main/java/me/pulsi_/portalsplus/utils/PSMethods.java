package me.pulsi_.portalsplus.utils;

import me.pulsi_.portalsplus.PortalsPlus;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PSMethods {

    public static boolean isPlayer(CommandSender s) {
        if (s instanceof Player) return true;
        PSMessages.send(s, "NOT_PLAYER");
        return false;
    }

    public static boolean hasPermission(CommandSender s, String permission) {
        if (s.hasPermission(permission)) return true;
        PSMessages.send(s, "NO_PERMISSION", "%permission%$" + permission);
        return false;
    }

    public static void spawnParticle(Location loc, String particlePath) {
        if (loc == null || loc.getWorld() == null || particlePath == null) return;
        Bukkit.getScheduler().runTaskAsynchronously(PortalsPlus.INSTANCE, () -> {
            String[] properties = particlePath.split(" ");
            if (properties.length < 6) {
                PSLogger.error("Invalid particle configuration for: " + particlePath + ". Please correct it as soon as possible!");
                return;
            }
            try {
                Particle particle = Particle.valueOf(properties[0]);
                int count = Integer.parseInt(properties[1]);
                double x = Double.parseDouble(properties[2]), y = Double.parseDouble(properties[3]), z = Double.parseDouble(properties[4]);
                double extra = Double.parseDouble(properties[5]);
                loc.getWorld().spawnParticle(particle, loc, count, x, y, z, extra, null, true);
            } catch (IllegalArgumentException e) {
                PSLogger.error(particlePath + " is an invalid particle configuration! " + e.getMessage());
            }
        });
    }

    public static String getStringLocation(Location location) {
        return location.toString().replace("Location{world=CraftWorld{name=", "").replace("},x=", " ")
                .replace(",y=", " ").replace(",z=", " ").replace(",pitch=", " ")
                .replace(",yaw=", " ").replace("}", "");
    }

    public static Location getLocation(String path) {
        if (path == null) return null;
        String[] s = path.split(" ");
        Location loc;
        try {
            loc = new Location(Bukkit.getWorld(s[0]),
                    Double.parseDouble(s[1]),
                    Double.parseDouble(s[2]),
                    Double.parseDouble(s[3]),
                    Float.parseFloat(s[5]),
                    Float.parseFloat(s[4]));
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            PSLogger.error("\"" + path + "\" Is an invalid location!");
            loc = null;
        }
        return loc;
    }

    public static void sendActionBar(Player player, String text) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(PSChat.color(text)).create());
    }

    public static boolean isLegacy() {
        String v = PortalsPlus.VERSION;
        return v.contains("1_7") || v.contains("1_8") || v.contains("1_9") || v.contains("1_10") || v.contains("1_11") || v.contains("1_12");
    }
}