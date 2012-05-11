/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.palmergames.bukkit.towny;

import com.palmergames.bukkit.towny.util.TestInstanceCreator;
import junit.framework.Assert;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Towny.class, PluginDescriptionFile.class, PluginCommand.class})
public class MainTests {
    TestInstanceCreator creator;
    Server mockServer;
    CommandSender mockCommandSender;

    @Before
    public void setUp() throws Exception {
        creator = new TestInstanceCreator();
        assertTrue(creator.setUp());
        mockServer = creator.getServer();
        mockCommandSender = creator.getCommandSender();
    }

    @After
    public void tearDown() throws Exception {
        creator.tearDown();
    }

    @Test
    public void testCommands() {
        // Pull a plugin instance from the server.
        Plugin plugin = mockServer.getPluginManager().getPlugin("Towny");
        Towny towny = (Towny) plugin;

        // Make sure plugin is not null
        assertNotNull(plugin);

        // Make sure plugin is enabled
        assertTrue(plugin.isEnabled());

        // Make a fake server folder to fool towny into thinking a world folder exists.
        File serverDirectory = new File(TestInstanceCreator.serverDirectory, "world");
        serverDirectory.mkdirs();

        assertFalse(TownySettings.getDebug());
        TownySettings.setDebug(true);
        assertTrue(TownySettings.getDebug());

        // Initialize a fake command
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("towny");
        CommandExecutor townyCommand = towny.getCommand(mockCommand.getName()).getExecutor();
        // Send a test command
        String[] cmdArgs = new String[]{};
        townyCommand.onCommand(mockCommandSender, mockCommand, mockCommand.getName(), cmdArgs);


        mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("town");
        CommandExecutor townCommand = towny.getCommand(mockCommand.getName()).getExecutor();
        // Send a test command
        cmdArgs = new String[]{};
        townCommand.onCommand(mockCommandSender, mockCommand, mockCommand.getName(), cmdArgs);


        mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("townyadmin");
        CommandExecutor townyAdminCommand = towny.getCommand(mockCommand.getName()).getExecutor();
        // Send a test command
        cmdArgs = new String[]{"reload"};
        townyAdminCommand.onCommand(mockCommandSender, mockCommand, mockCommand.getName(), cmdArgs);
    }
}
