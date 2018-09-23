package com.palmergames.bukkit.towny.event;

import com.palmergames.bukkit.towny.object.WorldCoord;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Author: Chris H (Zren / Shade)
 * Date: 4/15/12
 */
public class PlayerChangePlotEvent extends PlayerEvent implements Cancellable{

	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled = false;;

	private WorldCoord from;
	private WorldCoord to;
	private PlayerMoveEvent moveEvent;
	
	@Override
    public HandlerList getHandlers() {
    	
        return handlers;
    }
    
    public static HandlerList getHandlerList() {

		return handlers;
	}
	
	public PlayerChangePlotEvent(Player player, WorldCoord from, WorldCoord to, PlayerMoveEvent moveEvent) {

		super(player);
		this.from = from;
		this.to = to;
		this.moveEvent = moveEvent;
	}

	public WorldCoord getFrom() {

		return from;
	}

	@Override
	public boolean isCancelled(){
		return cancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		this.cancelled = b;
	}

	public PlayerMoveEvent getMoveEvent() {
		
		return moveEvent;
	}
	
	public WorldCoord getTo() {

		return to;
	}

}
