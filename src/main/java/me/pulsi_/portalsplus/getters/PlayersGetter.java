package me.pulsi_.portalsplus.getters;

import me.pulsi_.portalsplus.PortalsPlus;
import me.pulsi_.portalsplus.objects.PSPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayersGetter {

    private final Player p;
    private final PSPlayer player;

    public PlayersGetter(Player player) {
        this.p = player;
        PortalsPlus.INSTANCE.getPlayers().putIfAbsent(p.getUniqueId(), new PSPlayer());
        this.player = PortalsPlus.INSTANCE.getPlayers().get(p.getUniqueId());
    }

    public PSPlayer getPSPlayer() {
        return player;
    }

    public void unregister() {
        PortalsPlus.INSTANCE.getPlayers().remove(p.getUniqueId());
    }

    public HashMap<UUID, PSPlayer> getPSPlayers() {
        return PortalsPlus.INSTANCE.getPlayers();
    }
}