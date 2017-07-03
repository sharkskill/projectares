package tc.oc.pgm.achievements;

public abstract class Achievement {

    private final String name;
    private final String displayName;
    private final String description;

    public Achievement(String name, String displayName, String description) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSpecialAchievement;

}
