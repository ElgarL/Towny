package com.palmergames.bukkit.towny.object;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.exception.EconomyException;
import com.palmergames.bukkit.towny.exception.NotRegisteredException;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyLogger;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.util.StringMgmt;
import net.milkbowl.vault.economy.Economy;

public class TownyEconomyObject extends TownyObject {
    private static Towny plugin;

    private static final String townAccountPrefix = "town-";

    private static final String nationAccountPrefix = "nation-";

    public static void setPlugin(Towny plugin) {
        TownyEconomyObject.plugin = plugin;
    }

    /**
     * Tries to pay from the players main bank account first, if it fails try
     * their holdings
     *
     * @param n
     * @return if successfully payed amount to 'server'.
     * @throws EconomyException
     */
    public boolean pay(double n, String reason) throws EconomyException {
        boolean payed = _pay(n);
        if (payed) {
            TownyLogger.logMoneyTransaction(this, n, null, reason);
        }
        return payed;
    }

    public boolean pay(double n) throws EconomyException {
        return pay(n, null);
    }

    private Economy prepareEconomy() {
        Economy economy = plugin.getVaultHelper().getEconomy();
        if (!economy.hasAccount(getAccountName())) {
            economy.createPlayerAccount(getAccountName());
        }
        return economy;
    }

    private boolean _pay(double n) throws EconomyException {
        if (canPayFromHoldings(n)) {
            TownyMessaging.sendDebugMsg("Can Pay: " + n);
            prepareEconomy().withdrawPlayer(getAccountName(), n);
            return true;
        }
        return false;
    }

    /**
     * When collecting money add it to the Accounts bank
     *
     * @param n
     * @throws EconomyException
     */
    public void collect(double n, String reason) throws EconomyException {
        _collect(n);
        TownyLogger.logMoneyTransaction(null, n, this, reason);
    }

    public void collect(double n) throws EconomyException {
        collect(n, null);
    }

    private void _collect(double n) throws EconomyException {
        prepareEconomy().depositPlayer(getAccountName(), n);
    }

    /**
     * When one account is paying another account(Taxes/Plot Purchasing)
     *
     * @param n
     * @param collector
     * @return if successfully payed amount to collector.
     * @throws EconomyException
     */
    public boolean payTo(double n, TownyEconomyObject collector, String reason) throws EconomyException {
        boolean paid = _payTo(n, collector);
        if (paid) {
            TownyLogger.logMoneyTransaction(this, n, collector, reason);
        }
        return paid;
    }

    public boolean payTo(double n, TownyEconomyObject collector) throws EconomyException {
        return payTo(n, collector, null);
    }

    private boolean _payTo(double n, TownyEconomyObject collector) throws EconomyException {
        if (_pay(n)) {
            collector._collect(n);
            return true;
        } else {
            return false;
        }
    }

    public String getAccountName() {
        // TODO: Make this less hard coded.
        if (this instanceof Nation) {
            return StringMgmt.trimMaxLength(nationAccountPrefix + getName(), 32);
        } else if (this instanceof Town) {
            return StringMgmt.trimMaxLength(townAccountPrefix + getName(), 32);
        } else {
            return getName();
        }
    }

    public double getHoldingBalance() {
        return prepareEconomy().getBalance(getAccountName());
    }

    public boolean canPayFromHoldings(double n) throws EconomyException {
        return (getHoldingBalance() - n >= 0);
    }

    /*
     * Used To Get Balance of Players holdings in String format for printing
     */
    public String getHoldingFormattedBalance() {
        double balance = getHoldingBalance();
        return getFormattedBalance(balance);
    }

    public static String getFormattedBalance(double balance) {
        return plugin.getVaultHelper().getEconomy().format(balance);
    }

    public void removeAccount() {
        setBalance(0);
    }

    public void setBalance(double amount) {
        Economy eco = prepareEconomy();
        double deposit = amount - getHoldingBalance();
        if (deposit > 0) {
            eco.depositPlayer(getAccountName(), deposit);
        } else if (deposit < 0) {
            eco.withdrawPlayer(getAccountName(), -deposit);
        }
    }

}
