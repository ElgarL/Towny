package com.palmergames.bukkit.towny.war.flagwar.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.palmergames.bukkit.towny.war.flagwar.Cell;

public class CellDefendedEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {

		return handlers;
	}

	public static HandlerList getHandlerList() {

		return handlers;
	}

	//////////////////////////////

	private Player player;
	private Cell cell;

	public CellDefendedEvent(Player player, Cell cell) {

		super();
		this.player = player;
		this.cell = cell;
	}

	public Player getPlayer() {

		return player;
	}

	public Cell getCell() {

		return cell;
	}
}
