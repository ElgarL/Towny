package com.palmergames.bukkit.towny;

import com.earth2me.essentials.Essentials;
import com.palmergames.bukkit.metrics.BStats;
import com.palmergames.bukkit.metrics.MCStats;
import com.palmergames.bukkit.towny.chat.TNCRegister;
import com.palmergames.bukkit.towny.command.InviteCommand;
import com.palmergames.bukkit.towny.command.NationCommand;
import com.palmergames.bukkit.towny.command.PlotCommand;
import com.palmergames.bukkit.towny.command.ResidentCommand;
import com.palmergames.bukkit.towny.command.TownCommand;
import com.palmergames.bukkit.towny.command.TownyAdminCommand;
import com.palmergames.bukkit.towny.command.TownyCommand;
import com.palmergames.bukkit.towny.command.TownyWorldCommand;
import com.palmergames.bukkit.towny.command.commandobjects.AcceptCommand;
import com.palmergames.bukkit.towny.command.commandobjects.CancelCommand;
import com.palmergames.bukkit.towny.command.commandobjects.ConfirmCommand;
import com.palmergames.bukkit.towny.command.commandobjects.DenyCommand;
import com.palmergames.bukkit.towny.confirmations.ConfirmationHandler;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.huds.HUDManager;
import com.palmergames.bukkit.towny.invites.InviteHandler;
import com.palmergames.bukkit.towny.listeners.TownyBlockListener;
import com.palmergames.bukkit.towny.listeners.TownyCustomListener;
import com.palmergames.bukkit.towny.listeners.TownyEntityListener;
import com.palmergames.bukkit.towny.listeners.TownyEntityMonitorListener;
import com.palmergames.bukkit.towny.listeners.TownyLoginListener;
import com.palmergames.bukkit.towny.listeners.TownyPlayerListener;
import com.palmergames.bukkit.towny.listeners.TownyVehicleListener;
import com.palmergames.bukkit.towny.listeners.TownyWeatherListener;
import com.palmergames.bukkit.towny.listeners.TownyWorldListener;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.PlayerCache;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.palmergames.bukkit.towny.permissions.BukkitPermSource;
import com.palmergames.bukkit.towny.permissions.GroupManagerSource;
import com.palmergames.bukkit.towny.permissions.NullPermSource;
import com.palmergames.bukkit.towny.permissions.PEXSource;
import com.palmergames.bukkit.towny.permissions.TownyPerms;
import com.palmergames.bukkit.towny.permissions.VaultPermSource;
import com.palmergames.bukkit.towny.permissions.bPermsSource;
import com.palmergames.bukkit.towny.regen.TownyRegenAPI;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import com.palmergames.bukkit.towny.war.flagwar.TownyWar;
import com.palmergames.bukkit.towny.war.flagwar.listeners.TownyWarBlockListener;
import com.palmergames.bukkit.towny.war.flagwar.listeners.TownyWarCustomListener;
import com.palmergames.bukkit.towny.war.flagwar.listeners.TownyWarEntityListener;
import com.palmergames.bukkit.util.BukkitTools;
import com.palmergames.util.FileMgmt;
import com.palmergames.util.JavaUtil;
import com.palmergames.util.StringMgmt;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Towny Plugin for Bukkit
 * 
 * Website: http://code.google.com/a/eclipselabs.org/p/towny/ Source:
 * http://code.google.com/a/eclipselabs.org/p/towny/source/browse/
 * 
 * @author Shade, ElgarL
 */

public class Towny extends JavaPlugin {

	private String version = "2.0.0";

	private final TownyPlayerListener playerListener = new TownyPlayerListener(this);
	private final TownyVehicleListener vehicleListener = new TownyVehicleListener(this);
	private final TownyBlockListener blockListener = new TownyBlockListener(this);
	private final TownyCustomListener customListener = new TownyCustomListener(this);
	private final TownyEntityListener entityListener = new TownyEntityListener(this);
	private final TownyWeatherListener weatherListener = new TownyWeatherListener(this);
	private final TownyEntityMonitorListener entityMonitorListener = new TownyEntityMonitorListener(this);
	private final TownyWorldListener worldListener = new TownyWorldListener(this);
	private final TownyWarBlockListener townyWarBlockListener = new TownyWarBlockListener(this);
	private final TownyWarCustomListener townyWarCustomListener = new TownyWarCustomListener(this);
	private final TownyWarEntityListener townyWarEntityListener = new TownyWarEntityListener(this);
	private final TownyLoginListener loginListener = new TownyLoginListener(this);
	private final HUDManager HUDManager = new HUDManager(this);

