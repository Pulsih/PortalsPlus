package me.pulsi_.portalsplus.events;

import me.pulsi_.portalsplus.objects.Portal;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PSPortalEnterEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();

    private boolean cancelled;
    private final Player player;
    private final Location fromLocation;
    private final Location toLocation;
    private final Portal fromPortal;
    private final Portal toPortal;

    public PSPortalEnterEvent(Player player, Location fromLocation, Location toLocation, Portal fromPortal, Portal toPortal) {
        this.player = player;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.fromPortal = fromPortal;
        this.toPortal = toPortal;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getFromLocation() {
        return fromLocation;
    }

    public Location getToLocation() {
        return toLocation;
    }

    public Portal getFromPortal() {
        return fromPortal;
    }

    public Portal getToPortal() {
        return toPortal;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public String getEventName() {
        return "PSPortalEnterEvent";
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}