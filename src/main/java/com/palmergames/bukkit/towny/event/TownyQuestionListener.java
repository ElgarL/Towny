package com.palmergames.bukkit.towny.event;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.question.QuestionManager;
import java.util.logging.Level;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class TownyQuestionListener implements Listener {
    private Towny plugin;

    private QuestionManager questionManager;

    public TownyQuestionListener(Towny plugin) {
        this.plugin = plugin;
        this.questionManager = plugin.getQuestionManager();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }
        String command = event.getMessage().substring(1);
        Player player = event.getPlayer();
        try {
            Runnable reaction = this.questionManager.answerFirstQuestion(player.getName(), command);
            int id = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, reaction);
            if (id == -1) {
                plugin.getLogger().log(Level.SEVERE, "Could not schedule reaction to " + player.getName() + "'s question.");
            }
            this.questionManager.removeFirstQuestion(player.getName());
            event.setCancelled(true);
        } catch (Exception localException) {
        }
    }

}