	private TownyUniverse townyUniverse;

	private Map<String, PlayerCache> playerCache = Collections.synchronizedMap(new HashMap<>());

	private Essentials essentials = null;
	private boolean citizens2 = false;

	private boolean error = false;
	
	private static Towny plugin;
	
	public Towny() {
		
		plugin = this;
	}

	@Override
	public void onEnable() {

		System.out.println("====================      Towny      ========================");
		
		/*
		 * Register bStats Metrics
		 *  
		 */
		@SuppressWarnings("unused")
		BStats bStatsMetrics = new BStats(this);
		
		/*
		 * Register MCStats Metrics
		 */
		try {
		    MCStats mcStatsMetrics = new MCStats(this);
		    mcStatsMetrics.start();
		} catch (IOException e) {
			System.err.println("[Towny] Error setting up MCStats metrics");
		}


		version = this.getDescription().getVersion();

		townyUniverse = new TownyUniverse(this);

		// Setup classes
		BukkitTools.initialize(this);
		TownyTimerHandler.initialize(this);
		TownyEconomyHandler.initialize(this);
		TownyFormatter.initialize(this);
		TownyRegenAPI.initialize(this);
		PlayerCacheUtil.initialize(this);
		TownyPerms.initialize(this);
		InviteHandler.initialize(this);
		ConfirmationHandler.initialize(this);

		if (load()) {
			// Setup bukkit command interfaces
			registerSpecialCommands();
			getCommand("townyadmin").setExecutor(new TownyAdminCommand(this));
			getCommand("townyworld").setExecutor(new TownyWorldCommand(this));
			getCommand("resident").setExecutor(new ResidentCommand(this));
			getCommand("towny").setExecutor(new TownyCommand(this));
			getCommand("town").setExecutor(new TownCommand(this));
			getCommand("nation").setExecutor(new NationCommand(this));
			getCommand("plot").setExecutor(new PlotCommand(this));
			getCommand("invite").setExecutor(new InviteCommand(this));

			TownyWar.onEnable();

			if (TownySettings.isTownyUpdating(getVersion())) {
				update();
			}

			// Register all child permissions for ranks
			TownyPerms.registerPermissionNodes();
		}

		registerEvents();

		TownyLogger.log.info("=============================================================");
		if (isError())
			TownyLogger.log.info("[WARNING] - ***** SAFE MODE ***** " + version);
		else
			TownyLogger.log.info("[Towny] Version: " + version + " - Mod Enabled");
		TownyLogger.log.info("=============================================================");

		if (!isError()) {
			// Re login anyone online. (In case of plugin reloading)
			for (Player player : BukkitTools.getOnlinePlayers())
				if (player != null)
					try {
						getTownyUniverse().onLogin(player);
					} catch (TownyException x) {
						TownyMessaging.sendErrorMsg(player, x.getMessage());
					}
		}
	}

	public void setWorldFlags() {

		for (Town town : TownyUniverse.getDataSource().getTowns()) {
			TownyMessaging.sendDebugMsg("[Towny] Setting flags for: " + town.getName());

			if (town.getWorld() == null) {
				TownyLogger.log.warning("[Towny Error] Detected an error with the world files. Attempting to repair");
				if (town.hasHomeBlock())
					try {
						TownyWorld world = town.getHomeBlock().getWorld();
						if (!world.hasTown(town)) {
							world.addTown(town);
							TownyUniverse.getDataSource().saveTown(town);
							TownyUniverse.getDataSource().saveWorld(world);
						}
					} catch (TownyException e) {
						// Error fetching homeblock
						TownyLogger.log.warning("[Towny Error] Failed get world data for: " + town.getName());
					}
				else
					TownyLogger.log.warning("[Towny Error] No Homeblock - Failed to detect world for: " + town.getName());
			}
		}

	}

