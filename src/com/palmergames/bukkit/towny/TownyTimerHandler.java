/**
 * 
 */
package com.palmergames.bukkit.towny;

import static com.palmergames.bukkit.towny.object.TownyObservableType.NEW_DAY;
import static com.palmergames.bukkit.towny.object.TownyObservableType.TOGGLE_REPEATING_TIMER;
import static com.palmergames.bukkit.towny.object.TownyObservableType.TOGGLE_DAILY_TIMER;
import static com.palmergames.bukkit.towny.object.TownyObservableType.TOGGLE_HEALTH_REGEN;
import static com.palmergames.bukkit.towny.object.TownyObservableType.TOGGLE_MOB_REMOVAL;
import static com.palmergames.bukkit.towny.object.TownyObservableType.TOGGLE_TELEPORT_WARMUP;
import static com.palmergames.bukkit.towny.object.TownyObservableType.TOGGLE_DRAW_SMOKE_TIMER;

import java.util.Calendar;
import java.util.TimeZone;

import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.tasks.DailyTimerTask;
import com.palmergames.bukkit.towny.tasks.DrawSmokeTask;
import com.palmergames.bukkit.towny.tasks.HealthRegenTimerTask;
import com.palmergames.bukkit.towny.tasks.MobRemovalTimerTask;
import com.palmergames.bukkit.towny.tasks.RepeatingTimerTask;
import com.palmergames.bukkit.towny.tasks.TeleportWarmupTimerTask;
import com.palmergames.bukkit.util.BukkitTools;
import com.palmergames.util.TimeMgmt;
import com.palmergames.util.TimeTools;


/**
 * Handler for all running timers
 * 
 * @author ElgarL
 *
 */
public class TownyTimerHandler{
	
	private static Towny plugin;
	private static TownyUniverse universe;
	
	public static void initialize (Towny plugin) {
		
		TownyTimerHandler.plugin = plugin;
		universe = plugin.getTownyUniverse();
	}
	
	private static int townyRepeatingTask = -1;
	private static int dailyTask = -1;
	private static int mobRemoveTask = -1;
	private static int healthRegenTask = -1;
	private static int teleportWarmupTask = -1;
	private static int drawSmokeTask = -1;

	public static void newDay() {

		if (!isDailyTimerRunning())
			toggleDailyTimer(true);
		//dailyTimer.schedule(new DailyTimerTask(this), 0);
		if (TownySettings.isEconomyAsync()) {
			if (BukkitTools.scheduleAsyncDelayedTask(new DailyTimerTask(plugin),0L) == -1)
				TownyMessaging.sendErrorMsg("Could not schedule newDay.");
		} else {
			if (BukkitTools.scheduleSyncDelayedTask(new DailyTimerTask(plugin),0L) == -1)
				TownyMessaging.sendErrorMsg("Could not schedule newDay.");
		}
		universe.setChangedNotify(NEW_DAY);
	}

	public static void toggleTownyRepeatingTimer(boolean on) {

		if (on && !isTownyRepeatingTaskRunning()) {
			townyRepeatingTask = BukkitTools.scheduleSyncRepeatingTask(new RepeatingTimerTask(plugin), 0, TimeTools.convertToTicks(1L));
			if (townyRepeatingTask == -1)
				TownyMessaging.sendErrorMsg("Could not schedule Towny Timer Task.");
		} else if (!on && isTownyRepeatingTaskRunning()) {
			BukkitTools.getScheduler().cancelTask(townyRepeatingTask);
			townyRepeatingTask = -1;
		}
		universe.setChangedNotify(TOGGLE_REPEATING_TIMER);
	}

	public static void toggleMobRemoval(boolean on) {

		if (on && !isMobRemovalRunning()) {
			mobRemoveTask = BukkitTools.scheduleSyncRepeatingTask(new MobRemovalTimerTask(plugin, BukkitTools.getServer()), 0, TimeTools.convertToTicks(TownySettings.getMobRemovalSpeed()));
			if (mobRemoveTask == -1)
				TownyMessaging.sendErrorMsg("Could not schedule mob removal loop.");
		} else if (!on && isMobRemovalRunning()) {
			BukkitTools.getScheduler().cancelTask(mobRemoveTask);
			mobRemoveTask = -1;
		}
		universe.setChangedNotify(TOGGLE_MOB_REMOVAL);
	}

