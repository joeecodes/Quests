package me.joehosten.quests.data;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.concurrent.CompletableFuture;

public class MongoDatabase implements QuestDatabase {
    private final MongoClient client;
    private final com.mongodb.client.MongoDatabase database;
    private final MongoCollection<Document> collection;

    public MongoDatabase(String connectionString, String databaseName, String collectionName) {
        this.client = MongoClients.create(connectionString);
        this.database = client.getDatabase(databaseName);
        this.collection = database.getCollection(collectionName);
    }

    @Override
    public CompletableFuture<Document> saveQuestProgress(String playerId, String questId, int level, int progress) {
        Document questProgress = new Document("playerId", playerId)
                .append("questId", questId)
                .append("level", level)
                .append("progress", progress);

        return CompletableFuture.supplyAsync(() -> {
            collection.insertOne(questProgress);
            return questProgress;
        });
    }

    @Override
    public CompletableFuture<Document> getQuestProgress(String playerId, String questId) {
        return CompletableFuture.supplyAsync(() ->
                collection.find(new Document("playerId", playerId).append("questId", questId)).first());
    }

    @Override
    public CompletableFuture<Document> updateQuestProgress(String playerId, String questId, int level, int progress) {
        return CompletableFuture.supplyAsync(() ->
                collection.findOneAndUpdate(
                        new Document("playerId", playerId).append("questId", questId),
                        new Document("$set", new Document("level", level).append("progress", progress))));
    }
}
