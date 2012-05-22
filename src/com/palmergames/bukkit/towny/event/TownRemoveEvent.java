package com.palmergames.bukkit.towny.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import com.palmergames.bukkit.towny.object.Town;

public class TownRemoveEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
       	private Town town;
       	
    	public TownRemoveEvent(Town town) {
    		this.town = town;
       	}
        public Town getTown(){
        	return town;
        }
     
        public HandlerList getHandlers() {
            return handlers;
        }
     
        public static HandlerList getHandlerList() {
            return handlers;
        }
    }
