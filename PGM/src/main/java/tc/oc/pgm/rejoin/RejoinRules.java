package tc.oc.pgm.rejoin;

import java.time.Duration;

public class RejoinRules {
    public final Duration maxOfflineTime;
    public final int maxRejoins;

    public RejoinRules(Duration maxOfflineTime, int maxRejoins) {
        this.maxOfflineTime = maxOfflineTime;
        this.maxRejoins = maxRejoins;
    }
}
