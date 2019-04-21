package com.palmergames.bukkit.towny.event;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.WorldCoord;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerEnterTownEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private Town enteredtown;
	private PlayerMoveEvent pme;
	private WorldCoord from;
	private WorldCoord to;
	private Player player;

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public PlayerEnterTownEvent(Player player,WorldCoord to, WorldCoord from, Town enteredtown, PlayerMoveEvent pme) {
		this.enteredtown = enteredtown;
		this.player = player;
		this.from = from;
		this.pme = pme;
		this.to = to;
	}

	public Player getPlayer() {
		return player;
	}

	public PlayerMoveEvent getPlayerMoveEvent() {
		return pme;
	}

	public Town getEnteredtown() {
		return enteredtown;
	}

	public WorldCoord getFrom() {
		return from;
	}

	public WorldCoord getTo() {
		return to;
	}
}
