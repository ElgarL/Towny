package com.palmergames.bukkit.towny.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

public class ResidentJoinEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
       	private Resident resident;
       	private Town town;
       	
    	public ResidentJoinEvent(Resident resident, Town town) {
    		this.town = town;
    		this.resident = resident;
       	}
       	     
        public Resident getResident() {
            return resident;
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