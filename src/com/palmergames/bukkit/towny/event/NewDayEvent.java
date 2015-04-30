package com.palmergames.bukkit.towny.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by NeumimTo on 30.4.2015.
 * This event is fired when Towny starts a new day
 * @see com.palmergames.bukkit.towny.TownyTimerHandler#newDay()
 */
public class NewDayEvent extends Event {
    private static HandlerList handlerList = new HandlerList();

    public NewDayEvent(boolean isAsync) {
        super(isAsync);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