	@Override
	public void onDisable() {

		System.out.println("==============================================================");

		if (TownyUniverse.getDataSource() != null && error == false)
			TownyUniverse.getDataSource().saveQueues();

		if (error == false)
			TownyWar.onDisable();

		if (TownyUniverse.isWarTime())
			getTownyUniverse().getWarEvent().toggleEnd();

		TownyTimerHandler.toggleTownyRepeatingTimer(false);
		TownyTimerHandler.toggleDailyTimer(false);
		TownyTimerHandler.toggleMobRemoval(false);
		TownyTimerHandler.toggleHealthRegen(false);
		TownyTimerHandler.toggleTeleportWarmup(false);
		TownyTimerHandler.toggleDrawSmokeTask(false);

		TownyRegenAPI.cancelProtectionRegenTasks();

		playerCache.clear();
		
		// Shut down our saving task.
		TownyUniverse.getDataSource().cancelTask();

		townyUniverse = null;

		System.out.println("[Towny] Version: " + version + " - Mod Disabled");
		System.out.println("=============================================================");

		TownyLogger.shutDown();
	}

	public boolean load() {

		if (!townyUniverse.loadSettings()) {
			setError(true);
			return false;
		}

		setupLogger();

		checkPlugins();

		setWorldFlags();

		// make sure the timers are stopped for a reset
		TownyTimerHandler.toggleTownyRepeatingTimer(false);
		TownyTimerHandler.toggleDailyTimer(false);
		TownyTimerHandler.toggleMobRemoval(false);
		TownyTimerHandler.toggleHealthRegen(false);
		TownyTimerHandler.toggleTeleportWarmup(false);
		TownyTimerHandler.toggleDrawSmokeTask(false);

		// Start timers
		TownyTimerHandler.toggleTownyRepeatingTimer(true);
		TownyTimerHandler.toggleDailyTimer(true);
		TownyTimerHandler.toggleMobRemoval(true);
		TownyTimerHandler.toggleHealthRegen(TownySettings.hasHealthRegen());
		TownyTimerHandler.toggleTeleportWarmup(TownySettings.getTeleportWarmupTime() > 0);
		TownyTimerHandler.toggleDrawSmokeTask(true);
		resetCache();

		return true;
	}

	private void checkPlugins() {

		List<String> using = new ArrayList<>();
		Plugin test;

		if (TownySettings.isUsingPermissions()) {
			test = getServer().getPluginManager().getPlugin("GroupManager");
			if (test != null) {
				// groupManager = (GroupManager)test;
				this.getTownyUniverse().setPermissionSource(new GroupManagerSource(this, test));
				using.add(String.format("%s v%s", "GroupManager", test.getDescription().getVersion()));
			} else {
				test = getServer().getPluginManager().getPlugin("PermissionsEx");
				if (test != null) {
					// permissions = (PermissionsEX)test;
					getTownyUniverse().setPermissionSource(new PEXSource(this, test));
					using.add(String.format("%s v%s", "PermissionsEX", test.getDescription().getVersion()));
				} else {
					test = getServer().getPluginManager().getPlugin("bPermissions");
					if (test != null) {
						// permissions = (Permissions)test;
						getTownyUniverse().setPermissionSource(new bPermsSource(this, test));
						using.add(String.format("%s v%s", "bPermissions", test.getDescription().getVersion()));
					} else {
							// Try Vault NOTE: Permissions 3 Was Removed and moved to Legacy!
							test = getServer().getPluginManager().getPlugin("Vault");
							if (test != null) {
								net.milkbowl.vault.chat.Chat chat = getServer().getServicesManager().load(net.milkbowl.vault.chat.Chat.class);
								if (chat == null) {
									// No Chat implementation
									test = null;
									// Fall back to BukkitPermissions below
								} else {
									getTownyUniverse().setPermissionSource(new VaultPermSource(this, chat));
									using.add(String.format("%s v%s", "Vault", test.getDescription().getVersion()));
								}
							}

							if (test == null) {
								getTownyUniverse().setPermissionSource(new BukkitPermSource(this));
								using.add("BukkitPermissions");
							}
						}
				}
			}
		} else {
			// Not using Permissions
			getTownyUniverse().setPermissionSource(new NullPermSource(this));
		}

		if (TownySettings.isUsingEconomy()) {

			if (TownyEconomyHandler.setupEconomy())
				using.add(TownyEconomyHandler.getVersion());
			else {
				TownyMessaging.sendErrorMsg("No compatible Economy plugins found. Install Vault.jar with any of the supported eco systems.");
				TownyMessaging.sendErrorMsg("If you do not want an economy to be used, set using_economy: false in your Towny config.yml.");
			}
		}

		test = getServer().getPluginManager().getPlugin("Essentials");
		if (test == null)
			TownySettings.setUsingEssentials(false);
		else if (TownySettings.isUsingEssentials()) {
			this.essentials = (Essentials) test;
			using.add(String.format("%s v%s", "Essentials", test.getDescription().getVersion()));
		}
		
		test = getServer().getPluginManager().getPlugin("Questioner");	
		if (test != null) {
			TownyMessaging.sendErrorMsg("Questioner.jar present on server, Towny no longer requires Questioner for invites/confirmations.");
			TownyMessaging.sendErrorMsg("You may safely remove Questioner.jar from your plugins folder.");
		}

		/*
		 * Test for Citizens2 so we can avoid removing their NPC's
		 */
		test = getServer().getPluginManager().getPlugin("Citizens");
		if (test != null) 
			if (getServer().getPluginManager().getPlugin("Citizens").isEnabled())
				citizens2 = test.getDescription().getVersion().startsWith("2");

		if (using.size() > 0)
			TownyLogger.log.info("[Towny] Using: " + StringMgmt.join(using, ", "));


		//Add our chat handler to TheNewChat via the API.
		if(Bukkit.getPluginManager().isPluginEnabled("TheNewChat")) {
			TNCRegister.initialize();
		}
		
		/*
		 * Leaving this out for the time being, at the request of the authors of EssentialsX
		 */
//		if (TownySettings.isUsingEssentials()){
//			TownyLogger.log.warning("Essentials detected: The Towny authors would like to make you");
//			TownyLogger.log.warning("aware that Essentials has been causing town and nation bank");
//			TownyLogger.log.warning("accounts to reset. Furthermore their handling of bank accounts");
//			TownyLogger.log.warning("has left vital town, nation, warchest and server accounts");
//			TownyLogger.log.warning("vulnerable to exploitation. Towny has made changes to stop");
//			TownyLogger.log.warning("these exploits from occuring but we cannot stop Essentials");
//			TownyLogger.log.warning("Economy from reseting bank accounts. Please change to another");
//			TownyLogger.log.warning("Essentials-type plugin as soon as you are able.");
//		}
			
	}

