package me.joehosten.quests;

import me.joehosten.quests.data.MongoDatabase;
import me.joehosten.quests.data.QuestDatabase;
import me.joehosten.quests.quest.QuestManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Quests extends JavaPlugin {
    private QuestManager questManager;

    @Override
    public void onEnable() {
        // Connect to MongoDB
        QuestDatabase questDatabase = new MongoDatabase("mongodb://localhost:27017", "quests", "progress");

        // Initialize quest manager
        questManager = new QuestManager(questDatabase);

        getLogger().log(Level.INFO, "Quests plugin enabled.");
    }

    @Override
    public void onDisable() {
        // Save progress for all players before shutdown
        Bukkit.getOnlinePlayers().forEach(player -> questManager.playerLeft(player.getUniqueId().toString()));

        getLogger().log(Level.INFO, "Quests plugin disabled.");
    }
}
