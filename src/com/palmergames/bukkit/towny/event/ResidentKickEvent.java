package com.palmergames.bukkit.towny.event;


import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import com.palmergames.bukkit.towny.object.Resident;

public class ResidentKickEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
   	private Resident resident;

    	public ResidentKickEvent (Resident resident){
    	super();
    	this.resident = resident;
    	}
       	
       	     
        public Resident getResident() {
            return resident;
        }
     
        public HandlerList getHandlers() {
            return handlers;
        }
     
        public static HandlerList getHandlerList() {
            return handlers;
        }
    }