	private void registerEvents() {

		final PluginManager pluginManager = getServer().getPluginManager();

		if (!isError()) {
			// Have War Events get launched before regular events.
			pluginManager.registerEvents(townyWarBlockListener, this);
			pluginManager.registerEvents(townyWarEntityListener, this);
			
			// Huds
			pluginManager.registerEvents(HUDManager, this);

			// Manage player deaths and death payments
			pluginManager.registerEvents(entityMonitorListener, this);
			pluginManager.registerEvents(vehicleListener, this);
			pluginManager.registerEvents(weatherListener, this);
			pluginManager.registerEvents(townyWarCustomListener, this);
			pluginManager.registerEvents(customListener, this);
			pluginManager.registerEvents(worldListener, this);
			pluginManager.registerEvents(loginListener, this);
		}

		// Always register these events.
		pluginManager.registerEvents(playerListener, this);
		pluginManager.registerEvents(blockListener, this);
		pluginManager.registerEvents(entityListener, this);

	}

	private void update() {

		try {
			List<String> changeLog = JavaUtil.readTextFromJar("/ChangeLog.txt");
			boolean display = false;
			TownyLogger.log.info("------------------------------------");
			TownyLogger.log.info("[Towny] ChangeLog up until v" + getVersion());
			String lastVersion = TownySettings.getLastRunVersion(getVersion()).split("_")[0];
			for (String line : changeLog) { // TODO: crawl from the bottom, then
											// past from that index.
				if (line.startsWith("v" + lastVersion))
					display = true;
				if (display && line.replaceAll(" ", "").replaceAll("\t", "").length() > 0)
					TownyLogger.log.info(line);
			}
			TownyLogger.log.info("------------------------------------");
		} catch (IOException e) {
			TownyMessaging.sendDebugMsg("Could not read ChangeLog.txt");
		}
		TownySettings.setLastRunVersion(getVersion());
		
		TownyUniverse.getDataSource().saveAll();
		TownyUniverse.getDataSource().cleanup();
	}

	/**
	 * Fetch the TownyUniverse instance
	 * 
	 * @return TownyUniverse
	 */
	public TownyUniverse getTownyUniverse() {

		return townyUniverse;
	}

	public String getVersion() {

		return version;
	}

	/**
	 * @return the error
	 */
	public boolean isError() {

		return error;
	}

	/**
	 * @param error the error to set
	 */
	protected void setError(boolean error) {

		this.error = error;
	}

