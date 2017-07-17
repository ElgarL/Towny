package com.palmergames.bukkit.towny.war.flagwar;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.util.BukkitTools;

public class CellUnderAttack extends Cell {

	private Towny plugin;
	private String nameOfFlagOwner;
	private List<Block> beaconFlagBlocks;
	private List<Block> beaconWireframeBlocks;
	private Block flagBaseBlock, flagBlock, flagLightBlock;
	private int flagColorId;
	private int thread;

	public CellUnderAttack(Towny plugin, String nameOfFlagOwner, Block flagBaseBlock) {

		super(flagBaseBlock.getLocation());
		this.plugin = plugin;
		this.nameOfFlagOwner = nameOfFlagOwner;
		this.flagBaseBlock = flagBaseBlock;
		this.flagColorId = 0;
		this.thread = -1;

		World world = flagBaseBlock.getWorld();
		this.flagBlock = world.getBlockAt(flagBaseBlock.getX(), flagBaseBlock.getY() + 1, flagBaseBlock.getZ());
		this.flagLightBlock = world.getBlockAt(flagBaseBlock.getX(), flagBaseBlock.getY() + 2, flagBaseBlock.getZ());
	}

	public void loadBeacon() {

		beaconFlagBlocks = new ArrayList<Block>();
		beaconWireframeBlocks = new ArrayList<Block>();

		if (!TownyWarConfig.isDrawingBeacon())
			return;

		int beaconSize = TownyWarConfig.getBeaconSize();
		if (Coord.getCellSize() < beaconSize)
			return;

		Block minBlock = getBeaconMinBlock(getFlagBaseBlock().getWorld());
		if (getMinimumHeightForBeacon() >= minBlock.getY())
			return;

		int outerEdge = beaconSize - 1;
		for (int y = 0; y < beaconSize; y++) {
			for (int z = 0; z < beaconSize; z++) {
				for (int x = 0; x < beaconSize; x++) {
					Block block = flagBaseBlock.getWorld().getBlockAt(minBlock.getX() + x, minBlock.getY() + y, minBlock.getZ() + z);
					if (block.isEmpty()) {
						int edgeCount = getEdgeCount(x, y, z, outerEdge);
						if (edgeCount == 1) {
							beaconFlagBlocks.add(block);
						} else if (edgeCount > 1) {
							beaconWireframeBlocks.add(block);
						}
					}
				}
			}
		}
	}

	private Block getTopOfFlagBlock() {

		return flagLightBlock;
	}

	private int getMinimumHeightForBeacon() {

		return getTopOfFlagBlock().getY() + TownyWarConfig.getBeaconMinHeightAboveFlag();
	}

	private int getEdgeCount(int x, int y, int z, int outerEdge) {

		return (zeroOr(x, outerEdge) ? 1 : 0) + (zeroOr(y, outerEdge) ? 1 : 0) + (zeroOr(z, outerEdge) ? 1 : 0);
	}

	private boolean zeroOr(int n, int max) {

		return n == 0 || n == max;
	}

	private Block getBeaconMinBlock(World world) {

		int middle = (int) Math.floor(Coord.getCellSize() / 2.0);
		int radiusCenterExpansion = TownyWarConfig.getBeaconRadius() - 1;
		int fromCorner = middle - radiusCenterExpansion;

		int x = (getX() * Coord.getCellSize()) + fromCorner;
		int z = (getZ() * Coord.getCellSize()) + fromCorner;

		int maxY = world.getMaxHeight();
		int y = getTopOfFlagBlock().getY() + TownyWarConfig.getBeaconMaxHeightAboveFlag();
		if (y > maxY) {
			y = maxY - TownyWarConfig.getBeaconSize();
		}

		return world.getBlockAt(x, y, z);
	}

	public Block getFlagBaseBlock() {

		return flagBaseBlock;
	}

	public String getNameOfFlagOwner() {

		return nameOfFlagOwner;
	}

	public boolean hasEnded() {

		return flagColorId >= TownyWarConfig.getWoolColors().length;
	}

	public void changeFlag() {

		flagColorId += 1;
		updateFlag();
	}

	public void drawFlag() {

		loadBeacon();

		flagBaseBlock.setType(TownyWarConfig.getFlagBaseMaterial());
		updateFlag();
		flagLightBlock.setType(TownyWarConfig.getFlagLightMaterial());
		for (Block block : beaconWireframeBlocks)
			block.setType(TownyWarConfig.getBeaconWireFrameMaterial());
	}

	@SuppressWarnings("deprecation")
	public void updateFlag() {

		DyeColor[] woolColors = TownyWarConfig.getWoolColors();
		if (flagColorId < woolColors.length) {
			System.out.println(String.format("Flag at %s turned %s.", getCellString(), woolColors[flagColorId].toString()));
			int woolId = BukkitTools.getMaterialId(Material.WOOL);
			byte woolData = woolColors[flagColorId].getDyeData();

			BukkitTools.setTypeIdAndData(flagBlock, woolId, woolData, true);

			for (Block block : beaconFlagBlocks)
				BukkitTools.setTypeIdAndData(block, woolId, woolData, true);
			
		}
	}

	public void destroyFlag() {

		flagLightBlock.setType(Material.AIR);
		flagBlock.setType(Material.AIR);
		flagBaseBlock.setType(Material.AIR);
		for (Block block : beaconFlagBlocks)
			block.setType(Material.AIR);
		for (Block block : beaconWireframeBlocks)
			block.setType(Material.AIR);
	}

	public void begin() {

		drawFlag();
		thread = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new CellAttackThread(this), TownyWarConfig.getTimeBetweenFlagColorChange(), TownyWarConfig.getTimeBetweenFlagColorChange());
	}

	public void cancel() {

		if (thread != -1)
			plugin.getServer().getScheduler().cancelTask(thread);

		destroyFlag();
	}

	public String getCellString() {

		return String.format("%s (%d, %d)", getWorldName(), getX(), getZ());
	}

	public boolean isFlagLight(Block block) {

		return this.flagLightBlock.equals(block);
	}

	public boolean isFlag(Block block) {

		return this.flagBlock.equals(block);
	}

	public boolean isFlagBase(Block block) {

		return this.flagBaseBlock.equals(block);
	}

	public boolean isPartOfBeacon(Block block) {

		return beaconFlagBlocks.contains(block) || beaconWireframeBlocks.contains(block);
	}

	public boolean isUneditableBlock(Block block) {

		return isPartOfBeacon(block) || isFlagBase(block) || isFlagLight(block);
	}
}