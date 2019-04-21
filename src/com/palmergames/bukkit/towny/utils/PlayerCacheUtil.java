package com.palmergames.bukkit.towny.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.PlayerCache;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownBlockType;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.palmergames.bukkit.towny.object.PlayerCache.TownBlockStatus;
import com.palmergames.bukkit.towny.object.TownyPermission.ActionType;
import com.palmergames.bukkit.towny.permissions.PermissionNodes;
import com.palmergames.bukkit.towny.regen.TownyRegenAPI;
import com.palmergames.bukkit.util.BukkitTools;

/**
 * Groups all the cache status and permissions in one place.
 * 
 * @author ElgarL/Shade
 * 
 */
public class PlayerCacheUtil {
	
	static Towny plugin = null;
	
	public static void initialize(Towny plugin) {
		PlayerCacheUtil.plugin = plugin;
	}

	
	/**
	 * Returns player cached permission for BUILD, DESTROY, SWITCH or ITEM_USE
	 * at this location for the specified item id.
	 * 
	 * Generates the cache if it doesn't exist.
	 * 
	 * @param player
	 * @param location
	 * @param material
	 * @param action
	 * @return true if the player has permission.
	 */
	public static boolean getCachePermission(Player player, Location location, Material material, ActionType action) {

		WorldCoord worldCoord;

		try {
			worldCoord = new WorldCoord(player.getWorld().getName(), Coord.parseCoord(location));
			PlayerCache cache = plugin.getCache(player);
			cache.updateCoord(worldCoord);

			TownyMessaging.sendDebugMsg("Cache permissions for " + action.toString() + " : " + cache.getCachePermission(material, action));
			return cache.getCachePermission(material, action); // Throws NullPointerException if the cache is empty

		} catch (NullPointerException e) {
			// New or old cache permission was null, update it

			worldCoord = new WorldCoord(player.getWorld().getName(), Coord.parseCoord(location));

			TownBlockStatus status = cacheStatus(player, worldCoord, getTownBlockStatus(player, worldCoord));
			triggerCacheCreate(player, location, worldCoord, status, material, action);

			PlayerCache cache = plugin.getCache(player);
			cache.updateCoord(worldCoord);
			
			TownyMessaging.sendDebugMsg("New Cache Created and updated!");

			TownyMessaging.sendDebugMsg("New Cache permissions for " + material + ":" + action.toString() + ":" + status.name() + " = " + cache.getCachePermission(material, action));
			return cache.getCachePermission(material, action);
		}
	}

	/**
	 * Generate a new cache for this player/action.
	 * 
	 * @param player
	 * @param location
	 * @param worldCoord
	 * @param status
	 * @param material
	 * @param action
	 */
	private static void triggerCacheCreate(Player player, Location location, WorldCoord worldCoord, TownBlockStatus status, Material material, ActionType action) {

		switch (action) {

		case BUILD: // BUILD
			cacheBuild(player, worldCoord, material, getPermission(player, status, worldCoord, material, action));
			return;
		case DESTROY: // DESTROY
			cacheDestroy(player, worldCoord, material, getPermission(player, status, worldCoord, material, action));
			return;
		case SWITCH: // SWITCH
			cacheSwitch(player, worldCoord, material, getPermission(player, status, worldCoord, material, action));
			return;
		case ITEM_USE: // ITEM_USE
			cacheItemUse(player, worldCoord, material, getPermission(player, status, worldCoord, material, action));
			return;
		default:
			//for future expansion of permissions
		}
	}
	
	/**
	 * Update and return back the townBlockStatus for the player at this
	 * worldCoord.
	 * 
	 * @param player
	 * @param worldCoord
	 * @param townBlockStatus
	 * @return TownBlockStatus type.
	 */
	public static TownBlockStatus cacheStatus(Player player, WorldCoord worldCoord, TownBlockStatus townBlockStatus) {

		PlayerCache cache = plugin.getCache(player);
		cache.updateCoord(worldCoord);
		cache.setStatus(townBlockStatus);

		TownyMessaging.sendDebugMsg(player.getName() + " (" + worldCoord.toString() + ") Cached Status: " + townBlockStatus);
		return townBlockStatus;
	}

