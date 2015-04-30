package com.palmergames.bukkit.towny.event;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Created by NeumimTo on 30.4.2015.
 * This event is fired when player teleports to a town
 * @see com.palmergames.bukkit.towny.command.TownCommand#townSpawn(org.bukkit.entity.Player, String[], com.palmergames.bukkit.towny.object.Town, String, Boolean)
 */
public class PlayerTownTeleportEvent extends PlayerEvent implements Cancellable {

    private Town destination;
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private boolean isOutpost;
    public PlayerTownTeleportEvent(Player who, Town destination, Boolean outpost) {
        super(who);
        this.destination = destination;
        this.isOutpost = outpost;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return this.handlers;
    }

    /**
     *
     * @return The town, where is player being teleported
     */
    public Town getDestination() {
        return this.destination;
    }

    /**
     *
     * @param destination The town, where player will be teleported
     */
    public void setDestination(Town destination) {
        this.destination = destination;
    }

    /**
     *
     * @return true if player is teleporting to outpost
     */
    public boolean isOutpost() {
        return this.isOutpost;
    }
}
