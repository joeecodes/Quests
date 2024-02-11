package quest;

import data.QuestDatabase;
import org.bson.Document;

import java.util.Map;
import java.util.concurrent.*;

public class QuestManager {
    private final QuestDatabase questDatabase;
    private final Map<String, Map<String, Document>> questCache; // playerId -> questId -> progress

    public QuestManager(QuestDatabase questsDatabase) {
        this.questDatabase = questsDatabase;
        this.questCache = new ConcurrentHashMap<>();
        startCacheUpdater();
    }

    private void startCacheUpdater() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::updateCacheToDatabase, 0, 60, TimeUnit.SECONDS); // Update cache every 60 seconds
    }

    private void updateCacheToDatabase() {
        System.out.println("saving data");
        for (Map.Entry<String, Map<String, Document>> entry : questCache.entrySet()) {
            String playerId = entry.getKey();
            Map<String, Document> playerQuests = entry.getValue();
            for (Map.Entry<String, Document> questEntry : playerQuests.entrySet()) {
                String questId = questEntry.getKey();
                Document progress = questEntry.getValue();
                questDatabase.updateQuestProgress(playerId, questId, progress.getInteger("level"), progress.getInteger("progress"))
                        .thenAccept(updatedProgress -> playerQuests.put(questId, updatedProgress));
            }
        }
    }

    public CompletableFuture<Document> saveQuestProgress(String playerId, Quest quest, int level, int progress) {
        Map<String, Document> playerQuests = questCache.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>());
        Document questProgress = new Document("level", level).append("progress", progress);
        playerQuests.put(quest.getId(), questProgress);
        return questDatabase.saveQuestProgress(playerId, quest.getId(), level, progress);
    }

    public CompletableFuture<Document> getQuestProgress(String playerId, Quest quest) {
        Map<String, Document> playerQuests = questCache.getOrDefault(playerId, new ConcurrentHashMap<>());
        if (!playerQuests.containsKey(quest.getId())) {
            return questDatabase.getQuestProgress(playerId, quest.getId())
                    .thenApply(progress -> {
                        playerQuests.put(quest.getId(), progress);
                        return progress;
                    });
        } else {
            return CompletableFuture.completedFuture(playerQuests.get(quest.getId()));
        }
    }

    public CompletableFuture<Document> updateQuestProgress(String playerId, Quest quest, int level, int progress) {
        Map<String, Document> playerQuests = questCache.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>());
        Document questProgress = new Document("level", level).append("progress", progress);
        playerQuests.put(quest.getId(), questProgress);
        CompletableFuture<Document> result = questDatabase.updateQuestProgress(playerId, quest.getId(), level, progress);
        result.thenAccept(updatedProgress -> playerQuests.put(quest.getId(), updatedProgress));
        return result;
    }

    public void playerLeft(String playerId) {
        Map<String, Document> playerQuests = questCache.get(playerId);
        if (playerQuests != null) {
            for (Map.Entry<String, Document> entry : playerQuests.entrySet()) {
                String questId = entry.getKey();
                Document progress = entry.getValue();
                questDatabase.updateQuestProgress(playerId, questId, progress.getInteger("level"), progress.getInteger("progress"))
                        .thenAccept(updatedProgress -> playerQuests.put(questId, updatedProgress));
            }
            questCache.remove(playerId);
        }
    }
}
