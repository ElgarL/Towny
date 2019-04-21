package com.palmergames.bukkit.towny.listeners;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.TownyTimerHandler;
import com.palmergames.bukkit.towny.event.PlayerChangePlotEvent;
import com.palmergames.bukkit.towny.event.PlayerEnterTownEvent;
import com.palmergames.bukkit.towny.event.PlayerLeaveTownEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.PlayerCache;
import com.palmergames.bukkit.towny.object.PlayerCache.TownBlockStatus;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownBlockType;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.palmergames.bukkit.towny.permissions.PermissionNodes;
import com.palmergames.bukkit.towny.permissions.TownyPerms;
import com.palmergames.bukkit.towny.regen.TownyRegenAPI;
import com.palmergames.bukkit.towny.regen.block.BlockLocation;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import com.palmergames.bukkit.towny.war.eventwar.War;
import com.palmergames.bukkit.towny.war.flagwar.TownyWarConfig;
import com.palmergames.bukkit.util.BukkitTools;
import com.palmergames.bukkit.util.ChatTools;
import com.palmergames.bukkit.util.Colors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Attachable;
import org.bukkit.material.Door;
import org.bukkit.material.Sign;

import java.util.Arrays;

/**
 * Handle events for all Player related events
 * Players deaths are handled both here and in the TownyEntityMonitorListener
 * 
 * @author Shade/ElgarL
 * 
 */
public class TownyPlayerListener implements Listener {

	private final Towny plugin;

