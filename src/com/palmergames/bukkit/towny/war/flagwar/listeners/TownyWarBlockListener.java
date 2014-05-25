package com.palmergames.bukkit.towny.war.flagwar.listeners;

import org.bukkit.block.Block;
//import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
//import org.bukkit.event.block.BlockPlaceEvent;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.object.PlayerCache;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import com.palmergames.bukkit.towny.war.flagwar.TownyWar;
import com.palmergames.bukkit.towny.war.flagwar.TownyWarConfig;
import com.palmergames.bukkit.util.BukkitTools;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class TownyWarBlockListener implements Listener {

	//private Towny plugin;

	public TownyWarBlockListener(Towny plugin) {

		//this.plugin = plugin;
	}

	/**
	 * For Testing purposes only.
	 */
	/*
	 * @EventHandler(priority = EventPriority.LOWEST)
	 * public void onBlockPlace(BlockPlaceEvent event) {
	 * Player player = event.getPlayer();
	 * Block block = event.getBlockPlaced();
	 * 
	 * if (block == null)
	 * return;
	 * 
	 * if (block.getType() == TownyWarConfig.getFlagBaseMaterial()) {
	 * int topY = block.getWorld().getHighestBlockYAt(block.getX(),
	 * block.getZ()) - 1;
	 * if (block.getY() >= topY) {
	 * CellAttackEvent cellAttackEvent = new CellAttackEvent(player, block);
	 * this.plugin.getServer().getPluginManager().callEvent(cellAttackEvent);
	 * if (cellAttackEvent.isCancelled()) {
	 * event.setBuild(false);
	 * event.setCancelled(true);
	 * }
	 * }
	 * }
	 * }
	 */

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent event) {

		Block block = event.getBlock();
                Player player = event.getPlayer();
                
                if (TownyWarConfig.isEditableMaterialInWarZone(block.getType())) {
                    TownyWar.checkBlock(event.getPlayer(), event.getBlock(), event);
                    player.damage(TownyWarConfig.blockgriefingdamage());
			} 
                else {
                    event.setCancelled(true);
                    TownyMessaging.sendErrorMsg(event.getPlayer(), String.format(TownySettings.getLangString("msg_err_warzone_cannot_edit_material"), "destroy", block.getType().toString().toLowerCase()));
            }
	}
        public class TownyPlayerListener implements Listener {

                private final Towny plugin;

                public TownyPlayerListener(Towny instance) {

                        plugin = instance;
                }
        
        public void onPlayerSwitchEvent(PlayerInteractEvent event, String errMsg, TownyWorld world) {

		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		event.setCancelled(onPlayerSwitchEvent(player, block, errMsg, world));

                }
        
	public boolean onPlayerSwitchEvent(Player player, Block block, String errMsg, TownyWorld world) {

		if (!TownySettings.isSwitchMaterial(block.getType().name()))
			return false;

		// Get switch permissions (updates if none exist)
		boolean bSwitch = PlayerCacheUtil.getCachePermission(player, block.getLocation(), BukkitTools.getTypeId(block), BukkitTools.getData(block), TownyPermission.ActionType.SWITCH);

		// Allow switch if we are permitted
		if (bSwitch)
			return false;

		/*
		 * Fetch the players cache
		 */
		PlayerCache cache = plugin.getCache(player);
		PlayerCache.TownBlockStatus status = cache.getStatus();

		/*
		 * display any error recorded for this plot
		 */
		if (cache.hasBlockErrMsg())
			TownyMessaging.sendErrorMsg(player, cache.getBlockErrMsg());

		/*
		 * Flag war
		 */
		if (status == PlayerCache.TownBlockStatus.WARZONE) {
			if (!TownyWarConfig.isAllowingSwitchesInWarZone()) {
				TownyMessaging.sendErrorMsg(player, TownySettings.getLangString("msg_err_warzone_cannot_use_switches"));
				return true;
			}
			return false;
		} else {
			return true;
		}

	}
}
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockBurn(BlockBurnEvent event) {

		TownyWar.checkBlock(null, event.getBlock(), event);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {

		for (Block block : event.getBlocks())
			TownyWar.checkBlock(null, block, event);
	}

	/**
	 * TODO: Need to check if a immutable block is being moved with a sticky
	 * piston.
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {

	}
}
