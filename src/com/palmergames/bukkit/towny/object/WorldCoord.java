package com.palmergames.bukkit.towny.object;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class WorldCoord extends Coord {
    private String worldName;

    public WorldCoord(String worldName, int x, int z) {
        super(x,z);
        this.worldName = worldName;
    }

    public WorldCoord(String worldName, Coord coord) {
        super(coord);
        this.worldName = worldName;
    }

    public WorldCoord(WorldCoord worldCoord) {
        super(worldCoord);
        this.worldName = worldCoord.getWorldName();
    }

    public String getWorldName() {
        return worldName;
    }

    public Coord getCoord() {
        return new Coord(x, z);
    }

    @Deprecated
    public TownyWorld getWorld() {
        return getTownyWorld();
    }

    @Deprecated
    public WorldCoord(TownyWorld world, int x, int z) {
        super(x,z);
        this.worldName = world.getName();
    }

    @Deprecated
    public WorldCoord(TownyWorld world, Coord coord) {
        super(coord);
        this.worldName = world.getName();
    }

    public static WorldCoord parseWorldCoord(Entity entity) {
        return parseWorldCoord(entity.getLocation());
    }

    public static WorldCoord parseWorldCoord(Location loc) {
        return new WorldCoord(loc.getWorld().getName(), parseCoord(loc));
    }

    public static WorldCoord parseWorldCoord(Block block) {
        return new WorldCoord(block.getWorld().getName(), parseCoord(block.getX(), block.getZ()));
    }

    public WorldCoord add(int xOffset, int zOffset) {
        return new WorldCoord(getWorldName(), getX() + xOffset, getZ() + zOffset);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = hash * 27 + (worldName == null ? 0 : worldName.hashCode());
        hash = hash * 27 + x;
        hash = hash * 27 + z;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Coord))
            return false;

        if (!(obj instanceof WorldCoord)) {
            Coord that = (Coord) obj;
            return this.x == that.x && this.z == that.z;
        }

        WorldCoord that = (WorldCoord) obj;
        return this.x == that.x
                && this.z == that.z
                && (this.worldName == null ? that.worldName == null : this.worldName.equals(that.worldName));
    }

    @Override
    public String toString() {
        return worldName + "," + super.toString();
    }

    /**
     * Shortcut for Bukkit.getWorld(worldName)
     * @return the relevant org.bukkit.World instance
     */
    public World getBukkitWorld() {
        return Bukkit.getWorld(worldName);
    }

    /**
     * Shortcut for TownyUniverse.getDataSource().getWorld(worldName)
     * @return the relevant TownyWorld instance, or null if NotRegistered.
     */
    public TownyWorld getTownyWorld() {
        try {
            return TownyUniverse.getDataSource().getWorld(worldName);
        } catch (NotRegisteredException e) {
            return null;
        }
    }

    /**
     * Shortcut for getTownyWorld().getTownBlock(getCoord())
     * @return the relevant TownBlock instance.
     * @throws NotRegisteredException
     */
    public TownBlock getTownBlock() throws NotRegisteredException {
        return getTownyWorld().getTownBlock(getCoord());
    }
}
