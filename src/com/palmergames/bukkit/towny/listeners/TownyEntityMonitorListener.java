package com.palmergames.bukkit.towny.listeners;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.exceptions.EconomyException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.*;
import com.palmergames.bukkit.towny.permissions.PermissionNodes;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import com.palmergames.bukkit.towny.war.eventwar.War;
import com.palmergames.bukkit.towny.war.eventwar.WarSpoils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

//import org.bukkit.event.entity.EntityDamageEvent;

/**
 * @author Shade & ElgarL
 * 
 *         This class handles Player deaths and associated costs.
 * 
 */
public class TownyEntityMonitorListener implements Listener {

	private final Towny plugin;

	public TownyEntityMonitorListener(Towny instance) {

		plugin = instance;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event) throws NotRegisteredException {

		Entity defenderEntity = event.getEntity();

		TownyWorld World = null;

		try {
			World = TownyUniverse.getDataSource().getWorld(defenderEntity.getLocation().getWorld().getName());
			if (!World.isUsingTowny())
				return;

		} catch (NotRegisteredException e) {
			// World not registered with Towny.
			return;
		}

		// Was this a player death?
		if (defenderEntity instanceof Player) {

			// Killed by another entity?			
			if (defenderEntity.getLastDamageCause() instanceof EntityDamageByEntityEvent) {

				EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) defenderEntity.getLastDamageCause();

				Entity attackerEntity = damageEvent.getDamager();
				Player defenderPlayer = (Player) defenderEntity;
				Player attackerPlayer = null;
				Resident attackerResident = null;
				Resident defenderResident = null;

				try {
					defenderResident = TownyUniverse.getDataSource().getResident(defenderPlayer.getName());
				} catch (NotRegisteredException e) {
					return;
				}

				// Was this a missile?
				if (attackerEntity instanceof Projectile) {
					Projectile projectile = (Projectile) attackerEntity;
					if (projectile.getShooter() instanceof Player) {
						attackerPlayer = (Player) projectile.getShooter();

						try {
							attackerResident = TownyUniverse.getDataSource().getResident(attackerPlayer.getName());
						} catch (NotRegisteredException e) {
						}
					}

				} else if (attackerEntity instanceof Player) {
					// This was a player kill
					attackerPlayer = (Player) attackerEntity;
					try {
						attackerResident = TownyUniverse.getDataSource().getResident(attackerPlayer.getName());
					} catch (NotRegisteredException e) {
					}
				}

				/*
				 * If attackerPlayer or attackerResident are null at this point
				 * it was a natural death, not PvP.
				 */
				deathPayment(attackerPlayer, defenderPlayer, attackerResident, defenderResident);			
				if (attackerPlayer instanceof Player) {
					isJailingAttackers(attackerPlayer, defenderPlayer, attackerResident, defenderResident);
					
				}

				if (TownyUniverse.isWarTime())
					wartimeDeathPoints(attackerPlayer, defenderPlayer, attackerResident, defenderResident);

			}
		}

	}

	private void wartimeDeathPoints(Player attackerPlayer, Player defenderPlayer, Resident attackerResident, Resident defenderResident) {

		if (attackerPlayer != null && defenderPlayer != null && TownyUniverse.isWarTime())
			try {
				if (CombatUtil.isAlly(attackerPlayer.getName(), defenderPlayer.getName()))
					return;

				if (attackerResident.hasTown() && War.isWarringTown(attackerResident.getTown()) && defenderResident.hasTown() && War.isWarringTown(defenderResident.getTown())){
					if (TownySettings.isRemovingOnMonarchDeath())
						monarchDeath(attackerPlayer, defenderPlayer, attackerResident, defenderResident);

					if (TownySettings.getWarPointsForKill() > 0){
						plugin.getTownyUniverse().getWarEvent().townScored(defenderResident.getTown(), attackerResident.getTown(), defenderPlayer, attackerPlayer, TownySettings.getWarPointsForKill());
					}
				}
			} catch (NotRegisteredException e) {
			}
	}

	private void monarchDeath(Player attackerPlayer, Player defenderPlayer, Resident attackerResident, Resident defenderResident) {

		War warEvent = plugin.getTownyUniverse().getWarEvent();
		try {

			Nation defenderNation = defenderResident.getTown().getNation();
			Town defenderTown = defenderResident.getTown();
			if (warEvent.isWarringNation(defenderNation) && defenderResident.isKing()){
				TownyMessaging.sendGlobalMessage(TownySettings.getWarTimeKingKilled(defenderNation));
				if (attackerResident != null)
					warEvent.remove(attackerResident.getTown(), defenderNation);
			}else if (warEvent.isWarringNation(defenderNation) && defenderResident.isMayor()) {
				TownyMessaging.sendGlobalMessage(TownySettings.getWarTimeMayorKilled(defenderTown));
				if (attackerResident != null)
					warEvent.remove(attackerResident.getTown(), defenderResident.getTown());
			}
		} catch (NotRegisteredException e) {
		}
	}

	public void deathPayment(Player attackerPlayer, Player defenderPlayer, Resident attackerResident, Resident defenderResident) throws NotRegisteredException {

		if (attackerPlayer != null && TownyUniverse.isWarTime() && TownySettings.getWartimeDeathPrice() > 0 ) {
			try {
				if (attackerResident == null)
					throw new NotRegisteredException(String.format("The attackingResident %s has not been registered.", attackerPlayer.getName()));

				double price = TownySettings.getWartimeDeathPrice();
				double townPrice = 0;
				if (!defenderResident.canPayFromHoldings(price)) {
					townPrice = price - defenderResident.getHoldingBalance();
					price = defenderResident.getHoldingBalance();
				}

				if (price > 0) {
					if (!TownySettings.isEcoClosedEconomyEnabled()){
						defenderResident.payTo(price, attackerResident, "Death Payment (War)");
						TownyMessaging.sendMsg(attackerPlayer, String.format(TownySettings.getLangString("msg_you_robbed_player"), defenderResident.getName(), TownyEconomyHandler.getFormattedBalance(price)));
						TownyMessaging.sendMsg(defenderPlayer, String.format(TownySettings.getLangString("msg_player_robbed_you"), attackerResident.getName(), TownyEconomyHandler.getFormattedBalance(price)));
					} else {
						defenderResident.pay(price, "Death Payment (War)");
						TownyMessaging.sendMsg(defenderPlayer, String.format(TownySettings.getLangString("msg_you_lost_money"), TownyEconomyHandler.getFormattedBalance(price)));
					}
				}

				// Resident doesn't have enough funds.
				if (townPrice > 0) {
					Town town = defenderResident.getTown();
					if (!town.canPayFromHoldings(townPrice)) {
						// Town doesn't have enough funds.
						townPrice = town.getHoldingBalance();
						try {
							plugin.getTownyUniverse().getWarEvent().remove(attackerResident.getTown(), town);
						} catch (NotRegisteredException e) {
							plugin.getTownyUniverse().getWarEvent().remove(town);
						}
					} else if (!TownySettings.isEcoClosedEconomyEnabled()){
						TownyMessaging.sendTownMessage(town, String.format(TownySettings.getLangString("msg_player_couldnt_pay_player_town_bank_paying_instead"), defenderResident.getName(), attackerResident.getName(), townPrice));
						town.payTo(townPrice, attackerResident, String.format("Death Payment (War) (%s couldn't pay)", defenderResident.getName()));
					} else {
						TownyMessaging.sendTownMessage(town, String.format(TownySettings.getLangString("msg_player_couldnt_pay_player_town_bank_paying_instead"), defenderResident.getName(), attackerResident.getName(), townPrice));
						town.pay(townPrice, String.format("Death Payment (War) (%s couldn't pay)", defenderResident.getName()));
					}
				}
			} catch (NotRegisteredException e) {
			} catch (EconomyException e) {
				TownyMessaging.sendErrorMsg(attackerPlayer, TownySettings.getLangString("msg_err_wartime_could_not_take_deathfunds"));
				TownyMessaging.sendErrorMsg(defenderPlayer, TownySettings.getLangString("msg_err_wartime_could_not_take_deathfunds"));
			}			
		} else if (TownySettings.isChargingDeath() && ((TownySettings.isDeathPricePVPOnly() && attackerPlayer != null) || (!TownySettings.isDeathPricePVPOnly() && attackerPlayer == null))  ) {
			if (TownyUniverse.getTownBlock(defenderPlayer.getLocation()) != null) {
				if (TownyUniverse.getTownBlock(defenderPlayer.getLocation()).getType() == TownBlockType.ARENA || TownyUniverse.getTownBlock(defenderPlayer.getLocation()).getType() == TownBlockType.JAIL)
					return;				
			}
			if (defenderResident.isJailed())
				return;

			double total = 0.0;

			try {
				if (TownySettings.getDeathPrice() > 0) {

					double price = TownySettings.getDeathPrice();

					if (!TownySettings.isDeathPriceType()) {
						price = defenderResident.getHoldingBalance() * price;
					}

					if (!defenderResident.canPayFromHoldings(price))
						price = defenderResident.getHoldingBalance();

					if (attackerResident == null) {
						if (!TownySettings.isEcoClosedEconomyEnabled())
							defenderResident.payTo(price, new WarSpoils(), "Death Payment");
						else 
							defenderResident.pay(price, "Death Payment");
					} else {
						if (!TownySettings.isEcoClosedEconomyEnabled())
							defenderResident.payTo(price, attackerResident, "Death Payment");
						else 
							defenderResident.pay(price, "Death Payment");
					}
					total = total + price;

					TownyMessaging.sendMsg(defenderPlayer, String.format(TownySettings.getLangString("msg_you_lost_money_dying"), TownyEconomyHandler.getFormattedBalance(price)));
				}
			} catch (EconomyException e) {
				TownyMessaging.sendErrorMsg(defenderPlayer, TownySettings.getLangString("msg_err_could_not_take_deathfunds"));
			}

			try {
				if (TownySettings.getDeathPriceTown() > 0) {

					double price = TownySettings.getDeathPriceTown();

					if (!TownySettings.isDeathPriceType()) {
						price = defenderResident.getTown().getHoldingBalance() * price;
					}

					if (!defenderResident.getTown().canPayFromHoldings(price))
						price = defenderResident.getTown().getHoldingBalance();

					if (attackerResident == null) {
						if (!TownySettings.isEcoClosedEconomyEnabled())
							defenderResident.getTown().payTo(price, new WarSpoils(), "Death Payment Town");
						else 
							defenderResident.getTown().pay(price, "Death Payment Town");
					} else {
						if (!TownySettings.isEcoClosedEconomyEnabled())
							defenderResident.getTown().payTo(price, attackerResident, "Death Payment Town");
						else 
							defenderResident.getTown().pay(price, "Death Payment Town");
					}
					total = total + price;

					TownyMessaging.sendTownMessagePrefixed(defenderResident.getTown(), String.format(TownySettings.getLangString("msg_your_town_lost_money_dying"), TownyEconomyHandler.getFormattedBalance(price)));
				}
			} catch (EconomyException e) {
				TownyMessaging.sendErrorMsg(defenderPlayer, TownySettings.getLangString("msg_err_couldnt_take_deathfunds"));
			} catch (NotRegisteredException e) {
				TownyMessaging.sendErrorMsg(defenderPlayer, TownySettings.getLangString("msg_err_couldnt_take_town_deathfunds"));
			}

			try {
				if (TownySettings.getDeathPriceNation() > 0) {
					double price = TownySettings.getDeathPriceNation();

					if (!TownySettings.isDeathPriceType()) {
						price = defenderResident.getTown().getNation().getHoldingBalance() * price;
					}

					if (!defenderResident.getTown().getNation().canPayFromHoldings(price))
						price = defenderResident.getTown().getNation().getHoldingBalance();

					if (attackerResident == null) {
						if (!TownySettings.isEcoClosedEconomyEnabled())
							defenderResident.getTown().getNation().payTo(price, new WarSpoils(), "Death Payment Nation");
						else 
							defenderResident.getTown().getNation().pay(price, "Death Payment Nation");
					} else {
						if (!TownySettings.isEcoClosedEconomyEnabled())
							defenderResident.getTown().getNation().payTo(price, attackerResident, "Death Payment Nation");
						else 
							defenderResident.getTown().getNation().pay(price, "Death Payment Nation");
					}
					total = total + price;

					TownyMessaging.sendNationMessagePrefixed(defenderResident.getTown().getNation(), String.format(TownySettings.getLangString("msg_your_nation_lost_money_dying"), TownyEconomyHandler.getFormattedBalance(price)));
				}
			} catch (EconomyException e) {
				TownyMessaging.sendErrorMsg(defenderPlayer, TownySettings.getLangString("msg_err_couldnt_take_deathfunds"));
			} catch (NotRegisteredException e) {
				TownyMessaging.sendErrorMsg(defenderPlayer, TownySettings.getLangString("msg_err_couldnt_take_nation_deathfunds"));
			}

			if (attackerResident != null && !TownySettings.isEcoClosedEconomyEnabled()) {
				TownyMessaging.sendMsg(attackerResident, String.format(TownySettings.getLangString("msg_you_gained_money_for_killing"),TownyEconomyHandler.getFormattedBalance(total), defenderPlayer.getName()));

			}
		}
	}
	
	public void isJailingAttackers(Player attackerPlayer, Player defenderPlayer, Resident attackerResident, Resident defenderResident) throws NotRegisteredException {
		if (TownySettings.isJailingAttackingEnemies() || TownySettings.isJailingAttackingOutlaws()) {
			Location loc = defenderPlayer.getLocation();
			if (!TownyUniverse.getDataSource().getWorld(defenderPlayer.getLocation().getWorld().getName()).isUsingTowny())
				return;
			if (TownyUniverse.getTownBlock(defenderPlayer.getLocation()) == null)
				return;
			if (TownyUniverse.getTownBlock(defenderPlayer.getLocation()).getType() == TownBlockType.ARENA)
				return;
			if (defenderResident.isJailed()) {
				if (TownyUniverse.getTownBlock(defenderPlayer.getLocation()).getType() != TownBlockType.JAIL) {
					TownyMessaging.sendGlobalMessage(String.format(TownySettings.getLangString("msg_killed_attempting_to_escape_jail"), defenderPlayer.getName()));
					return;
				}							
				return;			
			}

			// Try outlaw jailing first.
			if (TownySettings.isJailingAttackingOutlaws()) {
				Town attackerTown = null;
				try {					
					attackerTown = attackerResident.getTown();
				} catch (NotRegisteredException e1) {				
				}
				
				if (attackerTown.hasOutlaw(defenderResident)) {

					if (TownyUniverse.getTownBlock(loc) == null)
						return;

					try {
						if (TownyUniverse.getTownBlock(loc).getTown().getName() != attackerResident.getTown().getName()) 
							return;
					} catch (NotRegisteredException e1) {
						e1.printStackTrace();
					}

					if (!attackerTown.hasJailSpawn()) 
						return;

					if (!TownyUniverse.isWarTime()) {
						if (!TownyUniverse.getPermissionSource().testPermission(attackerPlayer, PermissionNodes.TOWNY_OUTLAW_JAILER.getNode()))
							return;
						defenderResident.setJailed(defenderPlayer, 1, attackerTown);
						return;
						
					} else {
						TownBlock jailBlock = null;
						Integer index = 1;
						for (Location jailSpawn : attackerTown.getAllJailSpawns()) {
							try {
								jailBlock = TownyUniverse.getDataSource().getWorld(loc.getWorld().getName()).getTownBlock(Coord.parseCoord(jailSpawn));
							} catch (TownyException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
							if (War.isWarZone(jailBlock.getWorldCoord())) {
								defenderResident.setJailed(defenderPlayer, index, attackerTown);
								try {
									TownyMessaging.sendTitleMessageToResident(defenderResident, "You have been jailed", "Run to the wilderness or wait for a jailbreak.");
								} catch (TownyException e) {
								}
								return;
							}
							index++;
							TownyMessaging.sendDebugMsg("A jail spawn was skipped because the plot has fallen in war.");
						}
						TownyMessaging.sendTownMessage(attackerTown, TownySettings.getWarPlayerCannotBeJailedPlotFallenMsg());
						return;
					}
				}
			}
			// Try enemy jailing second
			if (!attackerResident.hasTown()) { 
				return;
			} else {
				Town town = null;
				try {					
					town = attackerResident.getTown();
				} catch (NotRegisteredException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}			
			
				if (TownyUniverse.getTownBlock(loc) == null)
					return;
					
				try {
					if (TownyUniverse.getTownBlock(loc).getTown().getName() != attackerResident.getTown().getName()) 
						return;
				} catch (NotRegisteredException e1) {
					e1.printStackTrace();
				}
				if (!attackerResident.hasNation() || !defenderResident.hasNation()) 
					return;
				try {
					if (!attackerResident.getTown().getNation().getEnemies().contains(defenderResident.getTown().getNation())) 
						return;
				} catch (NotRegisteredException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}								
				if (!town.hasJailSpawn()) 
					return;
				
				if (!TownyUniverse.isWarTime()) {
					defenderResident.setJailed(defenderPlayer, 1, town);
				} else {
					TownBlock jailBlock = null;
					Integer index = 1;
					for (Location jailSpawn : town.getAllJailSpawns()) {
						try {
							jailBlock = TownyUniverse.getDataSource().getWorld(loc.getWorld().getName()).getTownBlock(Coord.parseCoord(jailSpawn));
						} catch (TownyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						if (War.isWarZone(jailBlock.getWorldCoord())) {
							defenderResident.setJailed(defenderPlayer, index, town);
							try {
								TownyMessaging.sendTitleMessageToResident(defenderResident, "You have been jailed", "Run to the wilderness or wait for a jailbreak.");
							} catch (TownyException e) {
							}
							return;
						}
						index++;
						TownyMessaging.sendDebugMsg("A jail spawn was skipped because the plot has fallen in war.");
					}
					TownyMessaging.sendTownMessage(town, TownySettings.getWarPlayerCannotBeJailedPlotFallenMsg());
					return;
				}
			}
		}
	}
}
