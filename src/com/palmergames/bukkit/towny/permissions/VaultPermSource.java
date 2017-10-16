package com.palmergames.bukkit.towny.permissions;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.util.BukkitTools;

public class VaultPermSource extends TownyPermissionSource {

	private final Chat chat;

	public VaultPermSource(Towny plugin, Chat chat) {
		this.plugin = plugin;
		this.chat = chat;
	}

	@Override
	public String getPrefixSuffix(Resident resident, String node) {
		Player player = BukkitTools.getPlayerExact(resident.getName());
		if (player != null) {
			// Fetch primary group
			String primaryGroup = getPlayerGroup(player);

			String groupPrefixSuffix = "";
			String playerPrefixSuffix = "";

			// Pull prefix/suffix for both primary group and player
			if ("prefix".equalsIgnoreCase(node)) {
				if (!primaryGroup.isEmpty())
					groupPrefixSuffix = chat.getGroupPrefix(player.getWorld(), primaryGroup);
				playerPrefixSuffix = chat.getPlayerPrefix(player);
			}
			else if ("suffix".equalsIgnoreCase(node)) {
				if (!primaryGroup.isEmpty())
					groupPrefixSuffix = chat.getGroupSuffix(player.getWorld(), primaryGroup);
				playerPrefixSuffix = chat.getPlayerSuffix(player);
			} else if (node == "userprefix") {
				playerPrefixSuffix = chat.getPlayerPrefix(player);					
			} else if (node == "usersuffix") {
				playerPrefixSuffix = chat.getPlayerSuffix(player);					
			}

			// Normalize
			if (groupPrefixSuffix == null)
				groupPrefixSuffix = "";
			if (playerPrefixSuffix == null)
				playerPrefixSuffix = "";

			// Combine, if different
			String prefixSuffix = playerPrefixSuffix;
			if (!playerPrefixSuffix.equals(groupPrefixSuffix))
				prefixSuffix = groupPrefixSuffix + playerPrefixSuffix;

			return TownySettings.parseSingleLineString(prefixSuffix);
		}
		return "";
	}

	@Override
	public int getGroupPermissionIntNode(String playerName, String node) {
		
		int iReturn = -1;
		
		Player player = BukkitTools.getPlayerExact(playerName);
		
		if (player != null) {
			String primaryGroup = getPlayerGroup(player);

			if (!primaryGroup.isEmpty())
				iReturn = chat.getGroupInfoInteger(player.getWorld(), primaryGroup, node, -1);
		}
		
		
		if (iReturn == -1)
			iReturn = getEffectivePermIntNode(playerName, node);
		
		return iReturn;
	}

	@Override
	public String getPlayerGroup(Player player) {
		String result = chat.getPrimaryGroup(player.getWorld(), player.getName());
		return result != null ? result : "";
	}

	@Override
	public String getPlayerPermissionStringNode(String playerName, String node) {
		Player player = BukkitTools.getPlayerExact(playerName);
		if (player != null) {
			return chat.getPlayerInfoString(player, node, "");
		}
		return "";
	}

}
