package com.palmergames.bukkit.towny.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyLogger;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.palmergames.bukkit.towny.regen.PlotBlockData;
import com.palmergames.bukkit.towny.regen.TownyRegenAPI;
import com.palmergames.bukkit.util.NameValidation;
import com.palmergames.util.FileMgmt;
import com.palmergames.util.KeyValueFile;
import com.palmergames.util.StringMgmt;

// TODO: Make sure the lack of a particular value doesn't error out the entire file

public class TownyFlatFileSource extends TownyDatabaseHandler {

	protected final String newLine = System.getProperty("line.separator");
	protected String rootFolder = "";
	protected String dataFolder = FileMgmt.fileSeparator() + "data";
	protected String settingsFolder = FileMgmt.fileSeparator() + "settings";
	protected String logFolder = FileMgmt.fileSeparator() + "logs";

	private enum elements {
		VER, novalue;

		public static elements fromString(String Str) {

			try {
				return valueOf(Str);
			} catch (Exception ex) {
				return novalue;
			}
		}
	};

	@Override
	public void initialize(Towny plugin, TownyUniverse universe) {

		this.universe = universe;
		this.plugin = plugin;
		this.rootFolder = universe.getRootFolder();

		// Create files and folders if non-existent
		try {
			FileMgmt.checkFolders(new String[] {
					rootFolder,
					rootFolder + dataFolder,
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "residents",
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "towns",
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "towns" + FileMgmt.fileSeparator() + "deleted",
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "nations",
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "nations" + FileMgmt.fileSeparator() + "deleted",
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "worlds",
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "worlds" + FileMgmt.fileSeparator() + "deleted",
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "plot-block-data",
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "townblocks" });
			FileMgmt.checkFiles(new String[] {
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "residents.txt",
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "towns.txt",
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "nations.txt",
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "worlds.txt",
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "regen.txt",
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "snapshot_queue.txt" });
		} catch (IOException e) {
			TownyMessaging.sendErrorMsg("Could not create flatfile default files and folders.");
		}
	}

	@Override
	public synchronized void backup() throws IOException {

		String backupType = TownySettings.getFlatFileBackupType();
		if (!backupType.equalsIgnoreCase("none")) {

			TownyLogger.shutDown();

			long t = System.currentTimeMillis();
			String newBackupFolder = rootFolder + FileMgmt.fileSeparator() + "backup" + FileMgmt.fileSeparator() + new SimpleDateFormat("yyyy-MM-dd HH-mm").format(t) + " - " + Long.toString(t);
			FileMgmt.checkFolders(new String[] {
					rootFolder,
					rootFolder + FileMgmt.fileSeparator() + "backup" });
			if (backupType.equalsIgnoreCase("folder")) {
				FileMgmt.checkFolders(new String[] { newBackupFolder });
				FileMgmt.copyDirectory(new File(rootFolder + dataFolder), new File(newBackupFolder));
				FileMgmt.copyDirectory(new File(rootFolder + logFolder), new File(newBackupFolder));
				FileMgmt.copyDirectory(new File(rootFolder + settingsFolder), new File(newBackupFolder));
			} else if (backupType.equalsIgnoreCase("zip"))
				FileMgmt.zipDirectories(new File[] {
						new File(rootFolder + dataFolder),
						new File(rootFolder + logFolder),
						new File(rootFolder + settingsFolder) }, new File(newBackupFolder + ".zip"));
			else {
				plugin.setupLogger();
				throw new IOException("Unsupported flatfile backup type (" + backupType + ")");
			}
			plugin.setupLogger();
		}
	}

	@Override
	public synchronized void deleteUnusedResidentFiles() {

		String path;
		Set<String> names;

		path = rootFolder + dataFolder + FileMgmt.fileSeparator() + "residents";
		names = getResidentKeys();

		FileMgmt.deleteUnusedFiles(new File(path), names);

		path = rootFolder + dataFolder + FileMgmt.fileSeparator() + "towns";
		names = getTownsKeys();

		FileMgmt.deleteUnusedFiles(new File(path), names);

		path = rootFolder + dataFolder + FileMgmt.fileSeparator() + "nations";
		names = getNationsKeys();

		FileMgmt.deleteUnusedFiles(new File(path), names);
	}

	public String getResidentFilename(Resident resident) {

		return rootFolder + dataFolder + FileMgmt.fileSeparator() + "residents" + FileMgmt.fileSeparator() + resident.getName() + ".txt";
	}

	public String getTownFilename(Town town) {

		return rootFolder + dataFolder + FileMgmt.fileSeparator() + "towns" + FileMgmt.fileSeparator() + town.getName() + ".txt";
	}

	public String getNationFilename(Nation nation) {

		return rootFolder + dataFolder + FileMgmt.fileSeparator() + "nations" + FileMgmt.fileSeparator() + nation.getName() + ".txt";
	}

	public String getWorldFilename(TownyWorld world) {

		return rootFolder + dataFolder + FileMgmt.fileSeparator() + "worlds" + FileMgmt.fileSeparator() + world.getName() + ".txt";
	}

	public String getPlotFilename(PlotBlockData plotChunk) {

		return rootFolder + dataFolder + FileMgmt.fileSeparator() + "plot-block-data" + FileMgmt.fileSeparator() + plotChunk.getWorldName() + FileMgmt.fileSeparator() + plotChunk.getX() + "_" + plotChunk.getZ() + "_" + plotChunk.getSize() + ".data";
	}

	public String getPlotFilename(TownBlock townBlock) {

		return rootFolder + dataFolder + FileMgmt.fileSeparator() + "plot-block-data" + FileMgmt.fileSeparator() + townBlock.getWorld().getName() + FileMgmt.fileSeparator() + townBlock.getX() + "_" + townBlock.getZ() + "_" + TownySettings.getTownBlockSize() + ".data";
	}

	public String getTownBlockFilename(TownBlock townBlock) {

		return rootFolder + dataFolder + FileMgmt.fileSeparator() + "townblocks" + FileMgmt.fileSeparator() + townBlock.getWorld().getName() + FileMgmt.fileSeparator() + townBlock.getX() + "_" + townBlock.getZ() + "_" + TownySettings.getTownBlockSize() + ".data";
	}

	/*
	 * Load keys
	 */