	public static void toggleDailyTimer(boolean on) {

		if (on && !isDailyTimerRunning()) {
			long timeTillNextDay = townyTime();
			TownyMessaging.sendMsg("Time until a New Day: " + TimeMgmt.formatCountdownTime(timeTillNextDay));
			
			if (TownySettings.isEconomyAsync())
				dailyTask = BukkitTools.scheduleAsyncRepeatingTask(new DailyTimerTask(plugin), TimeTools.convertToTicks(timeTillNextDay), TimeTools.convertToTicks(TownySettings.getDayInterval()));
			else
				dailyTask = BukkitTools.scheduleSyncRepeatingTask(new DailyTimerTask(plugin), TimeTools.convertToTicks(timeTillNextDay), TimeTools.convertToTicks(TownySettings.getDayInterval()));
			
			if (dailyTask == -1)
				TownyMessaging.sendErrorMsg("Could not schedule new day loop.");
		} else if (!on && isDailyTimerRunning()) {
			BukkitTools.getScheduler().cancelTask(dailyTask);
			dailyTask = -1;
		}
		universe.setChangedNotify(TOGGLE_DAILY_TIMER);
	}

	public static void toggleHealthRegen(boolean on) {

		if (on && !isHealthRegenRunning()) {
			healthRegenTask = BukkitTools.scheduleSyncRepeatingTask(new HealthRegenTimerTask(plugin, BukkitTools.getServer()), 0, TimeTools.convertToTicks(TownySettings.getHealthRegenSpeed()));
			if (healthRegenTask == -1)
				TownyMessaging.sendErrorMsg("Could not schedule health regen loop.");
		} else if (!on && isHealthRegenRunning()) {
			BukkitTools.getScheduler().cancelTask(healthRegenTask);
			healthRegenTask = -1;
		}
		universe.setChangedNotify(TOGGLE_HEALTH_REGEN);
	}

	public static void toggleTeleportWarmup(boolean on) {

		if (on && !isTeleportWarmupRunning()) {
			teleportWarmupTask = BukkitTools.scheduleSyncRepeatingTask(new TeleportWarmupTimerTask(plugin), 0, 20);
			if (teleportWarmupTask == -1)
				TownyMessaging.sendErrorMsg("Could not schedule teleport warmup loop.");
		} else if (!on && isTeleportWarmupRunning()) {
			BukkitTools.getScheduler().cancelTask(teleportWarmupTask);
			teleportWarmupTask = -1;
		}
		universe.setChangedNotify(TOGGLE_TELEPORT_WARMUP);
	}
	
	public static void toggleDrawSmokeTask(boolean on) {
		if (on && !isDrawSmokeTaskRunning()) {
			drawSmokeTask = BukkitTools.scheduleAsyncRepeatingTask(new DrawSmokeTask(plugin), 0, 100);
			if (drawSmokeTask == -1)
				TownyMessaging.sendErrorMsg("Could not schedule draw smoke loop");			
		} else if (!on && isDrawSmokeTaskRunning()) {
			BukkitTools.getScheduler().cancelTask(drawSmokeTask);
			drawSmokeTask = -1;
		}
		universe.setChangedNotify(TOGGLE_DRAW_SMOKE_TIMER);			
	}

	public static boolean isTownyRepeatingTaskRunning() {

		return townyRepeatingTask != -1;

	}

	public static boolean isMobRemovalRunning() {

		return mobRemoveTask != -1;
	}

	public static boolean isDailyTimerRunning() {

		return dailyTask != -1;
	}

	public static boolean isHealthRegenRunning() {

		return healthRegenTask != -1;
	}

	public static boolean isTeleportWarmupRunning() {

		return teleportWarmupTask != -1;
	}
	
	public static boolean isDrawSmokeTaskRunning() {
		
		return drawSmokeTask != -1;
	}
	
	/**
	 * Calculates the time in seconds until the next new day event.
	 * TimeZone specific, including daylight savings.
	 * 
	 * @return seconds until event
	 */
	public static Long townyTime() {

		Long secondsInDay = TownySettings.getDayInterval();

		// Get Calendar instance
		Calendar now = Calendar.getInstance();

		// Get current TimeZone
		TimeZone timeZone = now.getTimeZone();
		
		// Get current system time in milliseconds
		Long timeMilli = System.currentTimeMillis();
		
		// Calculate the TimeZone specific offset (including DST)
		Integer timeOffset = timeZone.getOffset(timeMilli)/1000;

		return (secondsInDay + (TownySettings.getNewDayTime() - ((timeMilli/1000) % secondsInDay) - timeOffset)) % secondsInDay;
	}

}