	/**
	 * Update the player cache for Build rights at this WorldCoord.
	 * 
	 * @param player
	 * @param worldCoord
	 * @param material
	 * @param buildRight
	 */
	private static void cacheBuild(Player player, WorldCoord worldCoord, Material material, Boolean buildRight) {

		PlayerCache cache = plugin.getCache(player);
		cache.updateCoord(worldCoord);
		cache.setBuildPermission(material, buildRight);

		TownyMessaging.sendDebugMsg(player.getName() + " (" + worldCoord.toString() + ") Cached Build: " + buildRight);
	}

	/**
	 * Update the player cache for Destroy rights at this WorldCoord.
	 * 
	 * @param player
	 * @param worldCoord
	 * @param material
	 * @param destroyRight
	 */
	private static void cacheDestroy(Player player, WorldCoord worldCoord, Material material, Boolean destroyRight) {

		PlayerCache cache = plugin.getCache(player);
		cache.updateCoord(worldCoord);
		cache.setDestroyPermission(material, destroyRight);

		TownyMessaging.sendDebugMsg(player.getName() + " (" + worldCoord.toString() + ") Cached Destroy: " + destroyRight);
	}

	/**
	 * Update the player cache for Switch rights at this WorldCoord.
	 * 
	 * @param player
	 * @param worldCoord
	 * @param material
	 * @param switchRight
	 */
	private static void cacheSwitch(Player player, WorldCoord worldCoord, Material material, Boolean switchRight) {

		PlayerCache cache = plugin.getCache(player);
		cache.updateCoord(worldCoord);
		cache.setSwitchPermission(material, switchRight);

		TownyMessaging.sendDebugMsg(player.getName() + " (" + worldCoord.toString() + ") Cached Switch: " + switchRight);
	}

	/**
	 * Update the player cache for Item_use rights at this WorldCoord.
	 * 
	 * @param player
	 * @param worldCoord
	 * @param material
	 * @param itemUseRight
	 */
	private static void cacheItemUse(Player player, WorldCoord worldCoord, Material material, Boolean itemUseRight) {

		PlayerCache cache = plugin.getCache(player);
		cache.updateCoord(worldCoord);
		cache.setItemUsePermission(material, itemUseRight);

		TownyMessaging.sendDebugMsg(player.getName() + " (" + worldCoord.toString() + ") Cached Item Use: " + itemUseRight);
	}

	/**
	 * Update the cached BlockErrMsg for this player.
	 * 
	 * @param player
	 * @param msg
	 */
	public static void cacheBlockErrMsg(Player player, String msg) {

		PlayerCache cache = plugin.getCache(player);
		cache.setBlockErrMsg(msg);
	}

