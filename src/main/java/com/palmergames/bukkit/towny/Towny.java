package com.palmergames.bukkit.towny;

import com.palmergames.bukkit.towny.exception.TownyException;
import com.palmergames.bukkit.towny.exception.NotRegisteredException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.palmergames.bukkit.towny.command.*;
import com.palmergames.bukkit.towny.question.Option;
import com.palmergames.bukkit.towny.question.Question;

import com.palmergames.bukkit.towny.event.ListenerManager;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyEconomyObject;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.palmergames.bukkit.towny.permissions.TownyPermissionSource;
import com.palmergames.bukkit.towny.question.QuestionManager;
import com.palmergames.bukkit.towny.question.towny.TownyQuestionTask;
import com.palmergames.bukkit.towny.tasks.SetDefaultModes;
import com.palmergames.bukkit.townywar.TownyWar;
import com.palmergames.util.FileMgmt;
import com.palmergames.util.JavaUtil;
import com.palmergames.util.StringMgmt;

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

    private TownyUniverse townyUniverse;

    private Map<String, PlayerCache> playerCache = Collections.synchronizedMap(new HashMap<String, PlayerCache>());

    private Map<String, List<String>> playerMode = Collections.synchronizedMap(new HashMap<String, List<String>>());
    
    private ListenerManager listenerManager;
    
    private QuestionManager questionManager;

    private VaultHelper vaultHelper;

    private boolean error = false;

    @Override
    public void onEnable() {
        System.out.println("====================      Towny      ========================");

        version = this.getDescription().getVersion();
        townyUniverse = new TownyUniverse(this);

        if (load()) {
            // Setup bukkit command interfaces
            getCommand("townyadmin").setExecutor(new TownyAdminCommand(this));
            getCommand("townyworld").setExecutor(new TownyWorldCommand(this));
            getCommand("resident").setExecutor(new ResidentCommand(this));
            getCommand("towny").setExecutor(new TownyCommand(this));
            getCommand("town").setExecutor(new TownCommand(this));
            getCommand("nation").setExecutor(new NationCommand(this));
            getCommand("plot").setExecutor(new PlotCommand(this));
            getCommand("q").setExecutor(new QCommand(this));

            TownyWar.onEnable();

            if (TownySettings.isTownyUpdating(getVersion())) {
                update();
            }

            // Register all child permissions
            TownyUniverse.getPermissionSource().registerPermissionNodes();
        }

        listenerManager = new ListenerManager(this);

        TownyLogger.log.info("=============================================================");
        if (isError()) {
            TownyLogger.log.info("[WARNING] - ***** SAFE MODE ***** " + version);
        } else {
            TownyLogger.log.info("[Towny] Version: " + version + " - Mod Enabled");
        }
        TownyLogger.log.info("=============================================================");

        if (!isError()) {
            // Re login anyone online. (In case of plugin reloading)
            for (Player player : getServer().getOnlinePlayers()) {
                try {
                    getTownyUniverse().onLogin(player);
                } catch (TownyException x) {
                    TownyMessaging.sendErrorMsg(player, x.getMessage());
                }
            }
        }
        //setupDatabase();
    }

    public VaultHelper getVaultHelper() {
        return vaultHelper;
    }

    public boolean isOnline(String playerName) {

        for (Player player : getServer().getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(playerName)) {
                return true;
            }
        }

        return false;

    }

    public void setWorldFlags() {

        for (Town town : TownyUniverse.getDataSource().getTowns()) {
            TownyMessaging.sendDebugMsg("[Towny] Setting flags for: " + town.getName());

            if (town.getWorld() == null) {
                TownyLogger.log.warning("[Towny Error] Detected an error with the world files. Attempting to repair");
                if (town.hasHomeBlock()) {
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
                } else {
                    TownyLogger.log.warning("[Towny Error] No Homeblock - Failed to detect world for: " + town.getName());
                }
            }
        }

    }

    /*
     * private void setupDatabase() { try {
     * getDatabase().find(Towny.class).findRowCount(); }
     * catch(PersistenceException ex) { System.out.println("Installing database
     * for " + getDescription().getName() + " due to first time usage");
     * installDDL(); } }
     */
    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(Towny.class);
        return list;
    }

    @Override
    public void onDisable() {

        System.out.println("==============================================================");

        if (TownyUniverse.getDataSource() != null && error == false) {
            TownyUniverse.getDataSource().saveQueues();
        }

        if (error == false) {
            TownyWar.onDisable();
        }

        if (getTownyUniverse().isWarTime()) {
            getTownyUniverse().getWarEvent().toggleEnd();
        }

        townyUniverse.toggleTownyRepeatingTimer(false);
        townyUniverse.toggleDailyTimer(false);
        townyUniverse.toggleMobRemoval(false);
        townyUniverse.toggleHealthRegen(false);
        townyUniverse.toggleTeleportWarmup(false);
        townyUniverse.cancelProtectionRegenTasks();

        playerCache.clear();
        playerMode.clear();

        townyUniverse = null;

        System.out.println("[Towny] Version: " + version + " - Mod Disabled");
        System.out.println("=============================================================");

        TownyLogger.shutDown();
    }

    public boolean load() {

        Pattern pattern = Pattern.compile("-b(\\d*?)jnks", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(getServer().getVersion());

        TownyEconomyObject.setPlugin(this);

        if (!townyUniverse.loadSettings()) {
            setError(true);
            //getServer().getPluginManager().disablePlugin(this);
            return false;
        }

        setupLogger();

        if (TownySettings.isBypassVersionCheck()) {
            TownyLogger.log.info("[Towny] Bypassing CraftBukkit Version check.");
        } else {

            int bukkitVer = TownySettings.getMinBukkitVersion();

            if (!matcher.find() || matcher.group(1) == null) {
                error = true;
                TownyLogger.log.severe("[Towny Error] Unable to read CraftBukkit Version.");
                TownyLogger.log.severe("[Towny Error] Towny requires version " + bukkitVer + " or higher.");
                getServer().getPluginManager().disablePlugin(this);
                return false;
            }

            int curBuild = Integer.parseInt(matcher.group(1));

            if (curBuild < bukkitVer) {
                error = true;
                TownyLogger.log.severe("[Towny Error] CraftBukkit Version (" + curBuild + ") is outdated! ");
                TownyLogger.log.severe("[Towny Error] Towny requires version " + bukkitVer + " or higher.");
                getServer().getPluginManager().disablePlugin(this);
                return false;
            }

        }

        //Coord.setCellSize(TownySettings.getTownBlockSize());

        //TownyCommand.setUniverse(townyUniverse);

        checkPlugins();

        setWorldFlags();

        vaultHelper = new VaultHelper();

        //make sure the timers are stopped for a reset
        townyUniverse.toggleTownyRepeatingTimer(false);
        townyUniverse.toggleDailyTimer(false);
        townyUniverse.toggleMobRemoval(false);
        townyUniverse.toggleHealthRegen(false);
        townyUniverse.toggleTeleportWarmup(false);

        //Start timers
        townyUniverse.toggleTownyRepeatingTimer(true);
        townyUniverse.toggleDailyTimer(true);
        townyUniverse.toggleMobRemoval(true);
        townyUniverse.toggleHealthRegen(TownySettings.hasHealthRegen());
        townyUniverse.toggleTeleportWarmup(TownySettings.getTeleportWarmupTime() > 0);
        updateCache();

        return true;
    }

    private void checkPlugins() {
        List<String> using = new ArrayList<String>();
        Plugin test;

        getTownyUniverse().setPermissionSource(new TownyPermissionSource(this));

        if (using.size() > 0) {
            TownyLogger.log.info("[Towny] Using: " + StringMgmt.join(using, ", "));
        }
    }

    private void update() {
        try {
            List<String> changeLog = JavaUtil.readTextFromJar("/ChangeLog.txt");
            boolean display = false;
            TownyLogger.log.info("------------------------------------");
            TownyLogger.log.info("[Towny] ChangeLog up until v" + getVersion());
            String lastVersion = TownySettings.getLastRunVersion(getVersion());
            for (String line : changeLog) { //TODO: crawl from the bottom, then past from that index.
                if (line.startsWith("v" + lastVersion)) {
                    display = true;
                }
                if (display && line.replaceAll(" ", "").replaceAll("\t", "").length() > 0) {
                    TownyLogger.log.info(line);
                }
            }
            TownyLogger.log.info("------------------------------------");
        } catch (IOException e) {
            TownyMessaging.sendDebugMsg("Could not read ChangeLog.txt");
        }
        TownySettings.setLastRunVersion(getVersion());
    }

    /**
     * Fetch the TownyUniverse object
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

    public World getServerWorld(String name) throws NotRegisteredException {
        for (World world : getServer().getWorlds()) {
            if (world.getName().equals(name)) {
                return world;
            }
        }

        throw new NotRegisteredException();
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

    public PlayerCache getCache(Player player) {
        if (!hasCache(player)) {
            newCache(player);
            try {
                getTownyUniverse();
                getCache(player).setLastTownBlock(new WorldCoord(TownyUniverse.getDataSource().getWorld(player.getWorld().getName()), Coord.parseCoord(player)));
            } catch (NotRegisteredException e) {
                deleteCache(player);
            }
        }

        return playerCache.get(player.getName().toLowerCase());
    }

    public void updateCache(WorldCoord worldCoord) {
        for (Player player : getServer().getOnlinePlayers()) {
            if (Coord.parseCoord(player).equals(worldCoord)) {
                getCache(player).setLastTownBlock(worldCoord); //Automatically resets permissions.
            }
        }
    }

    public void updateCache() {
        for (Player player : getServer().getOnlinePlayers()) {
            try {
                getTownyUniverse();
                getCache(player).setLastTownBlock(new WorldCoord(TownyUniverse.getDataSource().getWorld(player.getWorld().getName()), Coord.parseCoord(player)));
            } catch (NotRegisteredException e) {
                deleteCache(player);
            }
        }
    }

    public void updateCache(Player player) {
        try {
            getTownyUniverse();
            getCache(player).setLastTownBlock(new WorldCoord(TownyUniverse.getDataSource().getWorld(player.getWorld().getName()), Coord.parseCoord(player)));
        } catch (NotRegisteredException e) {
            deleteCache(player);
        }
    }

    public void setPlayerMode(Player player, String[] modes, boolean notify) {

        if (player == null) {
            return;
        }

        if ((modes != null) && !modes[0].isEmpty()) {
            playerMode.remove(player.getName());

            playerMode.put(player.getName(), Arrays.asList(modes));
            if (notify) {
                TownyMessaging.sendMsg(player, ("Modes set: " + StringMgmt.join(modes, ",")));
            }
        } else {
            playerMode.remove(player.getName());
        }
    }

    /*
     * public void setPlayerChatMode(Player player, String newMode) {
     *
     * List<String> modes = new ArrayList<String>(); List<String> currentModes =
     * getPlayerMode(player); boolean toggle = false;
     *
     * if ((currentModes != null) && (!currentModes.isEmpty())) {
     * modes.addAll(currentModes);
     *
     * if (modes.contains(newMode)) toggle = true;
     *
     * // Clear all chat channels for (String channel :
     * TownySettings.getChatChannels()) { if
     * (modes.contains(channel.replace("/", ""))) if (modes.size() > 1)
     * modes.remove(channel.replace("/", "")); else modes = new
     * ArrayList<String>(); } }
     *
     * if (!modes.contains(newMode) && !toggle) modes.add(newMode);
     *
     * if (modes.isEmpty()) removePlayerMode(player); else setPlayerMode(player,
     * modes.toArray(new String[modes.size()]), true); }
     */
    public void removePlayerMode(Player player) {
        playerMode.remove(player.getName());

        if (getServer().getScheduler().scheduleSyncDelayedTask(this, new SetDefaultModes(getTownyUniverse(), player, true), 1) == -1) {
            TownyMessaging.sendErrorMsg("Could not set default modes for " + player.getName() + ".");
        }

        //TownyMessaging.sendMsg(player, ("Mode removed."));
    }

    public List<String> getPlayerMode(Player player) {
        return playerMode.get(player.getName());
    }

    public boolean hasPlayerMode(Player player, String mode) {
        List<String> modes = getPlayerMode(player);
        if (modes == null) {
            return false;
        } else {
            return modes.contains(mode);
        }
    }

    public List<String> getPlayerMode(String name) {
        return playerMode.get(name);
    }

    public boolean hasPlayerMode(String name, String mode) {
        List<String> modes = getPlayerMode(name);
        if (modes == null) {
            return false;
        } else {
            return modes.contains(mode);
        }
    }

    /*
     * public boolean checkEssentialsTeleport(Player player, Location lctn) { if
     * (!TownySettings.isUsingEssentials() ||
     * !TownySettings.isAllowingTownSpawn()) return false;
     *
     * Plugin test = getServer().getPluginManager().getPlugin("Essentials"); if
     * (test == null) return false; Essentials essentials = (Essentials)test;
     * //essentials.loadClasses(); sendDebugMsg("Using Essentials");
     *
     * try { User user = essentials.getUser(player);
     *
     * if (!user.isTeleportEnabled()) return false;
     *
     * if (!user.isJailed()){
     *
     * //user.getTeleport(); Teleport teleport = user.getTeleport();
     * teleport.teleport(lctn, null);
     *
     * }
     * return true;
     *
     * } catch (Exception e) { sendErrorMsg(player, "Error: " + e.getMessage());
     * // we still retun true here as it is a cooldown return true; }
     *
     * }
     */
    public String getConfigPath() {
        return getDataFolder().getPath() + FileMgmt.fileSeparator() + "settings" + FileMgmt.fileSeparator() + "config.yml";
    }

    //public void setSetting(String root, Object value, boolean saveYML) {
    //              TownySettings.setProperty(root, value, saveYML);
    //}
    public Object getSetting(String root) {
        return TownySettings.getProperty(root);
    }

    public void log(String msg) {
        if (TownySettings.isLogging()) {
            TownyLogger.log.info(ChatColor.stripColor(msg));
        }
    }

    public void setupLogger() {
        TownyLogger.setup(getTownyUniverse().getRootFolder(), TownySettings.isAppendingToLog());
    }

    public void appendQuestion(Question question) throws Exception {
        for (Option option : question.getOptions()) {
            if (option.getReaction() instanceof TownyQuestionTask) {
                ((TownyQuestionTask) option.getReaction()).setTowny(this);
            }
        }
        getQuestionManager().appendQuestion(question);
    }

    public boolean parseOnOff(String s) throws Exception {
        if (s.equalsIgnoreCase("on")) {
            return true;
        } else if (s.equalsIgnoreCase("off")) {
            return false;
        } else {
            throw new Exception(String.format(TownySettings.getLangString("msg_err_invalid_input"), " on/off."));
        }
    }

    @Deprecated
    public boolean isTownyAdmin(Player player) {
        return TownyUniverse.getPermissionSource().isTownyAdmin(player);
    }

    @Deprecated
    public boolean hasWildOverride(TownyWorld world, Player player, int blockId, TownyPermission.ActionType action) {
        return TownyUniverse.getPermissionSource().hasWildOverride(world, player, blockId, action);
    }

    public ListenerManager getListenerManager() {
        return listenerManager;
    }

    public QuestionManager getQuestionManager() {
        return questionManager;
    }

}
