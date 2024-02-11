package data;

import org.bson.Document;

import java.util.concurrent.CompletableFuture;

public interface QuestDatabase {
    CompletableFuture<Document> saveQuestProgress(String playerId, String questId, int level, int progress);

    CompletableFuture<Document> getQuestProgress(String playerId, String questId);

    CompletableFuture<Document> updateQuestProgress(String playerId, String questId, int level, int progress);
}