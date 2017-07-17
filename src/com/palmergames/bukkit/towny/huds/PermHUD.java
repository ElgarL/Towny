package com.palmergames.bukkit.towny.huds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownBlockOwner;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.object.TownyPermission.ActionType;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.palmergames.bukkit.towny.object.WorldCoord;

public class PermHUD {
	
	private static final String PLOTNAME_TITLE = "Plot: " + ChatColor.GRAY;

	public static void updatePerms (Player p) {
		WorldCoord worldCoord = new WorldCoord(p.getWorld().getName(), Coord.parseCoord(p));
		updatePerms(p, worldCoord);
	}

	public static void updatePerms(Player p, WorldCoord worldCoord) {
		String plotName, build, destroy, switching, item, pvp, explosions, firespread, mobspawn, title;
		Scoreboard board = p.getScoreboard();
		try {
			TownBlock townBlock = worldCoord.getTownBlock();
			TownBlockOwner owner = townBlock.hasResident() ? townBlock.getResident() : townBlock.getTown();
			Town town = townBlock.getTown();
			TownyWorld world = townBlock.getWorld();
			TownyPermission tp = townBlock.getPermissions();
			String v = (owner instanceof Resident) ? "r" : "f";
			build = (tp.getResidentPerm(ActionType.BUILD) ? v : "-") + (tp.getAllyPerm(ActionType.BUILD) ? "a" : "-") + (tp.getOutsiderPerm(ActionType.BUILD) ? "o" : "-");
			destroy = (tp.getResidentPerm(ActionType.DESTROY) ? v : "-") + (tp.getAllyPerm(ActionType.DESTROY) ? "a" : "-") + (tp.getOutsiderPerm(ActionType.DESTROY) ? "o" : "-");
			switching = (tp.getResidentPerm(ActionType.SWITCH) ? v : "-") + (tp.getAllyPerm(ActionType.SWITCH) ? "a" : "-") + (tp.getOutsiderPerm(ActionType.SWITCH) ? "o" : "-");
			item = (tp.getResidentPerm(ActionType.ITEM_USE) ? v : "-") + (tp.getAllyPerm(ActionType.ITEM_USE) ? "a" : "-") + (tp.getOutsiderPerm(ActionType.ITEM_USE) ? "o" : "-");
			pvp = (town.isPVP() || world.isForcePVP() || townBlock.getPermissions().pvp) ? ChatColor.DARK_RED + "ON" : ChatColor.GREEN + "OFF";
			explosions = (world.isForceExpl() || townBlock.getPermissions().explosion) ? ChatColor.DARK_RED + "ON" : ChatColor.GREEN + "OFF";
			firespread = (town.isFire() || world.isForceFire() || townBlock.getPermissions().fire) ? ChatColor.DARK_RED + "ON" : ChatColor.GREEN + "OFF";
			mobspawn = (town.hasMobs() || world.isForceTownMobs() || townBlock.getPermissions().mobs) ? ChatColor.DARK_RED + "ON" : ChatColor.GREEN + "OFF";
			if (townBlock.hasResident()) {
				title = ChatColor.GOLD + townBlock.getResident().getName() + "(" + townBlock.getTown().getName() + ")";
			} else {
				title = ChatColor.GOLD + townBlock.getTown().getName();
			}
			plotName = townBlock.getName() == null ? "" : (PLOTNAME_TITLE + townBlock.getName());
		} catch (NotRegisteredException e) {
			clearPerms(p);
			return;
		}
		board.getTeam("plot").setSuffix(HUDManager.check(plotName));
		board.getTeam("build").setSuffix(build);
		board.getTeam("destroy").setSuffix(destroy);
		board.getTeam("switching").setSuffix(switching);
		board.getTeam("item").setSuffix(item);
		board.getTeam("pvp").setSuffix(pvp);
		board.getTeam("explosions").setSuffix(explosions);
		board.getTeam("firespread").setSuffix(firespread);
		board.getTeam("mobspawn").setSuffix(mobspawn);
		board.getObjective("PERM_HUD_OBJ").setDisplayName(HUDManager.check(title));
	}

	private static void clearPerms (Player p) {
		Scoreboard board = p.getScoreboard();
		board.getTeam("plot").setSuffix("");
		board.getTeam("build").setSuffix("");
		board.getTeam("destroy").setSuffix("");
		board.getTeam("switching").setSuffix("");
		board.getTeam("item").setSuffix("");
		board.getTeam("pvp").setSuffix("");
		board.getTeam("explosions").setSuffix("");
		board.getTeam("firespread").setSuffix("");
		board.getTeam("mobspawn").setSuffix("");
		board.getObjective("PERM_HUD_OBJ").setDisplayName(HUDManager.check(getFormattedWildernessName(p.getWorld())));
	}
	
	private static String getFormattedWildernessName(World w) {
		StringBuilder wildernessName = new StringBuilder().append(ChatColor.DARK_RED).append(ChatColor.BOLD);
		try {
			wildernessName.append(TownyUniverse.getDataSource().getWorld(w.getName()).getUnclaimedZoneName());
		} catch (NotRegisteredException e) {
			wildernessName.append("Unknown");
		}
		return wildernessName.toString();
	}

