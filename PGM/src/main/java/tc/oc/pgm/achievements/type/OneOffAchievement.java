package tc.oc.pgm.achievements.type;

import tc.oc.pgm.achievements.Achievement;

public class OneOffAchievement extends Achievement {

    private String unlockMessage;

    public OneOffAchievement(String name, String displayName, String description, String unlockMessage) {
        super(name, displayName, description);
        this.unlockMessage = unlockMessage;
    }

    public String getUnlockMessage() {
        return unlockMessage;
    }

}
