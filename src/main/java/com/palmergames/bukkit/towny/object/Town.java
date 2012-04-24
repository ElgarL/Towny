package com.palmergames.bukkit.towny.object;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;

import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException;
import com.palmergames.bukkit.towny.exceptions.EconomyException;
import com.palmergames.bukkit.towny.exceptions.EmptyNationException;
import com.palmergames.bukkit.towny.exceptions.EmptyTownException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.wallgen.Wall;
import com.palmergames.bukkit.wallgen.WallSection;
import com.palmergames.bukkit.wallgen.Walled;

public class Town extends TownBlockOwner implements Walled, ResidentList {

	private List<Resident> residents = new ArrayList<Resident>();
	private List<Resident> assistants = new ArrayList<Resident>();
	private List<Location> outpostSpawns = new ArrayList<Location>();
	private Wall wall = new Wall();
	private Resident mayor;
	private int bonusBlocks, purchasedBlocks;
	private double taxes, plotTax, commercialPlotTax, embassyPlotTax,
			plotPrice, commercialPlotPrice, embassyPlotPrice;
	private Nation nation;
	private boolean hasUpkeep, isPublic, isTaxPercentage, isOpen;
	private String townBoard = "/town set board [msg]", tag;
	private TownBlock homeBlock;
	private TownyWorld world;
	private Location spawn;

	public Town(String name) {

		setName(name);
		tag = "";
		bonusBlocks = 0;
		purchasedBlocks = 0;
		taxes = 0.0;
		plotTax = 0.0;
		commercialPlotTax = 0;
		plotPrice = 0.0;
		hasUpkeep = true;
		isPublic = TownySettings.getTownDefaultPublic();
		isTaxPercentage = false;
		isOpen = TownySettings.getTownDefaultOpen();
		permissions.loadDefault(this);
	}

	@Override
	public void addTownBlock(TownBlock townBlock) throws AlreadyRegisteredException {

		if (hasTownBlock(townBlock))
			throw new AlreadyRegisteredException();
		else {
			townBlocks.add(townBlock);
			if (townBlocks.size() == 1 && !hasHomeBlock())
				try {
					setHomeBlock(townBlock);
				} catch (TownyException e) {
				}
		}
	}

	public void setTag(String text) throws TownyException {

		if (text.length() > 4)
			throw new TownyException("Tag too long");
		this.tag = text.toUpperCase();
		if (this.tag.matches(" "))
			this.tag = "";
		setChangedName(true);
	}

	public String getTag() {

		return tag;
	}

	public boolean hasTag() {

		return !tag.isEmpty();
	}

	public Resident getMayor() {

		return mayor;
	}

	public void setTaxes(double taxes) {

		if (isTaxPercentage)
			if (taxes > TownySettings.getMaxTaxPercent())
				this.taxes = TownySettings.getMaxTaxPercent();
			else
				this.taxes = taxes;
		else if (taxes > TownySettings.getMaxTax())
			this.taxes = TownySettings.getMaxTax();
		else
			this.taxes = taxes;
	}

	public double getTaxes() {

		setTaxes(taxes); //make sure the tax level is right.
		return taxes;
	}

	public void setMayor(Resident mayor) throws TownyException {

		if (!hasResident(mayor))
			throw new TownyException("Mayor doesn't belong to town.");
		this.mayor = mayor;
	}

	public Nation getNation() throws NotRegisteredException {

		if (hasNation())
			return nation;
		else
			throw new NotRegisteredException("Town doesn't belong to any nation.");
	}

	public void setNation(Nation nation) throws AlreadyRegisteredException {

		if (nation == null) {
			this.nation = null;
			return;
		}
		if (this.nation == nation)
			return;
		if (hasNation())
			throw new AlreadyRegisteredException();
		this.nation = nation;
	}

	@Override
	public List<Resident> getResidents() {

		return residents;
	}

	public List<Resident> getAssistants() {

		return assistants;
	}

	@Override
	public boolean hasResident(String name) {

		for (Resident resident : residents)
			if (resident.getName().equalsIgnoreCase(name))
				return true;
		return false;
	}