	@SuppressWarnings("deprecation")
	public static void toggleOn (Player p) {
		String PERM_HUD_TITLE = ChatColor.GOLD + "";
		String permsTitle_player = ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "Plot Perms";
		String plotName_player = ChatColor.DARK_GREEN + "";
		String build_player = ChatColor.DARK_GREEN + "Build: " + ChatColor.GRAY;
		String destroy_player = ChatColor.DARK_GREEN + "Destroy: " + ChatColor.GRAY;
		String switching_player = ChatColor.DARK_GREEN + "Switch: " + ChatColor.GRAY;
		String item_player = ChatColor.DARK_GREEN + "Item: " + ChatColor.GRAY;
		String pvp_player = ChatColor.DARK_GREEN + "PvP: ";
		String explosions_player = ChatColor.DARK_GREEN + "Explosions: ";
		String firespread_player = ChatColor.DARK_GREEN + "Firespread: ";
		String mobspawn_player = ChatColor.DARK_GREEN + "Mob Spawns: ";
		String keyTitle_player = ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "Key";
		String keyResident_player = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "r" + ChatColor.WHITE + " - " + ChatColor.GRAY + "residents";
		String keyFriend_player = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "f" + ChatColor.WHITE + " - " + ChatColor.GRAY + "friends";
		String keyAlly_player = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "a" + ChatColor.WHITE + " - " + ChatColor.GRAY + "allies";
		String keyOutsider_player = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "o" + ChatColor.WHITE + " - " + ChatColor.GRAY + "outsiders";
		//init objective
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective obj = board.registerNewObjective("PERM_HUD_OBJ", "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName(PERM_HUD_TITLE);
		//register teams
		Team permsTitle = board.registerNewTeam("permsTitle");
		Team plotName = board.registerNewTeam("plot");
		Team build = board.registerNewTeam("build");
		Team destroy = board.registerNewTeam("destroy");
		Team switching = board.registerNewTeam("switching");
		Team item = board.registerNewTeam("item");
		Team pvp = board.registerNewTeam("pvp");
		Team explosions = board.registerNewTeam("explosions");
		Team firespread = board.registerNewTeam("firespread");
		Team mobspawn= board.registerNewTeam("mobspawn");
		Team keyTitle = board.registerNewTeam("keyTitle");
		Team keyResident = board.registerNewTeam("keyResident");
		Team keyFriend = board.registerNewTeam("keyFriend");
		Team keyAlly = board.registerNewTeam("keyAlly");
		Team keyOutsider = board.registerNewTeam("keyOutsider");
		//register players
		permsTitle.addPlayer(Bukkit.getOfflinePlayer(permsTitle_player));
		plotName.addPlayer(Bukkit.getOfflinePlayer(plotName_player));
		build.addPlayer(Bukkit.getOfflinePlayer(build_player));
		destroy.addPlayer(Bukkit.getOfflinePlayer(destroy_player));
		switching.addPlayer(Bukkit.getOfflinePlayer(switching_player));
		item.addPlayer(Bukkit.getOfflinePlayer(item_player));
		pvp.addPlayer(Bukkit.getOfflinePlayer(pvp_player));
		explosions.addPlayer(Bukkit.getOfflinePlayer(explosions_player));
		firespread.addPlayer(Bukkit.getOfflinePlayer(firespread_player));
		mobspawn.addPlayer(Bukkit.getOfflinePlayer(mobspawn_player));
		keyTitle.addPlayer(Bukkit.getOfflinePlayer(keyTitle_player));
		keyResident.addPlayer(Bukkit.getOfflinePlayer(keyResident_player));
		keyFriend.addPlayer(Bukkit.getOfflinePlayer(keyFriend_player));
		keyAlly.addPlayer(Bukkit.getOfflinePlayer(keyAlly_player));
		keyOutsider.addPlayer(Bukkit.getOfflinePlayer(keyOutsider_player));
		//set scores for positioning
		obj.getScore(permsTitle_player).setScore(15);
		obj.getScore(plotName_player).setScore(14);
		obj.getScore(build_player).setScore(13);
		obj.getScore(destroy_player).setScore(12);
		obj.getScore(switching_player).setScore(11);
		obj.getScore(item_player).setScore(10);
		obj.getScore(pvp_player).setScore(9);
		obj.getScore(explosions_player).setScore(8);
		obj.getScore(firespread_player).setScore(7);
		obj.getScore(mobspawn_player).setScore(6);
		obj.getScore(keyTitle_player).setScore(5);
		obj.getScore(keyResident_player).setScore(4);
		obj.getScore(keyFriend_player).setScore(3);
		obj.getScore(keyAlly_player).setScore(2);
		obj.getScore(keyOutsider_player).setScore(1);
		//set the board
		p.setScoreboard(board);
		updatePerms(p);
	}
}
