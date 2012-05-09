package com.palmergames.bukkit.towny.war.flagwar;

import com.palmergames.bukkit.towny.*;
import com.palmergames.bukkit.towny.exceptions.EconomyException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.*;
import com.palmergames.bukkit.towny.utils.AreaSelectionUtil;
import com.palmergames.bukkit.towny.war.flagwar.events.CellAttackCanceledEvent;
import com.palmergames.bukkit.towny.war.flagwar.events.CellAttackEvent;
import com.palmergames.bukkit.towny.war.flagwar.events.CellDefendedEvent;
import com.palmergames.bukkit.towny.war.flagwar.events.CellWonEvent;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TownyWar {

	private static Map<Cell, CellUnderAttack> cellsUnderAttack;
	private static Map<String, List<CellUnderAttack>> cellsUnderAttackByPlayer;

	public static void onEnable() {

		cellsUnderAttack = new HashMap<Cell, CellUnderAttack>();
		cellsUnderAttackByPlayer = new HashMap<String, List<CellUnderAttack>>();
	}

	public static void onDisable() {

		for (CellUnderAttack cell : new ArrayList<CellUnderAttack>(cellsUnderAttack.values())) {
			attackCanceled(cell);
		}
	}

	public static void registerAttack(CellUnderAttack cell) throws Exception {

		CellUnderAttack currentData = cellsUnderAttack.get(cell);

		// Check if area is already under attack.
		if (currentData != null)
			throw new Exception(String.format(TownySettings.getLangString("msg_err_enemy_war_cell_already_under_attack"), currentData.getNameOfFlagOwner()));

		String playerName = cell.getNameOfFlagOwner();

		// Check that the user is under his limit of active warflags.
		int futureActiveFlagCount = getNumActiveFlags(playerName) + 1;
		if (futureActiveFlagCount > TownyWarConfig.getMaxActiveFlagsPerPerson())
			throw new Exception(String.format(TownySettings.getLangString("msg_err_enemy_war_reached_max_active_flags"), TownyWarConfig.getMaxActiveFlagsPerPerson()));

		addFlagToPlayerCount(playerName, cell);
		cellsUnderAttack.put(cell, cell);
		cell.begin();
	}

	/**
	 * Get the number of flags actively owned by the specified player.
	 * 
	 * @param playerName the name of the player
	 * @return the number of flags active
	 */
	public static int getNumActiveFlags(String playerName) {

		List<CellUnderAttack> activeFlags = cellsUnderAttackByPlayer.get(playerName);
		return activeFlags == null ? 0 : activeFlags.size();
	}

	public static boolean isUnderAttack(Cell cell) {

		return cellsUnderAttack.containsKey(cell);
	}

	public static CellUnderAttack getAttackData(Cell cell) {

		return cellsUnderAttack.get(cell);
	}

	public static void removeCellUnderAttack(CellUnderAttack cell) {

		removeFlagFromPlayerCount(cell.getNameOfFlagOwner(), cell);
		cellsUnderAttack.remove(cell);
	}

	public static void attackWon(CellUnderAttack cell) {

		CellWonEvent cellWonEvent = new CellWonEvent(cell);
		Bukkit.getServer().getPluginManager().callEvent(cellWonEvent);
		cell.cancel();
		removeCellUnderAttack(cell);
	}

	public static void attackDefended(Player player, CellUnderAttack cell) {

		CellDefendedEvent cellDefendedEvent = new CellDefendedEvent(player, cell);
		Bukkit.getServer().getPluginManager().callEvent(cellDefendedEvent);
		cell.cancel();
		removeCellUnderAttack(cell);
	}

	public static void attackCanceled(CellUnderAttack cell) {

		CellAttackCanceledEvent cellAttackCanceledEvent = new CellAttackCanceledEvent(cell);
		Bukkit.getServer().getPluginManager().callEvent(cellAttackCanceledEvent);
		cell.cancel();
		removeCellUnderAttack(cell);
	}

	public static void removeAttackerFlags(String playerName) {

		List<CellUnderAttack> cells = cellsUnderAttackByPlayer.get(playerName);
		if (cells != null)
			for (CellUnderAttack cell : cells)
				attackCanceled(cell);
	}

	public static List<CellUnderAttack> getCellsUnderAttackByPlayer(String playerName) {

		List<CellUnderAttack> cells = cellsUnderAttackByPlayer.get(playerName);
		if (cells == null)
			return null;
		else
			return new ArrayList<CellUnderAttack>(cells);
	}

	private static void addFlagToPlayerCount(String playerName, CellUnderAttack cell) {

		List<CellUnderAttack> activeFlags = getCellsUnderAttackByPlayer(playerName);
		if (activeFlags == null)
			activeFlags = new ArrayList<CellUnderAttack>();

		activeFlags.add(cell);
		cellsUnderAttackByPlayer.put(playerName, activeFlags);
	}

	private static void removeFlagFromPlayerCount(String playerName, Cell cell) {

		List<CellUnderAttack> activeFlags = cellsUnderAttackByPlayer.get(playerName);
		if (activeFlags != null) {
			if (activeFlags.size() <= 1)
				cellsUnderAttackByPlayer.remove(playerName);
			else {
				activeFlags.remove(cell);
				cellsUnderAttackByPlayer.put(playerName, activeFlags);
			}
		}
	}

	public static void checkBlock(Player player, Block block, Cancellable event) {

		if (TownyWarConfig.isAffectedMaterial(block.getType())) {
			Cell cell = Cell.parse(block.getLocation());
			if (cell.isUnderAttack()) {
				CellUnderAttack cellAttackData = cell.getAttackData();
				if (cellAttackData.isFlag(block)) {
					TownyWar.attackDefended(player, cellAttackData);
					event.setCancelled(true);
				} else if (cellAttackData.isUneditableBlock(block)) {
					event.setCancelled(true);
				}
			}
		}
	}

	public static boolean callAttackCellEvent(Towny plugin, Player player, Block block, WorldCoord worldCoord) throws TownyException {

		int topY = block.getWorld().getHighestBlockYAt(block.getX(), block.getZ()) - 1;
		if (block.getY() < topY)
			throw new TownyException(TownySettings.getLangString("msg_err_enemy_war_must_be_placed_above_ground"));

		TownyUniverse universe = plugin.getTownyUniverse();
		Resident attackingResident;
		Town landOwnerTown, attackingTown;
		Nation landOwnerNation, attackingNation;
		TownBlock townBlock;

		try {
			attackingResident = TownyUniverse.getDataSource().getResident(player.getName());
			attackingTown = attackingResident.getTown();
			attackingNation = attackingTown.getNation();
		} catch (NotRegisteredException e) {
			throw new TownyException(TownySettings.getLangString("msg_err_dont_belong_nation"));
		}

		try {
			landOwnerTown = worldCoord.getTownBlock().getTown();
			townBlock = worldCoord.getTownBlock();
			landOwnerNation = landOwnerTown.getNation();
		} catch (NotRegisteredException e) {
			throw new TownyException(TownySettings.getLangString("msg_err_enemy_war_not_part_of_nation"));
		}

		// Check Neutrality
		if (landOwnerNation.isNeutral())
			throw new TownyException(String.format(TownySettings.getLangString("msg_err_enemy_war_is_neutral"), landOwnerNation.getFormattedName()));
		if (!TownyUniverse.getPermissionSource().isTownyAdmin(player) && attackingNation.isNeutral())
			throw new TownyException(String.format(TownySettings.getLangString("msg_err_enemy_war_is_neutral"), attackingNation.getFormattedName()));

		// Check Minimum Players Online
		checkIfTownHasMinOnlineForWar(landOwnerTown);
		checkIfNationHasMinOnlineForWar(landOwnerNation);
		checkIfTownHasMinOnlineForWar(attackingTown);
		checkIfNationHasMinOnlineForWar(attackingNation);

		// Check that attack takes place on the edge of a town
		if (TownyWarConfig.isAttackingBordersOnly() && !AreaSelectionUtil.isOnEdgeOfOwnership(landOwnerTown, worldCoord))
			throw new TownyException(TownySettings.getLangString("msg_err_enemy_war_not_on_edge_of_town"));

		// Check that the user can pay for the warflag + fines from losing/winning.
		double costToPlaceWarFlag = TownyWarConfig.getCostToPlaceWarFlag();
		if (TownySettings.isUsingEconomy()) {
			try {
				double requiredAmount = costToPlaceWarFlag;
				double balance = attackingResident.getHoldingBalance();

				// Check that the user can pay for the warflag.
				if (balance < costToPlaceWarFlag)
					throw new TownyException(String.format(TownySettings.getLangString("msg_err_insuficient_funds_warflag"), TownyEconomyHandler.getFormattedBalance(costToPlaceWarFlag)));

				// Check that the user can pay the fines from losing/winning all future warflags.
				int activeFlagCount = getNumActiveFlags(attackingResident.getName());
				double defendedAttackCost = TownyWarConfig.getDefendedAttackReward() * (activeFlagCount + 1);
				double attackWinCost = 0;

				double amount;
				amount = TownyWarConfig.getWonHomeblockReward();
				double homeBlockFine = amount < 0 ? -amount : 0;
				amount = TownyWarConfig.getWonTownblockReward();
				double townBlockFine = amount < 0 ? -amount : 0;

				// Assume rest of attacks are townblocks.
				// Would be incorrect if is also currently attacking a homeblock.
				// Error would be caught when actually taking the money when the plot has been won.
				if (townBlock.isHomeBlock())
					attackWinCost = homeBlockFine + activeFlagCount * townBlockFine;
				else
					attackWinCost = (activeFlagCount + 1) * townBlockFine;

				if (defendedAttackCost > 0 && attackWinCost > 0) {
					// There could be a fine
					String reason;
					double cost;
					if (defendedAttackCost > attackWinCost) {
						// Worst case scenario that all attacks are defended.
						requiredAmount += defendedAttackCost;
						cost = defendedAttackCost;
						reason = TownySettings.getLangString("name_defended_attack");
					} else {
						// Worst case scenario that all attacks go through, but is forced to pay a rebuilding fine.
						requiredAmount += attackWinCost;
						cost = attackWinCost;
						reason = TownySettings.getLangString("name_rebuilding");
					}

					// Check if player can pay in worst case scenario.
					if (balance < requiredAmount)
						throw new TownyException(String.format(TownySettings.getLangString("msg_err_insuficient_funds_future"), TownyEconomyHandler.getFormattedBalance(cost), String.format("%d %s", activeFlagCount + 1, reason + "(s)")));
				}
			} catch (EconomyException e) {
				throw new TownyException(e.getError());
			}
		}

		// Call Event (and make sure an attack isn't already under way)
		CellAttackEvent cellAttackEvent = new CellAttackEvent(plugin, player, block);
		plugin.getServer().getPluginManager().callEvent(cellAttackEvent);

		if (cellAttackEvent.isCancelled()) {
			if (cellAttackEvent.hasReason())
				throw new TownyException(cellAttackEvent.getReason());
			else
				return false;
		}

		// Successful Attack

		// Pay for war flag
		if (TownySettings.isUsingEconomy()) {
			// Skip payment + message if no cost.
			if (costToPlaceWarFlag > 0) {
				try {
					attackingResident.pay(costToPlaceWarFlag, "War - WarFlag Cost");
					TownyMessaging.sendResidentMessage(attackingResident, String.format(TownySettings.getLangString("msg_enemy_war_purchased_warflag"), TownyEconomyHandler.getFormattedBalance(costToPlaceWarFlag)));
				} catch (EconomyException e) {
					e.printStackTrace();
				}
			}
		}

		// Set yourself as target's enemy so they can retaliate.
		if (!landOwnerNation.hasEnemy(attackingNation)) {
			landOwnerNation.addEnemy(attackingNation);
			plugin.getTownyUniverse();
			TownyUniverse.getDataSource().saveNation(landOwnerNation);
		}

		// Update Cache
		universe.addWarZone(worldCoord);
		plugin.updateCache(worldCoord);

		TownyMessaging.sendGlobalMessage(String.format(TownySettings.getLangString("msg_enemy_war_area_under_attack"), landOwnerTown.getFormattedName(), worldCoord.toString(), attackingResident.getFormattedName()));
		return true;
	}

	public static void checkIfTownHasMinOnlineForWar(Town town) throws TownyException {

		int requiredOnline = TownyWarConfig.getMinPlayersOnlineInTownForWar();
		int onlinePlayerCount = TownyUniverse.getOnlinePlayers(town).size();
		if (onlinePlayerCount < requiredOnline)
			throw new TownyException(String.format(TownySettings.getLangString("msg_err_enemy_war_require_online"), requiredOnline, town.getFormattedName()));
	}

	public static void checkIfNationHasMinOnlineForWar(Nation nation) throws TownyException {

		int requiredOnline = TownyWarConfig.getMinPlayersOnlineInNationForWar();
		int onlinePlayerCount = TownyUniverse.getOnlinePlayers(nation).size();
		if (onlinePlayerCount < requiredOnline)
			throw new TownyException(String.format(TownySettings.getLangString("msg_err_enemy_war_require_online"), requiredOnline, nation.getFormattedName()));
	}

	public static WorldCoord cellToWorldCoord(Cell cell) throws NotRegisteredException {

		return new WorldCoord(cell.getWorldName(), cell.getX(), cell.getZ());
	}
}
