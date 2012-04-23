package com.palmergames.bukkit.towny.object;

import org.bukkit.entity.Player;

import com.palmergames.bukkit.config.ConfigNodes;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.permissions.PermissionNodes;

public enum TownSpawnLevel {
	TOWN_RESIDENT(
			ConfigNodes.GTOWN_SETTINGS_ALLOW_TOWN_SPAWN,
			"msg_err_town_spawn_forbidden",
			ConfigNodes.ECO_PRICE_TOWN_SPAWN_TRAVEL,
			PermissionNodes.TOWNY_SPAWN_TOWN.getNode()), TOWN_RESIDENT_OUTPOST(
			ConfigNodes.GTOWN_SETTINGS_ALLOW_TOWN_SPAWN,
			"msg_err_town_spawn_forbidden",
			ConfigNodes.ECO_PRICE_TOWN_SPAWN_TRAVEL,
			PermissionNodes.TOWNY_SPAWN_OUTPOST.getNode()), PART_OF_NATION(
			ConfigNodes.GTOWN_SETTINGS_ALLOW_TOWN_SPAWN_TRAVEL_NATION,
			"msg_err_town_spawn_nation_forbidden",
			ConfigNodes.ECO_PRICE_TOWN_SPAWN_TRAVEL_NATION,
			PermissionNodes.TOWNY_SPAWN_NATION.getNode()), NATION_ALLY(
			ConfigNodes.GTOWN_SETTINGS_ALLOW_TOWN_SPAWN_TRAVEL_ALLY,
			"msg_err_town_spawn_ally_forbidden",
			ConfigNodes.ECO_PRICE_TOWN_SPAWN_TRAVEL_ALLY,
			PermissionNodes.TOWNY_SPAWN_ALLY.getNode()), UNAFFILIATED(
			ConfigNodes.GTOWN_SETTINGS_ALLOW_TOWN_SPAWN_TRAVEL,
			"msg_err_public_spawn_forbidden",
			ConfigNodes.ECO_PRICE_TOWN_SPAWN_TRAVEL_PUBLIC,
			PermissionNodes.TOWNY_SPAWN_PUBLIC.getNode()), ADMIN(
			null,
			null,
			null,
			null);

	private ConfigNodes isAllowingConfigNode, ecoPriceConfigNode;
	private String permissionNode, notAllowedLangNode;

	private TownSpawnLevel(ConfigNodes isAllowingConfigNode, String notAllowedLangNode, ConfigNodes ecoPriceConfigNode, String permissionNode) {

		this.isAllowingConfigNode = isAllowingConfigNode;
		this.notAllowedLangNode = notAllowedLangNode;
		this.ecoPriceConfigNode = ecoPriceConfigNode;
		this.permissionNode = permissionNode;
	}

	public void checkIfAllowed(Towny plugin, Player player) throws TownyException {

		if (!(isAllowed() && hasPermissionNode(plugin, player)))
			throw new TownyException(TownySettings.getLangString(notAllowedLangNode));
	}

	public boolean isAllowed() {

		return this == TownSpawnLevel.ADMIN ? true : TownySettings.getBoolean(this.isAllowingConfigNode);
	}

	public boolean hasPermissionNode(Towny plugin, Player player) {

		return this == TownSpawnLevel.ADMIN ? true : (plugin.isPermissions() && TownyUniverse.getPermissionSource().has(player, this.permissionNode)) || ((!plugin.isPermissions()) && (TownySettings.isAllowingTownSpawn()));
	}

	public double getCost() {

		return this == TownSpawnLevel.ADMIN ? 0 : TownySettings.getDouble(ecoPriceConfigNode);
	}
}
