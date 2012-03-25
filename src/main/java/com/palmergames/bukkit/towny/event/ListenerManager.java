/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.palmergames.bukkit.towny.event;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.townywar.listener.TownyWarBlockListener;
import com.palmergames.bukkit.townywar.listener.TownyWarCustomListener;
import com.palmergames.bukkit.townywar.listener.TownyWarEntityListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

/**
 *
 * @author simplyianm
 */
public class ListenerManager {
    private final Towny plugin;

    private final TownyPlayerListener playerListener;

    private final TownyBlockListener blockListener;

    private final TownyEntityListener entityListener;

    private final TownyWeatherListener weatherListener;

    private final TownyEntityMonitorListener entityMonitorListener;

    private final TownyWorldListener worldListener;

    private final TownyWarBlockListener townyWarBlockListener;

    private final TownyWarCustomListener customListener;

    private final TownyWarEntityListener townyWarEntityListener;

    private final TownyQuestionListener questionListener;

    public ListenerManager(Towny plugin) {
        this.plugin = plugin;

        playerListener = new TownyPlayerListener(plugin);
        blockListener = new TownyBlockListener(plugin);
        entityListener = new TownyEntityListener(plugin);
        weatherListener = new TownyWeatherListener(plugin);
        entityMonitorListener = new TownyEntityMonitorListener(plugin);
        worldListener = new TownyWorldListener(plugin);
        townyWarBlockListener = new TownyWarBlockListener(plugin);
        customListener = new TownyWarCustomListener(plugin);
        townyWarEntityListener = new TownyWarEntityListener(plugin);
        questionListener = new TownyQuestionListener(plugin);
    }

    private void registerEvents() {

        final PluginManager pluginManager = Bukkit.getPluginManager();

        // Have War Events get launched before regular events.
        pluginManager.registerEvents(townyWarBlockListener, plugin);
        pluginManager.registerEvents(townyWarEntityListener, plugin);

        // Manage player deaths and death payments
        pluginManager.registerEvents(entityMonitorListener, plugin);
        pluginManager.registerEvents(weatherListener, plugin);
        pluginManager.registerEvents(customListener, plugin);
        pluginManager.registerEvents(worldListener, plugin);

        // Always register these events.
        pluginManager.registerEvents(playerListener, plugin);
        pluginManager.registerEvents(blockListener, plugin);
        pluginManager.registerEvents(entityListener, plugin);
        pluginManager.registerEvents(questionListener, plugin);

    }

}
