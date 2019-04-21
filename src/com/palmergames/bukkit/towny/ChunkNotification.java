package com.palmergames.bukkit.towny;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import com.palmergames.bukkit.config.ConfigNodes;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownBlockType;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.palmergames.bukkit.util.Colors;
import com.palmergames.util.StringMgmt;

public class ChunkNotification {

	// Example:
	// ~ Wak Town - Lord Jebus - [Home] [For Sale: 50 Beli] [Shop]

	public static String notificationFormat = Colors.Gold + " ~ %s";
	public static String notificationSpliter = Colors.LightGray + " - ";
	public static String areaWildernessNotificationFormat = Colors.Green + "%s";
	public static String areaWildernessPvPNotificationFormat = Colors.Green + "%s";
	public static String areaTownNotificationFormat = Colors.Green + "%s";
	public static String areaTownPvPNotificationFormat = Colors.Green + "%s";
	public static String ownerNotificationFormat = Colors.LightGreen + "%s";
	public static String noOwnerNotificationFormat = Colors.LightGreen + "%s";
	public static String plotNotficationSplitter = " ";
	public static String plotNotificationFormat = "%s";
	public static String homeBlockNotification = Colors.LightBlue + "[Home]";
	public static String outpostBlockNotification = Colors.LightBlue + "[Outpost]";
	public static String forSaleNotificationFormat = Colors.Yellow + "[For Sale: %s]";
	public static String plotTypeNotificationFormat = Colors.Gold + "[%s]";	

	/**
	 * Called on Config load.
	 * Specifically: TownySettings.loadCachedLangStrings()
	 */
	public static void loadFormatStrings() {

		notificationFormat = TownySettings.getConfigLang(ConfigNodes.NOTIFICATION_FORMAT);
		notificationSpliter = TownySettings.getConfigLang(ConfigNodes.NOTIFICATION_SPLITTER);
		areaWildernessNotificationFormat = TownySettings.getConfigLang(ConfigNodes.NOTIFICATION_AREA_WILDERNESS);
		areaWildernessPvPNotificationFormat = TownySettings.getConfigLang(ConfigNodes.NOTIFICATION_AREA_WILDERNESS_PVP);
		areaTownNotificationFormat = TownySettings.getConfigLang(ConfigNodes.NOTIFICATION_AREA_TOWN);
		areaTownPvPNotificationFormat = TownySettings.getConfigLang(ConfigNodes.NOTIFICATION_AREA_TOWN_PVP);
		ownerNotificationFormat = TownySettings.getConfigLang(ConfigNodes.NOTIFICATION_OWNER);
		noOwnerNotificationFormat = TownySettings.getConfigLang(ConfigNodes.NOTIFICATION_NO_OWNER);
		plotNotficationSplitter = TownySettings.getConfigLang(ConfigNodes.NOTIFICATION_PLOT_SPLITTER);
		plotNotificationFormat = TownySettings.getConfigLang(ConfigNodes.NOTIFICATION_PLOT_FORMAT);
		homeBlockNotification = TownySettings.getConfigLang(ConfigNodes.NOTIFICATION_PLOT_HOMEBLOCK);
		outpostBlockNotification = TownySettings.getConfigLang(ConfigNodes.NOTIFICATION_PLOT_OUTPOSTBLOCK);
		forSaleNotificationFormat = TownySettings.getConfigLang(ConfigNodes.NOTIFICATION_PLOT_FORSALE);
		plotTypeNotificationFormat = TownySettings.getConfigLang(ConfigNodes.NOTIFICATION_PLOT_TYPE);
	}

	WorldCoord from, to;
	boolean fromWild = false, toWild = false, toForSale = false,
			toHomeBlock = false, toOutpostBlock = false;
	TownBlock fromTownBlock, toTownBlock = null;
	Town fromTown = null, toTown = null;
	Resident fromResident = null, toResident = null;
	TownBlockType fromPlotType = null, toPlotType = null;

	public ChunkNotification(WorldCoord from, WorldCoord to) {

		this.from = from;
		this.to = to;

		try {
			fromTownBlock = from.getTownBlock();
			fromPlotType = fromTownBlock.getType();
			try {
				fromTown = fromTownBlock.getTown();
			} catch (NotRegisteredException e) {
			}
			try {
				fromResident = fromTownBlock.getResident();
			} catch (NotRegisteredException e) {
			}
		} catch (NotRegisteredException e) {
			fromWild = true;
		}

		try {
			toTownBlock = to.getTownBlock();
			toPlotType = toTownBlock.getType();
			try {
				toTown = toTownBlock.getTown();
			} catch (NotRegisteredException e) {
			}
			try {
				toResident = toTownBlock.getResident();
			} catch (NotRegisteredException e) {
			}

			toForSale = toTownBlock.getPlotPrice() != -1;
			toHomeBlock = toTownBlock.isHomeBlock();
			toOutpostBlock = toTownBlock.isOutpost();
		} catch (NotRegisteredException e) {
			toWild = true;
		}
	}

	public String getNotificationString(Resident resident) {

		if (notificationFormat.length() == 0)
			return null;
		List<String> outputContent = getNotificationContent(resident);
		if (outputContent.size() == 0)
			return null;
		return String.format(notificationFormat, StringMgmt.join(outputContent, notificationSpliter));
	}

