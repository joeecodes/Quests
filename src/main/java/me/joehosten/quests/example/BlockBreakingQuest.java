package me.joehosten.quests.example;

import me.joehosten.quests.quest.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakingQuest extends Quest implements Listener {
    public BlockBreakingQuest() {
        super("blockbreaking", 5, "diamond", ((playerId, questId, newLevel) -> {
            System.out.println("Player " + playerId + " has reached level " + newLevel + " in quest " + questId);
        }));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        // for proof of concept, this will level up the quest every time a block is broken

        this.executeLevelUpHook(p.getUniqueId().toString(), 2);
        // where 2 is, this can be some logic to get the current level from data and increment it
    }
}
