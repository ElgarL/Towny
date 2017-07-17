/*
 * Towny MYSQL Source by StPinker
 * 
 * Released under LGPL
 */
package com.palmergames.bukkit.towny.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

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
import com.palmergames.bukkit.util.BukkitTools;
import com.palmergames.util.FileMgmt;
import com.palmergames.util.StringMgmt;

public class TownySQLSource extends TownyFlatFileSource {
	
	private Queue<SQL_Task> queryQueue = new ConcurrentLinkedQueue<SQL_Task>();
	private BukkitTask task = null;
	
	protected String driver = "";
	protected String dsn = "";
	protected String hostname = "";
	protected String port = "";
	protected String db_name = "";
	protected String username = "";
	protected String password = "";
	protected String tb_prefix = "";

	private Connection cntx = null;
	private String type = "";

	// private boolean ish2 = false;

	/**
	 * Flag if we are using h2 or standard SQL connectivity.
	 * 
	 * @param type
	 */
	public TownySQLSource(String type) {

		this.type = type.toLowerCase();
		// if ((type.equalsIgnoreCase("sqlite")) ||
		// (type.equalsIgnoreCase("h2")))
		// this.ish2 = true;
	}

	@Override
	public void initialize(Towny plugin, TownyUniverse universe) {

		this.universe = universe;
		this.plugin = plugin;
		this.rootFolder = universe.getRootFolder();

		try {

			FileMgmt.checkFolders(new String[] {
					rootFolder,
					rootFolder + dataFolder,
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "plot-block-data" });
			FileMgmt.checkFiles(new String[] {
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "regen.txt",
					rootFolder + dataFolder + FileMgmt.fileSeparator() + "snapshot_queue.txt" });

		} catch (IOException e) {
			TownyMessaging.sendErrorMsg("Could not create flatfile default files and folders.");
		}

		/*
		 *  Setup SQL connection
		 */
		hostname = TownySettings.getSQLHostName();
		port = TownySettings.getSQLPort();
		db_name = TownySettings.getSQLDBName();
		tb_prefix = TownySettings.getSQLTablePrefix().toUpperCase();

		if (this.type.equals("h2")) {

			this.driver = "org.h2.Driver";
			this.dsn = ("jdbc:h2:" + rootFolder + dataFolder + File.separator + db_name + ".h2db;AUTO_RECONNECT=TRUE");
			username = "sa";
			password = "sa";

		} else if (this.type.equals("mysql")) {

			this.driver = "com.mysql.jdbc.Driver";
			if (TownySettings.getSQLUsingSSL())
				this.dsn = ("jdbc:mysql://" + hostname + ":" + port + "/" + db_name + "?useUnicode=true&characterEncoding=utf-8");
			else 
				this.dsn = ("jdbc:mysql://" + hostname + ":" + port + "/" + db_name + "?verifyServerCertificate=false&useSSL=false&useUnicode=true&characterEncoding=utf-8");
			username = TownySettings.getSQLUsername();
			password = TownySettings.getSQLPassword();

		} else {

			this.driver = "org.sqlite.JDBC";
			this.dsn = ("jdbc:sqlite:" + rootFolder + dataFolder + File.separator + db_name + ".sqldb");
			username = "";
			password = "";

		}

		/*
		 * Register the driver (if possible)
		 */
		try {
			Driver driver = (Driver) Class.forName(this.driver).newInstance();
			DriverManager.registerDriver(driver);
		} catch (Exception e) {
			System.out.println("[Towny] Driver error: " + e);
		}

		/*
		 * Attempt to get a connection to the database
		 */
		if (getContext()) {

			TownyMessaging.sendDebugMsg("[Towny] Connected to Database");

		} else {

			TownyMessaging.sendErrorMsg("Failed when connecting to Database");
			return;

		}

		/*
		 *  Initialise database Schema.
		 */
		SQL_Schema.initTables(cntx, db_name);

		/*
		 * Start our Async queue for pushing data to the database.
		 */
		task = BukkitTools.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {

			public void run() {

				while (!TownySQLSource.this.queryQueue.isEmpty()) {

					SQL_Task query = TownySQLSource.this.queryQueue.poll();

					if (query.update) {

						TownySQLSource.this.QueueUpdateDB(query.tb_name, query.args, query.keys);

					} else {

						TownySQLSource.this.QueueDeleteDB(query.tb_name, query.args);
						
					}

				}

			}

		}, 5L, 5L);
	}
	
	@Override
	public void cancelTask() {
		
		task.cancel();
		
	}

	/**
	 * open a connection to the SQL server.
	 * 
	 * @return true if we successfully connected to the db.
	 */
	public boolean getContext() {

		try {
			if (cntx == null || cntx.isClosed() || ( !this.type.equals("sqlite") && !cntx.isValid(1))) {

				if (cntx != null && !cntx.isClosed()) {

					try {

						cntx.close();

					} catch (SQLException e) {
						/*
						 *  We're disposing of an old stale connection just be nice to the GC
						 *  as well as mysql, so ignore the error as there's nothing we can do
						 *  if it fails
						 */
					}
					cntx = null;
				}

				if ((this.username.equalsIgnoreCase("")) && (this.password.equalsIgnoreCase(""))) {

					cntx = DriverManager.getConnection(this.dsn);

				} else {

					cntx = DriverManager.getConnection(this.dsn, this.username, this.password);
				}

				if (cntx == null || cntx.isClosed())
					return false;
			}

			return true;

		} catch (SQLException e) {
			TownyMessaging.sendErrorMsg("Error could not Connect to db " + this.dsn + ": " + e.getMessage());
		}

		return false;
	}

	/**
	 * Build the SQL string and execute to INSERT/UPDATE
	 * 
	 * @param tb_name
	 * @param args
	 * @param keys
	 * @return true if the update was successful.
	 */
	public boolean UpdateDB(String tb_name, HashMap<String, Object> args, List<String> keys) {

		/*
		 *  Make sure we only execute queries in async
		 */

		this.queryQueue.add(new SQL_Task(tb_name, args, keys));

		return true;

		
	}
	
	public boolean QueueUpdateDB(String tb_name, HashMap<String, Object> args, List<String> keys) {

		/*
		 *  Attempt to get a database connection.
		 */
		if (!getContext())
			return false;

		String code = null;
		PreparedStatement stmt = null;
		List<Object> parameters = new ArrayList<Object>();
		int rs = 0;

		try {

			if (keys == null) {

				/*
				 * No keys so this is an INSERT not an UPDATE.
				 */

				// Push all values to a parameter list.

				parameters.addAll(args.values());

				String[] aKeys = args.keySet().toArray(new String[args.keySet().size()]);

				// Build the prepared statement string appropriate for
				// the number of keys/values we are inserting.

				code = "REPLACE INTO " + tb_prefix + (tb_name.toUpperCase()) + " ";
				String keycode = "(";
				String valuecode = " VALUES (";

				for (int count = 0; count < args.size(); count++) {

					keycode += "`" + aKeys[count] + "`";
					valuecode += "?";

					if ((count < (args.size() - 1))) {
						keycode += ", ";
						valuecode += ",";
					} else {
						keycode += ")";
						valuecode += ")";
					}
				}

				code += keycode;
				code += valuecode;

			} else {

				/*
				 * We have keys so this is a conditional UPDATE.
				 */

				String[] aKeys = args.keySet().toArray(new String[args.keySet().size()]);

				// Build the prepared statement string appropriate for
				// the number of keys/values we are inserting.

				code = "UPDATE " + tb_prefix + (tb_name.toUpperCase()) + " SET ";

				for (int count = 0; count < args.size(); count++) {

					code += "`" + aKeys[count] + "` = ?";

					// Push value for each entry.

					parameters.add(args.get(aKeys[count]));

					if ((count < (args.size() - 1))) {
						code += ",";
					}
				}

				code += " WHERE ";

				for (int count = 0; count < keys.size(); count++) {

					code += "`" + keys.get(count) + "` = ?";

					// Add extra values for the WHERE conditionals.

					parameters.add(args.get(keys.get(count)));

					if ((count < (keys.size() - 1))) {
						code += " AND ";
					}
				}

			}

			// Populate the prepared statement parameters.

			stmt = cntx.prepareStatement(code);

			for (int count = 0; count < parameters.size(); count++) {

				Object element = parameters.get(count);

				if (element instanceof String) {

					stmt.setString(count + 1, (String) element);

				} else if (element instanceof Boolean) {

					stmt.setString(count + 1, ((Boolean) element) ? "1" : "0");

				} else {

					stmt.setObject(count + 1, element.toString());

				}

			}

			rs = stmt.executeUpdate();

		} catch (SQLException e) {

			TownyMessaging.sendErrorMsg("SQL: " + e.getMessage() + " --> " + stmt.toString());

		} finally {

			try {

				if (stmt != null) {
					stmt.close();
				}

				if (rs == 0) // if entry doesn't exist then try to insert
					return UpdateDB(tb_name, args, null);

			} catch (SQLException e) {
				TownyMessaging.sendErrorMsg("SQL closing: " + e.getMessage() + " --> " + stmt.toString());
			}

		}

		// Failed?
		if (rs == 0)
			return false;

		// Success!
		return true;

	}

	/**
	 * Build the SQL string and execute to DELETE
	 * 
	 * @param tb_name
	 * @param args
	 * @return true if the delete was a success.
	 */
	public boolean DeleteDB(String tb_name, HashMap<String, Object> args) {

		// Make sure we only execute queries in async
		
		this.queryQueue.add(new SQL_Task(tb_name, args));
		
		return true;


	}
	
	public boolean QueueDeleteDB(String tb_name, HashMap<String, Object> args) {

		if (!getContext())
			return false;
		try {
			String wherecode = "DELETE FROM " + tb_prefix + (tb_name.toUpperCase()) + " WHERE ";
			Set<Map.Entry<String, Object>> set = args.entrySet();
			Iterator<Map.Entry<String, Object>> i = set.iterator();
			while (i.hasNext()) {
				Map.Entry<String, Object> me = (Map.Entry<String, Object>) i.next();
				wherecode += "`" + me.getKey() + "` = ";
				if (me.getValue() instanceof String)
					wherecode += "'" + ((String) me.getValue()).replace("'", "\''") + "'";
				else if (me.getValue() instanceof Boolean)
					wherecode += "'" + (((Boolean) me.getValue()) ? "1" : "0") + "'";
				else
					wherecode += "'" + me.getValue() + "'";

				wherecode += (i.hasNext() ? " AND " : "");
			}
			Statement s = cntx.createStatement();
			int rs = s.executeUpdate(wherecode);
			s.close();
			if (rs == 0) {
				TownyMessaging.sendDebugMsg("SQL: delete returned 0: " + wherecode);
			}
		} catch (SQLException e) {
			TownyMessaging.sendErrorMsg("SQL: Error delete : " + e.getMessage());
		}
		return false;
	}


	/*
	 * Load keys
	 */
	@Override
	public boolean loadTownBlockList() {
		
		TownyMessaging.sendDebugMsg("Loading TownBlock List");
		if (!getContext())
			return false;
		try {
			Statement s = cntx.createStatement();
			ResultSet rs = s.executeQuery("SELECT world,x,z FROM " + tb_prefix + "TOWNBLOCKS");

			while (rs.next()) {
				
				TownyWorld world = getWorld(rs.getString("world"));
				int x = Integer.parseInt(rs.getString("x"));
				int z = Integer.parseInt(rs.getString("z"));

				try {
					world.newTownBlock(x, z);
				} catch (AlreadyRegisteredException e) {
				}
				
			}
			
			s.close();
			
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		
		
	}

	@Override
	public boolean loadResidentList() {

		TownyMessaging.sendDebugMsg("Loading Resident List");
		if (!getContext())
			return false;
		try {
			Statement s = cntx.createStatement();
			ResultSet rs = s.executeQuery("SELECT name FROM " + tb_prefix + "RESIDENTS");

			while (rs.next()) {
				try {
					newResident(rs.getString("name"));
				} catch (AlreadyRegisteredException e) {
				}
			}
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean loadTownList() {

		TownyMessaging.sendDebugMsg("Loading Town List");
		if (!getContext())
			return false;
		try {
			Statement s = cntx.createStatement();
			ResultSet rs = s.executeQuery("SELECT name FROM " + tb_prefix + "TOWNS");

			while (rs.next()) {
				try {
					newTown(rs.getString("name"));
				} catch (AlreadyRegisteredException e) {
				}
			}
			s.close();
			return true;
		} catch (SQLException e) {
			TownyMessaging.sendErrorMsg("SQL: town list sql error : " + e.getMessage());
		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("SQL: town list unknown error: ");
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean loadNationList() {

		TownyMessaging.sendDebugMsg("Loading Nation List");
		if (!getContext())
			return false;
		try {
			Statement s = cntx.createStatement();
			ResultSet rs = s.executeQuery("SELECT name FROM " + tb_prefix + "NATIONS");
			while (rs.next()) {
				try {
					newNation(rs.getString("name"));
				} catch (AlreadyRegisteredException e) {
				}
			}
			s.close();
			return true;
		} catch (SQLException e) {
			TownyMessaging.sendErrorMsg("SQL: nation list sql error : " + e.getMessage());
		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("SQL: nation list unknown error : ");
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean loadWorldList() {

		TownyMessaging.sendDebugMsg("Loading World List");

		if (!getContext())
			return false;
		try {
			Statement s = cntx.createStatement();
			ResultSet rs = s.executeQuery("SELECT name FROM " + tb_prefix + "WORLDS");
			while (rs.next()) {
				try {
					newWorld(rs.getString("name"));
				} catch (AlreadyRegisteredException e) {
				}
			}
			s.close();
		} catch (SQLException e) {
			TownyMessaging.sendErrorMsg("SQL: world list sql error : " + e.getMessage());
		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("SQL: world list unknown error : ");
			e.printStackTrace();
		}

		// Check for any new worlds registered with bukkit.
		if (plugin != null) {
			for (World world : plugin.getServer().getWorlds())
				try {
					newWorld(world.getName());
				} catch (AlreadyRegisteredException e) {
					// e.printStackTrace();
				} catch (NotRegisteredException e) {
					// e.printStackTrace();
				}
		}
		return true;
	}

	/*
	 * Load individual towny object
	 */

	@SuppressWarnings("deprecation")
	@Override
	public boolean loadResident(Resident resident) {

		TownyMessaging.sendDebugMsg("Loading resident " + resident.getName());
		if (!getContext())
			return false;
		try {
			Statement s = cntx.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM " + tb_prefix + "RESIDENTS " + " WHERE name='" + resident.getName() + "'");
			String search;

			while (rs.next()) {
				try {
					resident.setLastOnline(rs.getLong("lastOnline"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					resident.setRegistered(rs.getLong("registered"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					resident.setNPC(rs.getBoolean("isNPC"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					resident.setJailed(rs.getBoolean("isJailed"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					resident.setJailSpawn(rs.getInt("JailSpawn"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					resident.setJailTown(rs.getString("JailTown"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					resident.setTitle(rs.getString("title"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					resident.setSurname(rs.getString("surname"));
				} catch (Exception e) {
					e.printStackTrace();
				}

				String line = rs.getString("town");
				if ((line != null) && (!line.isEmpty())) {
					resident.setTown(getTown(line));
					TownyMessaging.sendDebugMsg("Resident " + resident.getName() + " set to Town " + line);
				}

				line = rs.getString("town-ranks");
				if ((line != null) && (!line.isEmpty())) {
					search = (line.contains("#")) ? "#" : ",";
					resident.setTownRanks(new ArrayList<String>(Arrays.asList((line.split(search)))));
					TownyMessaging.sendDebugMsg("Resident " + resident.getName() + " set Town-ranks " + line);
				}

				line = rs.getString("nation-ranks");
				if ((line != null) && (!line.isEmpty())) {
					search = (line.contains("#")) ? "#" : ",";
					resident.setNationRanks(new ArrayList<String>(Arrays.asList((line.split(search)))));
					TownyMessaging.sendDebugMsg("Resident " + resident.getName() + " set Nation-ranks " + line);
				}

				try {
					line = rs.getString("friends");
					if (line != null) {
						search = (line.contains("#")) ? "#" : ",";
						String[] tokens = line.split(search);
						for (String token : tokens) {
							if (!token.isEmpty()) {
								Resident friend = getResident(token);
								if (friend != null)
									resident.addFriend(friend);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					resident.setPermissions(rs.getString("protectionStatus").replaceAll("#", ","));
				} catch (Exception e) {
					e.printStackTrace();
				}

				/*
				 * Attempt these for older databases.
				 */
				try {

					line = rs.getString("townBlocks");
					if ((line != null) && (!line.isEmpty()))
						utilLoadTownBlocks(line, null, resident);

				} catch (SQLException e) {
				}

				s.close();
				return true;
			}
			return false;
		} catch (SQLException e) {
			TownyMessaging.sendErrorMsg("SQL: Load resident sql error : " + e.getMessage());
		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("SQL: Load resident unknown error");
			e.printStackTrace();
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean loadTown(Town town) {

		String line;
		String[] tokens;
		TownyMessaging.sendDebugMsg("Loading town " + town.getName());
		if (!getContext())
			return false;

		try {
			Statement s = cntx.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM " + tb_prefix + "TOWNS " + " WHERE name='" + town.getName() + "'");
			String search;
			
			while (rs.next()) {

				line = rs.getString("residents");
				if (line != null) {
					search = (line.contains("#")) ? "#" : ",";
					tokens = line.split(search);
					for (String token : tokens) {
						if (!token.isEmpty()) {
							Resident resident = getResident(token);
							if (resident != null)
								town.addResident(resident);
						}
					}
				}
				
				town.setMayor(getResident(rs.getString("mayor")));
				// line = rs.getString("assistants");
				// if (line != null) {
				// tokens = line.split(",");
				// for (String token : tokens) {
				// if (!token.isEmpty()) {
				// Resident assistant = getResident(token);
				// if ((assistant != null) && (town.hasResident(assistant)))
				// town.addAssistant(assistant);
				// }
				// }
				// }
				town.setTownBoard(rs.getString("townBoard"));
				line = rs.getString("tag");
				if (line != null)
					try {
						town.setTag(line);
					} catch (TownyException e) {
						town.setTag("");
					}
				town.setPermissions(rs.getString("protectionStatus").replaceAll("#", ","));
				town.setBonusBlocks(rs.getInt("bonus"));
				town.setTaxPercentage(rs.getBoolean("taxpercent"));
				town.setTaxes(rs.getFloat("taxes"));
				town.setHasUpkeep(rs.getBoolean("hasUpkeep"));
				town.setPlotPrice(rs.getFloat("plotPrice"));
				town.setPlotTax(rs.getFloat("plotTax"));
				town.setEmbassyPlotPrice(rs.getFloat("embassyPlotPrice"));
				town.setEmbassyPlotTax(rs.getFloat("embassyPlotTax"));
				town.setCommercialPlotPrice(rs.getFloat("commercialPlotPrice"));
				town.setCommercialPlotTax(rs.getFloat("commercialPlotTax"));
				town.setOpen(rs.getBoolean("open"));
				town.setPublic(rs.getBoolean("public"));
				town.setAdminDisabledPVP(rs.getBoolean("admindisabledpvp"));

				town.setPurchasedBlocks(rs.getInt("purchased"));

				line = rs.getString("homeBlock");
				if (line != null) {
					search = (line.contains("#")) ? "#" : ",";
					tokens = line.split(search);
					if (tokens.length == 3)
						try {
							TownyWorld world = getWorld(tokens[0]);

							try {
								int x = Integer.parseInt(tokens[1]);
								int z = Integer.parseInt(tokens[2]);
								TownBlock homeBlock = world.getTownBlock(x, z);
								town.forceSetHomeBlock(homeBlock);
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

				line = rs.getString("spawn");
				if (line != null) {
					search = (line.contains("#")) ? "#" : ",";
					tokens = line.split(search);
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
							town.forceSetSpawn(loc);
						} catch (NumberFormatException e) {
						} catch (NotRegisteredException e) {
						} catch (NullPointerException e) {
					}
				}
				// Load outpost spawns
				line = rs.getString("outpostSpawns");
				if (line != null) {
					String[] outposts = line.split(";");
					for (String spawn : outposts) {
						search = (line.contains("#")) ? "#" : ",";
						tokens = spawn.split(search);
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
								town.forceAddOutpostSpawn(loc);
							} catch (NumberFormatException e) {
							} catch (NotRegisteredException e) {
							} catch (NullPointerException e) {
							}
					}
				}
				// Load jail spawns
				line = rs.getString("jailSpawns");
				if (line != null) {
					String[] jails = line.split(";");
					for (String spawn : jails) {
						search = (line.contains("#")) ? "#" : ",";
						tokens = spawn.split(search);
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
								town.forceAddJailSpawn(loc);
							} catch (NumberFormatException e) {
							} catch (NotRegisteredException e) {
							} catch (NullPointerException e) {
							}
					}
				}
				line = rs.getString("outlaws");
				if (line != null) {
					search = (line.contains("#")) ? "#" : ",";
					tokens = line.split(search);
					for (String token : tokens) {
						if (!token.isEmpty()) {
							Resident resident = getResident(token);
							if (resident != null)
								town.addOutlaw(resident);
						}
					}
				}


				/*
				 * Attempt these for older databases.
				 */
				try {

					line = rs.getString("townBlocks");
					if (line != null)
						utilLoadTownBlocks(line, town, null);

				} catch (SQLException e) {
				}

				s.close();
				return true;
			}
			s.close();
			return false;
		} catch (SQLException e) {
			TownyMessaging.sendErrorMsg("SQL: Load Town sql Error - " + e.getMessage());
		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("SQL: Load Town unknown Error - ");
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean loadNation(Nation nation) {

		String line = "";
		String[] tokens;
		TownyMessaging.sendDebugMsg("Loading nation " + nation.getName());
		if (!getContext())
			return false;
		try {
			Statement s = cntx.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM " + tb_prefix + "NATIONS WHERE name='" + nation.getName() + "'");
			String search;
			
			while (rs.next()) {
				line = rs.getString("towns");
				if (line != null) {
					search = (line.contains("#")) ? "#" : ",";
					tokens = line.split(search);
					for (String token : tokens) {
						if (!token.isEmpty()) {
							Town town = getTown(token);
							if (town != null)
								nation.addTown(town);
						}
					}
				}
				nation.setCapital(getTown(rs.getString("capital")));
				// line = rs.getString("assistants");
				// if (line != null) {
				// tokens = line.split(",");
				// for (String token : tokens) {
				// if (!token.isEmpty()) {
				// Resident assistant = getResident(token);
				// if (assistant != null)
				// nation.addAssistant(assistant);
				// }
				// }
				// }

				nation.setTag(rs.getString("tag"));

				line = rs.getString("allies");
				if (line != null) {
					search = (line.contains("#")) ? "#" : ",";
					tokens = line.split(search);
					for (String token : tokens) {
						if (!token.isEmpty()) {
							Nation friend = getNation(token);
							if (friend != null)
								nation.addAlly(friend); // ("ally", friend);
						}
					}
				}

				line = rs.getString("enemies");
				if (line != null) {
					search = (line.contains("#")) ? "#" : ",";
					tokens = line.split(search);
					for (String token : tokens) {
						if (!token.isEmpty()) {
							Nation enemy = getNation(token);
							if (enemy != null)
								nation.addEnemy(enemy); // ("enemy", enemy);
						}
					}
				}
				nation.setTaxes(rs.getDouble("taxes"));
				nation.setNeutral(rs.getBoolean("neutral"));
			}

			s.close();
			return true;
		} catch (SQLException e) {
			TownyMessaging.sendErrorMsg("SQL: Load Nation sql error " + e.getMessage());
		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("SQL: Load Nation unknown error - ");
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean loadWorld(TownyWorld world) {

		String line = "";
		Boolean result = false;
		Long resultLong;
		String[] tokens;
		TownyMessaging.sendDebugMsg("Loading world " + world.getName());
		if (!getContext())
			return false;
		try {
			Statement s = cntx.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM " + tb_prefix + "WORLDS WHERE name='" + world.getName() + "'");
			String search;
			
			while (rs.next()) {
				line = rs.getString("towns");
				if (line != null) {
					search = (line.contains("#")) ? "#" : ",";
					tokens = line.split(search);
					for (String token : tokens) {
						if (!token.isEmpty()) {
							Town town = getTown(token);
							if (town != null) {
								town.setWorld(world);
							}
						}
					}
				}

				result = rs.getBoolean("claimable");
				if (result != null)
					try {
						world.setClaimable(result);
					} catch (Exception e) {
					}

				result = rs.getBoolean("pvp");
				if (result != null)
					try {
						world.setPVP(result);
					} catch (Exception e) {
					}

				result = rs.getBoolean("forcepvp");
				if (result != null)
					try {
						world.setForcePVP(result);
					} catch (Exception e) {
					}

				result = rs.getBoolean("forcetownmobs");
				if (result != null)
					try {
						world.setForceTownMobs(result);
					} catch (Exception e) {
					}

				result = rs.getBoolean("worldmobs");
				if (result != null)
					try {
						world.setWorldMobs(result);
					} catch (Exception e) {
					}

				result = rs.getBoolean("firespread");
				if (result != null)
					try {
						world.setFire(result);
					} catch (Exception e) {
					}

				result = rs.getBoolean("forcefirespread");
				if (result != null)
					try {
						world.setForceFire(result);
					} catch (Exception e) {
					}

				result = rs.getBoolean("explosions");
				if (result != null)
					try {
						world.setExpl(result);
					} catch (Exception e) {
					}

				result = rs.getBoolean("forceexplosions");
				if (result != null)
					try {
						world.setForceExpl(result);
					} catch (Exception e) {
					}

				result = rs.getBoolean("endermanprotect");
				if (result != null)
					try {
						world.setEndermanProtect(result);
					} catch (Exception e) {
					}

				result = rs.getBoolean("disableplayertrample");
				if (result != null)
					try {
						world.setDisablePlayerTrample(result);
					} catch (Exception e) {
					}

				result = rs.getBoolean("disablecreaturetrample");
				if (result != null)
					try {
						world.setDisableCreatureTrample(result);
					} catch (Exception e) {
					}

				result = rs.getBoolean("unclaimedZoneBuild");
				if (result != null)
					try {
						world.setUnclaimedZoneBuild(result);
					} catch (Exception e) {
					}

				result = rs.getBoolean("unclaimedZoneDestroy");
				if (result != null)
					try {
						world.setUnclaimedZoneDestroy(result);
					} catch (Exception e) {
					}

				result = rs.getBoolean("unclaimedZoneSwitch");
				if (result != null)
					try {
						world.setUnclaimedZoneSwitch(result);
					} catch (Exception e) {
					}

				result = rs.getBoolean("unclaimedZoneItemUse");
				if (result != null)
					try {
						world.setUnclaimedZoneItemUse(result);
					} catch (Exception e) {
					}

				line = rs.getString("unclaimedZoneName");
				if (result != null)
					try {
						world.setUnclaimedZoneName(line);
					} catch (Exception e) {
					}

				line = rs.getString("unclaimedZoneIgnoreIds");
				if (line != null)
					try {
						List<String> mats = new ArrayList<String>();
						search = (line.contains("#")) ? "#" : ",";
						for (String split : line.split(search))
							if (!split.isEmpty())
								try {
									int id = Integer.parseInt(split);

									mats.add(BukkitTools.getMaterial(id).name());

								} catch (NumberFormatException e) {
									mats.add(split);
								}
						world.setUnclaimedZoneIgnore(mats);
					} catch (Exception e) {
					}

				result = rs.getBoolean("usingPlotManagementDelete");
				if (result != null)
					try {
						world.setUsingPlotManagementDelete(result);
					} catch (Exception e) {
					}

				line = rs.getString("plotManagementDeleteIds");
				if (line != null)
					try {
						List<String> mats = new ArrayList<String>();
						search = (line.contains("#")) ? "#" : ",";
						for (String split : line.split(search))
							if (!split.isEmpty())
								try {
									int id = Integer.parseInt(split);

									mats.add(BukkitTools.getMaterial(id).name());

								} catch (NumberFormatException e) {
									mats.add(split);
								}
						world.setPlotManagementDeleteIds(mats);
					} catch (Exception e) {
					}

				result = rs.getBoolean("usingPlotManagementMayorDelete");
				if (result != null)
					try {
						world.setUsingPlotManagementMayorDelete(result);
					} catch (Exception e) {
					}

				line = rs.getString("plotManagementMayorDelete");
				if (line != null)
					try {
						List<String> materials = new ArrayList<String>();
						search = (line.contains("#")) ? "#" : ",";
						for (String split : line.split(search))
							if (!split.isEmpty())
								try {
									materials.add(split.toUpperCase().trim());
								} catch (NumberFormatException e) {
								}
						world.setPlotManagementMayorDelete(materials);
					} catch (Exception e) {
					}

				result = rs.getBoolean("usingPlotManagementRevert");
				if (result != null)
					try {
						world.setUsingPlotManagementRevert(result);
					} catch (Exception e) {
					}

				/*
				 * No longer used - Never was used. Sadly not configurable per-world based on how the timer runs.
				 */
//				resultLong = rs.getLong("PlotManagementRevertSpeed");
////				if (resultLong != null)
////					try {
////						world.setPlotManagementRevertSpeed(resultLong);
////					} catch (Exception e) {
////					}

				line = rs.getString("plotManagementIgnoreIds");
				if (line != null)
					try {
						List<String> mats = new ArrayList<String>();
						search = (line.contains("#")) ? "#" : ",";
						for (String split : line.split(search))
							if (!split.isEmpty())
								try {
									int id = Integer.parseInt(split);

									mats.add(BukkitTools.getMaterial(id).name());

								} catch (NumberFormatException e) {
									mats.add(split);
								}
						world.setPlotManagementIgnoreIds(mats);
					} catch (Exception e) {
					}

				result = rs.getBoolean("usingPlotManagementWildRegen");
				if (result != null)
					try {
						world.setUsingPlotManagementWildRevert(result);
					} catch (Exception e) {
					}

				line = rs.getString("plotManagementWildRegenEntities");
				if (line != null)
					try {
						List<String> entities = new ArrayList<String>();
						search = (line.contains("#")) ? "#" : ",";
						for (String split : line.split(search))
							if (!split.isEmpty())
								try {
									entities.add(split.trim());
								} catch (NumberFormatException e) {
								}
						world.setPlotManagementWildRevertEntities(entities);
					} catch (Exception e) {
					}

				resultLong = rs.getLong("plotManagementWildRegenSpeed");
				if (resultLong != null)
					try {
						world.setPlotManagementWildRevertDelay(resultLong);
					} catch (Exception e) {
					}

				result = rs.getBoolean("usingTowny");
				if (result != null)
					try {
						world.setUsingTowny(result);
					} catch (Exception e) {
					}

			}

			s.close();
			return true;

		} catch (SQLException e) {
			TownyMessaging.sendErrorMsg("SQL: Load world sql error (" + world.getName() + ")" + e.getMessage());
		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("SQL: Load world unknown error - ");
			e.printStackTrace();
		}
		return false;

	}

	@Override
	public boolean loadTownBlocks() {

		String line = "";
		Boolean result = false;

		// Load town blocks
		if (!getContext())
			return false;

		ResultSet rs;

		for (TownBlock townBlock : getAllTownBlocks()) {
			//boolean set = false;

			try {
				Statement s = cntx.createStatement();
				rs = s.executeQuery("SELECT * FROM " + tb_prefix + "TOWNBLOCKS" + " WHERE world='" + townBlock.getWorld().getName() + "' AND x='" + townBlock.getX() + "' AND z='" + townBlock.getZ() + "'");

				while (rs.next()) {
					line = rs.getString("name");
					if (line != null)
						try {
							townBlock.setName(line.trim());
						} catch (Exception e) {
						}

					line = rs.getString("price");
					if (line != null)
						try {
							townBlock.setPlotPrice(Float.parseFloat(line.trim()));
						} catch (Exception e) {
						}

					line = rs.getString("town");
					if (line != null)
						try {
							Town town = getTown(line.trim());
							townBlock.setTown(town);
						} catch (Exception e) {
						}

					line = rs.getString("resident");
					if (line != null && !line.isEmpty())
						try {
							Resident res = getResident(line.trim());
							townBlock.setResident(res);
						} catch (Exception e) {
						}

					line = rs.getString("type");
					if (line != null)
						try {
							townBlock.setType(Integer.parseInt(line));
						} catch (Exception e) {
						}

					line = rs.getString("outpost");
					if (line != null)
						try {
							townBlock.setOutpost(Boolean.parseBoolean(line));
						} catch (Exception e) {
						}

					line = rs.getString("permissions");
					if ((line != null) && !line.isEmpty())
						try {
							townBlock.setPermissions(line.trim().replaceAll("#", ","));
							//set = true;
						} catch (Exception e) {
						}

					result = rs.getBoolean("changed");
					if (result != null)
						try {
							townBlock.setChanged(result);
						} catch (Exception e) {
						}

					result = rs.getBoolean("locked");
					if (result != null)
						try {
							townBlock.setLocked(result);
						} catch (Exception e) {
						}

				}

				//				if (!set) {
				//					// no permissions found so set in relation to it's
				//					// owners perms.
				//					try {
				//						if (townBlock.hasResident()) {
				//							townBlock.setPermissions(townBlock.getResident().getPermissions().toString());
				//						} else {
				//							townBlock.setPermissions(townBlock.getTown().getPermissions().toString());
				//						}
				//					} catch (NotRegisteredException e) {
				//						// Will never reach here
				//					}
				//				}

				s.close();

			} catch (SQLException e) {
				TownyMessaging.sendErrorMsg("Loading Error: Exception while reading TownBlock: " + townBlock + " at line: " + line + " in the sql database");
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/*
	 * Save individual towny objects
	 */

	@Override
	public synchronized boolean saveResident(Resident resident) {

		TownyMessaging.sendDebugMsg("Saving Resident");
		try {
			HashMap<String, Object> res_hm = new HashMap<String, Object>();
			res_hm.put("name", resident.getName());
			res_hm.put("lastOnline", resident.getLastOnline());
			res_hm.put("registered", resident.getRegistered());
			res_hm.put("isNPC", resident.isNPC());
			res_hm.put("isJailed", resident.isJailed());
			res_hm.put("JailSpawn", resident.getJailSpawn());
			res_hm.put("JailTown", resident.getJailTown());
			res_hm.put("title", resident.getTitle());
			res_hm.put("surname", resident.getSurname());
			res_hm.put("town", resident.hasTown() ? resident.getTown().getName() : "");
			res_hm.put("town-ranks", resident.hasTown() ? StringMgmt.join(resident.getTownRanks(), "#") : "");
			res_hm.put("nation-ranks", resident.hasTown() ? StringMgmt.join(resident.getNationRanks(), "#") : "");
			res_hm.put("friends", StringMgmt.join(resident.getFriends(), "#"));
			//res_hm.put("townBlocks", utilSaveTownBlocks(new ArrayList<TownBlock>(resident.getTownBlocks())));
			res_hm.put("protectionStatus", resident.getPermissions().toString().replaceAll(",", "#"));

			UpdateDB("RESIDENTS", res_hm, Arrays.asList("name"));
			return true;

		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("SQL: Save Resident unknown error " + e.getMessage());
		}
		return false;
	}

	@Override
	public synchronized boolean saveTown(Town town) {

		TownyMessaging.sendDebugMsg("Saving town " + town.getName());
		try {
			HashMap<String, Object> twn_hm = new HashMap<String, Object>();
			twn_hm.put("name", town.getName());
			twn_hm.put("residents", StringMgmt.join(town.getResidents(), "#"));
			twn_hm.put("outlaws", StringMgmt.join(town.getOutlaws(), "#"));
			twn_hm.put("mayor", town.hasMayor() ? town.getMayor().getName() : "");
			twn_hm.put("nation", town.hasNation() ? town.getNation().getName() : "");
			twn_hm.put("assistants", StringMgmt.join(town.getAssistants(), "#"));
			twn_hm.put("townBoard", town.getTownBoard());
			twn_hm.put("tag", town.getTag());
			twn_hm.put("protectionStatus", town.getPermissions().toString().replaceAll(",", "#"));
			twn_hm.put("bonus", town.getBonusBlocks());
			twn_hm.put("purchased", town.getPurchasedBlocks());
			twn_hm.put("commercialPlotPrice", town.getCommercialPlotPrice());
			twn_hm.put("commercialPlotTax", town.getCommercialPlotTax());
			twn_hm.put("embassyPlotPrice", town.getEmbassyPlotPrice());
			twn_hm.put("embassyPlotTax", town.getEmbassyPlotTax());
			twn_hm.put("plotPrice", town.getPlotPrice());
			twn_hm.put("plotTax", town.getPlotTax());
			twn_hm.put("taxes", town.getTaxes());
			twn_hm.put("hasUpkeep", town.hasUpkeep());
			twn_hm.put("taxpercent", town.isTaxPercentage());
			twn_hm.put("open", town.isOpen());
			twn_hm.put("public", town.isPublic());
			twn_hm.put("admindisabledpvp", town.isAdminDisabledPVP());

			//twn_hm.put("townBlocks", utilSaveTownBlocks(new ArrayList<TownBlock>(town.getTownBlocks())));
			twn_hm.put("homeblock", town.hasHomeBlock() ? town.getHomeBlock().getWorld().getName() + "#" + Integer.toString(town.getHomeBlock().getX()) + "#" + Integer.toString(town.getHomeBlock().getZ()) : "");
			twn_hm.put("spawn", town.hasSpawn() ? town.getSpawn().getWorld().getName() + "#" + Double.toString(town.getSpawn().getX()) + "#" + Double.toString(town.getSpawn().getY()) + "#" + Double.toString(town.getSpawn().getZ()) + "#" + Float.toString(town.getSpawn().getPitch()) + "#" + Float.toString(town.getSpawn().getYaw()) : "");
			// Outpost Spawns
			String outpostArray = "";
			if (town.hasOutpostSpawn()) 
				for (Location spawn : new ArrayList<Location>(town.getAllOutpostSpawns())) {
					outpostArray += (spawn.getWorld().getName() + "#" + Double.toString(spawn.getX()) + "#" + Double.toString(spawn.getY()) + "#" + Double.toString(spawn.getZ()) + "#" + Float.toString(spawn.getPitch()) + "#" + Float.toString(spawn.getYaw()) + ";");
				}
			twn_hm.put("outpostSpawns", outpostArray);			// Jail Spawns
			String jailArray = "";
			if (town.hasJailSpawn()) 		
				for (Location spawn : new ArrayList<Location>(town.getAllJailSpawns())) {
					jailArray += (spawn.getWorld().getName() + "#" + Double.toString(spawn.getX()) + "#" + Double.toString(spawn.getY()) + "#" + Double.toString(spawn.getZ()) + "#" + Float.toString(spawn.getPitch()) + "#" + Float.toString(spawn.getYaw()) + ";");
				}				
			twn_hm.put("jailSpawns", jailArray);

			UpdateDB("TOWNS", twn_hm, Arrays.asList("name"));
			return true;

		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("SQL: Save Town unknown error");
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public synchronized boolean saveNation(Nation nation) {

		TownyMessaging.sendDebugMsg("Saving nation " + nation.getName());
		try {
			HashMap<String, Object> nat_hm = new HashMap<String, Object>();
			nat_hm.put("name", nation.getName());
			nat_hm.put("towns", StringMgmt.join(nation.getTowns(), "#"));
			nat_hm.put("capital", nation.hasCapital() ? nation.getCapital().getName() : "");
			nat_hm.put("tag", nation.hasTag() ? nation.getTag() : "");
			nat_hm.put("assistants", StringMgmt.join(nation.getAssistants(), "#"));
			nat_hm.put("allies", StringMgmt.join(nation.getAllies(), "#"));
			nat_hm.put("enemies", StringMgmt.join(nation.getEnemies(), "#"));
			nat_hm.put("taxes", nation.getTaxes());
			nat_hm.put("neutral", nation.isNeutral());

			UpdateDB("NATIONS", nat_hm, Arrays.asList("name"));

		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("SQL: Save Nation unknown error");
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public synchronized boolean saveWorld(TownyWorld world) {

		TownyMessaging.sendDebugMsg("Saving world " + world.getName());
		try {
			HashMap<String, Object> nat_hm = new HashMap<String, Object>();

			nat_hm.put("name", world.getName());

			// Towns
			nat_hm.put("towns", StringMgmt.join(world.getTowns(), "#"));
			// PvP
			nat_hm.put("pvp", world.isPVP());
			// Force PvP
			nat_hm.put("forcepvp", world.isForcePVP());
			// Claimable
			nat_hm.put("claimable", world.isClaimable());
			// has monster spawns
			nat_hm.put("worldmobs", world.hasWorldMobs());
			// force town mob spawns
			nat_hm.put("forcetownmobs", world.isForceTownMobs());
			// has firespread enabled
			nat_hm.put("firespread", world.isFire());
			nat_hm.put("forcefirespread", world.isForceFire());
			// has explosions enabled
			nat_hm.put("explosions", world.isExpl());
			nat_hm.put("forceexplosions", world.isForceExpl());
			// Enderman block protection
			nat_hm.put("endermanprotect", world.isEndermanProtect());
			// PlayerTrample
			nat_hm.put("disableplayertrample", world.isDisablePlayerTrample());
			// CreatureTrample
			nat_hm.put("disablecreaturetrample", world.isDisableCreatureTrample());

			// Unclaimed Zone Build
			nat_hm.put("unclaimedZoneBuild", world.getUnclaimedZoneBuild());
			// Unclaimed Zone Destroy
			nat_hm.put("unclaimedZoneDestroy", world.getUnclaimedZoneDestroy());
			// Unclaimed Zone Switch
			nat_hm.put("unclaimedZoneSwitch", world.getUnclaimedZoneSwitch());
			// Unclaimed Zone Item Use
			nat_hm.put("unclaimedZoneItemUse", world.getUnclaimedZoneItemUse());
			// Unclaimed Zone Name
			if (world.getUnclaimedZoneName() != null)
				nat_hm.put("unclaimedZoneName", world.getUnclaimedZoneName());

			// Unclaimed Zone Ignore Ids
			if (world.getUnclaimedZoneIgnoreMaterials() != null)
				nat_hm.put("unclaimedZoneIgnoreIds", StringMgmt.join(world.getUnclaimedZoneIgnoreMaterials(), "#"));

			// Using PlotManagement Delete
			nat_hm.put("usingPlotManagementDelete", world.isUsingPlotManagementDelete());
			// Plot Management Delete Ids
			if (world.getPlotManagementDeleteIds() != null)
				nat_hm.put("plotManagementDeleteIds", StringMgmt.join(world.getPlotManagementDeleteIds(), "#"));

			// Using PlotManagement Mayor Delete
			nat_hm.put("usingPlotManagementMayorDelete", world.isUsingPlotManagementMayorDelete());
			// Plot Management Mayor Delete
			if (world.getPlotManagementMayorDelete() != null)
				nat_hm.put("plotManagementMayorDelete", StringMgmt.join(world.getPlotManagementMayorDelete(), "#"));

			// Using PlotManagement Revert
			nat_hm.put("usingPlotManagementRevert", world.isUsingPlotManagementRevert());
			// Using PlotManagement Revert Speed
			//nat_hm.put("plotManagementRevertSpeed", world.getPlotManagementRevertSpeed());

			// Plot Management Ignore Ids
			if (world.getPlotManagementIgnoreIds() != null)
				nat_hm.put("plotManagementIgnoreIds", StringMgmt.join(world.getPlotManagementIgnoreIds(), "#"));

			// Using PlotManagement Wild Regen
			nat_hm.put("usingPlotManagementWildRegen", world.isUsingPlotManagementWildRevert());

			// Wilderness Explosion Protection entities
			if (world.getPlotManagementWildRevertEntities() != null)
				nat_hm.put("PlotManagementWildRegenEntities", StringMgmt.join(world.getPlotManagementWildRevertEntities(), "#"));

			// Using PlotManagement Wild Regen Delay
			nat_hm.put("plotManagementWildRegenSpeed", world.getPlotManagementWildRevertDelay());

			// Using Towny
			nat_hm.put("usingTowny", world.isUsingTowny());

			UpdateDB("WORLDS", nat_hm, Arrays.asList("name"));

		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("SQL: Save world unknown error (" + world.getName() + ")");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public synchronized boolean saveTownBlock(TownBlock townBlock) {

		TownyMessaging.sendDebugMsg("Saving town block " + townBlock.getWorld().getName() + ":" + townBlock.getX() + "x" + townBlock.getZ());
		try {
			HashMap<String, Object> tb_hm = new HashMap<String, Object>();
			tb_hm.put("world", townBlock.getWorld().getName());
			tb_hm.put("x", townBlock.getX());
			tb_hm.put("z", townBlock.getZ());
			tb_hm.put("name", townBlock.getName());
			tb_hm.put("price", townBlock.getPlotPrice());
			tb_hm.put("town", townBlock.getTown().getName());
			tb_hm.put("resident", (townBlock.hasResident()) ? townBlock.getResident().getName() : "");
			tb_hm.put("type", townBlock.getType().getId());
			tb_hm.put("outpost", townBlock.isOutpost());
			tb_hm.put("permissions", (townBlock.isChanged()) ? townBlock.getPermissions().toString().replaceAll(",", "#") : "");
			tb_hm.put("locked", townBlock.isLocked());
			tb_hm.put("changed", townBlock.isChanged());

			UpdateDB("TOWNBLOCKS", tb_hm, Arrays.asList("world", "x", "z"));

		} catch (Exception e) {
			TownyMessaging.sendErrorMsg("SQL: Save TownBlock unknown error");
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void deleteResident(Resident resident) {

		HashMap<String, Object> res_hm = new HashMap<String, Object>();
		res_hm.put("name", resident.getName());
		DeleteDB("RESIDENTS", res_hm);
	}

	@Override
	public void deleteTown(Town town) {

		HashMap<String, Object> twn_hm = new HashMap<String, Object>();
		twn_hm.put("name", town.getName());
		DeleteDB("TOWNS", twn_hm);
	}

	@Override
	public void deleteNation(Nation nation) {

		HashMap<String, Object> nat_hm = new HashMap<String, Object>();
		nat_hm.put("name", nation.getName());
		DeleteDB("NATIONS", nat_hm);
	}

	@Override
	public void deleteTownBlock(TownBlock townBlock) {

		HashMap<String, Object> twn_hm = new HashMap<String, Object>();
		twn_hm.put("world", townBlock.getWorld().getName());
		twn_hm.put("x", townBlock.getX());
		twn_hm.put("z", townBlock.getZ());
		DeleteDB("TOWNBLOCKS", twn_hm);

	}

	@Override
	public synchronized void backup() throws IOException {

		TownyMessaging.sendMsg("Performing backup");
		TownyMessaging.sendMsg("***** Warning *****");
		TownyMessaging.sendMsg("***** Only Snapshots and Regen files will be backed up");
		TownyMessaging.sendMsg("***** Make sure you schedule a backup in MySQL too!!!");
		String backupType = TownySettings.getFlatFileBackupType();
		if (!backupType.equalsIgnoreCase("none")) {

			TownyLogger.shutDown();

			long t = System.currentTimeMillis();
			String newBackupFolder = rootFolder + FileMgmt.fileSeparator() + "backup" + FileMgmt.fileSeparator() + new SimpleDateFormat("yyyy-MM-dd HH-mm").format(t) + " - " + Long.toString(t);
			FileMgmt.checkFolders(new String[] { rootFolder,
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
				throw new IOException("[Towny] Unsupported flatfile backup type (" + backupType + ")");
			}
			plugin.setupLogger();
		}
	}

	@Override
	public boolean cleanup() {

		/*
		 *  Attempt to get a database connection.
		 */
		if (!getContext())
			return false;

		SQL_Schema.cleanup(cntx, db_name);

		return true;
	}

	/*
	 * Save keys
	 */

	@Override
	public boolean saveTownBlockList() {

		return true;
	}

	@Override
	public boolean saveResidentList() {

		return true;
	}

	@Override
	public boolean saveTownList() {

		return true;
	}

	@Override
	public boolean saveNationList() {

		return true;
	}

	@Override
	public boolean saveWorldList() {

		return true;
	}

}
