package com.palmergames.bukkit.towny.permissions;

import java.util.Arrays;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;

import ru.tehkode.permissions.PermissionEntity;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import ru.tehkode.permissions.events.PermissionEntityEvent;
import ru.tehkode.permissions.events.PermissionSystemEvent;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.util.BukkitTools;

/**
 * @author ElgarL
 * 
 */
public class PEXSource extends TownyPermissionSource {

	public PEXSource(Towny towny, Plugin test) {

		this.pex = (PermissionsEx) test;
		this.plugin = towny;

		try {
			plugin.getServer().getPluginManager().registerEvents(new PEXCustomEventListener(), plugin);
		} catch (IllegalPluginAccessException e) {
			System.out.print("Your Version of PEX is out of date. Please update.");
		}
	}

	/**
	 * getPermissionNode
	 * 
	 * returns the specified prefix/suffix nodes from permissionsEX
	 * 
	 * @param resident
	 * @param node
	 * @return String of the prefix or suffix
	 */
	@Override
	public String getPrefixSuffix(Resident resident, String node) {

		String group = "", user = "";
		Player player = plugin.getServer().getPlayer(resident.getName());

		PermissionManager pexPM = PermissionsEx.getPermissionManager();

		if (node == "prefix") {
			group = pexPM.getUser(player).getPrefix(player.getWorld().getName());
			user = pexPM.getUser(player).getOwnPrefix();
		} else if (node == "suffix") {
			group = pexPM.getUser(player).getSuffix(player.getWorld().getName());
			user = pexPM.getUser(player).getOwnSuffix();
		}
		if (group == null)
			group = "";
		if (user == null)
			user = "";

		if (!group.equals(user))
			user = group + user;
		user = TownySettings.parseSingleLineString(user);

		return user;

	}

	/**
	 * 
	 * @param playerName
	 * @param node
	 * @return -1 = can't find
	 */
	@Override
	public int getGroupPermissionIntNode(String playerName, String node) {

		Player player = plugin.getServer().getPlayer(playerName);
		String worldName = player.getWorld().getName();

		PermissionManager pexPM = PermissionsEx.getPermissionManager();

		//return pexPM.getUser(player).getOptionInteger(node, worldName, -1);

		String result = pexPM.getUser(player).getOption(node, worldName);

		try {
			return Integer.parseInt(result);
		} catch (NumberFormatException e) {
			return -1;
		}

	}

	/**
	 * 
	 * @param playerName
	 * @param node
	 * @return empty = can't find
	 */
	@Override
	public String getPlayerPermissionStringNode(String playerName, String node) {

		Player player = plugin.getServer().getPlayer(playerName);
		String worldName = player.getWorld().getName();

		PermissionManager pexPM = PermissionsEx.getPermissionManager();

		//return pexPM.getUser(player).getOptionInteger(node, worldName, -1);
		String result = pexPM.getUser(player).getOption(node, worldName);
		if (result != null)
			return result;

		return "";

	}

	/**
	 * Returns the players Group name.
	 * 
	 * @param player
	 * @return Name of the players group
	 */
	@Override
	public String getPlayerGroup(Player player) {

		PermissionManager pexPM = PermissionsEx.getPermissionManager();

		return pexPM.getUser(player).getGroupsNames()[0];

	}

	/**
	 * Returns an array of Groups this player is a member of.
	 * 
	 * @param player
	 * @return Array of groups for this player
	 */
	public PermissionGroup[] getPlayerGroups(Player player) {

		PermissionManager pexPM = PermissionsEx.getPermissionManager();

		return pexPM.getUser(player).getGroups();

	}

	protected class PEXCustomEventListener implements Listener {

		public PEXCustomEventListener() {

		}

		@EventHandler(priority = EventPriority.HIGH)
		public void onPermissionEntityEvent(PermissionEntityEvent event) {

			Resident resident = null;
			Player player = null;

			try {
				if (PermissionEventEnums.PEXEntity_Action.valueOf(event.getEventName()) != null) {
					PermissionEntityEvent EntityEvent = (PermissionEntityEvent) event;
					PermissionEntity entity = EntityEvent.getEntity();
					if (entity instanceof PermissionGroup) {
						PermissionGroup group = (PermissionGroup) entity;

						// Update all players who are in this group.
						for (Player toUpdate : BukkitTools.getOnlinePlayers()) {
							if (Arrays.asList(getPlayerGroups(toUpdate)).contains(group)) {
								//setup default modes
								String[] modes = getPlayerPermissionStringNode(toUpdate.getName(), PermissionNodes.TOWNY_DEFAULT_MODES.getNode()).split(",");
								plugin.setPlayerMode(player, modes, false);
							}
						}

					} else if (entity instanceof PermissionUser) {

						try {
							resident = TownyUniverse.getDataSource().getResident(((PermissionUser) entity).getName());
							player = plugin.getServer().getPlayerExact(resident.getName());
							if (player != null) {
								//setup default modes for this player.
								String[] modes = getPlayerPermissionStringNode(player.getName(), PermissionNodes.TOWNY_DEFAULT_MODES.getNode()).split(",");
								plugin.setPlayerMode(player, modes, false);
							}
						} catch (NotRegisteredException x) {
						}
					}
				}
			} catch (IllegalArgumentException e) {
				// Not tracking this event type
			}
		}

		@EventHandler(priority = EventPriority.HIGH)
		public void onPermissionSystemEvent(PermissionSystemEvent event) {

			Player player = null;

			try {
				if (PermissionEventEnums.PEXSystem_Action.valueOf(event.getEventName()) != null) {
					// Update all players.
					for (Player toUpdate : BukkitTools.getOnlinePlayers()) {
						//setup default modes
						String[] modes = getPlayerPermissionStringNode(toUpdate.getName(), PermissionNodes.TOWNY_DEFAULT_MODES.getNode()).split(",");
						plugin.setPlayerMode(player, modes, false);
					}
				}
			} catch (IllegalArgumentException e) {
				// Not tracking this event type
			}

		}
	}
}