	@Override
	public boolean loadResidentList() {

		TownyMessaging.sendDebugMsg("Loading Resident List");
		String line;
		BufferedReader fin = null;

		try {
			fin = new BufferedReader(new FileReader(rootFolder + dataFolder + FileMgmt.fileSeparator() + "residents.txt"));
			
			while ((line = fin.readLine()) != null)
				if (!line.equals(""))
					newResident(line);

			return true;
			
		} catch (AlreadyRegisteredException e) {
			e.printStackTrace();
			return false;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
			
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

	@Override
	public boolean loadTownList() {

		TownyMessaging.sendDebugMsg("Loading Town List");
		String line;
		BufferedReader fin = null;

		try {
			fin = new BufferedReader(new FileReader(rootFolder + dataFolder + FileMgmt.fileSeparator() + "towns.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		try {
			while ((line = fin.readLine()) != null)
				if (!line.equals(""))
					newTown(line);
			
			return true;

		} catch (AlreadyRegisteredException e) {
			e.printStackTrace();
			return false;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
			
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException ignore) {
				}
			}
		}
		
	}

	@Override
	public boolean loadNationList() {

		TownyMessaging.sendDebugMsg("Loading Nation List");
		String line;
		BufferedReader fin = null;

		try {
			fin = new BufferedReader(new FileReader(rootFolder + dataFolder + FileMgmt.fileSeparator() + "nations.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		try {
			while ((line = fin.readLine()) != null)
				if (!line.equals(""))
					newNation(line);

			return true;
			
		} catch (AlreadyRegisteredException e) {
			e.printStackTrace();
			return false;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
			
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

	@Override
	public boolean loadWorldList() {

		if (plugin != null) {
			TownyMessaging.sendDebugMsg("Loading Server World List");
			for (World world : plugin.getServer().getWorlds())
				try {
					newWorld(world.getName());
				} catch (AlreadyRegisteredException e) {
					//e.printStackTrace();
				} catch (NotRegisteredException e) {
					//e.printStackTrace();
				}
		}

		// Can no longer reply on Bukkit to report ALL available worlds.

		TownyMessaging.sendDebugMsg("Loading World List");

		String line;
		BufferedReader fin = null;

		try {
			fin = new BufferedReader(new FileReader(rootFolder + dataFolder + FileMgmt.fileSeparator() + "worlds.txt"));
			
			while ((line = fin.readLine()) != null)
				if (!line.equals(""))
					newWorld(line);

			return true;
			
		} catch (AlreadyRegisteredException e) {
			// Ignore this as the world may have been passed to us by bukkit
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
			
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException ignore) {
				}
			}
		}
		
	}

	@Override
	public boolean loadRegenList() {

		TownyMessaging.sendDebugMsg("Loading Regen List");

		String line;
		BufferedReader fin = null;
		String[] split;
		PlotBlockData plotData;

		try {
			fin = new BufferedReader(new FileReader(rootFolder + dataFolder + FileMgmt.fileSeparator() + "regen.txt"));
			
			while ((line = fin.readLine()) != null)
				if (!line.equals("")) {
					split = line.split(",");
					plotData = loadPlotData(split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]));
					if (plotData != null) {
						TownyRegenAPI.addPlotChunk(plotData, false);
					}
				}

			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
			
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException ignore) {
				}
			}
		}
		

	}

