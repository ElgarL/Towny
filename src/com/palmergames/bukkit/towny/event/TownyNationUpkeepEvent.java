package com.palmergames.bukkit.towny.event;

import org.bukkit.event.HandlerList;

import com.palmergames.bukkit.towny.object.Nation;


public class TownyNationUpkeepEvent extends TownyUpkeepEvent  {

    private static final HandlerList handlers = new HandlerList();
    
    private Nation	nation;

    @Override
    public HandlerList getHandlers() {
    	
        return handlers;
    }
    
    public static HandlerList getHandlerList() {

		return handlers;
	}

    public TownyNationUpkeepEvent(Nation nation, Double upkeep) {
    	super(upkeep);
        this.nation = nation;
    }

    
    /**
    *
    * @return the nation
    */
   public Nation getNation() {
       return this.nation;
   }
    
}