import data.MongoDatabase;
import data.QuestDatabase;
import quest.Quest;
import quest.QuestManager;

public class QuestAPITest {
    public static void main(String[] args) {
        // Connect to MongoDB
        QuestDatabase questDatabase = new MongoDatabase("mongodb+srv://joeecodes:123123!!!@devroom.q9bqsla.mongodb.net/?retryWrites=true&w=majority", "questdb", "quests");

        // Create walking and block breaking quests
        Quest walkingQuest = new Quest("walking", 10, "Walking Boots", (playerId, questId, newLevel) -> {
            System.out.println("Player " + playerId + " leveled up in quest " + questId + " to level " + newLevel);
            // Reward the player with walking boots or something similar
        });

        Quest blockBreakingQuest = new Quest("blockBreaking", 5, "Mining Helmet", (playerId, questId, newLevel) -> {
            System.out.println("Player " + playerId + " leveled up in quest " + questId + " to level " + newLevel);
            // Reward the player with a mining helmet or something similar
        });

        blockBreakingQuest.executeLevelUpHook("player123", 2);

        // Initialize quest manager
        QuestManager questManager = new QuestManager(questDatabase);

        // Save progress for a player in walking quest
        questManager.saveQuestProgress("player123", blockBreakingQuest, 1, 50)
                .thenAccept(System.out::println)
                .join();

        // Get progress for a player in walking quest
        questManager.getQuestProgress("player123", blockBreakingQuest)
                .thenAccept(System.out::println)
                .join();

        // Update progress for a player in walking quest
        questManager.updateQuestProgress("player123", blockBreakingQuest, 10, 100)
                .thenAccept(System.out::println)
                .join();

        // Simulate player leaving
        questManager.playerLeft("player123");
    }
}