	public boolean hasResident(Resident resident) {

		return residents.contains(resident);
	}

	public boolean hasAssistant(Resident resident) {

		return assistants.contains(resident);
	}

	public void addResident(Resident resident) throws AlreadyRegisteredException {

		addResidentCheck(resident);
		residents.add(resident);
		resident.setTown(this);
	}

	public void addResidentCheck(Resident resident) throws AlreadyRegisteredException {

		if (hasResident(resident))
			throw new AlreadyRegisteredException(String.format(TownySettings.getLangString("msg_err_already_in_town"), resident.getName(), getFormattedName()));
		else if (resident.hasTown())
			try {
				if (!resident.getTown().equals(this))
					throw new AlreadyRegisteredException(String.format(TownySettings.getLangString("msg_err_already_in_town"), resident.getName(), getFormattedName()));
			} catch (NotRegisteredException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public void addAssistant(Resident resident) throws AlreadyRegisteredException, NotRegisteredException {

		if (hasAssistant(resident))
			throw new AlreadyRegisteredException();

		if (!hasResident(resident))
			throw new NotRegisteredException(resident.getName() + " doesn't belong to your town.");

		assistants.add(resident);
	}

	public boolean isMayor(Resident resident) {

		return resident == mayor;
	}

	public boolean hasNation() {

		return nation != null;
	}

	public int getNumResidents() {

		return residents.size();
	}

	public boolean isCapital() {

		return hasNation() ? nation.isCapital(this) : false;
	}

	public void setHasUpkeep(boolean hasUpkeep) {

		this.hasUpkeep = hasUpkeep;
	}

	public boolean hasUpkeep() {

		return hasUpkeep;
	}

	public void setHasMobs(boolean hasMobs) {

		this.permissions.mobs = hasMobs;
	}

	public boolean hasMobs() {

		return this.permissions.mobs;
	}

	public void setPVP(boolean isPVP) {

		this.permissions.pvp = isPVP;
	}

	public boolean isPVP() {

		return this.permissions.pvp;
	}

	public void setBANG(boolean isBANG) {

		this.permissions.explosion = isBANG;
	}

	public boolean isBANG() {

		return this.permissions.explosion;
	}

	public void setTaxPercentage(boolean isPercentage) {

		this.isTaxPercentage = isPercentage;
		if (this.getTaxes() > 100) {
			this.setTaxes(0);
		}
	}

	public boolean isTaxPercentage() {

		return isTaxPercentage;
	}

	public void setFire(boolean isFire) {

		this.permissions.fire = isFire;
	}

	public boolean isFire() {

		return this.permissions.fire;
	}

	public void setTownBoard(String townBoard) {

		this.townBoard = townBoard;
	}

	public String getTownBoard() {

		return townBoard;
	}

	public void setBonusBlocks(int bonusBlocks) {

		this.bonusBlocks = bonusBlocks;
	}

	public int getTotalBlocks() {

		return TownySettings.getMaxTownBlocks(this);
	}

	public int getBonusBlocks() {

		return bonusBlocks;
	}

	public void addBonusBlocks(int bonusBlocks) {

		this.bonusBlocks += bonusBlocks;
	}

	public void setPurchasedBlocks(int purchasedBlocks) {

		this.purchasedBlocks = purchasedBlocks;
	}

	public int getPurchasedBlocks() {

		return purchasedBlocks;
	}

	public void addPurchasedBlocks(int purchasedBlocks) {

		this.purchasedBlocks += purchasedBlocks;
	}

	/**
	 * @param homeBlock
	 * @return true if the world/homeblock has changed
	 * @throws TownyException
	 */
	public boolean setHomeBlock(TownBlock homeBlock) throws TownyException {

		if (homeBlock == null) {
			this.homeBlock = null;
			return false;
		}
		if (!hasTownBlock(homeBlock))
			throw new TownyException("Town has no claim over this town block.");
		this.homeBlock = homeBlock;

		// Set the world as it may have changed
		if (this.world != homeBlock.getWorld()) {
			if ((world != null) && (world.hasTown(this)))
				world.removeTown(this);

			setWorld(homeBlock.getWorld());
		}

		// Attempt to reset the spawn to make sure it's in the homeblock
		try {
			setSpawn(spawn);
		} catch (TownyException e) {
			// Spawn is not in the homeblock so null.
			spawn = null;
		} catch (NullPointerException e) {
			// In the event that spawn is already null
		}

		return true;
	}

	public TownBlock getHomeBlock() throws TownyException {

		if (hasHomeBlock())
			return homeBlock;
		else
			throw new TownyException("Town has not set a home block.");
	}

	/**
	 * Sets the world this town belongs to. If it's a world change it will
	 * remove the town from the old world and place in the new.
	 * 
	 * @param world
	 */
	public void setWorld(TownyWorld world) {

		if (world == null) {
			this.world = null;
			return;
		}
		if (this.world == world)
			return;

		if (hasWorld()) {
			try {
				world.removeTown(this);
			} catch (NotRegisteredException e) {
			}
		}

		this.world = world;

		try {
			this.world.addTown(this);
		} catch (AlreadyRegisteredException e) {
		}
	}

	/**
	 * Fetch the World this town is registered too. If (for any reason) it's
	 * null it will attempt to find the owning world from TownyUniverse.
	 * 
	 * @return world or null
	 */
	public TownyWorld getWorld() {

		if (world != null)
			return world;

		return TownyUniverse.getDataSource().getTownWorld(this.getName());
	}

	public boolean hasMayor() {

		return mayor != null;
	}

	public void removeResident(Resident resident) throws EmptyTownException, NotRegisteredException {

		if (!hasResident(resident))
			throw new NotRegisteredException();
		else {

			remove(resident);

			if (getNumResidents() == 0)
					throw new EmptyTownException(this);
		}
	}

	private void removeAllResidents() {

		for (Resident resident : new ArrayList<Resident>(residents))
			remove(resident);
	}

	private void remove(Resident resident) {

		for (TownBlock townBlock : new ArrayList<TownBlock>(resident.getTownBlocks())) {
			townBlock.setResident(null);
			try {
				townBlock.setPlotPrice(townBlock.getTown().getPlotPrice());
			} catch (NotRegisteredException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//getPlugin().getTownyUniverse().getDataSource().saveResident(resident); //TODO: BAD!
		}

		if (isMayor(resident)) {

			if (residents.size() > 1) {
				for (Resident assistant : new ArrayList<Resident>(getAssistants()))
					if (assistant != resident) {
						try {
							setMayor(assistant);
							continue;
						} catch (TownyException e) {
							// Error setting mayor.
							e.printStackTrace();
						}
					}
				if (isMayor(resident)) {
					// Still mayor and no assistants so pick a resident to be mayor
					for (Resident newMayor : new ArrayList<Resident>(getResidents()))
						if (newMayor != resident) {
							try {
								setMayor(newMayor);
								continue;
							} catch (TownyException e) {
								// Error setting mayor.
								e.printStackTrace();
							}
						}
				}
			}

		}

		if (hasNation() && nation.hasAssistant(resident))
			try {
				nation.removeAssistant(resident);
			} catch (NotRegisteredException e) {
			}
		if (hasAssistant(resident))
			try {
				removeAssistant(resident);
			} catch (NotRegisteredException e) {
			}

		try {
			resident.setTown(null);
		} catch (AlreadyRegisteredException e) {
		}
		residents.remove(resident);
	}

	public void removeAssistant(Resident resident) throws NotRegisteredException {

		if (!hasAssistant(resident))
			throw new NotRegisteredException();
		else
			assistants.remove(resident);
	}

	public void setSpawn(Location spawn) throws TownyException {

		if (!hasHomeBlock())
			throw new TownyException("Home Block has not been set");
		Coord spawnBlock = Coord.parseCoord(spawn);
		if (homeBlock.getX() == spawnBlock.getX() && homeBlock.getZ() == spawnBlock.getZ()) {
			this.spawn = spawn;
		} else
			throw new TownyException("Spawn is not within the homeBlock.");
	}

	public Location getSpawn() throws TownyException {

		if (hasHomeBlock() && spawn != null) {
			return spawn;
		}

		else {
			this.spawn = null;
			throw new TownyException("Town has not set a spawn location.");
		}
	}

	public boolean hasSpawn() {

		return (hasHomeBlock() && spawn != null);
	}

	public boolean hasHomeBlock() {

		return homeBlock != null;
	}

	public void clear() throws EmptyNationException {

		//Cleanup
		removeAllResidents();
		mayor = null;
		residents.clear();
		assistants.clear();
		homeBlock = null;
		outpostSpawns.clear();

		try {
			if (hasWorld()) {
				world.removeTownBlocks(getTownBlocks());
				world.removeTown(this);
			}
		} catch (NotRegisteredException e) {
		}
		if (hasNation())
			try {
				nation.removeTown(this);
			} catch (NotRegisteredException e) {
			}
	}

	private boolean hasWorld() {

		return world != null;
	}

	@Override
	public void removeTownBlock(TownBlock townBlock) throws NotRegisteredException {

		if (!hasTownBlock(townBlock))
			throw new NotRegisteredException();
		else {
			// Remove the spawn point for this outpost.
			if (townBlock.isOutpost())
				removeOutpostSpawn(townBlock.getCoord());

			// Clear the towns homeblock if this is it.
			try {
				if (getHomeBlock() == townBlock)
					setHomeBlock(null);
			} catch (TownyException e) {
			}
			townBlocks.remove(townBlock);
		}
	}

	/**
	 * Add or update an outpost spawn
	 * 
	 * @param spawn
	 * @throws TownyException
	 */
	public void addOutpostSpawn(Location spawn) throws TownyException {

		removeOutpostSpawn(Coord.parseCoord(spawn));

		Coord spawnBlock = Coord.parseCoord(spawn);

		try {
			TownBlock outpost = TownyUniverse.getDataSource().getWorld(spawn.getWorld().getName()).getTownBlock(spawnBlock);
			if (outpost.getX() == spawnBlock.getX() && outpost.getZ() == spawnBlock.getZ()) {
				if (!outpost.isOutpost())
					throw new TownyException("Location is not within an outpost plot.");

				outpostSpawns.add(spawn);
			}

		} catch (NotRegisteredException e) {
			throw new TownyException("Location is not within a Town.");
		}

	}

	/**
	 * Return the Location for this Outpost index.
	 * 
	 * @param index
	 * @return Location of outpost spawn
	 * @throws TownyException
	 */
	public Location getOutpostSpawn(Integer index) throws TownyException {

		if (getMaxOutpostSpawn() == 0)
			throw new TownyException("Town has no outpost spawns set.");

		return outpostSpawns.get(Math.min(getMaxOutpostSpawn() - 1, Math.max(0, index - 1)));
	}

	public int getMaxOutpostSpawn() {

		return outpostSpawns.size();
	}

	public boolean hasOutpostSpawn() {

		return (outpostSpawns.size() > 0);
	}

	/**
	 * Get an unmodifiable List of all outpost spawns.
	 * 
	 * @return List of outpostSpawns
	 */
	public List<Location> getAllOutpostSpawns() {

		return Collections.unmodifiableList(outpostSpawns);
	}

	private void removeOutpostSpawn(Coord coord) {

		for (Location spawn : new ArrayList<Location>(outpostSpawns)) {
			Coord spawnBlock = Coord.parseCoord(spawn);
			if ((coord.getX() == spawnBlock.getX()) && (coord.getZ() == coord.getZ())) {
				outpostSpawns.remove(spawn);
			}
		}
	}

	public void setPlotPrice(double plotPrice) {

		this.plotPrice = plotPrice;
	}

	public double getPlotPrice() {

		return plotPrice;
	}

	public double getPlotTypePrice(TownBlockType type) {

		double plotPrice = 0;
		switch (type.ordinal()) {

		case 0:
			plotPrice = getPlotPrice();
			break;
		case 1:
			plotPrice = getCommercialPlotPrice();
			break;
		case 3:
			plotPrice = getEmbassyPlotPrice();
			break;
		default:
			plotPrice = getPlotPrice();

		}
		// check price isn't negative
		if (plotPrice < 0)
			plotPrice = 0;

		return plotPrice;
	}

	public void setCommercialPlotPrice(double commercialPlotPrice) {

		this.commercialPlotPrice = commercialPlotPrice;
	}

	public double getCommercialPlotPrice() {

		return commercialPlotPrice;
	}

	public void setEmbassyPlotPrice(double embassyPlotPrice) {

		this.embassyPlotPrice = embassyPlotPrice;
	}

	public double getEmbassyPlotPrice() {

		return embassyPlotPrice;
	}

	@Override
	public Wall getWall() {

		return wall;
	}

	@Override
	public List<WallSection> getWallSections() {

		return getWall().getWallSections();
	}

	@Override
	public void setWallSections(List<WallSection> wallSections) {

		getWall().setWallSections(wallSections);

	}

	@Override
	public boolean hasWallSection(WallSection wallSection) {

		return getWall().hasWallSection(wallSection);
	}

	@Override
	public void addWallSection(WallSection wallSection) {

		getWall().addWallSection(wallSection);
	}

	@Override
	public void removeWallSection(WallSection wallSection) {

		getWall().removeWallSection(wallSection);
	}

	public boolean isHomeBlock(TownBlock townBlock) {

		return hasHomeBlock() ? townBlock == homeBlock : false;
	}

	public void setPlotTax(double plotTax) {

		this.plotTax = plotTax;
	}

	public double getPlotTax() {

		return plotTax;
	}

	public void setCommercialPlotTax(double commercialTax) {

		this.commercialPlotTax = commercialTax;
	}

	public double getCommercialPlotTax() {

		return commercialPlotTax;
	}

	public void setEmbassyPlotTax(double embassyPlotTax) {

		this.embassyPlotTax = embassyPlotTax;
	}

	public double getEmbassyPlotTax() {

		return embassyPlotTax;
	}

	public void setOpen(boolean isOpen) {

		this.isOpen = isOpen;
	}

	public boolean isOpen() {

		return isOpen;
	}

	public void withdrawFromBank(Resident resident, int amount) throws EconomyException, TownyException {

		if (!isMayor(resident) && !hasAssistant(resident))
			throw new TownyException("You don't have access to the town's bank.");

		if (TownySettings.isUsingEconomy()) {
			if (!payTo(amount, resident, "Town Widthdraw"))
				throw new TownyException("There is not enough money in the bank.");
		} else
			throw new TownyException("Economy has not been turned on.");

	}

	@Override
	public List<String> getTreeString(int depth) {

		List<String> out = new ArrayList<String>();
		out.add(getTreeDepth(depth) + "Town (" + getName() + ")");
		out.add(getTreeDepth(depth + 1) + "Mayor: " + (hasMayor() ? getMayor().getName() : "None"));
		out.add(getTreeDepth(depth + 1) + "Home: " + homeBlock);
		out.add(getTreeDepth(depth + 1) + "Bonus: " + bonusBlocks);
		out.add(getTreeDepth(depth + 1) + "TownBlocks (" + getTownBlocks().size() + "): " /*
																						 * +
																						 * getTownBlocks
																						 * (
																						 * )
																						 */);
		if (getAssistants().size() > 0)
			out.add(getTreeDepth(depth + 1) + "Assistants (" + getAssistants().size() + "): " + Arrays.toString(getAssistants().toArray(new Resident[0])));
		out.add(getTreeDepth(depth + 1) + "Residents (" + getResidents().size() + "):");
		for (Resident resident : getResidents())
			out.addAll(resident.getTreeString(depth + 2));
		return out;
	}

	public void setPublic(boolean isPublic) {

		this.isPublic = isPublic;
	}

	public boolean isPublic() {

		return isPublic;
	}

}
