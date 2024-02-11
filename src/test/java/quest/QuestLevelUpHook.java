package quest;

@FunctionalInterface
public interface QuestLevelUpHook {
    void onLevelUp(String playerId, String questId, int newLevel);
}
