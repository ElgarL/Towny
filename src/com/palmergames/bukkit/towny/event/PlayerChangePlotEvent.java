package com.palmergames.bukkit.towny.event;

import com.palmergames.bukkit.towny.object.WorldCoord;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Author: Chris H (Zren / Shade)
 * Date: 4/15/12
 */
public class PlayerChangePlotEvent extends PlayerEvent {

	private static final HandlerList handlers = new HandlerList();
	private WorldCoord from;
	private WorldCoord to;
	private PlayerMoveEvent moveEvent;
	
	public PlayerChangePlotEvent(Player player, WorldCoord from, WorldCoord to, PlayerMoveEvent moveEvent) {

		super(player);
		this.from = from;
		this.to = to;
		this.moveEvent = moveEvent;
	}

	public WorldCoord getFrom() {

		return from;
	}

	public PlayerMoveEvent getMoveEvent() {
		
		return moveEvent;
	}
	
	public WorldCoord getTo() {

		return to;
	}

	@Override
	public HandlerList getHandlers() {

		return handlers;
	}

	public static HandlerList getHandlerList() {

		return handlers;
	}
}