	public TownyPlayerListener(Towny instance) {

		plugin = instance;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {

		Player player = event.getPlayer();

		if (plugin.isError()) {
			player.sendMessage(Colors.Rose + "[Towny Error] Locked in Safe mode!");
			return;
		}

		try {
			plugin.getTownyUniverse().onLogin(player);
		} catch (TownyException x) {
			TownyMessaging.sendErrorMsg(player, x.getMessage());
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {

		if (plugin.isError()) {
			return;
		}

		plugin.getTownyUniverse().onLogout(event.getPlayer());

		// Remove from teleport queue (if exists)
		try {
			if (TownyTimerHandler.isTeleportWarmupRunning())
				plugin.getTownyUniverse().abortTeleportRequest(TownyUniverse.getDataSource().getResident(event.getPlayer().getName().toLowerCase()));
		} catch (NotRegisteredException e) {
		}

		plugin.deleteCache(event.getPlayer());
		TownyPerms.removeAttachment(event.getPlayer().getName());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRespawn(PlayerRespawnEvent event) {

		if (plugin.isError()) {
			return;
		}

		Player player = event.getPlayer();

		if (!TownySettings.isTownRespawning())
			return;

		try {
			Location respawn = null;			
			Resident resident = TownyUniverse.getDataSource().getResident(player.getName());

			// If player is jailed send them to their jailspawn.
			if (resident.isJailed()) {
				Town respawnTown = TownyUniverse.getDataSource().getTown(resident.getJailTown()); 
				respawn = respawnTown.getJailSpawn(resident.getJailSpawn());
//				resident.setJailed(false);
				event.setRespawnLocation(respawn);
//				resident.setJailed(true);
			} else {
				respawn = plugin.getTownyUniverse().getTownSpawnLocation(player);
				// Check if only respawning in the same world as the town's spawn.
				if (TownySettings.isTownRespawningInOtherWorlds() && !player.getWorld().equals(respawn.getWorld()))
					return;
		
				// Bed spawn or town.
				if (TownySettings.getBedUse() && (player.getBedSpawnLocation() != null)) {		
					event.setRespawnLocation(player.getBedSpawnLocation());
		
				} else {		
					event.setRespawnLocation(respawn);
		
				}
			}

		} catch (TownyException e) {
			// Town has not set respawn location. Using default.
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {

		if (plugin.isError()) {
			event.setCancelled(true);
			return;
		}
		
		// Test against the item in hand as we need to test the bucket contents
		// we are trying to empty.
		event.setCancelled(onPlayerInteract(event.getPlayer(), event.getBlockClicked().getRelative(event.getBlockFace()), event.getPlayer().getItemInHand()));

		// Test on the resulting empty bucket to see if we have permission to
		// empty a bucket.
		if (!event.isCancelled())
			event.setCancelled(onPlayerInteract(event.getPlayer(), event.getBlockClicked(), event.getItemStack()));

	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketFill(PlayerBucketFillEvent event) {

		if (plugin.isError()) {
			event.setCancelled(true);
			return;
		}
		// test against the bucket we will finish up with to see if we are
		// allowed to fill this item.
		event.setCancelled(onPlayerInteract(event.getPlayer(), event.getBlockClicked(), event.getItemStack()));

	}

	
	/*
	* PlayerInteractEvent 
	* 
	*  Used to stop trampling of crops,
	*  admin infotool,
	*  item use check,
	*  switch use check
	*/
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {

		if (plugin.isError()) {
			event.setCancelled(true);
			return;
		}

		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		TownyWorld World = null;

		try {
			World = TownyUniverse.getDataSource().getWorld(block.getLocation().getWorld().getName());
			if (!World.isUsingTowny())
				return;

		} catch (NotRegisteredException e) {
			// World not registered with Towny.
			e.printStackTrace();
			return;
		}
		
		// prevent players trampling crops

		if ((event.getAction() == Action.PHYSICAL)) {
			if ((block.getType() == Material.FARMLAND))				
				if (World.isDisablePlayerTrample() || !PlayerCacheUtil.getCachePermission(player, block.getLocation(), block.getType(), TownyPermission.ActionType.DESTROY)) {
					event.setCancelled(true);
					return;
				}
		}

		if (event.hasItem()) {

			/*
			 * Info Tool
			 */
			if (event.getPlayer().getItemInHand().getType() == Material.getMaterial(TownySettings.getTool())) {

				if (TownyUniverse.getPermissionSource().isTownyAdmin(player)) {
					if (event.getClickedBlock() instanceof Block) {

						block = (Block) event.getClickedBlock();
						
						if (block.getState().getData() instanceof Sign) {
							Sign sign = (Sign) block.getState().getData();
							BlockFace facing = sign.getFacing();
							BlockFace attachedFace = sign.getAttachedFace();
							
							TownyMessaging.sendMessage(player, Arrays.asList(
									ChatTools.formatTitle("Sign Info"),
									ChatTools.formatCommand("", "Sign Type", "", block.getType().name()),
									ChatTools.formatCommand("", "Facing", "", facing.toString()),
									ChatTools.formatCommand("", "AttachedFace", "", attachedFace.toString())
									));
						} else if (block.getState().getData() instanceof Door) {
							Door door = (Door) block.getState().getData();
							BlockFace face = null;
							boolean isOpen = false;
							boolean isHinge = false;
							if (door.isTopHalf()) {
								isHinge = door.getHinge();
								Door otherdoor = (Door) block.getRelative(BlockFace.DOWN).getState().getData();
								isOpen = otherdoor.isOpen();
								face = otherdoor.getFacing();										
							} else {
								isOpen = door.isOpen();
								face = door.getFacing();
								Door otherdoor = (Door) block.getRelative(BlockFace.UP).getState().getData();
								isHinge=otherdoor.getHinge();
							}
							
							TownyMessaging.sendMessage(player, Arrays.asList(
									ChatTools.formatTitle("Door Info"),
									ChatTools.formatCommand("", "Door Type", "", block.getType().name()),
									ChatTools.formatCommand("", "isHingedOnRight", "", String.valueOf(isHinge)),
									ChatTools.formatCommand("", "isOpen", "", String.valueOf(isOpen)),
									ChatTools.formatCommand("", "getFacing", "", face.toString())									
									//ChatTools.formatCommand("", "Old Data value", "", Byte.toString(BukkitTools.getData(block)))
									));							
						} else {
							TownyMessaging.sendMessage(player, Arrays.asList(
									ChatTools.formatTitle("Block Info"),
									ChatTools.formatCommand("", "Material", "", block.getType().name()),								      
									ChatTools.formatCommand("", "MaterialData", "", block.getBlockData().getAsString())
									));
						}
						event.setCancelled(true);

					}
				}

			}
			if (TownySettings.isItemUseMaterial(event.getItem().getType().name())) {
				TownyMessaging.sendDebugMsg("ItemUse Material found: " + event.getItem().getType().name());
				event.setCancelled(onPlayerInteract(player, event.getClickedBlock(), event.getItem()));
			}
		}

		if (event.getClickedBlock() != null) {
			if (TownySettings.isSwitchMaterial(event.getClickedBlock().getType().name()) || event.getAction() == Action.PHYSICAL) {
				onPlayerSwitchEvent(event, null, World);
			}
		}

	}

	
	/*
	* PlayerInteractAtEntity event
	* 
	* Handles protection of Armor Stands,
	* Admin infotool for entities.
	* 
	*/	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteractEntity(PlayerInteractAtEntityEvent event) {

		if (plugin.isError()) {
			event.setCancelled(true);
			return;
		}

		if (event.getRightClicked() != null) {

			TownyWorld World = null;

			try {
				World = TownyUniverse.getDataSource().getWorld(event.getPlayer().getWorld().getName());
				if (!World.isUsingTowny())
					return;

			} catch (NotRegisteredException e) {
				// World not registered with Towny.
				e.printStackTrace();
				return;
			}

			Player player = event.getPlayer();
			boolean bBuild = true;
			Material block = null;

			/*
			 * Protect specific entity interactions.
			 */
			switch (event.getRightClicked().getType()) {

			case ARMOR_STAND:
				
				TownyMessaging.sendDebugMsg("ArmorStand Right Clicked");
				block = Material.ARMOR_STAND;
				// Get permissions (updates if none exist)
				bBuild = PlayerCacheUtil.getCachePermission(player, event.getRightClicked().getLocation(), block, TownyPermission.ActionType.DESTROY);
				break;
			
			default:
				break;

			}

			if (block != null) {

				// Allow the removal if we are permitted
				if (bBuild)
					return;

				event.setCancelled(true);

				/*
				 * Fetch the players cache
				 */
				PlayerCache cache = plugin.getCache(player);

				if (cache.hasBlockErrMsg())
					TownyMessaging.sendErrorMsg(player, cache.getBlockErrMsg());

				return;
			}

			/*
			 * Item_use protection.
			 */
			if (event.getPlayer().getItemInHand() != null) {

				/*
				 * Info Tool
				 */
				if (event.getPlayer().getItemInHand().getType() == Material.getMaterial(TownySettings.getTool())) {

					Entity entity = event.getRightClicked();

					TownyMessaging.sendMessage(player, Arrays.asList(
							ChatTools.formatTitle("Entity Info"),
							ChatTools.formatCommand("", "Entity Class", "", entity.getType().getEntityClass().getSimpleName())
							));

					event.setCancelled(true);

				}

				if (TownySettings.isItemUseMaterial(event.getPlayer().getItemInHand().getType().name())) {
					event.setCancelled(onPlayerInteract(event.getPlayer(), null, event.getPlayer().getItemInHand()));
				}
			}
		}
	}
	
	/*
	* PlayerInteractEntity event
	* 
	* Handles right clicking of entities: Item Frames, Paintings, Minecarts,
	* Admin infotool for entities.
	*/
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

		if (plugin.isError()) {
			event.setCancelled(true);
			return;
		}

		if (event.getRightClicked() != null) {

			TownyWorld World = null;

			try {
				World = TownyUniverse.getDataSource().getWorld(event.getPlayer().getWorld().getName());
				if (!World.isUsingTowny())
					return;

			} catch (NotRegisteredException e) {
				// World not registered with Towny.
				e.printStackTrace();
				return;
			}

			Player player = event.getPlayer();
			boolean bBuild = true;
			Material block = null;

			/*
			 * Protect specific entity interactions.
			 */
			switch (event.getRightClicked().getType()) {
			
				case ITEM_FRAME:
					
					TownyMessaging.sendDebugMsg("ItemFrame Right Clicked");
					block = Material.ITEM_FRAME;
					// Get permissions (updates if none exist)
					bBuild = PlayerCacheUtil.getCachePermission(player, event.getRightClicked().getLocation(), block, TownyPermission.ActionType.DESTROY);
					break;
	
				case PAINTING:
	
					block = Material.PAINTING;
					// Get permissions (updates if none exist)
					bBuild = PlayerCacheUtil.getCachePermission(player, event.getRightClicked().getLocation(), block, TownyPermission.ActionType.DESTROY);
					break;
	
				case MINECART:
					
					block = Material.MINECART;
					if ((block != null) && (!TownySettings.isSwitchMaterial(block.name())))
						return;
					bBuild = PlayerCacheUtil.getCachePermission(player, event.getRightClicked().getLocation(), block, TownyPermission.ActionType.SWITCH);
					break;
				
				case MINECART_CHEST:					      
					block = Material.CHEST_MINECART;
					if ((block != null) && (!TownySettings.isSwitchMaterial(block.name())))
						return;
					bBuild = PlayerCacheUtil.getCachePermission(player, event.getRightClicked().getLocation(), block, TownyPermission.ActionType.SWITCH);
					break;
				
				case MINECART_FURNACE:
					
					block = Material.FURNACE_MINECART;
					if ((block != null) && (!TownySettings.isSwitchMaterial(block.name())))
						return;
					bBuild = PlayerCacheUtil.getCachePermission(player, event.getRightClicked().getLocation(), block, TownyPermission.ActionType.SWITCH);
					break;
				
				case MINECART_COMMAND:
					
					block = Material.COMMAND_BLOCK_MINECART;
					if ((block != null) && (!TownySettings.isSwitchMaterial(block.name())))
						return;
					bBuild = PlayerCacheUtil.getCachePermission(player, event.getRightClicked().getLocation(), block, TownyPermission.ActionType.SWITCH);
					break;
				
				case MINECART_HOPPER:
					
					block = Material.HOPPER_MINECART;
					if ((block != null) && (!TownySettings.isSwitchMaterial(block.name())))
						return;
					bBuild = PlayerCacheUtil.getCachePermission(player, event.getRightClicked().getLocation(), block, TownyPermission.ActionType.SWITCH);
					break;
					
				case MINECART_TNT:
					
					block = Material.TNT_MINECART;
					if ((block != null) && (!TownySettings.isSwitchMaterial(block.name())))
						return;
					bBuild = PlayerCacheUtil.getCachePermission(player, event.getRightClicked().getLocation(), block, TownyPermission.ActionType.SWITCH);
					break;
					
				case MINECART_MOB_SPAWNER:
					
					block = Material.MINECART;
					if ((block != null) && (!TownySettings.isSwitchMaterial(block.name())))
						return;
					bBuild = PlayerCacheUtil.getCachePermission(player, event.getRightClicked().getLocation(), block, TownyPermission.ActionType.SWITCH);
					break;
					
				default:
					break;
			}
					
				if ((block != null ) && (!TownySettings.isSwitchMaterial(block.toString())))
					return;
	
					// Get permissions (updates if none exist)
					bBuild = PlayerCacheUtil.getCachePermission(player, event.getRightClicked().getLocation(), block, TownyPermission.ActionType.SWITCH);

			if (block != null) {

				// Allow the removal if we are permitted
				if (bBuild)
					return;

				event.setCancelled(true);

				/*
				 * Fetch the players cache
				 */
				PlayerCache cache = plugin.getCache(player);

				if (cache.hasBlockErrMsg())
					TownyMessaging.sendErrorMsg(player, cache.getBlockErrMsg());

				return;
			}

			/*
			 * Item_use protection.
			 */
			if (event.getPlayer().getItemInHand() != null) {

				/*
				 * Info Tool
				 */
				if (event.getPlayer().getItemInHand().getType() == Material.getMaterial(TownySettings.getTool())) {

					Entity entity = event.getRightClicked();

					TownyMessaging.sendMessage(player, Arrays.asList(
							ChatTools.formatTitle("Entity Info"),
							ChatTools.formatCommand("", "Entity Class", "", entity.getType().getEntityClass().getSimpleName())
							));

					event.setCancelled(true);

				}

				if (TownySettings.isItemUseMaterial(event.getPlayer().getItemInHand().getType().name())) {
					event.setCancelled(onPlayerInteract(event.getPlayer(), null, event.getPlayer().getItemInHand()));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {

		if (plugin.isError()) {
			event.setCancelled(true);
			return;
		}

		/*
		 * Abort if we havn't really moved
		 */
		if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ() && event.getFrom().getBlockY() == event.getTo().getBlockY()) {
			return;
		}

		Player player = event.getPlayer();
		Location to = event.getTo();
		Location from;
		PlayerCache cache = plugin.getCache(player);

		try {
			from = cache.getLastLocation();
		} catch (NullPointerException e) {
			from = event.getFrom();
		}

		// Prevent fly/double jump cheats
		if (!(event instanceof PlayerTeleportEvent)) {
			if (TownySettings.isUsingCheatProtection() && (player.getGameMode() != GameMode.CREATIVE) && !TownyUniverse.getPermissionSource().has(player, PermissionNodes.CHEAT_BYPASS.getNode())) {
				try {
					if (TownyUniverse.getDataSource().getWorld(player.getWorld().getName()).isUsingTowny())
						if ((from.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) && (player.getFallDistance() == 0) && (player.getVelocity().getY() <= -0.6) && (player.getLocation().getY() > 0)) {
							// plugin.sendErrorMsg(player, "Cheat Detected!");

							Location blockLocation = from;

							// find the first non air block below us
							while ((blockLocation.getBlock().getType() == Material.AIR) && (blockLocation.getY() > 0))
								blockLocation.setY(blockLocation.getY() - 1);

							// set to 1 block up so we are not sunk in the
							// ground
							blockLocation.setY(blockLocation.getY() + 1);

							// Update the cache for this location (same
							// WorldCoord).
							cache.setLastLocation(blockLocation);
							player.teleport(blockLocation);
							return;
						}
				} catch (NotRegisteredException e1) {
					TownyMessaging.sendErrorMsg(player, TownySettings.getLangString("msg_err_not_configured"));
					return;
				}
			}
		}

		try {
			TownyWorld fromWorld = TownyUniverse.getDataSource().getWorld(from.getWorld().getName());
			WorldCoord fromCoord = new WorldCoord(fromWorld.getName(), Coord.parseCoord(from));
			TownyWorld toWorld = TownyUniverse.getDataSource().getWorld(to.getWorld().getName());
			WorldCoord toCoord = new WorldCoord(toWorld.getName(), Coord.parseCoord(to));
			if (!fromCoord.equals(toCoord))
				onPlayerMoveChunk(player, fromCoord, toCoord, from, to, event);
				//  else : plugin.sendDebugMsg("    From: " + fromCoord);
				// plugin.sendDebugMsg("    To:   " + toCoord);
				// plugin.sendDebugMsg("        " + from.toString());
				// plugin.sendDebugMsg("        " + to.toString());

		} catch (NotRegisteredException e) {
			TownyMessaging.sendErrorMsg(player, e.getMessage());
		}

		// Update the cached players current location
		cache.setLastLocation(to);

		// plugin.updateCache(player);
		// plugin.sendDebugMsg("onBlockMove: " + player.getName() + ": ");
		// plugin.sendDebugMsg("        " + from.toString());
		// plugin.sendDebugMsg("        " + to.toString());
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent event) {


		Player player = event.getPlayer();
		// Cancel teleport if Jailed by Towny.
		try {
			if (TownyUniverse.getDataSource().getResident(player.getName()).isJailed()) {
				if ((event.getCause() == TeleportCause.COMMAND)) {
					TownyMessaging.sendErrorMsg(event.getPlayer(), String.format(TownySettings.getLangString("msg_err_jailed_players_no_teleport")));
					event.setCancelled(true);
					return;
				}
				if (event.getCause() == TeleportCause.PLUGIN) 
					return;
				if ((event.getCause() != TeleportCause.ENDER_PEARL) || (!TownySettings.JailAllowsEnderPearls())) {
					TownyMessaging.sendErrorMsg(event.getPlayer(), String.format(TownySettings.getLangString("msg_err_jailed_players_no_teleport")));
					event.setCancelled(true);
				}
			}
		} catch (NotRegisteredException e) {
			// Not a valid resident, probably an NPC from Citizens.
		}
		

		/*
		 * Test to see if CHORUS_FRUIT is in the item_use list.
		 */
		if (event.getCause() == TeleportCause.CHORUS_FRUIT)
			if (TownySettings.isItemUseMaterial(Material.CHORUS_FRUIT.name()))
				if (onPlayerInteract(event.getPlayer(), event.getTo().getBlock(), new ItemStack(Material.CHORUS_FRUIT))) {
					event.setCancelled(true);					
					return;
				}	
			
		/*
		 * Test to see if Ender pearls are disabled.
		 */		
		if (event.getCause() == TeleportCause.ENDER_PEARL)
			if (TownySettings.isItemUseMaterial(Material.ENDER_PEARL.name()))
				if (onPlayerInteract(event.getPlayer(), event.getTo().getBlock(), new ItemStack(Material.ENDER_PEARL))) {
					event.setCancelled(true);
					TownyMessaging.sendErrorMsg(event.getPlayer(), String.format(TownySettings.getLangString("msg_err_ender_pearls_disabled")));
					return;
				}
		
		onPlayerMove(event);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChangeWorld(PlayerChangedWorldEvent event) { // has changed worlds

		TownyPerms.assignPermissions(null, event.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerBedEnter(PlayerBedEnterEvent event) {

		if (!TownySettings.getBedUse())
			return;

		boolean isOwner = false;
		boolean isInnPlot = false;

		try {
			
			Resident resident = TownyUniverse.getDataSource().getResident(event.getPlayer().getName());
			
			WorldCoord worldCoord = new WorldCoord(event.getPlayer().getWorld().getName(), Coord.parseCoord(event.getBed().getLocation()));

			TownBlock townblock = worldCoord.getTownBlock();
			
			isOwner = townblock.isOwner(resident);

			isInnPlot = townblock.getType() == TownBlockType.INN;			
			
			if (resident.hasNation() && townblock.getTown().hasNation()) {
				
				Nation residentNation = resident.getTown().getNation();
				
				Nation townblockNation = townblock.getTown().getNation();			
				
				if (townblockNation.hasEnemy(residentNation)) {
					event.setCancelled(true);
					TownyMessaging.sendErrorMsg(event.getPlayer(), String.format(TownySettings.getLangString("msg_err_no_sleep_in_enemy_inn")));
					return;
				}
			}
			
		} catch (NotRegisteredException e) {
			// Wilderness as it error'd getting a townblock.
		}
		
		if (!isOwner && !isInnPlot) {

			event.setCancelled(true);
			TownyMessaging.sendErrorMsg(event.getPlayer(), String.format(TownySettings.getLangString("msg_err_cant_use_bed")));

		}
		
	}

	/*
	*  ItemUse protection handling
	*/
	public boolean onPlayerInteract(Player player, Block block, ItemStack item) {

		boolean cancelState = false;
		WorldCoord worldCoord;

		try {
			String worldName = player.getWorld().getName();

			if (block != null)
				worldCoord = new WorldCoord(worldName, Coord.parseCoord(block));
			else
				worldCoord = new WorldCoord(worldName, Coord.parseCoord(player));

			// Get itemUse permissions (updates if none exist)
			boolean bItemUse;

			if (block != null)
				bItemUse = PlayerCacheUtil.getCachePermission(player, block.getLocation(), item.getType(), TownyPermission.ActionType.ITEM_USE);
			else
				bItemUse = PlayerCacheUtil.getCachePermission(player, player.getLocation(), item.getType(), TownyPermission.ActionType.ITEM_USE);

			boolean wildOverride = TownyUniverse.getPermissionSource().hasWildOverride(worldCoord.getTownyWorld(), player, item.getType(), TownyPermission.ActionType.ITEM_USE);

			PlayerCache cache = plugin.getCache(player);
			// cache.updateCoord(worldCoord);
			try {

				TownBlockStatus status = cache.getStatus();
				if (status == TownBlockStatus.UNCLAIMED_ZONE && wildOverride)
					return cancelState;

				// Allow item_use if we have an override
				if (((status == TownBlockStatus.TOWN_RESIDENT) && (TownyUniverse.getPermissionSource().hasOwnTownOverride(player, item.getType(), TownyPermission.ActionType.ITEM_USE)))
						|| (((status == TownBlockStatus.OUTSIDER) || (status == TownBlockStatus.TOWN_ALLY) || (status == TownBlockStatus.ENEMY)) 
						&& (TownyUniverse.getPermissionSource().hasAllTownOverride(player, item.getType(), TownyPermission.ActionType.ITEM_USE))))
					return cancelState;
				
				// Allow item_use for Event War if isAllowingItemUseInWarZone is true,
				boolean playerNeutral = false;
				if (TownyUniverse.isWarTime()) {
					try {
						Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
						if (resident.isJailed())
							playerNeutral = true;
						if (resident.hasTown())
							if (!War.isWarringTown(resident.getTown())) {
								playerNeutral = true;
							}
					} catch (NotRegisteredException e) {
					}			
				}

				// FlagWar here
				if (status == TownBlockStatus.WARZONE || (TownyUniverse.isWarTime() && status == TownBlockStatus.ENEMY && !playerNeutral)) {
					if (!TownyWarConfig.isAllowingItemUseInWarZone()) {
						cancelState = true;
						TownyMessaging.sendErrorMsg(player, TownySettings.getLangString("msg_err_warzone_cannot_use_item"));
					}
					return cancelState;
				}

				// Wilderness Handled here.
				if (((status == TownBlockStatus.UNCLAIMED_ZONE) && (!wildOverride)) || ((!bItemUse) && (status != TownBlockStatus.UNCLAIMED_ZONE))) {
					// if (status == TownBlockStatus.UNCLAIMED_ZONE)
					// TownyMessaging.sendErrorMsg(player,
					// String.format(TownySettings.getLangString("msg_err_cannot_perform_action"),
					// world.getUnclaimedZoneName()));

					cancelState = true;
				}

				if ((cache.hasBlockErrMsg())) 
					TownyMessaging.sendErrorMsg(player, cache.getBlockErrMsg());

			} catch (NullPointerException e) {
				System.out.print("NPE generated!");
				System.out.print("Player: " + player.getName());
				System.out.print("Item: " + item.getData().getItemType().name());
				// System.out.print("Block: " + block.getType().toString());
			}

		} catch (NotRegisteredException e1) {
			TownyMessaging.sendErrorMsg(player, TownySettings.getLangString("msg_err_not_configured"));
			cancelState = true;
			return cancelState;
		}

		return cancelState;

	}

	/*
	*  Switch protection handling
	*/	
	public void onPlayerSwitchEvent(PlayerInteractEvent event, String errMsg, TownyWorld world) {

		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		event.setCancelled(onPlayerSwitchEvent(player, block, errMsg, world));

	}

	public boolean onPlayerSwitchEvent(Player player, Block block, String errMsg, TownyWorld world) {

		if (!TownySettings.isSwitchMaterial(block.getType().name()))
			return false;

		// Get switch permissions (updates if none exist)
		boolean bSwitch = PlayerCacheUtil.getCachePermission(player, block.getLocation(), block.getType(), TownyPermission.ActionType.SWITCH);

		// Allow switch if we are permitted
		if (bSwitch)
			return false;

		/*
		 * Fetch the players cache
		 */
		PlayerCache cache = plugin.getCache(player);
		TownBlockStatus status = cache.getStatus();
		
		boolean playerNeutral = false;
		if (TownyUniverse.isWarTime()) {			
			try {
				Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
				if (resident.isJailed())
					playerNeutral = true;
				if (resident.hasTown())
					if (!War.isWarringTown(resident.getTown())) {
						playerNeutral = true;
					}
			} catch (NotRegisteredException e) {
			}			
		}

		/*
		 * Flag war & now Event War
		 */
		if (status == TownBlockStatus.WARZONE || (TownyUniverse.isWarTime() && status == TownBlockStatus.ENEMY && !playerNeutral)) {
			if (!TownyWarConfig.isAllowingSwitchesInWarZone()) {
				TownyMessaging.sendErrorMsg(player, TownySettings.getLangString("msg_err_warzone_cannot_use_switches"));
				return true;
			}
			return false;
		} else {
			/*
			 * display any error recorded for this plot
			 */
			if (cache.hasBlockErrMsg())
				TownyMessaging.sendErrorMsg(player, cache.getBlockErrMsg());
			return true;
		}

	}
	
	/*
	 * PlayerFishEvent
	 * 
	 * Prevents players from fishing for entities in protected regions.
	 * - Armorstands, animals, any entity affected by rods.
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerFishEvent(PlayerFishEvent event) {
		if (event.getState().equals(PlayerFishEvent.State.CAUGHT_ENTITY)) {
			Player player = event.getPlayer();
			Entity caught = event.getCaught();				
			boolean bDestroy = PlayerCacheUtil.getCachePermission(player, caught.getLocation(), Material.GRASS, TownyPermission.ActionType.DESTROY);
			if (!bDestroy) {
				event.setCancelled(true);
				event.getHook().remove();
			}
		}	
	}

	/*
	* PlayerMoveEvent that can fire the PlayerChangePlotEvent
	*/
	public void onPlayerMoveChunk(Player player, WorldCoord from, WorldCoord to, Location fromLoc, Location toLoc, PlayerMoveEvent moveEvent) {

		plugin.getCache(player).setLastLocation(toLoc);
		plugin.getCache(player).updateCoord(to);

		PlayerChangePlotEvent event = new PlayerChangePlotEvent(player, from, to, moveEvent);
		Bukkit.getServer().getPluginManager().callEvent(event);
	}
	
	/*
	* PlayerChangePlotEvent that can fire the PlayerLeaveTownEvent and PlayerEnterTownEvent
	*/
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChangePlotEvent(PlayerChangePlotEvent event) {

		PlayerMoveEvent pme = event.getMoveEvent();
		Player player = event.getPlayer();		
		WorldCoord from = event.getFrom();
		WorldCoord to = event.getTo();
		try {
			@SuppressWarnings("unused")
			// Required so we don't fire events on NPCs from plugins like citizens.
			Resident resident = TownyUniverse.getDataSource().getResident(player.getName());			
			try {
				to.getTownBlock();
				if (to.getTownBlock().hasTown()) { 
					try {
						Town fromTown = from.getTownBlock().getTown();
						if (!to.getTownBlock().getTown().equals(fromTown)){
							Bukkit.getServer().getPluginManager().callEvent(new PlayerEnterTownEvent(player,to,from,to.getTownBlock().getTown(), pme)); // From Town into different Town.
							Bukkit.getServer().getPluginManager().callEvent(new PlayerLeaveTownEvent(player,to,from,from.getTownBlock().getTown(), pme));//
						}
						// Both are the same town, do nothing, no Event should fire here.
					} catch (NotRegisteredException e) { // From Wilderness into Town.
						Bukkit.getServer().getPluginManager().callEvent(new PlayerEnterTownEvent(player,to, from, to.getTownBlock().getTown(), pme));
					}
				} else {
					if (from.getTownBlock().hasTown() && !(to.getTownBlock().hasTown())){ // From has a town, to doesn't so: From Town into Wilderness
						Bukkit.getServer().getPluginManager().callEvent(new PlayerLeaveTownEvent(player,to,from, from.getTownBlock().getTown(), pme));
					}
				}
			} catch (NotRegisteredException e) {
				Bukkit.getServer().getPluginManager().callEvent(new PlayerLeaveTownEvent(player,to,from, from.getTownBlock().getTown(), pme));
			}

		} catch (NotRegisteredException e) {
			// If not registered, it is most likely an NPC			
		}		
	}
	
	/*
	 * onOutlawEnterTown
	 * - Shows message to outlaws entering towns in which they are considered an outlaw.
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onOutlawEnterTown(PlayerEnterTownEvent event) throws NotRegisteredException {
		
		Player player = event.getPlayer();		
		WorldCoord to = event.getTo();
		Resident resident = TownyUniverse.getDataSource().getResident(player.getName());

		if (to.getTownBlock().getTown().hasOutlaw(resident))
			TownyMessaging.sendMsg(player, String.format(TownySettings.getLangString("msg_you_are_an_outlaw_in_this_town"),to.getTownBlock().getTown()));
	}


	/**
	 * @author - Articdive (Just the small codeblock below)
	 */
	/*
	 * onPlayerDieInTown
	 * - Handles death events and the KeepInventory/KeepLevel options are being used.
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	// Why Highest??, so that we are the last ones to check for if it keeps their inventory, and then have no problems with it.
	public void onPlayerDieInTown(PlayerDeathEvent event) {
		boolean keepInventory = event.getKeepInventory();
		boolean keepLevel = event.getKeepLevel();
		Player player = event.getEntity();
		Location deathloc = player.getLocation();
		if (TownySettings.getKeepInventoryInTowns()) {
			if (!keepInventory) { // If you don't keep your inventory via any other plugin or the server
				TownBlock tb = TownyUniverse.getTownBlock(deathloc);
				if (tb != null) { // So a valid TownBlock appears, how wonderful
					if (tb.hasTown()) { // So the townblock has a town, and we keep inventory in towns, deathloc in a town. Do it!
						event.setKeepInventory(true);
					}
				}
			}
		}
		if (TownySettings.getKeepExperienceInTowns()) {
			if (!keepLevel) { // If you don't keep your levels via any other plugin or the server, other events fire first, we just ignore it if they do save thier invs.
				TownBlock tb = TownyUniverse.getTownBlock(deathloc);
				if (tb != null) { // So a valid TownBlock appears, how wonderful
					if (tb.hasTown()) { // So the townblock has atown, and is at the death location
						event.setKeepLevel(true);
					}
				}

			}
		}
	}


	/*
	 * PlayerEnterTownEvent
	 * Currently used for:
	 *   - showing NotificationsUsingTitles upon entering a town.
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerEnterTown(PlayerEnterTownEvent event) throws TownyException {
		
		Resident resident = TownyUniverse.getDataSource().getResident(event.getPlayer().getName());
		WorldCoord to = event.getTo();
		if (TownySettings.isNotificationUsingTitles()) {
			
			String title = ChatColor.translateAlternateColorCodes('&', TownySettings.getNotificationTitlesTownTitle());
			String subtitle = ChatColor.translateAlternateColorCodes('&', TownySettings.getNotificationTitlesTownSubtitle());
			if (title.contains("{townname}")) {
				String replacement = title.replace("{townname}", to.getTownBlock().getTown().getName());
				title = replacement;
			}
			if (subtitle.contains("{townname}")) {
				String replacement = subtitle.replace("{townname}", to.getTownBlock().getTown().getName());
				subtitle = replacement;
			}
			TownyMessaging.sendTitleMessageToResident(resident, title, subtitle);
		}
	}
	
	/*
	 * PlayerLeaveTownEvent
	 * Currently used for:
	 *   - showing NotificationsUsingTitles upon entering the wilderness.
	 *   - unjailing residents
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerLeaveTown(PlayerLeaveTownEvent event) throws TownyException {
		
		Resident resident = TownyUniverse.getDataSource().getResident(event.getPlayer().getName());
		WorldCoord to = event.getTo();
		if (TownySettings.isNotificationUsingTitles()) {
			try {
				@SuppressWarnings("unused")
				Town toTown = to.getTownBlock().getTown();
			} catch (NotRegisteredException e) { // No town being entered so this is a move into the wilderness.
				String title = ChatColor.translateAlternateColorCodes('&', TownySettings.getNotificationTitlesWildTitle().toString());
				String subtitle = ChatColor.translateAlternateColorCodes('&', TownySettings.getNotificationTitlesWildSubtitle());
				if (title.contains("{wilderness}")) {
					String replacement = title.replace("{wilderness}", TownyUniverse.getDataSource().getWorld(event.getPlayer().getLocation().getWorld().getName()).getUnclaimedZoneName());
					title = replacement;
				}
				if (subtitle.contains("{wilderness}")) {
					String replacement = subtitle.replace("{wilderness}", TownyUniverse.getDataSource().getWorld(event.getPlayer().getLocation().getWorld().getName()).getUnclaimedZoneName());
					subtitle = replacement;
				}
				TownyMessaging.sendTitleMessageToResident(resident, title, subtitle);
			}			
		}

		Player player = event.getPlayer();
		if (TownyUniverse.getDataSource().getResident(player.getName()).isJailed()) {								
			resident.setJailed(false);
			resident.setJailSpawn(0);
			resident.setJailTown("");
			TownyMessaging.sendGlobalMessage(String.format(TownySettings.getLangString("msg_player_escaped_jail_into_wilderness"), player.getName(), TownyUniverse.getDataSource().getWorld(player.getLocation().getWorld().getName()).getUnclaimedZoneName()));								
			TownyUniverse.getDataSource().saveResident(resident);
		}		
	}
}