	// is permissions active
	public boolean isPermissions() {

		return TownySettings.isUsingPermissions();
	}

	// is Essentials active
	public boolean isEssentials() {

		return (TownySettings.isUsingEssentials() && (this.essentials != null));
	}

	// is Citizens2 active
	public boolean isCitizens2() {

		return citizens2;
	}

	/**
	 * @return Essentials object
	 * @throws TownyException - If Towny can't find Essentials.
	 */
	public Essentials getEssentials() throws TownyException {

		if (essentials == null)
			throw new TownyException("Essentials is not installed, or not enabled!");
		else
			return essentials;
	}

	public World getServerWorld(String name) throws NotRegisteredException {

		for (World world : BukkitTools.getWorlds())
			if (world.getName().equals(name))
				return world;

		throw new NotRegisteredException(String.format("A world called '$%s' has not been registered.", name));
	}

	public boolean hasCache(Player player) {

		return playerCache.containsKey(player.getName().toLowerCase());
	}

	public void newCache(Player player) {

		try {
			getTownyUniverse();
			playerCache.put(player.getName().toLowerCase(), new PlayerCache(TownyUniverse.getDataSource().getWorld(player.getWorld().getName()), player));
		} catch (NotRegisteredException e) {
			TownyMessaging.sendErrorMsg(player, "Could not create permission cache for this world (" + player.getWorld().getName() + ".");
		}

	}

	public void deleteCache(Player player) {

		deleteCache(player.getName());
	}

	public void deleteCache(String name) {

		playerCache.remove(name.toLowerCase());
	}

	/**
	 * Fetch the current players cache
	 * Creates a new one, if one doesn't exist.
	 * 
	 * @param player - Player to get the current cache from.
	 * @return the current (or new) cache for this player.
	 */
	public PlayerCache getCache(Player player) {

		if (!hasCache(player)) {
			newCache(player);
			getCache(player).setLastTownBlock(new WorldCoord(player.getWorld().getName(), Coord.parseCoord(player)));
		}

		return playerCache.get(player.getName().toLowerCase());
	}

	/**
	 * Resets all Online player caches, retaining their location info.
	 */
	public void resetCache() {

		for (Player player : BukkitTools.getOnlinePlayers())
			if (player != null)
				getCache(player).resetAndUpdate(new WorldCoord(player.getWorld().getName(), Coord.parseCoord(player))); // Automatically
																														// resets
																														// permissions.
	}

	/**
	 * Resets all Online player caches if their location equals this one
	 */
	public void updateCache(WorldCoord worldCoord) {

		for (Player player : BukkitTools.getOnlinePlayers())
			if (player != null)
				if (Coord.parseCoord(player).equals(worldCoord))
					getCache(player).resetAndUpdate(worldCoord); // Automatically
																	// resets
																	// permissions.
	}

	/**
	 * Resets all Online player caches if their location has changed
	 */
	public void updateCache() {

		WorldCoord worldCoord = null;

		for (Player player : BukkitTools.getOnlinePlayers()) {
			if (player != null) {
				worldCoord = new WorldCoord(player.getWorld().getName(), Coord.parseCoord(player));
				PlayerCache cache = getCache(player);
				if (cache.getLastTownBlock() != worldCoord)
					cache.resetAndUpdate(worldCoord);
			}
		}
	}

	/**
	 * Resets a specific players cache if their location has changed
	 * 
	 * @param player - Player, whose cache is to be updated.
	 */
	public void updateCache(Player player) {

		WorldCoord worldCoord = new WorldCoord(player.getWorld().getName(), Coord.parseCoord(player));
		PlayerCache cache = getCache(player);

		if (cache.getLastTownBlock() != worldCoord)
			cache.resetAndUpdate(worldCoord);
	}

	/**
	 * Resets a specific players cache
	 * 
	 * @param player - Player, whose cache is to be reset.
	 */
	public void resetCache(Player player) {

		getCache(player).resetAndUpdate(new WorldCoord(player.getWorld().getName(), Coord.parseCoord(player)));
	}

	public void setPlayerMode(Player player, String[] modes, boolean notify) {

		if (player == null)
			return;

		try {
			Resident resident = TownyUniverse.getDataSource().getResident(player.getName());			 
			resident.setModes(modes, notify);

		} catch (NotRegisteredException e) {
			// Resident doesn't exist
		}
	}

