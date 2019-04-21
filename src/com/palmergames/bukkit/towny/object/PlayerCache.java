package com.palmergames.bukkit.towny.object;

import com.palmergames.bukkit.towny.object.TownyPermission.ActionType;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerCache {

	private HashMap<Material, Boolean> buildMatPermission = new HashMap<Material,Boolean>();
	private HashMap<Material, Boolean> destroyMatPermission = new HashMap<Material,Boolean>();
	private HashMap<Material, Boolean> switchMatPermission = new HashMap<Material,Boolean>();
	private HashMap<Material, Boolean> itemUseMatPermission = new HashMap<Material,Boolean>();
	
	private HashMap<Integer, HashMap<Byte, Boolean>> buildPermission = new HashMap<Integer, HashMap<Byte,Boolean>>();
	private HashMap<Integer, HashMap<Byte, Boolean>> destroyPermission = new HashMap<Integer, HashMap<Byte,Boolean>>();
	private HashMap<Integer, HashMap<Byte, Boolean>> switchPermission = new HashMap<Integer, HashMap<Byte,Boolean>>();
	private HashMap<Integer, HashMap<Byte, Boolean>> itemUsePermission = new HashMap<Integer, HashMap<Byte,Boolean>>();
	
	private WorldCoord lastWorldCoord;
	private String blockErrMsg;
	private Location lastLocation;

	//TODO: cache last entity attacked

	public PlayerCache(TownyWorld world, Player player) {

		this(new WorldCoord(world.getName(), Coord.parseCoord(player)));
		setLastLocation(player.getLocation());
	}

	public PlayerCache(WorldCoord worldCoord) {

		this.setLastTownBlock(worldCoord);
	}

	/**
	 * Update the cache with new coordinates.
	 * 
	 * @param worldCoord
	 */
	public void setLastTownBlock(WorldCoord worldCoord) {

		this.lastWorldCoord = worldCoord;
	}
	
	/**
	 * Reset the cache permissions and update the cache with new coordinates.
	 * 
	 * @param worldCoord
	 */
	public void resetAndUpdate(WorldCoord worldCoord) {
		
		reset();
		setLastTownBlock(worldCoord);
	}

	/**
	 * Retrieve the last cached WorldCoord
	 * 
	 * @return WorldCoord of the last acted upon TownBlock
	 */
	public WorldCoord getLastTownBlock() {

		return lastWorldCoord;
	}
	
	/**
	 * Update the players WorldCoord, resetting all permissions if it has changed.
	 * 
	 * @param pos
	 * @return true if changed.
	 */
	public boolean updateCoord(WorldCoord pos) {

		if (!getLastTownBlock().equals(pos)) {
			reset();
			setLastTownBlock(pos);
			return true;
		} else
			return false;
	}
	
	public boolean getCachePermission(Material material, ActionType action) throws NullPointerException {

		switch (action) {

		case BUILD: // BUILD
			return getBuildPermission(material);

		case DESTROY: // DESTROY
			return getDestroyPermission(material);

		case SWITCH: // SWITCH
			return getSwitchPermission(material);

		case ITEM_USE: // ITEM_USE
			return getItemUsePermission(material);

		default:
			throw new NullPointerException();

		}

	}

	public void setBuildPermission(Material material, Boolean value) {

		updateMaps(buildMatPermission, material, value);

	}
	public void setDestroyPermission(Material material, Boolean value) {

		updateMaps(destroyMatPermission, material, value);
	}
	public void setSwitchPermission(Material material, Boolean value) {

		updateMaps(switchMatPermission, material, value);

	}
	public void setItemUsePermission(Material material, Boolean value) {

		updateMaps(itemUseMatPermission, material, value);
		
	}
	
	public boolean getBuildPermission(Material material) throws NullPointerException {

		return getBlockPermission(buildMatPermission, material);

	}
	public boolean getDestroyPermission(Material material) throws NullPointerException {

		return getBlockPermission(destroyMatPermission, material);
		
	}
	public boolean getSwitchPermission(Material material) throws NullPointerException {

		return getBlockPermission(switchMatPermission, material);
		
	}
	public Boolean getItemUsePermission(Material material) throws NullPointerException {

		return getBlockPermission(itemUseMatPermission, material);
		
	}
	
	private void updateMaps(HashMap<Material, Boolean> blockMap, Material material, Boolean value) {
		
		if (!blockMap.containsKey(material)) {
			/*
			 * We have no permissions cached for this block.
			 */			
			blockMap.put(material, value);
		} else {
			/*
			 * We have cached permissions for this block type so just push updated data.
			 */
			blockMap.get(material);
		}
	}
	
	private boolean getBlockPermission(HashMap<Material, Boolean> blockMap, Material material) throws NullPointerException {
		
		if (!blockMap.containsKey(material))
			throw new NullPointerException();
		
		return blockMap.get(material);
		
	}

	private void reset() {

		lastWorldCoord = null;
		townBlockStatus = null;
		blockErrMsg = null;
		
		buildMatPermission = new HashMap<Material,Boolean>();
		destroyMatPermission = new HashMap<Material,Boolean>();
		switchMatPermission = new HashMap<Material,Boolean>();
		itemUseMatPermission = new HashMap<Material,Boolean>();
		
		// Pre 1.13 hashmaps here.
		buildPermission = new HashMap<Integer, HashMap<Byte,Boolean>>();
		destroyPermission = new HashMap<Integer, HashMap<Byte,Boolean>>();
		switchPermission = new HashMap<Integer, HashMap<Byte,Boolean>>();
		itemUsePermission = new HashMap<Integer, HashMap<Byte,Boolean>>();
	}

	public enum TownBlockStatus {
		UNKOWN, NOT_REGISTERED, OFF_WORLD, // In a world untouched by towny.
		ADMIN,
		UNCLAIMED_ZONE,
		NATION_ZONE,
		LOCKED,
		WARZONE,
		OUTSIDER,
		PLOT_OWNER,
		PLOT_FRIEND,
		PLOT_ALLY,
		TOWN_OWNER,
		TOWN_RESIDENT,
		TOWN_ALLY,
		ENEMY
	}

	private TownBlockStatus townBlockStatus = TownBlockStatus.UNKOWN;

	public void setStatus(TownBlockStatus townBlockStatus) {

		this.townBlockStatus = townBlockStatus;
	}

	public TownBlockStatus getStatus() throws NullPointerException {

		if (townBlockStatus == null)
			throw new NullPointerException();
		else
			return townBlockStatus;
	}

	public void setBlockErrMsg(String blockErrMsg) {

		this.blockErrMsg = blockErrMsg;
	}

	public String getBlockErrMsg() {

		String temp = blockErrMsg;
		setBlockErrMsg(null); // Delete error msg after reading it.
		return temp;
	}

	public boolean hasBlockErrMsg() {

		return blockErrMsg != null;
	}

	public void setLastLocation(Location lastLocation) {

		this.lastLocation = lastLocation.clone();
	}

	public Location getLastLocation() throws NullPointerException {

		if (lastLocation == null)
			throw new NullPointerException();
		else
			return lastLocation;
	}
	
	/*
	 * All pre-1.13 playercache checks below here now. * 
	 * 
	 */
	
	
	@Deprecated
	public boolean getCachePermission(Integer id, byte data, ActionType action) throws NullPointerException {
		switch (action) {
			case BUILD: // BUILD
				return getBuildPermission(id, data);	
			case DESTROY: // DESTROY
				return getDestroyPermission(id, data);	
			case SWITCH: // SWITCH
				return getSwitchPermission(id, data);	
			case ITEM_USE: // ITEM_USE
				return getItemUsePermission(id, data);	
			default:
				throw new NullPointerException();
		}
	}
	
	@Deprecated
	public void setBuildPermission(Integer id, byte data, Boolean value) {
		updateMaps(buildPermission, id, data, value);
	}	
	@Deprecated
	public void setDestroyPermission(Integer id, byte data, Boolean value) {
		updateMaps(destroyPermission, id, data, value);
	}
	@Deprecated
	public void setSwitchPermission(Integer id, byte data, Boolean value) {
		updateMaps(switchPermission, id, data, value);
	}
	@Deprecated
	public void setItemUsePermission(Integer id, byte data, Boolean value) {
		updateMaps(itemUsePermission, id, data, value);		
	}
	
	@Deprecated
	public boolean getBuildPermission(Integer id, byte data) throws NullPointerException {
		return getBlockPermission(buildPermission, id, data);
	}
	@Deprecated
	public boolean getDestroyPermission(Integer id, byte data) throws NullPointerException {
		return getBlockPermission(destroyPermission, id, data);		
	}
	@Deprecated
	public boolean getSwitchPermission(Integer id, byte data) throws NullPointerException {
		return getBlockPermission(switchPermission, id, data);		
	}
	@Deprecated
	public Boolean getItemUsePermission(Integer id, byte data) throws NullPointerException {
		return getBlockPermission(itemUsePermission, id, data);		
	}
	
	@Deprecated
	private void updateMaps(HashMap<Integer, HashMap<Byte, Boolean>> blockMap, Integer id, byte data, Boolean value) {
		
		if (!blockMap.containsKey(id)) {
			/*
			 * We have no permissions cached for this block.
			 */
			HashMap<Byte, Boolean> map = new HashMap<Byte, Boolean>();
			map.put(data, value);
			blockMap.put(id, map);
		} else {
			/*
			 * We have cached permissions for this block type so just push updated data.
			 */
			blockMap.get(id).put(data, value);
		}
	}
	@Deprecated
	private boolean getBlockPermission(HashMap<Integer, HashMap<Byte, Boolean>> blockMap, Integer id, byte data) throws NullPointerException {
		
		if (!blockMap.containsKey(id))
			throw new NullPointerException();
		
		HashMap<Byte, Boolean> map = blockMap.get(id);
		
		if (!map.containsKey(data))
			throw new NullPointerException();
		
		return map.get(data);		
	}
	
}