	/**
	 * Fetch the TownBlockStatus type for this player at this WorldCoord.
	 * 
	 * @param player
	 * @param worldCoord
	 * @return TownBlockStatus type.
	 */
	public static TownBlockStatus getTownBlockStatus(Player player, WorldCoord worldCoord) {
		
		//if (isTownyAdmin(player))
		//        return TownBlockStatus.ADMIN;

		try {
			if (!worldCoord.getTownyWorld().isUsingTowny())
				return TownBlockStatus.OFF_WORLD;
		} catch (NotRegisteredException ex) {
			// Not a registered world
			return TownBlockStatus.NOT_REGISTERED;
		}

		//TownyUniverse universe = plugin.getTownyUniverse();
		TownBlock townBlock = null;
		Town town = null;
		try {
			townBlock = worldCoord.getTownBlock();
			town = townBlock.getTown();

			if (townBlock.isLocked()) {
				// Push the TownBlock location to the queue for a snapshot (if it's not already in the queue).
				if (town.getWorld().isUsingPlotManagementRevert() && (TownySettings.getPlotManagementSpeed() > 0)) {
					TownyRegenAPI.addWorldCoord(townBlock.getWorldCoord());
					return TownBlockStatus.LOCKED;
				}
				townBlock.setLocked(false);
			}

		} catch (NotRegisteredException e) {
			// Has to be wilderness because townblock = null;

			// When nation zones are enabled we do extra tests to determine if this is near to a nation.
			if (TownySettings.getNationZonesEnabled()) {
				// This nation zone system can be disabled during wartime.
				if (TownySettings.getNationZonesWarDisables() && !TownyUniverse.isWarTime()) {
					Town nearestTown = null;
					int distance = 0;
					try {
						nearestTown = worldCoord.getTownyWorld().getClosestTownWithNationFromCoord(worldCoord.getCoord(), nearestTown);
						if (nearestTown == null) {
							return TownBlockStatus.UNCLAIMED_ZONE;
						}
						distance = worldCoord.getTownyWorld().getMinDistanceFromOtherTownsPlots(worldCoord.getCoord());
					} catch (NotRegisteredException e1) {
						// There will almost always be a town in any world where towny is enabled. 
						// If there isn't then we fall back on normal unclaimed zone status.
						return TownBlockStatus.UNCLAIMED_ZONE;
					}					
					distance = distance + TownySettings.getNationZonesCapitalBonusSize();
					// It is possible to only have nation zones surrounding nation capitals. If this is true, we treat this like a normal wilderness.
					if (!nearestTown.isCapital() && TownySettings.getNationZonesCapitalsOnly()) {
						return TownBlockStatus.UNCLAIMED_ZONE;
					}					
					try {
						if (distance <= Integer.valueOf(TownySettings.getNationLevel(nearestTown.getNation()).get(TownySettings.NationLevel.NATIONZONES_SIZE).toString())) {
							return TownBlockStatus.NATION_ZONE;
						}
					} catch (NumberFormatException | NotRegisteredException x) {
					}
				}				
			}
	
			// Otherwise treat as normal wilderness. 
			return TownBlockStatus.UNCLAIMED_ZONE;
		}

		/*
		 * Find the resident data for this player.
		 */
		Resident resident;
		try {
			resident = TownyUniverse.getDataSource().getResident(player.getName());
		} catch (TownyException e) {
			System.out.print("Failed to fetch resident: " + player.getName());
			return TownBlockStatus.NOT_REGISTERED;
		}

		try {
			// War Time switch rights
			if (TownyUniverse.isWarTime()) {
				if (TownySettings.isAllowWarBlockGriefing()) {
					try {
						if (!resident.getTown().getNation().isNeutral() && !town.getNation().isNeutral())
							return TownBlockStatus.WARZONE;
					} catch (NotRegisteredException e) {

					}
				}
				//If this town is not in a nation and we are set to non peaceful/neutral status during war.
				if (!TownySettings.isWarTimeTownsNeutral() && !town.hasNation())
					return TownBlockStatus.WARZONE;
			}

			// Town Owner Override
			try {
				if (townBlock.getTown().isMayor(resident)) // || townBlock.getTown().hasAssistant(resident))
					return TownBlockStatus.TOWN_OWNER;
			} catch (NotRegisteredException e) {
			}
			
			// Resident Plot rights
			try {
				Resident owner = townBlock.getResident();
				if (resident == owner)
					return TownBlockStatus.PLOT_OWNER;
				else if (owner.hasFriend(resident))
					return TownBlockStatus.PLOT_FRIEND;
				else if (resident.hasTown() && CombatUtil.isAlly(owner.getTown(), resident.getTown()))
					return TownBlockStatus.PLOT_ALLY;
				else
					// Exit out and use town permissions
					throw new TownyException();
			} catch (TownyException e) {
			}

			// Resident with no town.
			if (!resident.hasTown()) {
				
				if (townBlock.isWarZone()) {
					if (!TownySettings.isWarTimeTownsNeutral())
						return TownBlockStatus.WARZONE;
					else
						return TownBlockStatus.OUTSIDER;
				}
				throw new TownyException();
			}	
			
			if (resident.getTown() != town) {
				// Allied destroy rights
				if (CombatUtil.isAlly(town, resident.getTown()))
					return TownBlockStatus.TOWN_ALLY;
				else if (CombatUtil.isEnemy(resident.getTown(), town)) {
					if (townBlock.isWarZone())
						return TownBlockStatus.WARZONE;
					else
						return TownBlockStatus.ENEMY;
				} else
					return TownBlockStatus.OUTSIDER;
			} else if (resident.isMayor()) // || resident.getTown().hasAssistant(resident))
				return TownBlockStatus.TOWN_OWNER;
			else
				return TownBlockStatus.TOWN_RESIDENT;
		} catch (TownyException e) {
			// Outsider destroy rights
			return TownBlockStatus.OUTSIDER;
		}
	}