	/**
	 * Remove ALL current modes (and set the defaults)
	 * 
	 * @param player - player, whose modes are to be reset (all removed).
	 */
	public void removePlayerMode(Player player) {

		try {
			Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
			resident.clearModes();

		} catch (NotRegisteredException e) {
			// Resident doesn't exist
		}

	}

	/**
	 * Fetch a list of all the players current modes.
	 * 
	 * @param player - player, whose modes are to be listed, taken.
	 * @return list of modes
	 */
	public List<String> getPlayerMode(Player player) {

		return getPlayerMode(player.getName());
	}

	public List<String> getPlayerMode(String name) {

		try {
			Resident resident = TownyUniverse.getDataSource().getResident(name);
			return resident.getModes();

		} catch (NotRegisteredException e) {
			// Resident doesn't exist
			return null;
		}
	}

	/**
	 * Check if the player has a specific mode.
	 * 
	 * @param player - Player to be checked
	 * @param mode - Mode to be checked for within player.
	 * @return true if the mode is present.
	 */
	public boolean hasPlayerMode(Player player, String mode) {

		return hasPlayerMode(player.getName(), mode);
	}

	public boolean hasPlayerMode(String name, String mode) {

		try {
			Resident resident = TownyUniverse.getDataSource().getResident(name);
			return resident.hasMode(mode);

		} catch (NotRegisteredException e) {
			// Resident doesn't exist
			return false;
		}
	}

	public String getConfigPath() {

		return getDataFolder().getPath() + FileMgmt.fileSeparator() + "settings" + FileMgmt.fileSeparator() + "config.yml";
	}

	public Object getSetting(String root) {

		return TownySettings.getProperty(root);
	}

	public void log(String msg) {

		if (TownySettings.isLogging())
			TownyLogger.log.info(ChatColor.stripColor(msg));
	}

	public void setupLogger() {

		TownyLogger.setup(getTownyUniverse().getRootFolder(), TownySettings.isAppendingToLog());
	}
	

	public boolean parseOnOff(String s) throws Exception {

		if (s.equalsIgnoreCase("on"))
			return true;
		else if (s.equalsIgnoreCase("off"))
			return false;
		else
			throw new Exception(String.format(TownySettings.getLangString("msg_err_invalid_input"), " on/off."));
	}

	/**
	 * @return the Towny instance
	 */
	public static Towny getPlugin() {
		return plugin;
	}

	/**
	 * @return the playerListener
	 */
	public TownyPlayerListener getPlayerListener() {
	
		return playerListener;
	}

	
	/**
	 * @return the vehicleListener
	 */
	public TownyVehicleListener getVehicleListener() {
	
		return vehicleListener;
	}

	
	/**
	 * @return the entityListener
	 */
	public TownyEntityListener getEntityListener() {
	
		return entityListener;
	}

	
	/**
	 * @return the weatherListener
	 */
	public TownyWeatherListener getWeatherListener() {
	
		return weatherListener;
	}

	
	/**
	 * @return the entityMonitorListener
	 */
	public TownyEntityMonitorListener getEntityMonitorListener() {
	
		return entityMonitorListener;
	}

	
	/**
	 * @return the worldListener
	 */
	public TownyWorldListener getWorldListener() {
	
		return worldListener;
	}

	
	/**
	 * @return the townyWarBlockListener
	 */
	public TownyWarBlockListener getTownyWarBlockListener() {
	
		return townyWarBlockListener;
	}

	
	/**
	 * @return the townyWarCustomListener
	 */
	public TownyWarCustomListener getTownyWarCustomListener() {
	
		return townyWarCustomListener;
	}

	
	/**
	 * @return the townyWarEntityListener
	 */
	public TownyWarEntityListener getTownyWarEntityListener() {
	
		return townyWarEntityListener;
	}
	
	/**
	 * @return the HUDManager
	 */
	public HUDManager getHUDManager() {
		
		return HUDManager;
	}

	// https://www.spigotmc.org/threads/small-easy-register-command-without-plugin-yml.38036/
	private void registerSpecialCommands() {
		List<Command> commands = new ArrayList<>();
		commands.add(new AcceptCommand(TownySettings.getAcceptCommand()));
		commands.add(new DenyCommand(TownySettings.getDenyCommand()));
		commands.add(new ConfirmCommand(TownySettings.getConfirmCommand()));
		commands.add(new CancelCommand(TownySettings.getCancelCommand()));
		try {
			final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

			bukkitCommandMap.setAccessible(true);
			CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

			commandMap.registerAll("towny", commands);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
