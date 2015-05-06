package com.palmergames.bukkit.towny.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class TownyUpkeepEvent extends Event  {

    private static final HandlerList handlers = new HandlerList();
    
    private double upkeep;

    public TownyUpkeepEvent(Double upkeep) {
		this.upkeep = upkeep;
	}

	@Override
    public HandlerList getHandlers() {
    	
        return handlers;
    }
    
    public static HandlerList getHandlerList() {

		return handlers;
	}
    
    public void setUpkeep(Double cost) {
        this.upkeep = cost;
    }

    /**
     *
     * @return the upkeep
     */
    public double getUpkeep() {
        return upkeep;
    }
    
}