	/**
	 * Test if the player has permission to perform a certain action at this
	 * WorldCoord.
	 * 
	 * @param player
	 * @param status
	 * @param pos
	 * @param material
	 * @param action
	 * @return true if allowed.
	 */
	private static boolean getPermission(Player player, TownBlockStatus status, WorldCoord pos, Material material, TownyPermission.ActionType action) {

		if (status == TownBlockStatus.OFF_WORLD || status == TownBlockStatus.WARZONE || status == TownBlockStatus.PLOT_OWNER || status == TownBlockStatus.TOWN_OWNER) // || plugin.isTownyAdmin(player)) // status == TownBlockStatus.ADMIN ||
			return true;

		if (status == TownBlockStatus.NOT_REGISTERED) {
			cacheBlockErrMsg(player, TownySettings.getLangString("msg_cache_block_error"));
			return false;
		}

		if (status == TownBlockStatus.LOCKED) {
			cacheBlockErrMsg(player, TownySettings.getLangString("msg_cache_block_error_locked"));
			return false;
		}

		TownBlock townBlock = null;
		Town playersTown = null;
		Town targetTown = null;

		try {
			playersTown = TownyUniverse.getDataSource().getResident(player.getName()).getTown();
		} catch (NotRegisteredException e) {
		}

		try {
			townBlock = pos.getTownBlock();
			targetTown = townBlock.getTown();
		} catch (NotRegisteredException e) {

			try {
				// Wilderness Permissions
				if (status == TownBlockStatus.UNCLAIMED_ZONE) {
					if (TownyUniverse.getPermissionSource().hasWildOverride(pos.getTownyWorld(), player, material, action)) {
						return true;
					} else {
						// Don't have permission to build/destroy/switch/item_use here
						cacheBlockErrMsg(player, String.format(TownySettings.getLangString("msg_cache_block_error_wild"), TownySettings.getLangString(action.toString())));
						return false;
					}
				}
				if (TownySettings.getNationZonesEnabled()) {
					// Nation_Zone wilderness type Permissions 
					if (status == TownBlockStatus.NATION_ZONE) {
						// Admins that also have wilderness permission can bypass the nation zone.
						if (TownyUniverse.getPermissionSource().testPermission(player, PermissionNodes.TOWNY_ADMIN_NATION_ZONE.getNode()) && TownyUniverse.getPermissionSource().hasWildOverride(pos.getTownyWorld(), player, material, action)) {
							return true;
						} else {
						
							Nation playersNation = null;
							Town nearestTown = null; 
							nearestTown = pos.getTownyWorld().getClosestTownWithNationFromCoord(pos.getCoord(), nearestTown);
							Nation nearestNation = nearestTown.getNation();
			
							try {
								playersNation = playersTown.getNation();
							} catch (Exception e1) {							
								cacheBlockErrMsg(player, String.format(TownySettings.getLangString("nation_zone_this_area_under_protection_of"), pos.getTownyWorld().getUnclaimedZoneName() ,nearestNation.getName()));
								return false;
							}
							if (playersNation.equals(nearestNation)){
								if (TownyUniverse.getPermissionSource().hasWildOverride(pos.getTownyWorld(), player, material, action)) {
									return true;
								} else {
									// Don't have permission to build/destroy/switch/item_use here
									cacheBlockErrMsg(player, String.format(TownySettings.getLangString("msg_cache_block_error_wild"), TownySettings.getLangString(action.toString())));
									return false;
								}
							} else {
								cacheBlockErrMsg(player, String.format(TownySettings.getLangString("nation_zone_this_area_under_protection_of"), pos.getTownyWorld().getUnclaimedZoneName() ,nearestNation.getName()));
								return false;
							}
						}
					}
				}
			} catch (NotRegisteredException e2) {
				TownyMessaging.sendErrorMsg(player, "Error updating " + action.toString() + " permission.");
				return false;
			}

		}

		// Allow admins to have ALL permissions over towns.
		if (TownyUniverse.getPermissionSource().isTownyAdmin(player))
			return true;


		// Plot Permissions

		if (townBlock.hasResident()) {

			/*
			 * Check town overrides before testing plot permissions
			 */
			if (targetTown.equals(playersTown) && (TownyUniverse.getPermissionSource().hasOwnTownOverride(player, material, action))) {
				return true;

			} else if (!targetTown.equals(playersTown) && (TownyUniverse.getPermissionSource().hasAllTownOverride(player, material, action))) {
				return true;

			} else if (status == TownBlockStatus.PLOT_FRIEND) {
				if (townBlock.getPermissions().getResidentPerm(action)) {

					if (townBlock.getType() == TownBlockType.WILDS) {

						try {
							if (TownyUniverse.getPermissionSource().unclaimedZoneAction(pos.getTownyWorld(), material, action))
								return true;
						} catch (NotRegisteredException e) {
						}

					} else if (townBlock.getType() == TownBlockType.FARM && (action.equals(ActionType.BUILD) || action.equals(ActionType.DESTROY))) {		
						
						if (TownySettings.getFarmPlotBlocks().contains(material.toString()))
							return true;
						
					} else {
						return true;
					}

				}

				cacheBlockErrMsg(player, String.format(TownySettings.getLangString("msg_cache_block_error_plot"), "friends", TownySettings.getLangString(action.toString())));
				return false;

			} else if (status == TownBlockStatus.PLOT_ALLY) {
				if (townBlock.getPermissions().getAllyPerm(action)) {

					if (townBlock.getType() == TownBlockType.WILDS) {

						try {
							if (TownyUniverse.getPermissionSource().unclaimedZoneAction(pos.getTownyWorld(), material, action))
								return true;
						} catch (NotRegisteredException e) {
						}

					} else if (townBlock.getType() == TownBlockType.FARM && (action == ActionType.BUILD || action == ActionType.DESTROY)) {		
						
						if (TownySettings.getFarmPlotBlocks().contains(material.toString()))
							return true;
						
					} else {
						return true;
					}

				}
				
				cacheBlockErrMsg(player, String.format(TownySettings.getLangString("msg_cache_block_error_plot"), "allies", TownySettings.getLangString(action.toString())));
				return false;

			} else {

				if (townBlock.getPermissions().getOutsiderPerm(action)) {

					if (townBlock.getType() == TownBlockType.WILDS) {

						try {
							if (TownyUniverse.getPermissionSource().unclaimedZoneAction(pos.getTownyWorld(), material, action))
								return true;
						} catch (NotRegisteredException e) {
						}

					} else if (townBlock.getType() == TownBlockType.FARM && (action == ActionType.BUILD || action == ActionType.DESTROY)) {		
						
						if (TownySettings.getFarmPlotBlocks().contains(material.toString()))
							return true;
						
					} else {
						return true;
					}

				}

				cacheBlockErrMsg(player, String.format(TownySettings.getLangString("msg_cache_block_error_plot"), "outsiders", TownySettings.getLangString(action.toString())));
				return false;

			}
		}

		// Town Permissions
		if (status == TownBlockStatus.TOWN_RESIDENT) {

			/*
			 * Check town overrides before testing town permissions
			 */
			if (targetTown.equals(playersTown) && (TownyUniverse.getPermissionSource().hasTownOwnedOverride(player, material, action))) {
				return true;

			} else if (!targetTown.equals(playersTown) && (TownyUniverse.getPermissionSource().hasAllTownOverride(player, material, action))) {
				return true;

			} else if (townBlock.getPermissions().getResidentPerm(action)) {

				if (townBlock.getType() == TownBlockType.WILDS) {

					try {
						if (TownyUniverse.getPermissionSource().unclaimedZoneAction(pos.getTownyWorld(), material, action))
							return true;
					} catch (NotRegisteredException e) {
					}

				} else if (townBlock.getType() == TownBlockType.FARM && (action == ActionType.BUILD || action == ActionType.DESTROY)) {		
					
					if (TownySettings.getFarmPlotBlocks().contains(material.toString()))
						return true;
					
				} else {
					return true;
				}

			}

			cacheBlockErrMsg(player, String.format(TownySettings.getLangString("msg_cache_block_error_town_resident"), TownySettings.getLangString(action.toString())));
			return false;

		} else if (status == TownBlockStatus.TOWN_ALLY) {

			/*
			 * Check town overrides before testing town permissions
			 */
			if (targetTown.equals(playersTown) && (TownyUniverse.getPermissionSource().hasOwnTownOverride(player, material, action))) {
				return true;

			} else if (!targetTown.equals(playersTown) && (TownyUniverse.getPermissionSource().hasAllTownOverride(player, material, action))) {
				return true;

			} else if (townBlock.getPermissions().getAllyPerm(action)) {

				if (townBlock.getType() == TownBlockType.WILDS) {

					try {
						if (TownyUniverse.getPermissionSource().unclaimedZoneAction(pos.getTownyWorld(), material, action))
							return true;
					} catch (NotRegisteredException e) {
					}

				} else if (townBlock.getType() == TownBlockType.FARM && (action == ActionType.BUILD || action == ActionType.DESTROY)) {		
					
					if (TownySettings.getFarmPlotBlocks().contains(material.toString()))
						return true;
					
				} else {
					return true;
				}

			}

			cacheBlockErrMsg(player, String.format(TownySettings.getLangString("msg_cache_block_error_town_allies"), TownySettings.getLangString(action.toString())));
			return false;

		} else if (status == TownBlockStatus.OUTSIDER || status == TownBlockStatus.ENEMY) {

			/*
			 * Check town overrides before testing town permissions
			 */
			if (TownyUniverse.getPermissionSource().hasAllTownOverride(player, material, action)) {
				return true;

			} else if (townBlock.getPermissions().getOutsiderPerm(action)) {

				if (townBlock.getType() == TownBlockType.WILDS) {

					try {
						if (TownyUniverse.getPermissionSource().unclaimedZoneAction(pos.getTownyWorld(), material, action))
							return true;
					} catch (NotRegisteredException e) {
					}

				} else if (townBlock.getType() == TownBlockType.FARM && (action == ActionType.BUILD || action == ActionType.DESTROY)) {
					
					if (TownySettings.getFarmPlotBlocks().contains(material.toString()))
						return true;
					
				} else {
					return true;
				}

			}
			cacheBlockErrMsg(player, String.format(TownySettings.getLangString("msg_cache_block_error_town_outsider"), TownySettings.getLangString(action.toString())));
			return false;
		}

		TownyMessaging.sendErrorMsg(player, "Error updating " + action.toString() + " permission.");
		return false;
	}
}