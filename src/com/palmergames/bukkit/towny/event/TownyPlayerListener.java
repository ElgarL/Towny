package com.palmergames.bukkit.towny.event;


import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.material.Attachable;

import com.palmergames.bukkit.towny.ChunkNotification;
import com.palmergames.bukkit.towny.NotRegisteredException;
import com.palmergames.bukkit.towny.PlayerCache;
import com.palmergames.bukkit.towny.PlayerCache.TownBlockStatus;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyException;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.command.TownCommand;
import com.palmergames.bukkit.towny.command.TownyCommand;
import com.palmergames.bukkit.towny.object.BlockLocation;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.palmergames.bukkit.towny.permissions.PermissionNodes;
import com.palmergames.bukkit.townywar.TownyWarConfig;
import com.palmergames.bukkit.util.Colors;


/**
 * Handle events for all Player related events
 * 
 * @author Shade
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
			if (plugin.getTownyUniverse().isTeleportWarmupRunning())
				plugin.getTownyUniverse().abortTeleportRequest(TownyUniverse.getDataSource().getResident(event.getPlayer().getName().toLowerCase()));
		} catch (NotRegisteredException e) {
		}

		plugin.deleteCache(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		
		if (plugin.isError()) {
			return;
		}
		
		Player player = event.getPlayer();
		TownyMessaging.sendDebugMsg("onPlayerDeath: " + player.getName());
		
		if (!TownySettings.isTownRespawning())
			return;
		
		try {
			Location respawn = plugin.getTownyUniverse().getTownSpawnLocation(player);
			
			// Check if only respawning in the same world as the town's spawn.
			if (TownySettings.isTownRespawningInOtherWorlds() && !player.getWorld().equals(respawn.getWorld()))
				return;
			
			event.setRespawnLocation(respawn);
		} catch (TownyException e) {
			// Town has not set respawn location. Using default.
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (plugin.isError()) {
			event.setCancelled(true);
			return;
		}

		//System.out.println("onPlayerInteract2");
		//long start = System.currentTimeMillis();

		if (event.isCancelled()) {
			// Fix for bucket bug.
			if (event.getAction() == Action.RIGHT_CLICK_AIR) {
				Integer item = event.getPlayer().getItemInHand().getTypeId();
				// block cheats for placing water/lava/fire/lighter use.
				if (item == 326 || item == 327 || item == 259 || (item >= 8 && item <= 11) || item == 51)
					event.setCancelled(true);
			}
			return;
		}

		Block block = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN);
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
			if ((block.getType() == Material.SOIL) || (block.getType() == Material.CROPS))
				if (World.isDisablePlayerTrample()) {
					event.setCancelled(true);
					return;
				}
		}

		// Towny regen
		if (TownySettings.getRegenDelay() > 0) {
			if (event.getClickedBlock().getState().getData() instanceof Attachable) {
				Attachable attachable = (Attachable) event.getClickedBlock().getState().getData();
				BlockLocation attachedToBlock = new BlockLocation(event.getClickedBlock().getRelative(attachable.getAttachedFace()).getLocation());
				// Prevent attached blocks from falling off when interacting
				if (plugin.getTownyUniverse().hasProtectionRegenTask(attachedToBlock)) {
					event.setCancelled(true);
					return;
				}
			}
		}

		if (event.hasItem()) {

			if (TownySettings.isItemUseId(event.getItem().getTypeId())) {
				onPlayerInteractEvent(event);
				return;
			}
		}
		// fix for minequest causing null block interactions.
		if (event.getClickedBlock() != null)
			if (TownySettings.isSwitchId(event.getClickedBlock().getTypeId()) || event.getAction() == Action.PHYSICAL) {
				onPlayerSwitchEvent(event, null, World);
				return;
			}
		//plugin.sendDebugMsg("onPlayerItemEvent took " + (System.currentTimeMillis() - start) + "ms");
		//}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMove(PlayerMoveEvent event) {

		if (plugin.isError()) {
			event.setCancelled(true);
			return;
		}

		Player player = event.getPlayer();
		Location from = event.getFrom();
		Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ()
                && from.getWorld().equals(to.getWorld())) {
            // Player didn't move by at least one block.
            return;
        }

		// Prevent fly/double jump cheats
		if (!(event instanceof PlayerTeleportEvent)) {
			if (TownySettings.isUsingCheatProtection() && (player.getGameMode() != GameMode.CREATIVE) && !TownyUniverse.getPermissionSource().hasPermission(player, PermissionNodes.CHEAT_BYPASS.getNode())) {
				try {
					if (TownyUniverse.getDataSource().getWorld(player.getWorld().getName()).isUsingTowny())
						if ((from.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)
							&& (player.getFallDistance() == 0)
							&& (player.getVelocity().getY() <= -0.6)
							&& (player.getLocation().getY() > 0)) {
							//plugin.sendErrorMsg(player, "Cheat Detected!");
	
							Location blockLocation = from;
	
							//find the first non air block below us
							while ((blockLocation.getBlock().getType() == Material.AIR) && (blockLocation.getY() > 0))
								blockLocation.setY(blockLocation.getY() - 1);
	
							// set to 1 block up so we are not sunk in the ground
							blockLocation.setY(blockLocation.getY() + 1);
	
							plugin.getCache(player).setLastLocation(blockLocation);
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
			WorldCoord fromCoord = new WorldCoord(fromWorld, Coord.parseCoord(from));
			TownyWorld toWorld = TownyUniverse.getDataSource().getWorld(to.getWorld().getName());
			WorldCoord toCoord = new WorldCoord(toWorld, Coord.parseCoord(to));
			if (!fromCoord.equals(toCoord))
				onPlayerMoveChunk(player, fromCoord, toCoord, from, to);
			else {
				//plugin.sendDebugMsg("    From: " + fromCoord);
				//plugin.sendDebugMsg("    To:   " + toCoord);
				//plugin.sendDebugMsg("        " + from.toString());
				//plugin.sendDebugMsg("        " + to.toString());
			}
		} catch (NotRegisteredException e) {
			TownyMessaging.sendErrorMsg(player, e.getMessage());
		}

		plugin.getCache(player).setLastLocation(to);
		plugin.updateCache(player);
		//plugin.sendDebugMsg("onBlockMove: " + player.getName() + ": ");
		//plugin.sendDebugMsg("        " + from.toString());
		//plugin.sendDebugMsg("        " + to.toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		onPlayerMove(event);
	}

	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		
		Player player = event.getPlayer();

		Block block = event.getClickedBlock();
		WorldCoord worldCoord;
		TownyWorld world = null;
		//System.out.println("onPlayerInteractEvent");

		try {
			world = TownyUniverse.getDataSource().getWorld(player.getWorld().getName());
			if (block != null)
				worldCoord = new WorldCoord(world, Coord.parseCoord(block));
			else
				worldCoord = new WorldCoord(world, Coord.parseCoord(player));
		} catch (NotRegisteredException e1) {
			TownyMessaging.sendErrorMsg(player, TownySettings.getLangString("msg_err_not_configured"));
			event.setCancelled(true);
			return;
		}

		//Get itemUse permissions (updates if none exist)
		boolean bItemUse;
		
		if (block != null)
			bItemUse = TownyUniverse.getCachePermissions().getCachePermission(player, block.getLocation(), TownyPermission.ActionType.ITEM_USE);
		else
			bItemUse = TownyUniverse.getCachePermissions().getCachePermission(player, player.getLocation(), TownyPermission.ActionType.ITEM_USE);
		
		boolean wildOverride = TownyUniverse.getPermissionSource().hasWildOverride(worldCoord.getWorld(), player, event.getItem().getTypeId(), TownyPermission.ActionType.ITEM_USE);

		PlayerCache cache = plugin.getCache(player);
		//cache.updateCoord(worldCoord);
		try {

			TownBlockStatus status = cache.getStatus();
			if (status == TownBlockStatus.UNCLAIMED_ZONE && wildOverride)
				return;
			
			// Allow item_use if we have an override
			if (((status == TownBlockStatus.TOWN_RESIDENT) && (TownyUniverse.getPermissionSource().hasOwnTownOverride(player, event.getItem().getTypeId(), TownyPermission.ActionType.ITEM_USE)))
				|| (((status == TownBlockStatus.OUTSIDER) || (status == TownBlockStatus.TOWN_ALLY) || (status == TownBlockStatus.ENEMY)) && (TownyUniverse.getPermissionSource().hasAllTownOverride(player, event.getItem().getTypeId(), TownyPermission.ActionType.ITEM_USE))))
				return;
			
			if (status == TownBlockStatus.WARZONE) {
				if (!TownyWarConfig.isAllowingItemUseInWarZone()) {
					event.setCancelled(true);
					TownyMessaging.sendErrorMsg(player, TownySettings.getLangString("msg_err_warzone_cannot_use_item"));
				}
				return;
			}
			if (((status == TownBlockStatus.UNCLAIMED_ZONE) && (!wildOverride)) || ((!bItemUse) && (status != TownBlockStatus.UNCLAIMED_ZONE))) {
				//if (status == TownBlockStatus.UNCLAIMED_ZONE)
				//	TownyMessaging.sendErrorMsg(player, String.format(TownySettings.getLangString("msg_err_cannot_perform_action"), world.getUnclaimedZoneName()));
				
				event.setCancelled(true);
			}
			
			if ((cache.hasBlockErrMsg())) // && (status != TownBlockStatus.UNCLAIMED_ZONE))
				TownyMessaging.sendErrorMsg(player, cache.getBlockErrMsg());

		} catch (NullPointerException e) {
			System.out.print("NPE generated!");
			System.out.print("Player: " + event.getPlayer().getName());
			System.out.print("Item: " + event.getItem().getType().toString());
			//System.out.print("Block: " + block.getType().toString());
		}

	}

	public void onPlayerSwitchEvent(PlayerInteractEvent event, String errMsg, TownyWorld world) {

		Player player = event.getPlayer();
		Block block = event.getClickedBlock();

		if (!TownySettings.isSwitchId(block.getTypeId()))
			return;

		//Get switch permissions (updates if none exist)
		boolean bSwitch = TownyUniverse.getCachePermissions().getCachePermission(player, block.getLocation(), TownyPermission.ActionType.SWITCH);
		
		boolean wildOverride = TownyUniverse.getPermissionSource().hasWildOverride(world, player, block.getTypeId(), TownyPermission.ActionType.SWITCH);

		PlayerCache cache = plugin.getCache(player);
		
		TownBlockStatus status = cache.getStatus();
		if (status == TownBlockStatus.UNCLAIMED_ZONE && wildOverride)
			return;
		
		// Allow item_use if we have an override
		if (((status == TownBlockStatus.TOWN_RESIDENT) && (TownyUniverse.getPermissionSource().hasOwnTownOverride(player, block.getTypeId(), TownyPermission.ActionType.SWITCH)))
			|| (((status == TownBlockStatus.OUTSIDER) || (status == TownBlockStatus.TOWN_ALLY) || (status == TownBlockStatus.ENEMY)) && (TownyUniverse.getPermissionSource().hasAllTownOverride(player, block.getTypeId(), TownyPermission.ActionType.SWITCH))))
			return;
					
		if (status == TownBlockStatus.WARZONE) {
			if (!TownyWarConfig.isAllowingSwitchesInWarZone()) {
				event.setCancelled(true);
				TownyMessaging.sendErrorMsg(player, TownySettings.getLangString("msg_err_warzone_cannot_use_switches"));
			}
			return;
		}
		if (((status == TownBlockStatus.UNCLAIMED_ZONE) && (!wildOverride)) || ((!bSwitch) && (status != TownBlockStatus.UNCLAIMED_ZONE))) {

			event.setCancelled(true);
		}
		if (cache.hasBlockErrMsg()) // && (status != TownBlockStatus.UNCLAIMED_ZONE))
			TownyMessaging.sendErrorMsg(player, cache.getBlockErrMsg());
	}

	public void onPlayerMoveChunk(Player player, WorldCoord from, WorldCoord to, Location fromLoc, Location toLoc) {
		//plugin.sendDebugMsg("onPlayerMoveChunk: " + player.getName());

		plugin.getCache(player).setLastLocation(toLoc);
		plugin.getCache(player).updateCoord(to);

		// TODO: Player mode
		if (plugin.hasPlayerMode(player, "townclaim"))
			TownCommand.parseTownClaimCommand(player, new String[] {});
		if (plugin.hasPlayerMode(player, "townunclaim"))
			TownCommand.parseTownUnclaimCommand(player, new String[] {});
		if (plugin.hasPlayerMode(player, "map"))
			TownyCommand.showMap(player);

		// claim: attempt to claim area
		// claim remove: remove area from town

		// Check if player has entered a new town/wilderness
		if (to.getWorld().isUsingTowny() && TownySettings.getShowTownNotifications()) {
			ChunkNotification chunkNotifier = new ChunkNotification(from, to);
			String msg = chunkNotifier.getNotificationString();
			if (msg != null)
				player.sendMessage(msg);
		}
	}
}