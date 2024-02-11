package quest;

public class Quest {
    private final String id;
    private final int maxLevels;
    private final String reward;
    private final QuestLevelUpHook levelUpHook;

    public Quest(String id, int maxLevels, String reward, QuestLevelUpHook levelUpHook) {
        this.id = id;
        this.maxLevels = maxLevels;
        this.reward = reward;
        this.levelUpHook = levelUpHook;
    }

    public String getId() {
        return id;
    }

    public int getMaxLevels() {
        return maxLevels;
    }

    public String getReward() {
        return reward;
    }

    public void executeLevelUpHook(String playerId, int newLevel) {
        if (levelUpHook != null) {
            levelUpHook.onLevelUp(playerId, id, newLevel);
        }
    }
}
