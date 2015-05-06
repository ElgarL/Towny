package com.palmergames.bukkit.towny.event;

import org.bukkit.event.HandlerList;

import com.palmergames.bukkit.towny.object.Town;


public class TownyTownUpkeepEvent extends TownyUpkeepEvent  {

    private static final HandlerList handlers = new HandlerList();
    
    private Town	town;

    @Override
    public HandlerList getHandlers() {
    	
        return handlers;
    }
    
    public static HandlerList getHandlerList() {

		return handlers;
	}

    public TownyTownUpkeepEvent(Town town,Double upkeep) {
        super(upkeep);
        this.town = town;
    }

    /**
    *
    * @return the town
    */
   public Town getTown() {
       return this.town;
   }
    
}