	public List<String> getNotificationContent(Resident resident) {

		List<String> out = new ArrayList<String>();
		String output;

		output = getAreaNotification();
		if (output != null && output.length() > 0)
			out.add(output);
		
		// Only adds this if entering the wilderness
		output = getAreaPvPNotification();
		if (output != null && output.length() > 0)
			out.add(output);
		
		// Only show the owner of individual plots if they do not have this mode applied 
		if (!resident.hasMode("ignoreplots")) {
			output = getOwnerNotification();
			if (output != null && output.length() > 0)
				out.add(output);
		}
	
		// Only adds this IF in town.
		output = getTownPVPNotification();
		if (output != null && output.length() > 0)
			out.add(output);

		// Only show the names of plots if they do not have this mode applied
		if (!resident.hasMode("ignoreplots")) {
			output = getPlotNotification();
			if (output != null && output.length() > 0)
				out.add(output);
		}

		return out;
	}

	public String getAreaNotification() {

		if (fromWild ^ toWild || !fromWild && !toWild && fromTown != null && toTown != null && fromTown != toTown) {
			if (toWild)
				try {
					return String.format(areaWildernessNotificationFormat, to.getTownyWorld().getUnclaimedZoneName());
				} catch (NotRegisteredException ex) {
					// Not a Towny registered world
				}
			else
				return String.format(areaTownNotificationFormat, TownyFormatter.getFormattedName(toTown));
		}
		return null;
	}
	
	public String getAreaPvPNotification() {

		if (fromWild ^ toWild || !fromWild && !toWild && fromTown != null && toTown != null && fromTown != toTown) {
			if (toWild)
				try {
					return String.format(areaWildernessPvPNotificationFormat, ((to.getTownyWorld().isPVP() && testWorldPVP()) ? Colors.Red + " (PvP)" : ""));
				} catch (NotRegisteredException ex) {
					// Not a Towny registered world
				}
		}
		return null;
	}

	public String getOwnerNotification() {
			
		if (((fromResident != toResident) || ((fromTownBlock != null) && (toTownBlock != null) && (!fromTownBlock.getName().equalsIgnoreCase(toTownBlock.getName()))))
				&& !toWild) {
			
			if (toResident != null)
				if (TownySettings.isNotificationOwnerShowingNationTitles()) {
					return String.format(ownerNotificationFormat, (toTownBlock.getName().isEmpty()) ? TownyFormatter.getFormattedResidentTitleName(toResident) : toTownBlock.getName());
				} else {
					return String.format(ownerNotificationFormat, (toTownBlock.getName().isEmpty()) ? TownyFormatter.getFormattedName(toResident) : toTownBlock.getName());
				}
			else
				return  String.format(noOwnerNotificationFormat, (toTownBlock.getName().isEmpty()) ? TownySettings.getUnclaimedPlotName() : toTownBlock.getName());

		}
		return null;
	}

	public String getTownPVPNotification() {

		if (!toWild && ((fromWild) || ((toTownBlock.getPermissions().pvp != fromTownBlock.getPermissions().pvp) && !toTown.isPVP()))) {
			try {
				return String.format(areaTownPvPNotificationFormat, ((testWorldPVP() && ((!toTown.isAdminDisabledPVP() && (to.getTownyWorld().isForcePVP() || toTown.isPVP() || toTownBlock.getPermissions().pvp)))) ? Colors.Red + "(PvP)" : Colors.Green + "(No PVP)"));
			} catch (NotRegisteredException e) {
				// Not a Towny registered world.
			}
		}
		return null;
	}

	private boolean testWorldPVP() {

		try {
			return Bukkit.getServer().getWorld(to.getTownyWorld().getName()).getPVP();
		} catch (NotRegisteredException e) {
			// Not a Towny registered world
			return true;
		}
	}

	public String getPlotNotification() {

		if (plotNotificationFormat.length() == 0)
			return null;
		List<String> outputContent = getPlotNotificationContent();
		if (outputContent.size() == 0)
			return null;
		return String.format(plotNotificationFormat, StringMgmt.join(outputContent, plotNotficationSplitter));
	}

	public List<String> getPlotNotificationContent() {

		List<String> out = new ArrayList<String>();
		String output;

		output = getHomeblockNotification();
		if (output != null && output.length() > 0)
			out.add(output);

		output = getOutpostblockNotification();
		if (output != null && output.length() > 0)
			out.add(output);

		output = getForSaleNotification();
		if (output != null && output.length() > 0)
			out.add(output);

		output = getPlotTypeNotification();
		if (output != null && output.length() > 0)
			out.add(output);

		return out;
	}

	public String getHomeblockNotification() {

		if (toHomeBlock)
			return homeBlockNotification;
		return null;
	}

	public String getOutpostblockNotification() {

		if (toOutpostBlock)
			return outpostBlockNotification;
		return null;
	}

	public String getForSaleNotification() {

		if (toForSale)
			return String.format(forSaleNotificationFormat, TownyEconomyHandler.getFormattedBalance(toTownBlock.getPlotPrice()));
		return null;
	}

	public String getPlotTypeNotification() {

		if (fromPlotType != toPlotType && toPlotType != null && toPlotType != TownBlockType.RESIDENTIAL)
			return String.format(plotTypeNotificationFormat, toPlotType.toString());
		return null;
	}
}