	@Override
	public boolean loadSnapshotList() {

		TownyMessaging.sendDebugMsg("Loading Snapshot Queue");

		String line;
		BufferedReader fin = null;
		String[] split;

		try {
			fin = new BufferedReader(new FileReader(rootFolder + dataFolder + FileMgmt.fileSeparator() + "snapshot_queue.txt"));
			
			while ((line = fin.readLine()) != null)
				if (!line.equals("")) {
					split = line.split(",");
					WorldCoord worldCoord = new WorldCoord(split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]));
					TownyRegenAPI.addWorldCoord(worldCoord);
				}
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
			
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException ignore) {
				}
			}
		}

		

	}

	/*
	 * Load individual towny object
	 */

	@Override
	public boolean loadResident(Resident resident) {

		String line;
		String path = getResidentFilename(resident);
		File fileResident = new File(path);
		if (fileResident.exists() && fileResident.isFile()) {
			try {
				KeyValueFile kvFile = new KeyValueFile(path);
				resident.setLastOnline(Long.parseLong(kvFile.get("lastOnline")));

				line = kvFile.get("registered");
				if (line != null)
					resident.setRegistered(Long.parseLong(line));
				else
					resident.setRegistered(resident.getLastOnline());

				line = kvFile.get("isNPC");
				if (line != null)
					resident.setNPC(Boolean.parseBoolean(line));

				line = kvFile.get("title");
				if (line != null)
					resident.setTitle(line);

				line = kvFile.get("surname");
				if (line != null)
					resident.setSurname(line);

				line = kvFile.get("town");
				if (line != null)
					resident.setTown(getTown(line));

				line = kvFile.get("friends");
				if (line != null) {
					String[] tokens = line.split(",");
					for (String token : tokens) {
						if (!token.isEmpty()) {
							Resident friend = getResident(token);
							if (friend != null)
								resident.addFriend(friend);
						}
					}
				}

				line = kvFile.get("protectionStatus");
				if (line != null)
					resident.setPermissions(line);

				line = kvFile.get("townBlocks");
				if (line != null)
					utilLoadTownBlocks(line, null, resident);

			} catch (Exception e) {
				TownyMessaging.sendErrorMsg("Loading Error: Exception while reading resident file " + resident.getName());
				return false;
			}

			return true;
		} else
			return false;
	}

	@Override
	public boolean loadTown(Town town) {

		String line;
		String[] tokens;
		String path = getTownFilename(town);
		File fileTown = new File(path);
		if (fileTown.exists() && fileTown.isFile()) {
			try {
				KeyValueFile kvFile = new KeyValueFile(path);

				line = kvFile.get("residents");
				if (line != null) {
					tokens = line.split(",");
					for (String token : tokens) {
						if (!token.isEmpty()) {
							Resident resident = getResident(token);
							if (resident != null)
								town.addResident(resident);
						}
					}
				}

				line = kvFile.get("mayor");
				if (line != null)
					town.setMayor(getResident(line));

				line = kvFile.get("assistants");
				if (line != null) {
					tokens = line.split(",");
					for (String token : tokens) {
						if (!token.isEmpty()) {
							Resident assistant = getResident(token);
							if ((assistant != null) && (town.hasResident(assistant)))
								town.addAssistant(assistant);
						}
					}
				}

				town.setTownBoard(kvFile.get("townBoard"));

				line = kvFile.get("tag");
				if (line != null)
					try {
						town.setTag(line);
					} catch (TownyException e) {
						town.setTag("");
					}

				line = kvFile.get("protectionStatus");
				if (line != null)
					town.setPermissions(line);

				line = kvFile.get("bonusBlocks");
				if (line != null)
					try {
						town.setBonusBlocks(Integer.parseInt(line));
					} catch (Exception e) {
						town.setBonusBlocks(0);
					}

				line = kvFile.get("purchasedBlocks");
				if (line != null)
					try {
						town.setPurchasedBlocks(Integer.parseInt(line));
					} catch (Exception e) {
						town.setPurchasedBlocks(0);
					}

				line = kvFile.get("plotPrice");
				if (line != null)
					try {
						town.setPlotPrice(Double.parseDouble(line));
					} catch (Exception e) {
						town.setPlotPrice(0);
					}

				line = kvFile.get("hasUpkeep");
				if (line != null)
					try {
						town.setHasUpkeep(Boolean.parseBoolean(line));
					} catch (NumberFormatException nfe) {
					} catch (Exception e) {
					}

				line = kvFile.get("taxpercent");
				if (line != null)
					try {
						town.setTaxPercentage(Boolean.parseBoolean(line));
					} catch (NumberFormatException nfe) {
					} catch (Exception e) {
					}

				line = kvFile.get("taxes");
				if (line != null)
					try {
						town.setTaxes(Double.parseDouble(line));
					} catch (Exception e) {
						town.setTaxes(0);
					}

				line = kvFile.get("plotTax");
				if (line != null)
					try {
						town.setPlotTax(Double.parseDouble(line));
					} catch (Exception e) {
						town.setPlotTax(0);
					}

				line = kvFile.get("commercialPlotPrice");
				if (line != null)
					try {
						town.setCommercialPlotPrice(Double.parseDouble(line));
					} catch (Exception e) {
						town.setCommercialPlotPrice(0);
					}

				line = kvFile.get("commercialPlotTax");
				if (line != null)
					try {
						town.setCommercialPlotTax(Double.parseDouble(line));
					} catch (Exception e) {
						town.setCommercialPlotTax(0);
					}

				line = kvFile.get("embassyPlotPrice");
				if (line != null)
					try {
						town.setEmbassyPlotPrice(Double.parseDouble(line));
					} catch (Exception e) {
						town.setEmbassyPlotPrice(0);
					}

				line = kvFile.get("embassyPlotTax");
				if (line != null)
					try {
						town.setEmbassyPlotTax(Double.parseDouble(line));
					} catch (Exception e) {
						town.setEmbassyPlotTax(0);
					}
				/*
				 * line = kvFile.get("pvp");
				 * if (line != null)
				 * try {
				 * town.setPVP(Boolean.parseBoolean(line));
				 * } catch (NumberFormatException nfe) {
				 * } catch (Exception e) {
				 * }
				 * 
				 * line = kvFile.get("mobs");
				 * if (line != null)
				 * try {
				 * town.setHasMobs(Boolean.parseBoolean(line));
				 * } catch (NumberFormatException nfe) {
				 * } catch (Exception e) {
				 * }
				 */
				line = kvFile.get("open");
				if (line != null)
					try {
						town.setOpen(Boolean.parseBoolean(line));
					} catch (NumberFormatException nfe) {
					} catch (Exception e) {
					}
				line = kvFile.get("public");
				if (line != null)
					try {
						town.setPublic(Boolean.parseBoolean(line));
					} catch (NumberFormatException nfe) {
					} catch (Exception e) {
					}
				/*
				 * line = kvFile.get("explosion");
				 * if (line != null)
				 * try {
				 * town.setBANG(Boolean.parseBoolean(line));
				 * } catch (NumberFormatException nfe) {
				 * } catch (Exception e) {
				 * }
				 * 
				 * line = kvFile.get("fire");
				 * if (line != null)
				 * try {
				 * town.setFire(Boolean.parseBoolean(line));
				 * } catch (NumberFormatException nfe) {
				 * } catch (Exception e) {
				 * }
				 */
				line = kvFile.get("townBlocks");
				if (line != null)
					utilLoadTownBlocks(line, town, null);

				line = kvFile.get("homeBlock");
				if (line != null) {
					tokens = line.split(",");
					if (tokens.length == 3)
						try {
							TownyWorld world = getWorld(tokens[0]);

							try {
								int x = Integer.parseInt(tokens[1]);
								int z = Integer.parseInt(tokens[2]);
								TownBlock homeBlock = world.getTownBlock(x, z);
								town.setHomeBlock(homeBlock);
							} catch (NumberFormatException e) {
								TownyMessaging.sendErrorMsg("[Warning] " + town.getName() + " homeBlock tried to load invalid location.");
							} catch (NotRegisteredException e) {
								TownyMessaging.sendErrorMsg("[Warning] " + town.getName() + " homeBlock tried to load invalid TownBlock.");
							} catch (TownyException e) {
								TownyMessaging.sendErrorMsg("[Warning] " + town.getName() + " does not have a home block.");
							}

						} catch (NotRegisteredException e) {
							TownyMessaging.sendErrorMsg("[Warning] " + town.getName() + " homeBlock tried to load invalid world.");
						}
				}

				line = kvFile.get("spawn");
				if (line != null) {
					tokens = line.split(",");
					if (tokens.length >= 4)
						try {
							World world = plugin.getServerWorld(tokens[0]);
							double x = Double.parseDouble(tokens[1]);
							double y = Double.parseDouble(tokens[2]);
							double z = Double.parseDouble(tokens[3]);

							Location loc = new Location(world, x, y, z);
							if (tokens.length == 6) {
								loc.setPitch(Float.parseFloat(tokens[4]));
								loc.setYaw(Float.parseFloat(tokens[5]));
							}
							town.setSpawn(loc);
						} catch (NumberFormatException e) {
						} catch (NotRegisteredException e) {
						} catch (NullPointerException e) {
						} catch (TownyException e) {
							TownyMessaging.sendErrorMsg("[Warning] " + town.getName() + " does not have a spawn point.");
						}
				}

				// Load outpost spawns
				line = kvFile.get("outpostspawns");
				if (line != null) {
					String[] outposts = line.split(";");
					for (String spawn : outposts) {
						tokens = spawn.split(",");
						if (tokens.length >= 4)
							try {
								World world = plugin.getServerWorld(tokens[0]);
								double x = Double.parseDouble(tokens[1]);
								double y = Double.parseDouble(tokens[2]);
								double z = Double.parseDouble(tokens[3]);

								Location loc = new Location(world, x, y, z);
								if (tokens.length == 6) {
									loc.setPitch(Float.parseFloat(tokens[4]));
									loc.setYaw(Float.parseFloat(tokens[5]));
								}
								town.addOutpostSpawn(loc);
							} catch (NumberFormatException e) {
							} catch (NotRegisteredException e) {
							} catch (NullPointerException e) {
							} catch (TownyException e) {
							}
					}
				}

			} catch (Exception e) {
				TownyMessaging.sendErrorMsg("Loading Error: Exception while reading town file " + town.getName());
				return false;
			}

			return true;
		} else
			return false;
	}

	@Override
	public boolean loadNation(Nation nation) {

		String line = "";
		String[] tokens;
		String path = getNationFilename(nation);
		File fileResident = new File(path);
		if (fileResident.exists() && fileResident.isFile()) {
			try {
				KeyValueFile kvFile = new KeyValueFile(path);

				line = kvFile.get("towns");
				if (line != null) {
					tokens = line.split(",");
					for (String token : tokens) {
						if (!token.isEmpty()) {
							Town town = getTown(token);
							if (town != null)
								nation.addTown(town);
						}
					}
				}

				line = kvFile.get("capital");
				if (line != null)
					nation.setCapital(getTown(line));

				line = kvFile.get("assistants");
				if (line != null) {
					tokens = line.split(",");
					for (String token : tokens) {
						if (!token.isEmpty()) {
							Resident assistant = getResident(token);
							if (assistant != null)
								nation.addAssistant(assistant);
						}
					}
				}

				line = kvFile.get("tag");
				if (line != null)
					try {
						nation.setTag(line);
					} catch (TownyException e) {
						nation.setTag("");
					}

				line = kvFile.get("allies");
				if (line != null) {
					tokens = line.split(",");
					for (String token : tokens) {
						if (!token.isEmpty()) {
							Nation friend = getNation(token);
							if (friend != null)
								nation.addAlly(friend); //("ally", friend);
						}
					}
				}

				line = kvFile.get("enemies");
				if (line != null) {
					tokens = line.split(",");
					for (String token : tokens) {
						if (!token.isEmpty()) {
							Nation enemy = getNation(token);
							if (enemy != null)
								nation.addEnemy(enemy); //("enemy", enemy);
						}
					}
				}

				line = kvFile.get("taxes");
				if (line != null)
					try {
						nation.setTaxes(Double.parseDouble(line));
					} catch (Exception e) {
						nation.setTaxes(0.0);
					}

				line = kvFile.get("neutral");
				if (line != null)
					try {
						nation.setNeutral(Boolean.parseBoolean(line));
					} catch (Exception e) {
					}

			} catch (Exception e) {
				TownyMessaging.sendErrorMsg("Loading Error: Exception while reading nation file " + nation.getName());
				return false;
			}

			return true;
		} else
			return false;
	}

	@Override
	public boolean loadWorld(TownyWorld world) {

		String line = "";
		String[] tokens;
		String path = getWorldFilename(world);

		// create the world file if it doesn't exist
		try {
			FileMgmt.checkFiles(new String[] { path });
		} catch (IOException e1) {
			TownyMessaging.sendErrorMsg("Loading Error: Exception while reading file " + path);
		}

		File fileWorld = new File(path);
		if (fileWorld.exists() && fileWorld.isFile()) {
			try {
				KeyValueFile kvFile = new KeyValueFile(path);

				line = kvFile.get("towns");
				if (line != null) {
					tokens = line.split(",");
					for (String token : tokens) {
						if (!token.isEmpty()) {
							Town town = getTown(token);
							if (town != null) {
								town.setWorld(world);
								//world.addTown(town); not needed as it's handled in the Town object
							}
						}
					}
				}

				line = kvFile.get("claimable");
				if (line != null)
					try {
						world.setClaimable(Boolean.parseBoolean(line));
					} catch (Exception e) {
					}

				line = kvFile.get("pvp");
				if (line != null)
					try {
						world.setPVP(Boolean.parseBoolean(line));
					} catch (Exception e) {
					}

				line = kvFile.get("forcepvp");
				if (line != null)
					try {
						world.setForcePVP(Boolean.parseBoolean(line));
					} catch (Exception e) {
					}

				line = kvFile.get("forcetownmobs");
				if (line != null)
					try {
						world.setForceTownMobs(Boolean.parseBoolean(line));
					} catch (Exception e) {
					}

				line = kvFile.get("worldmobs");
				if (line != null)
					try {
						world.setWorldMobs(Boolean.parseBoolean(line));
					} catch (Exception e) {
					}

				line = kvFile.get("firespread");
				if (line != null)
					try {
						world.setFire(Boolean.parseBoolean(line));
					} catch (Exception e) {
					}

				line = kvFile.get("forcefirespread");
				if (line != null)
					try {
						world.setForceFire(Boolean.parseBoolean(line));
					} catch (Exception e) {
					}

				line = kvFile.get("explosions");
				if (line != null)
					try {
						world.setExpl(Boolean.parseBoolean(line));
					} catch (Exception e) {
					}

				line = kvFile.get("forceexplosions");
				if (line != null)
					try {
						world.setForceExpl(Boolean.parseBoolean(line));
					} catch (Exception e) {
					}

				line = kvFile.get("endermanprotect");
				if (line != null)
					try {
						world.setEndermanProtect(Boolean.parseBoolean(line));
					} catch (Exception e) {
					}

				line = kvFile.get("disableplayertrample");
				if (line != null)
					try {
						world.setDisablePlayerTrample(Boolean.parseBoolean(line));
					} catch (Exception e) {
					}

				line = kvFile.get("disablecreaturetrample");
				if (line != null)
					try {
						world.setDisableCreatureTrample(Boolean.parseBoolean(line));
					} catch (Exception e) {
					}

				line = kvFile.get("unclaimedZoneBuild");
				if (line != null)
					try {
						world.setUnclaimedZoneBuild(Boolean.parseBoolean(line));
					} catch (Exception e) {
					}
				line = kvFile.get("unclaimedZoneDestroy");
				if (line != null)
					try {
						world.setUnclaimedZoneDestroy(Boolean.parseBoolean(line));
					} catch (Exception e) {
					}
				line = kvFile.get("unclaimedZoneSwitch");
				if (line != null)
					try {
						world.setUnclaimedZoneSwitch(Boolean.parseBoolean(line));
					} catch (Exception e) {
					}
				line = kvFile.get("unclaimedZoneItemUse");
				if (line != null)
					try {
						world.setUnclaimedZoneItemUse(Boolean.parseBoolean(line));
					} catch (Exception e) {
					}
				line = kvFile.get("unclaimedZoneName");
				if (line != null)
					try {
						world.setUnclaimedZoneName(line);
					} catch (Exception e) {
					}
				line = kvFile.get("unclaimedZoneIgnoreIds");
				if (line != null)
					try {
						List<Integer> nums = new ArrayList<Integer>();
						for (String s : line.split(","))
							if (!s.isEmpty())
								try {
									nums.add(Integer.parseInt(s));
								} catch (NumberFormatException e) {
								}
						world.setUnclaimedZoneIgnore(nums);
					} catch (Exception e) {
					}

				line = kvFile.get("usingPlotManagementDelete");
				if (line != null)
					try {
						world.setUsingPlotManagementDelete(Boolean.parseBoolean(line));
					} catch (Exception e) {
					}
				line = kvFile.get("plotManagementDeleteIds");
				if (line != null)
					try {
						List<Integer> nums = new ArrayList<Integer>();
						for (String s : line.split(","))
							if (!s.isEmpty())
								try {
									nums.add(Integer.parseInt(s));
								} catch (NumberFormatException e) {
								}
						world.setPlotManagementDeleteIds(nums);
					} catch (Exception e) {
					}

				line = kvFile.get("usingPlotManagementMayorDelete");
				if (line != null)
					try {
						world.setUsingPlotManagementMayorDelete(Boolean.parseBoolean(line));
					} catch (Exception e) {
					}
				line = kvFile.get("plotManagementMayorDelete");
				if (line != null)
					try {
						List<String> materials = new ArrayList<String>();
						for (String s : line.split(","))
							if (!s.isEmpty())
								try {
									materials.add(s.toUpperCase().trim());
								} catch (NumberFormatException e) {
								}
						world.setPlotManagementMayorDelete(materials);
					} catch (Exception e) {
					}

				line = kvFile.get("usingPlotManagementRevert");
				if (line != null)
					try {
						world.setUsingPlotManagementRevert(Boolean.parseBoolean(line));
					} catch (Exception e) {
					}
				line = kvFile.get("usingPlotManagementRevertSpeed");
				if (line != null)
					try {
						world.setPlotManagementRevertSpeed(Long.parseLong(line));
					} catch (Exception e) {
					}
				line = kvFile.get("plotManagementIgnoreIds");
				if (line != null)
					try {
						List<Integer> nums = new ArrayList<Integer>();
						for (String s : line.split(","))
							if (!s.isEmpty())
								try {
									nums.add(Integer.parseInt(s));
								} catch (NumberFormatException e) {
								}
						world.setPlotManagementIgnoreIds(nums);
					} catch (Exception e) {
					}

				line = kvFile.get("usingPlotManagementWildRegen");
				if (line != null)
					try {
						world.setUsingPlotManagementWildRevert(Boolean.parseBoolean(line));
					} catch (Exception e) {
					}

				line = kvFile.get("PlotManagementWildRegenEntities");
				if (line != null)
					try {
						List<String> entities = new ArrayList<String>();
						for (String s : line.split(","))
							if (!s.isEmpty())
								try {
									entities.add(s.trim());
								} catch (NumberFormatException e) {
								}
						world.setPlotManagementWildRevertEntities(entities);
					} catch (Exception e) {
					}

				line = kvFile.get("usingPlotManagementWildRegenDelay");
				if (line != null)
					try {
						world.setPlotManagementWildRevertDelay(Long.parseLong(line));
					} catch (Exception e) {
					}

				line = kvFile.get("usingTowny");
				if (line != null)
					try {
						world.setUsingTowny(Boolean.parseBoolean(line));
					} catch (Exception e) {
					}

				// loadTownBlocks(world);

			} catch (Exception e) {
				TownyMessaging.sendErrorMsg("Loading Error: Exception while reading world file " + path);
				return false;
			}

			return true;
		} else {
			TownyMessaging.sendErrorMsg("Loading Error: File error while reading " + world.getName());
			return false;
		}
	}

	@Override
	public boolean loadTownBlocks() {

		String line = "";
		String path;

		for (TownBlock townBlock : getAllTownBlocks()) {
			path = getTownBlockFilename(townBlock);
			boolean set = false;

			File fileTownBlock = new File(path);
			if (fileTownBlock.exists() && fileTownBlock.isFile()) {
				try {
					KeyValueFile kvFile = new KeyValueFile(path);

					line = kvFile.get("permissions");
					if (line != null)
						try {
							townBlock.setPermissions(line.trim());
							set = true;
						} catch (Exception e) {
						}

					line = kvFile.get("changed");
					if (line != null)
						try {
							townBlock.setChanged(Boolean.parseBoolean(line.trim()));
						} catch (Exception e) {
						}

					line = kvFile.get("locked");
					if (line != null)
						try {
							townBlock.setLocked(Boolean.parseBoolean(line.trim()));
						} catch (Exception e) {
						}

				} catch (Exception e) {
					TownyMessaging.sendErrorMsg("Loading Error: Exception while reading TownBlock file " + path);
					return false;
				}
				if (!set) {
					// no permissions found so set in relation to it's owners perms.
					try {
						if (townBlock.hasResident()) {
							townBlock.setPermissions(townBlock.getResident().getPermissions().toString());
						} else {
							townBlock.setPermissions(townBlock.getTown().getPermissions().toString());
						}
					} catch (NotRegisteredException e) {
						// Will never reach here
					}
				}
			}
		}

		return true;
	}

	/*
	 * Save keys
	 */

	@Override
	public boolean saveResidentList() {

		BufferedWriter fout = null;

		try {
			fout = new BufferedWriter(new FileWriter(rootFolder + dataFolder + FileMgmt.fileSeparator() + "residents.txt"));
			for (Resident resident : getResidents())
				fout.write(NameValidation.checkAndFilterPlayerName(resident.getName()) + newLine);

			return true;
			
		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("Saving Error: Exception while saving residents list file");
			e.printStackTrace();
			return false;
			
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

	@Override
	public boolean saveTownList() {

		BufferedWriter fout = null;
		
		try {
			fout = new BufferedWriter(new FileWriter(rootFolder + dataFolder + FileMgmt.fileSeparator() + "towns.txt"));
			for (Town town : getTowns())
				fout.write(town.getName() + newLine);

			return true;
			
		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("Saving Error: Exception while saving town list file");
			e.printStackTrace();
			return false;
			
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

	@Override
	public boolean saveNationList() {

		BufferedWriter fout = null;
		
		try {
			fout = new BufferedWriter(new FileWriter(rootFolder + dataFolder + FileMgmt.fileSeparator() + "nations.txt"));
			for (Nation nation : getNations())
				fout.write(nation.getName() + newLine);

			return true;
			
		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("Saving Error: Exception while saving nation list file");
			e.printStackTrace();
			return false;
			
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

	@Override
	public boolean saveWorldList() {

		BufferedWriter fout = null;
		
		try {
			fout = new BufferedWriter(new FileWriter(rootFolder + dataFolder + FileMgmt.fileSeparator() + "worlds.txt"));
			for (TownyWorld world : getWorlds())
				fout.write(world.getName() + newLine);

			return true;
			
		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("Saving Error: Exception while saving world list file");
			e.printStackTrace();
			return false;
			
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

	@Override
	public boolean saveRegenList() {

		BufferedWriter fout = null;

		try {
			fout = new BufferedWriter(new FileWriter(rootFolder + dataFolder + FileMgmt.fileSeparator() + "regen.txt"));
			for (PlotBlockData plot : new ArrayList<PlotBlockData>(TownyRegenAPI.getPlotChunks().values()))
				fout.write(plot.getWorldName() + "," + plot.getX() + "," + plot.getZ() + newLine);

			return true;
			
		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("Saving Error: Exception while saving regen file");
			e.printStackTrace();
			return false;
			
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

	@Override
	public boolean saveSnapshotList() {

		BufferedWriter fout = null;
		
		try {
			fout = new BufferedWriter(new FileWriter(rootFolder + dataFolder + FileMgmt.fileSeparator() + "snapshot_queue.txt"));
			while (TownyRegenAPI.hasWorldCoords()) {
				WorldCoord worldCoord = TownyRegenAPI.getWorldCoord();
				fout.write(worldCoord.getWorldName() + "," + worldCoord.getX() + "," + worldCoord.getZ() + newLine);
			}
			return true;
			
		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("Saving Error: Exception while saving snapshot_queue file");
			e.printStackTrace();
			return false;
			
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

	/*
	 * Save individual towny objects
	 */

	@Override
	public boolean saveResident(Resident resident) {

		BufferedWriter fout = null;

		try {
			String path = getResidentFilename(resident);
			fout = new BufferedWriter(new FileWriter(path));
			// Last Online
			fout.write("lastOnline=" + Long.toString(resident.getLastOnline()) + newLine);
			// Registered
			fout.write("registered=" + Long.toString(resident.getRegistered()) + newLine);
			// isNPC
			fout.write("isNPC=" + Boolean.toString(resident.isNPC()) + newLine);
			// title
			fout.write("title=" + resident.getTitle() + newLine);
			// surname
			fout.write("surname=" + resident.getSurname() + newLine);
			if (resident.hasTown())
				fout.write("town=" + resident.getTown().getName() + newLine);
			// Friends
			fout.write("friends=");
			for (Resident friend : resident.getFriends())
				fout.write(friend.getName() + ",");
			fout.write(newLine);
			// TownBlocks
			fout.write("townBlocks=" + utilSaveTownBlocks(new ArrayList<TownBlock>(resident.getTownBlocks())) + newLine);
			// Plot Protection
			fout.write("protectionStatus=" + resident.getPermissions().toString() + newLine);
			
			return true;
			
		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("Saving Error: Exception while saving resident file (" + resident.getName() + ")");
			e.printStackTrace();
			return false;
			
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException ignore) {
				}
			}
		}
		
	}

	@Override
	public boolean saveTown(Town town) {

		BufferedWriter fout = null;

		try {
			String path = getTownFilename(town);
			fout = new BufferedWriter(new FileWriter(path));
			// Name
			fout.write("name=" + town.getName() + newLine);
			// Residents
			fout.write("residents=" + StringMgmt.join(town.getResidents(), ",") + newLine);
			// Mayor
			if (town.hasMayor())
				fout.write("mayor=" + town.getMayor().getName() + newLine);
			// Nation
			if (town.hasNation())
				fout.write("nation=" + town.getNation().getName() + newLine);
			// Assistants
			fout.write("assistants=");
			for (Resident assistant : town.getAssistants())
				fout.write(assistant.getName() + ",");
			fout.write(newLine);
			// Town Board
			fout.write("townBoard=" + town.getTownBoard() + newLine);
			// tag
			fout.write("tag=" + town.getTag() + newLine);
			// Town Protection
			fout.write("protectionStatus=" + town.getPermissions().toString() + newLine);
			// Bonus Blocks
			fout.write("bonusBlocks=" + Integer.toString(town.getBonusBlocks()) + newLine);
			// Purchased Blocks
			fout.write("purchasedBlocks=" + Integer.toString(town.getPurchasedBlocks()) + newLine);
			// Taxpercent
			fout.write("taxpercent=" + Boolean.toString(town.isTaxPercentage()) + newLine);
			// Taxes
			fout.write("taxes=" + Double.toString(town.getTaxes()) + newLine);
			// Plot Price
			fout.write("plotPrice=" + Double.toString(town.getPlotPrice()) + newLine);
			// Plot Tax
			fout.write("plotTax=" + Double.toString(town.getPlotTax()) + newLine);
			// Commercial Plot Price
			fout.write("commercialPlotPrice=" + Double.toString(town.getCommercialPlotPrice()) + newLine);
			// Commercial Tax
			fout.write("commercialPlotTax=" + Double.toString(town.getCommercialPlotTax()) + newLine);
			// Embassy Plot Price
			fout.write("embassyPlotPrice=" + Double.toString(town.getEmbassyPlotPrice()) + newLine);
			// Embassy Tax
			fout.write("embassyPlotTax=" + Double.toString(town.getEmbassyPlotTax()) + newLine);
			// Upkeep
			fout.write("hasUpkeep=" + Boolean.toString(town.hasUpkeep()) + newLine);
			// Open
			fout.write("open=" + Boolean.toString(town.isOpen()) + newLine);
			/*
			 * // PVP
			 * fout.write("pvp=" + Boolean.toString(town.isPVP()) + newLine);
			 * // Mobs
			 * fout.write("mobs=" + Boolean.toString(town.hasMobs()) + newLine);
			 */
			// Public
			fout.write("public=" + Boolean.toString(town.isPublic()) + newLine);
			/*
			 * // Explosions
			 * fout.write("explosion=" + Boolean.toString(town.isBANG()) +
			 * newLine);
			 * // Firespread
			 * fout.write("fire=" + Boolean.toString(town.isFire()) + newLine);
			 */
			// TownBlocks
			fout.write("townBlocks=" + utilSaveTownBlocks(new ArrayList<TownBlock>(town.getTownBlocks())) + newLine);
			// Home Block
			if (town.hasHomeBlock())
				fout.write("homeBlock=" + town.getHomeBlock().getWorld().getName() + "," + Integer.toString(town.getHomeBlock().getX()) + "," + Integer.toString(town.getHomeBlock().getZ()) + newLine);
			// Spawn
			if (town.hasSpawn())
				fout.write("spawn=" + town.getSpawn().getWorld().getName() + "," + Double.toString(town.getSpawn().getX()) + "," + Double.toString(town.getSpawn().getY()) + "," + Double.toString(town.getSpawn().getZ()) + "," + Float.toString(town.getSpawn().getPitch()) + "," + Float.toString(town.getSpawn().getYaw()) + newLine);

			// Outpost Spawns
			if (town.hasOutpostSpawn()) {
				String outpostArray = "outpostspawns=";
				for (Location spawn : new ArrayList<Location>(town.getAllOutpostSpawns())) {
					outpostArray += (spawn.getWorld().getName() + "," + Double.toString(spawn.getX()) + "," + Double.toString(spawn.getY()) + "," + Double.toString(spawn.getZ()) + "," + Float.toString(spawn.getPitch()) + "," + Float.toString(spawn.getYaw()) + ";");
				}
				fout.write(outpostArray);
			}

			return true;
			
		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("Saving Error: Exception while saving town file (" + town.getName() + ")");
			e.printStackTrace();
			return false;
			
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException ignore) {
				}
			}
		}
		
	}

	@Override
	public boolean saveNation(Nation nation) {

		BufferedWriter fout = null;
		
		try {
			String path = getNationFilename(nation);
			fout = new BufferedWriter(new FileWriter(path));
			fout.write("towns=");
			for (Town town : nation.getTowns())
				fout.write(town.getName() + ",");
			fout.write(newLine);
			if (nation.hasCapital())
				fout.write("capital=" + nation.getCapital().getName() + newLine);
			if (nation.hasTag())
				fout.write("tag=" + nation.getTag() + newLine);
			fout.write("assistants=");
			for (Resident assistant : nation.getAssistants())
				fout.write(assistant.getName() + ",");
			fout.write(newLine);
			fout.write("allies=");
			for (Nation allyNation : nation.getAllies())
				fout.write(allyNation.getName() + ",");
			fout.write(newLine);
			fout.write("enemies=");
			for (Nation enemyNation : nation.getEnemies())
				fout.write(enemyNation.getName() + ",");
			fout.write(newLine);
			// Taxes
			fout.write("taxes=" + Double.toString(nation.getTaxes()) + newLine);
			// Neutral
			fout.write("neutral=" + Boolean.toString(nation.isNeutral()) + newLine);

			return true;
			
		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("Saving Error: Exception while saving nation file (" + nation.getName() + ")");
			e.printStackTrace();
			return false;
			
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException ignore) {
				}
			}
		}
		
	}

	@Override
	public boolean saveWorld(TownyWorld world) {

		BufferedWriter fout = null;
		
		try {
			String path = getWorldFilename(world);
			fout = new BufferedWriter(new FileWriter(path));

			// Towns
			fout.write("towns=");
			for (Town town : world.getTowns()) {
				TownyMessaging.sendDebugMsg("   Town - " + town.getName());
				fout.write(town.getName() + ",");
			}

			fout.write(newLine);
			fout.write(newLine);

			// PvP
			fout.write("pvp=" + Boolean.toString(world.isPVP()) + newLine);
			// Force PvP
			fout.write("forcepvp=" + Boolean.toString(world.isForcePVP()) + newLine);
			// Claimable
			fout.write("# Can players found towns and claim plots in this world?" + newLine);
			fout.write("claimable=" + Boolean.toString(world.isClaimable()) + newLine);
			// has monster spawns			
			fout.write("worldmobs=" + Boolean.toString(world.hasWorldMobs()) + newLine);
			// force town mob spawns			
			fout.write("forcetownmobs=" + Boolean.toString(world.isForceTownMobs()) + newLine);
			// has firespread enabled
			fout.write("firespread=" + Boolean.toString(world.isFire()) + newLine);
			fout.write("forcefirespread=" + Boolean.toString(world.isForceFire()) + newLine);
			// has explosions enabled
			fout.write("explosions=" + Boolean.toString(world.isExpl()) + newLine);
			fout.write("forceexplosions=" + Boolean.toString(world.isForceExpl()) + newLine);
			// Enderman block protection
			fout.write("endermanprotect=" + Boolean.toString(world.isEndermanProtect()) + newLine);
			// PlayerTrample
			fout.write("disableplayertrample=" + Boolean.toString(world.isDisablePlayerTrample()) + newLine);
			// CreatureTrample
			fout.write("disablecreaturetrample=" + Boolean.toString(world.isDisableCreatureTrample()) + newLine);

			// Unclaimed
			fout.write(newLine);
			fout.write("# Unclaimed Zone settings." + newLine);

			// Unclaimed Zone Build
			if (world.getUnclaimedZoneBuild() != null)
				fout.write("unclaimedZoneBuild=" + Boolean.toString(world.getUnclaimedZoneBuild()) + newLine);
			// Unclaimed Zone Destroy
			if (world.getUnclaimedZoneDestroy() != null)
				fout.write("unclaimedZoneDestroy=" + Boolean.toString(world.getUnclaimedZoneDestroy()) + newLine);
			// Unclaimed Zone Switch
			if (world.getUnclaimedZoneSwitch() != null)
				fout.write("unclaimedZoneSwitch=" + Boolean.toString(world.getUnclaimedZoneSwitch()) + newLine);
			// Unclaimed Zone Item Use
			if (world.getUnclaimedZoneItemUse() != null)
				fout.write("unclaimedZoneItemUse=" + Boolean.toString(world.getUnclaimedZoneItemUse()) + newLine);
			// Unclaimed Zone Name
			if (world.getUnclaimedZoneName() != null)
				fout.write("unclaimedZoneName=" + world.getUnclaimedZoneName() + newLine);

			fout.write(newLine);
			fout.write("# The following settings are only used if you are not using any permissions provider plugin" + newLine);

			// Unclaimed Zone Ignore Ids
			if (world.getUnclaimedZoneIgnoreIds() != null)
				fout.write("unclaimedZoneIgnoreIds=" + StringMgmt.join(world.getUnclaimedZoneIgnoreIds(), ",") + newLine);

			// PlotManagement Delete
			fout.write(newLine);
			fout.write("# The following settings control what blocks are deleted upon a townblock being unclaimed" + newLine);

			// Using PlotManagement Delete
			fout.write("usingPlotManagementDelete=" + Boolean.toString(world.isUsingPlotManagementDelete()) + newLine);
			// Plot Management Delete Ids
			if (world.getPlotManagementDeleteIds() != null)
				fout.write("plotManagementDeleteIds=" + StringMgmt.join(world.getPlotManagementDeleteIds(), ",") + newLine);

			// PlotManagement
			fout.write(newLine);
			fout.write("# The following settings control what blocks are deleted upon a mayor issuing a '/plot clear' command" + newLine);

			// Using PlotManagement Mayor Delete
			fout.write("usingPlotManagementMayorDelete=" + Boolean.toString(world.isUsingPlotManagementMayorDelete()) + newLine);
			// Plot Management Mayor Delete
			if (world.getPlotManagementMayorDelete() != null)
				fout.write("plotManagementMayorDelete=" + StringMgmt.join(world.getPlotManagementMayorDelete(), ",") + newLine);

			// PlotManagement Revert
			fout.write(newLine + "# If enabled when a town claims a townblock a snapshot will be taken at the time it is claimed." + newLine);
			fout.write("# When the townblock is unclaimded its blocks will begin to revert to the original snapshot." + newLine);

			// Using PlotManagement Revert
			fout.write("usingPlotManagementRevert=" + Boolean.toString(world.isUsingPlotManagementRevert()) + newLine);
			// Using PlotManagement Revert Speed
			fout.write("usingPlotManagementRevertSpeed=" + Long.toString(world.getPlotManagementRevertSpeed()) + newLine);

			fout.write("# Any block Id's listed here will not be respawned. Instead it will revert to air." + newLine);

			// Plot Management Ignore Ids
			if (world.getPlotManagementIgnoreIds() != null)
				fout.write("plotManagementIgnoreIds=" + StringMgmt.join(world.getPlotManagementIgnoreIds(), ",") + newLine);

			// PlotManagement Wild Regen
			fout.write(newLine);
			fout.write("# If enabled any damage caused by explosions will repair itself." + newLine);

			// Using PlotManagement Wild Regen
			fout.write("usingPlotManagementWildRegen=" + Boolean.toString(world.isUsingPlotManagementWildRevert()) + newLine);

			// Wilderness Explosion Protection entities
			if (world.getPlotManagementWildRevertEntities() != null)
				fout.write("PlotManagementWildRegenEntities=" + StringMgmt.join(world.getPlotManagementWildRevertEntities(), ",") + newLine);

			// Using PlotManagement Wild Regen Delay
			fout.write("usingPlotManagementWildRegenDelay=" + Long.toString(world.getPlotManagementWildRevertDelay()) + newLine);

			// Using Towny
			fout.write(newLine);
			fout.write("# This setting is used to enable or disable Towny in this world." + newLine);

			// Using Towny
			fout.write("usingTowny=" + Boolean.toString(world.isUsingTowny()) + newLine);

			return true;
			
		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("Saving Error: Exception while saving world file (" + world.getName() + ")");
			e.printStackTrace();
			return false;
			
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException ignore) {
				}
			}
		}
		
	}

	@Override
	public boolean saveAllTownBlocks() {

		for (TownyWorld world : getWorlds()) {
			for (TownBlock townBlock : world.getTownBlocks())
				saveTownBlock(townBlock);
		}

		return true;
	}

	@Override
	public boolean saveTownBlock(TownBlock townBlock) {

		FileMgmt.checkFolders(new String[] { rootFolder + dataFolder + FileMgmt.fileSeparator() + "townblocks" + FileMgmt.fileSeparator() + townBlock.getWorld().getName() });

		BufferedWriter fout = null;
		String path = getTownBlockFilename(townBlock);

		try {
			fout = new BufferedWriter(new FileWriter(path));
			// permissions
			fout.write("permissions=" + townBlock.getPermissions().toString() + newLine);
			// Have permissions been manually changed
			fout.write("changed=" + Boolean.toString(townBlock.isChanged()) + newLine);

			fout.write("locked=" + Boolean.toString(townBlock.isLocked()) + newLine);

		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("Saving Error: Exception while saving TownBlock file (" + path + ")");
			e.printStackTrace();
			return false;
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException ignore) {
				}
			}
		}
		return true;

	}

	/*
	 * public boolean saveTownBlocks(TownyWorld world) { try { BufferedWriter
	 * fout = new BufferedWriter(new FileWriter(rootFolder +
	 * dataFolder + "/townblocks/"+world.getName()+".csv")); for (TownBlock
	 * townblock :
	 * world.getTownBlocks()) { String line = townblock.getX() + "," +
	 * Long.toString(townblock.getZ()); line += ","; if (townblock.hasTown())
	 * line += townblock.getTown().getName(); line += ","; if
	 * (townblock.hasResident()) line += townblock.getResident().getName(); line
	 * += "," + Boolean.toString(townblock.getPlotPrice()); fout.write(line +
	 * newLine); } fout.close(); return true; } catch (Exception e) {
	 * System.out.println(
	 * "[Towny] Loading Error: Exception while saving town blocks list file");
	 * e.printStackTrace();
	 * return false; } }
	 */

	/**
	 * Load townblocks according to the given line Townblock: x,y,forSale Eg:
	 * townBlocks=world:10,11;10,12,true;|nether:1,1|
	 * 
	 * @param line
	 * @param town
	 * @param resident
	 */

	public void utilLoadTownBlocks(String line, Town town, Resident resident) {

		String[] worlds = line.split("\\|");
		for (String w : worlds) {
			String[] split = w.split(":");
			if (split.length != 2) {
				TownyMessaging.sendErrorMsg("[Warning] " + town.getName() + " BlockList does not have a World or data.");
				continue;
			}
			try {
				TownyWorld world = getWorld(split[0]);
				for (String s : split[1].split(";")) {
					String blockTypeData = null;
					int indexOfType = s.indexOf("[");
					if (indexOfType != -1) { //is found
						int endIndexOfType = s.indexOf("]");
						if (endIndexOfType != -1) {
							blockTypeData = s.substring(indexOfType + 1, endIndexOfType);
						}
						s = s.substring(endIndexOfType + 1);
					}
					String[] tokens = s.split(",");
					if (tokens.length < 2)
						continue;
					try {
						int x = Integer.parseInt(tokens[0]);
						int z = Integer.parseInt(tokens[1]);

						try {
							world.newTownBlock(x, z);
						} catch (AlreadyRegisteredException e) {
						}
						TownBlock townblock = world.getTownBlock(x, z);

						if (town != null)
							townblock.setTown(town);

						if (resident != null && townblock.hasTown())
							townblock.setResident(resident);

						if (blockTypeData != null) {
							utilLoadTownBlockTypeData(townblock, blockTypeData);
						}

						//if present set the plot price
						if (tokens.length >= 3) {
							if (tokens[2] == "true")
								townblock.setPlotPrice(town.getPlotPrice());
							else
								townblock.setPlotPrice(Double.parseDouble(tokens[2]));
						}

					} catch (NumberFormatException e) {
					} catch (NotRegisteredException e) {
					}
				}
			} catch (NotRegisteredException e) {
				continue;
			}
		}
	}

	public void utilLoadTownBlockTypeData(TownBlock townBlock, String data) {

		String[] tokens = data.split(",");

		// Plot Type
		if (tokens.length >= 1)
			townBlock.setType(Integer.valueOf(tokens[0]));

		// Outpost or normal plot.
		if (tokens.length >= 2)
			townBlock.setOutpost(tokens[1].equalsIgnoreCase("1") ? true : false);
	}

	public String utilSaveTownBlocks(List<TownBlock> townBlocks) {

		HashMap<TownyWorld, ArrayList<TownBlock>> worlds = new HashMap<TownyWorld, ArrayList<TownBlock>>();
		String out = "";

		// Sort all town blocks according to what world its in
		for (TownBlock townBlock : townBlocks) {
			TownyWorld world = townBlock.getWorld();
			if (!worlds.containsKey(world))
				worlds.put(world, new ArrayList<TownBlock>());
			worlds.get(world).add(townBlock);
		}

		for (TownyWorld world : worlds.keySet()) {
			out += world.getName() + ":";
			for (TownBlock townBlock : worlds.get(world)) {
				out += "[" + townBlock.getType().getId();
				out += "," + (townBlock.isOutpost() ? "1" : "0");
				out += "]" + townBlock.getX() + "," + townBlock.getZ() + "," + townBlock.getPlotPrice() + ";";
			}
			out += "|";

		}

		return out;
	}

	/**
	 * Save PlotBlockData
	 * 
	 * @param plotChunk
	 * @return true if saved
	 */
	@Override
	public boolean savePlotData(PlotBlockData plotChunk) {

		FileMgmt.checkFolders(new String[] { rootFolder + dataFolder + FileMgmt.fileSeparator() + "plot-block-data" + FileMgmt.fileSeparator() + plotChunk.getWorldName() });

		BufferedWriter fout = null;
		String path = getPlotFilename(plotChunk);
		
		try {
			fout = new BufferedWriter(new FileWriter(path));
			
			switch (plotChunk.getVersion()) {

			case 1:
				/*
				 * New system requires pushing
				 * version data first
				 */
				fout.write("VER");
				fout.write(plotChunk.getVersion());

				break;

			default:

			}

			// Push the plot height, then the plot block data types.
			fout.write(plotChunk.getHeight());
			for (int block : new ArrayList<Integer>(plotChunk.getBlockList())) {
				fout.write(block);
			}

		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("Saving Error: Exception while saving PlotBlockData file (" + path + ")");
			e.printStackTrace();
			return false;
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException ignore) {
				}
			}
		}
		return true;

	}

	/**
	 * Load PlotBlockData
	 * 
	 * @param worldName
	 * @param x
	 * @param z
	 * @return PlotBlockData or null
	 */
	@Override
	public PlotBlockData loadPlotData(String worldName, int x, int z) {

		try {
			TownyWorld world = getWorld(worldName);
			TownBlock townBlock = new TownBlock(x, z, world);

			return loadPlotData(townBlock);
		} catch (NotRegisteredException e) {
			// Failed to get world
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Load PlotBlockData for regen at unclaim
	 * 
	 * @param townBlock
	 * @return PlotBlockData or null
	 */
	@Override
	public PlotBlockData loadPlotData(TownBlock townBlock) {

		String fileName = getPlotFilename(townBlock);

		int value;

		if (isFile(fileName)) {
			PlotBlockData plotBlockData = new PlotBlockData(townBlock);
			List<Integer> IntArr = new ArrayList<Integer>();

			BufferedReader fin = null;
			
			try {
				fin = new BufferedReader(new FileReader(fileName));

				//read the first 3 characters to test for version info
				char[] key = new char[3];
				fin.read(key, 0, 3);
				String test = new String(key);

				switch (elements.fromString(test)) {
				case VER:
					// Read the file version
					int version = fin.read();
					plotBlockData.setVersion(version);

					// next entry is the plot height
					plotBlockData.setHeight(fin.read());
					break;

				default:
					/*
					 * no version field so set height
					 * and push rest to queue
					 */
					plotBlockData.setVersion(0);
					// First entry is the plot height
					plotBlockData.setHeight(key[0]);
					IntArr.add((int) key[1]);
					IntArr.add((int) key[2]);
				}

				// load remainder of file
				while ((value = fin.read()) >= 0) {
					IntArr.add(value);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fin != null) {
					try {
						fin.close();
					} catch (IOException ignore) {
					}
				}
			}
			
			
			plotBlockData.setBlockList(IntArr);
			plotBlockData.resetBlockListRestored();
			return plotBlockData;
		}
		return null;
	}

	@Override
	public void deletePlotData(PlotBlockData plotChunk) {

		File file = new File(getPlotFilename(plotChunk));
		if (file.exists())
			file.delete();
	}

	private boolean isFile(String fileName) {

		File file = new File(fileName);
		if (file.exists() && file.isFile())
			return true;

		return false;
	}

	@Override
	public void deleteFile(String fileName) {

		File file = new File(fileName);
		if (file.exists())
			file.delete();
	}

	@Override
	public void deleteResident(Resident resident) {

		File file = new File(getResidentFilename(resident));
		if (file.exists())
			file.delete();
	}

	@Override
	public void deleteTown(Town town) {

		File file = new File(getTownFilename(town));
		if (file.exists()) {
			try {
				FileMgmt.moveFile(file, ("deleted"));
			} catch (IOException e) {
				TownyMessaging.sendErrorMsg("Error moving Town txt file.");
				file.delete();
			}
		}
	}

	@Override
	public void deleteNation(Nation nation) {

		File file = new File(getNationFilename(nation));
		if (file.exists()) {
			try {
				FileMgmt.moveFile(file, ("deleted"));
			} catch (IOException e) {
				TownyMessaging.sendErrorMsg("Error moving Nation txt file.");
				file.delete();
			}
		}
	}

	@Override
	public void deleteWorld(TownyWorld world) {

		File file = new File(getWorldFilename(world));
		if (file.exists()) {
			try {
				FileMgmt.moveFile(file, ("deleted"));
			} catch (IOException e) {
				TownyMessaging.sendErrorMsg("Error moving World txt file.");
			}
		}
	}

	@Override
	public void deleteTownBlock(TownBlock townBlock) {

		File file = new File(getTownBlockFilename(townBlock));
		if (file.exists())
			file.delete();
	}

}