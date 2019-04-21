package com.palmergames.bukkit.towny;

import com.palmergames.bukkit.towny.exceptions.EconomyException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.ResidentList;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownBlockOwner;
import com.palmergames.bukkit.towny.object.TownBlockType;
import com.palmergames.bukkit.towny.object.TownyObject;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.palmergames.bukkit.towny.permissions.TownyPerms;
import com.palmergames.bukkit.util.BukkitTools;
import com.palmergames.bukkit.util.ChatTools;
import com.palmergames.bukkit.util.Colors;
import com.palmergames.util.StringMgmt;

import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TownyFormatter {

	// private static Towny plugin = null;

	public static final SimpleDateFormat lastOnlineFormat = new SimpleDateFormat("MMMMM dd '@' HH:mm");
	public static final SimpleDateFormat lastOnlineFormatIncludeYear = new SimpleDateFormat("MMMMM dd yyyy");
	public static final SimpleDateFormat registeredFormat = new SimpleDateFormat("MMM d yyyy");

	/**
	 * 1 = Description 2 = Count
	 * 
	 * Colours: 3 = Description and : 4 = Count 5 = Colour for the start of the
	 * list
	 */
	public static final String residentListPrefixFormat = "%3$s%1$s %4$s[%2$d]%3$s:%5$s ";

	public static void initialize(Towny plugin) {

		// TownyFormatter.plugin = plugin;
	}

	public static List<String> getFormattedOnlineResidents(String prefix, ResidentList residentList, Player player) {

		List<Resident> onlineResidents = TownyUniverse.getOnlineResidentsViewable(player, residentList);
		return getFormattedResidents(prefix, onlineResidents);
	}

	public static List<String> getFormattedResidents(Town town) {

		List<String> out = new ArrayList<String>();

		String[] residents = getFormattedNames(town.getResidents().toArray(new Resident[0]));

		out.addAll(ChatTools.listArr(residents, Colors.Green + TownySettings.getLangString("res_list") + " " + Colors.LightGreen + "[" + town.getNumResidents() + "]" + Colors.Green + ":" + Colors.White + " "));

		return out;

	}

	public static List<String> getFormattedOutlaws(Town town) {

		List<String> out = new ArrayList<String>();

		String[] residents = getFormattedNames(town.getOutlaws().toArray(new Resident[0]));

		out.addAll(ChatTools.listArr(residents, TownySettings.getLangString("outlaws") +  " "));

		return out;

	}
	
	public static List<String> getFormattedResidents(String prefix, List<Resident> residentList) {

		return ChatTools.listArr(getFormattedNames(residentList), String.format(residentListPrefixFormat, prefix, residentList.size(), Colors.Green, Colors.LightGreen, Colors.White));
	}

	public static String[] getFormattedNames(List<Resident> residentList) {

		return getFormattedNames(residentList.toArray(new Resident[0]));
	}

	public static String getTime() {

		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
		return sdf.format(System.currentTimeMillis());
	}

	/**
	 * 
	 * @param townBlock
	 * @return a string list containing the results.
	 */
	public static List<String> getStatus(TownBlock townBlock) {

		List<String> out = new ArrayList<String>();

		try {
			TownBlockOwner owner;
			Town town = townBlock.getTown();
			TownyWorld world = townBlock.getWorld();

			if (townBlock.hasResident()) {
				owner = townBlock.getResident();
			} else {
				owner = townBlock.getTown();
			}
			
			

			out.add(ChatTools.formatTitle(TownyFormatter.getFormattedName(owner) + ((BukkitTools.isOnline(owner.getName())) ? TownySettings.getLangString("online") : "")));
			if (!townBlock.getType().equals(TownBlockType.RESIDENTIAL))
				out.add(TownySettings.getLangString("status_plot_type") + townBlock.getType().toString());							
			out.add(TownySettings.getLangString("status_perm") + ((owner instanceof Resident) ? townBlock.getPermissions().getColourString() : townBlock.getPermissions().getColourString().replace("f", "r")));
			out.add(TownySettings.getLangString("status_pvp") + ((town.isPVP() || world.isForcePVP() || townBlock.getPermissions().pvp) ? TownySettings.getLangString("status_on"): TownySettings.getLangString("status_off")) + 
					TownySettings.getLangString("explosions") + ((world.isForceExpl() || townBlock.getPermissions().explosion) ? TownySettings.getLangString("status_on"): TownySettings.getLangString("status_off")) + 
					TownySettings.getLangString("firespread") + ((town.isFire() || world.isForceFire() || townBlock.getPermissions().fire) ? TownySettings.getLangString("status_on"):TownySettings.getLangString("status_off")) + 
					TownySettings.getLangString("mobspawns") + ((town.hasMobs() || world.isForceTownMobs() || townBlock.getPermissions().mobs) ?  TownySettings.getLangString("status_on"): TownySettings.getLangString("status_off")));

		} catch (NotRegisteredException e) {
			out.add("Error: " + e.getMessage());
		}
		out = formatStatusScreens(out);
		return out;
	}

	/**
	 * 
	 * @param resident
	 * @return a string list containing the results.
	 */
	public static List<String> getStatus(Resident resident, Player player) {

		List<String> out = new ArrayList<String>();

		// ___[ King Harlus ]___
		out.add(ChatTools.formatTitle(getFormattedName(resident) + ((BukkitTools.isOnline(resident.getName()) && (player != null) && (player.canSee(BukkitTools.getPlayer(resident.getName())))) ? TownySettings.getLangString("online2") : "")));

		// First used if last online is this year, 2nd used if last online is early than this year.
		// Registered: Sept 3 2009 | Last Online: March 7 @ 14:30
		// Registered: Sept 3 2009 | Last Online: March 7 2009
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(resident.getLastOnline());
		int currentYear = cal.get(Calendar.YEAR);
		cal.setTimeInMillis(System.currentTimeMillis());
		int lastOnlineYear = cal.get(Calendar.YEAR);
		if (currentYear == lastOnlineYear)
			out.add(String.format(TownySettings.getLangString("registered_last_online"), registeredFormat.format(resident.getRegistered()), lastOnlineFormat.format(resident.getLastOnline())));
		else 
			out.add(String.format(TownySettings.getLangString("registered_last_online"), registeredFormat.format(resident.getRegistered()), lastOnlineFormatIncludeYear.format(resident.getLastOnline())));

		// Owner of: 4 plots
		// Perm: Build = f-- Destroy = fa- Switch = fao Item = ---
		// if (resident.getTownBlocks().size() > 0) {
		out.add(String.format(TownySettings.getLangString("owner_of_x_plots"), resident.getTownBlocks().size()));
		out.add(TownySettings.getLangString("status_perm") + resident.getPermissions().getColourString());
		out.add(TownySettings.getLangString("status_pvp") + ((resident.getPermissions().pvp) ? TownySettings.getLangString("status_on"): TownySettings.getLangString("status_off")) + 
				TownySettings.getLangString("explosions") + ((resident.getPermissions().explosion) ? TownySettings.getLangString("status_on"): TownySettings.getLangString("status_off")) + 
				TownySettings.getLangString("firespread") + ((resident.getPermissions().fire) ? TownySettings.getLangString("status_on"): TownySettings.getLangString("status_off")) + 
				TownySettings.getLangString("mobspawns") + ((resident.getPermissions().mobs) ? TownySettings.getLangString("status_on"): TownySettings.getLangString("status_off")));
		// }

		// Bank: 534 coins
		if (TownySettings.isUsingEconomy())
			if (TownyEconomyHandler.isActive())
				out.add(String.format(TownySettings.getLangString("status_bank"), resident.getHoldingFormattedBalance()));

		// Town: Camelot
		String line = TownySettings.getLangString("status_town");
		if (!resident.hasTown())
			line += TownySettings.getLangString("status_no_town");
		else
			try {
				line += getFormattedName(resident.getTown());
			} catch (TownyException e) {
				line += "Error: " + e.getMessage();
			}
		out.add(line);
		
		// Town ranks
		if (resident.hasTown()) {
			if (!resident.getTownRanks().isEmpty())
				out.add(TownySettings.getLangString("status_town_ranks") + StringMgmt.join(resident.getTownRanks(), ","));
		}
		
		//Nation ranks
		if (resident.hasNation()) {
			if (!resident.getNationRanks().isEmpty())
				out.add(TownySettings.getLangString("status_nation_ranks") + StringMgmt.join(resident.getNationRanks(), ","));
		}
		
		// Jailed: yes if they are jailed.
		if (resident.isJailed()){
			out.add(String.format(TownySettings.getLangString("jailed_in_town"), resident.getJailTown()));
		}
		
		// Friends [12]: James, Carry, Mason
		List<Resident> friends = resident.getFriends();
		out.addAll(getFormattedResidents(TownySettings.getLangString("status_friends"), friends));
		
		out = formatStatusScreens(out);
		return out;
	}

	/**
	 * Returns a Chat Formatted List of all town residents who hold a rank.
	 * 
	 * @param town
	 * @return a list containing formatted rank data.
	 */
	public static List<String> getRanks(Town town) {

		List<String> ranklist = new ArrayList<String>();

		String towntitle = getFormattedName(town);
		towntitle += TownySettings.getLangString("rank_list_title");
		ranklist.add(ChatTools.formatTitle(towntitle));
		ranklist.add(String.format(TownySettings.getLangString("rank_list_mayor"), getFormattedName(town.getMayor())));

		List<Resident> residents = town.getResidents();
		List<String> townranks = TownyPerms.getTownRanks();
		List<Resident> residentwithrank = new ArrayList<Resident>();

		for (String rank : townranks) {
			for (Resident r : residents) {

				if ((r.getTownRanks() != null) && (r.getTownRanks().contains(rank))) {
					residentwithrank.add(r);
				}
			}
			ranklist.addAll(getFormattedResidents(rank, residentwithrank));
			residentwithrank.clear();
		}
		return ranklist;
	}

	/**
	 * 
	 * @param out - List<String>
	 * @return a string list with all lines split at a middle-ish " " and shortened so that they fit equally into 80 characters chat line.
	 * Please do not pass this anything longer than 159 characters. 
	 */
	public static List<String> formatStatusScreens(List<String> out) {
		
		List<String> formattedOut = new ArrayList<String>();
		for (String line: out) {
			if (line.length() > 80) {
				int middle = (line.length()/2);
				int before = line.lastIndexOf(' ', middle);
				int after = line.lastIndexOf(' ', middle + 1);
				if (middle - before < after - middle) 
					middle = before;
				else
					middle = after;
				
				String first = line.substring(0, middle);
				String second = line.substring(middle + 1);
				formattedOut.add(first);
				formattedOut.add(second);					
			} else {
				formattedOut.add(line);
			}
		}
		return formattedOut;
	}	
	
	/**
	 * 
	 * @param town
	 * @return a string list containing the results.
	 */
	public static List<String> getStatus(Town town) {

		List<String> out = new ArrayList<String>();

		TownyWorld world = town.getWorld();

		// ___[ Raccoon City (PvP) (Open) ]___
		String title = getFormattedName(town);
		title += ((!town.isAdminDisabledPVP()) && ((town.isPVP() || town.getWorld().isForcePVP())) ? TownySettings.getLangString("status_title_pvp") : "");
		title += (town.isOpen() ? TownySettings.getLangString("status_title_open") : "");
		out.add(ChatTools.formatTitle(title));

		// Lord: Mayor Quimby
		// Board: Get your fried chicken
		try {
			out.add(String.format(TownySettings.getLangString("status_town_board"), town.getTownBoard()));
		} catch (NullPointerException e) {
		}
		// Created Date
		Long registered= town.getRegistered();
		if (registered != 0) {
			out.add(String.format(TownySettings.getLangString("status_founded"), registeredFormat.format(town.getRegistered())));
		}


		// Town Size: 0 / 16 [Bought: 0/48] [Bonus: 0] [Home: 33,44]
		try {
			out.add(String.format(TownySettings.getLangString("status_town_size_part_1"), town.getTownBlocks().size(), TownySettings.getMaxTownBlocks(town)) +  
		            (TownySettings.isSellingBonusBlocks() ? String.format(TownySettings.getLangString("status_town_size_part_2"), town.getPurchasedBlocks(), TownySettings.getMaxPurchedBlocks()) : "") + 
		            (town.getBonusBlocks() > 0 ? String.format(TownySettings.getLangString("status_town_size_part_3"), town.getBonusBlocks()) : "") + 
		            (TownySettings.getNationBonusBlocks(town) > 0 ? String.format(TownySettings.getLangString("status_town_size_part_4"), TownySettings.getNationBonusBlocks(town)) : "") + 
		            (town.isPublic() ? TownySettings.getLangString("status_town_size_part_5") + (town.hasHomeBlock() ? town.getHomeBlock().getCoord().toString() : TownySettings.getLangString("status_no_town")) + "]" : "")
		           );
		} catch (TownyException e) {
		}

		if (TownySettings.isAllowingOutposts()) {
			if (TownySettings.isOutpostsLimitedByLevels()) {
				if (town.hasOutpostSpawn())
					if (!town.hasNation())
						out.add(String.format(TownySettings.getLangString("status_town_outposts"), town.getMaxOutpostSpawn(), town.getOutpostLimit()));
					else {
						int nationBonus = 0;
						try {
							nationBonus =  (Integer) TownySettings.getNationLevel(town.getNation()).get(TownySettings.NationLevel.NATION_BONUS_OUTPOST_LIMIT);
						} catch (NotRegisteredException e1) {
						}
						out.add(String.format(TownySettings.getLangString("status_town_outposts"), town.getMaxOutpostSpawn(), town.getOutpostLimit()) + 
								(nationBonus > 0 ? String.format(TownySettings.getLangString("status_town_outposts2"), nationBonus) : "")
							   );
						}
					
				else 
					out.add(String.format(TownySettings.getLangString("status_town_outposts3"), town.getOutpostLimit()));
			} else if (town.hasOutpostSpawn()) {
				out.add(String.format(TownySettings.getLangString("status_town_outposts4"), town.getMaxOutpostSpawn()));
			}
		}

		// Permissions: B=rao D=--- S=ra-
		out.add(TownySettings.getLangString("status_permissions") + town.getPermissions().getColourString().replace("f", "r"));
		out.add(TownySettings.getLangString("explosions2") + ((town.isBANG() || world.isForceExpl()) ? TownySettings.getLangString("status_on"): TownySettings.getLangString("status_off")) + 
				TownySettings.getLangString("firespread") + ((town.isFire() || world.isForceFire()) ? TownySettings.getLangString("status_on"): TownySettings.getLangString("status_off")) + 
				TownySettings.getLangString("mobspawns") + ((town.hasMobs() || world.isForceTownMobs()) ? TownySettings.getLangString("status_on"): TownySettings.getLangString("status_off")));

		// | Bank: 534 coins
		String bankString = "";
		if (TownySettings.isUsingEconomy()) {
			if (TownyEconomyHandler.isActive()) {
				bankString = String.format(TownySettings.getLangString("status_bank"), town.getHoldingFormattedBalance());
				if (town.hasUpkeep())
					bankString += String.format(TownySettings.getLangString("status_bank_town2"), new BigDecimal(TownySettings.getTownUpkeepCost(town)).setScale(2, RoundingMode.HALF_UP).doubleValue());
				bankString += String.format(TownySettings.getLangString("status_bank_town3"), town.getTaxes()) + (town.isTaxPercentage() ? "%" : "");
			}
			out.add(bankString);
		}

		// Mayor: MrSand | Bank: 534 coins
		out.add(String.format(TownySettings.getLangString("rank_list_mayor"), getFormattedName(town.getMayor())));

		// Assistants [2]: Sammy, Ginger
		List<String> ranklist = new ArrayList<String>();
		List<Resident> residentss = town.getResidents();
		List<String> townranks = TownyPerms.getTownRanks();
		List<Resident> residentwithrank = new ArrayList<Resident>();

		for (String rank : townranks) {
			for (Resident r : residentss) {

				if ((r.getTownRanks() != null) && (r.getTownRanks().contains(rank))) {
					residentwithrank.add(r);
				}
			}
			ranklist.addAll(getFormattedResidents(rank, residentwithrank));
			residentwithrank.clear();
		}

		out.addAll(ranklist);

		// Nation: Azur Empire
		try {
			out.add(String.format(TownySettings.getLangString("status_town_nation"), getFormattedName(town.getNation())));
		} catch (TownyException e) {
		}

		// Residents [12]: James, Carry, Mason

		String[] residents = getFormattedNames(town.getResidents().toArray(new Resident[0]));
		if (residents.length > 34) {
			String[] entire = residents;
			residents = new String[36];
			System.arraycopy(entire, 0, residents, 0, 35);
			residents[35] = TownySettings.getLangString("status_town_reslist_overlength");
		}
		out.addAll(ChatTools.listArr(residents, String.format(TownySettings.getLangString("status_town_reslist"), town.getNumResidents() )));		

		out = formatStatusScreens(out);
		return out;
	}

	/**
	 * 
	 * @param nation
	 * @return a string list containing the results.
	 */
	public static List<String> getStatus(Nation nation) {

		List<String> out = new ArrayList<String>();

		// ___[ Azur Empire ]___
		out.add(ChatTools.formatTitle(getFormattedName(nation)));

		// Created Date
		Long registered = nation.getRegistered();
		if (registered != 0) {
			out.add(String.format(TownySettings.getLangString("status_founded"),  registeredFormat.format(nation.getRegistered())));
		}
		// Bank: 534 coins
		String line = "";
		if (TownySettings.isUsingEconomy())
			if (TownyEconomyHandler.isActive()) {
				line = String.format(TownySettings.getLangString("status_bank"), nation.getHoldingFormattedBalance());

				if (TownySettings.getNationUpkeepCost(nation) > 0)
					line += String.format(TownySettings.getLangString("status_bank_town2"), TownySettings.getNationUpkeepCost(nation));

			}

		if (nation.isNeutral()) {
			if (line.length() > 0)
				line += Colors.Gray + " | ";
			line += TownySettings.getLangString("status_nation_peaceful");
		}
		
		if (nation.isPublic()) {
			if (line.length() > 0)
				line += Colors.Gray + " | ";
			line += TownySettings.getLangString("status_public");
		}		
		// Bank: 534 coins | Peaceful | Public
		
		if (line.length() > 0)
			out.add(line);

		// King: King Harlus
		if (nation.getNumTowns() > 0 && nation.hasCapital() && nation.getCapital().hasMayor())
			out.add(String.format(TownySettings.getLangString("status_nation_king"), getFormattedName(nation.getCapital().getMayor())) + 
					String.format(TownySettings.getLangString("status_nation_tax"), nation.getTaxes())
				   );
		// Assistants [2]: Sammy, Ginger
		List<String> ranklist = new ArrayList<String>();
		List<Town> towns = nation.getTowns();
		List<Resident> residents = new ArrayList<Resident>();
		
		for (Town town: towns) {
			 residents.addAll(town.getResidents());
		}
		
		List<String> nationranks = TownyPerms.getNationRanks();
		List<Resident> residentwithrank = new ArrayList<Resident>();

		for (String rank : nationranks) {
			for (Resident r : residents) {
				if ((r.getNationRanks() != null) && (r.getNationRanks().contains(rank))) {
					residentwithrank.add(r);
				}
			}
			ranklist.addAll(getFormattedResidents(rank, residentwithrank));
			residentwithrank.clear();
		}
		if (ranklist != null)
			out.addAll(ranklist);
		
		// Towns [44]: James City, Carry Grove, Mason Town
		out.addAll(ChatTools.listArr(getFormattedNames(nation.getTowns().toArray(new Town[0])), String.format(TownySettings.getLangString("status_nation_towns"), nation.getNumTowns())));		
		// Allies [4]: James Nation, Carry Territory, Mason Country
		out.addAll(ChatTools.listArr(getFormattedNames(nation.getAllies().toArray(new Nation[0])), String.format(TownySettings.getLangString("status_nation_allies"), nation.getAllies().size())));
		// Enemies [4]: James Nation, Carry Territory, Mason Country
        out.addAll(ChatTools.listArr(getFormattedNames(nation.getEnemies().toArray(new Nation[0])), String.format(TownySettings.getLangString("status_nation_enemies"), nation.getEnemies().size())));

		out = formatStatusScreens(out);
		return out;
	}

	/**
	 * 
	 * @param world
	 * @return a string list containing the results.
	 */
	public static List<String> getStatus(TownyWorld world) {

		List<String> out = new ArrayList<String>();

		// ___[ World (PvP) ]___
		String title = getFormattedName(world);
		title += ((world.isPVP() || world.isForcePVP()) ? TownySettings.getLangString("status_title_pvp") : "");
		title += (world.isClaimable() ? TownySettings.getLangString("status_world_claimable") : TownySettings.getLangString("status_world_noclaims"));
		out.add(ChatTools.formatTitle(title));

		if (!world.isUsingTowny()) {
			out.add(TownySettings.getLangString("msg_set_use_towny_off"));
		} else {
			// ForcePvP: No | Fire: Off
			out.add(TownySettings.getLangString("status_world_forcepvp") + (world.isForcePVP() ? TownySettings.getLangString("status_on") : TownySettings.getLangString("status_off")) + Colors.Gray + " | " + 
					TownySettings.getLangString("status_world_fire") + (world.isFire() ? TownySettings.getLangString("status_on") : TownySettings.getLangString("status_off")) + Colors.Gray + " | " + 
					TownySettings.getLangString("status_world_forcefire") + (world.isForceFire() ? TownySettings.getLangString("status_forced") : TownySettings.getLangString("status_adjustable"))
				   );
			out.add(TownySettings.getLangString("explosions2") + ": " + (world.isExpl() ? TownySettings.getLangString("status_on") : TownySettings.getLangString("status_off")) + Colors.Gray + " | " + 
				    TownySettings.getLangString("status_world_forceexplosion") + (world.isForceExpl() ? TownySettings.getLangString("status_forced") : TownySettings.getLangString("status_adjustable"))
				   );
			out.add(TownySettings.getLangString("status_world_worldmobs") + (world.hasWorldMobs() ? TownySettings.getLangString("status_on") : TownySettings.getLangString("status_off")) + Colors.Gray + " | " + 
				    TownySettings.getLangString("status_world_forcetownmobs") + (world.isForceTownMobs() ? TownySettings.getLangString("status_forced") : TownySettings.getLangString("status_adjustable"))
				   );
			// Using Default Settings: Yes
			// out.add(Colors.Green + "Using Default Settings: " +
			// (world.isUsingDefault() ? Colors.LightGreen + "Yes" : Colors.Rose
			// + "No"));

			out.add(TownySettings.getLangString("status_world_unclaimrevert") + (world.isUsingPlotManagementRevert() ? TownySettings.getLangString("status_on_good") : TownySettings.getLangString("status_off_bad")) + Colors.Gray + " | " + 
			        TownySettings.getLangString("status_world_explrevert") + (world.isUsingPlotManagementWildRevert() ? TownySettings.getLangString("status_on_good") : TownySettings.getLangString("status_off_bad")));
			// Wilderness:
			// Build, Destroy, Switch
			// Ignored Blocks: 34, 45, 64
			out.add(Colors.Green + world.getUnclaimedZoneName() + ":");
			out.add("    " + (world.getUnclaimedZoneBuild() ? Colors.LightGreen : Colors.Rose) + "Build" + Colors.Gray + ", " + (world.getUnclaimedZoneDestroy() ? Colors.LightGreen : Colors.Rose) + "Destroy" + Colors.Gray + ", " + (world.getUnclaimedZoneSwitch() ? Colors.LightGreen : Colors.Rose) + "Switch" + Colors.Gray + ", " + (world.getUnclaimedZoneItemUse() ? Colors.LightGreen : Colors.Rose) + "ItemUse");
			out.add("    " + TownySettings.getLangString("status_world_ignoredblocks") + Colors.LightGreen + " " + StringMgmt.join(world.getUnclaimedZoneIgnoreMaterials(), ", "));
		}
		
		out = formatStatusScreens(out);
		return out;
	}

	/**
	 * Returns the tax info this resident will have to pay at the next new day.
	 * 
	 * @param resident
	 * @return tax status message
	 */
	public static List<String> getTaxStatus(Resident resident) {

		List<String> out = new ArrayList<String>();
		Town town = null;

		double plotTax = 0.0;

		out.add(ChatTools.formatTitle(getFormattedName(resident) + ((BukkitTools.isOnline(resident.getName())) ? Colors.LightGreen + " (Online)" : "")));

		if (resident.hasTown()) {
			try {
				town = resident.getTown();
				out.add(String.format(TownySettings.getLangString("owner_of_x_plots"),resident.getTownBlocks().size()));

				if (TownyPerms.getResidentPerms(resident).containsKey("towny.tax_exempt")) {
					out.add(TownySettings.getLangString("status_res_taxexempt"));
				} else {
					if (town.isTaxPercentage()) {
						out.add(String.format(TownySettings.getLangString("status_res_tax"), resident.getHoldingBalance() * town.getTaxes() / 100));
					} else {
						out.add(String.format(TownySettings.getLangString("status_res_tax"), town.getTaxes()));

						if ((resident.getTownBlocks().size() > 0)) {

							for (TownBlock townBlock : new ArrayList<TownBlock>(resident.getTownBlocks())) {
								plotTax += townBlock.getType().getTax(townBlock.getTown());
							}

							out.add(TownySettings.getLangString("status_res_plottax") + plotTax);
						}
						out.add(TownySettings.getLangString("status_res_totaltax") + (town.getTaxes() + plotTax));
					}
				}

			} catch (NotRegisteredException e) {
				// Failed to fetch town
			} catch (EconomyException e) {
				// Economy failed
			}
		}

		return out;
	}

	public static String getNamePrefix(Resident resident) {

		if (resident == null)
			return "";
		if (resident.isKing())
			return TownySettings.getKingPrefix(resident);
		else if (resident.isMayor())
			return TownySettings.getMayorPrefix(resident);
		return "";
	}

	public static String getNamePostfix(Resident resident) {

		if (resident == null)
			return "";
		if (resident.isKing())
			return TownySettings.getKingPostfix(resident);
		else if (resident.isMayor())
			return TownySettings.getMayorPostfix(resident);
		return "";
	}

	public static String getFormattedName(TownyObject obj) {

		if (obj == null)
			return "Null";
		else if (obj instanceof Resident)
			return getFormattedResidentName((Resident) obj);
		else if (obj instanceof Town)
			return getFormattedTownName((Town) obj);
		else if (obj instanceof Nation)
			return getFormattedNationName((Nation) obj);
		// System.out.println("just name: " + obj.getName());
		return obj.getName().replaceAll("_", " ");
	}

	public static String getFormattedResidentName(Resident resident) {

		if (resident == null)
			return "null";
		if (resident.isKing())
			return TownySettings.getKingPrefix(resident) + resident.getName() + TownySettings.getKingPostfix(resident);
		else if (resident.isMayor())
			return TownySettings.getMayorPrefix(resident) + resident.getName() + TownySettings.getMayorPostfix(resident);
		return resident.getName();
	}

	public static String getFormattedTownName(Town town) {

		if (town.isCapital())
			return TownySettings.getCapitalPrefix(town) + town.getName().replaceAll("_", " ") + TownySettings.getCapitalPostfix(town);
		return TownySettings.getTownPrefix(town) + town.getName().replaceAll("_", " ") + TownySettings.getTownPostfix(town);
	}

	public static String getFormattedNationName(Nation nation) {

		return TownySettings.getNationPrefix(nation) + nation.getName().replaceAll("_", " ") + TownySettings.getNationPostfix(nation);
	}
	
	public static String getFormattedResidentTitleName(Resident resident) {
		if (!resident.hasTitle())
			return getFormattedName(resident);
		else 
			return resident.getTitle() + " " + resident.getName();		
		
	}

	public static String[] getFormattedNames(Resident[] residents) {

		List<String> names = new ArrayList<String>();
		for (Resident resident : residents)
			names.add(getFormattedName(resident));
		return names.toArray(new String[0]);
	}

	public static String[] getFormattedNames(Town[] towns) {

		List<String> names = new ArrayList<String>();
		for (Town town : towns)
			names.add(getFormattedName(town));
		return names.toArray(new String[0]);
	}

	public static String[] getFormattedNames(Nation[] nations) {

		List<String> names = new ArrayList<String>();
		for (Nation nation : nations)
			names.add(getFormattedName(nation));
		return names.toArray(new String[0]);
	}
}
