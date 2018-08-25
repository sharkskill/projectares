package tc.oc.api.leaderboard;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import tc.oc.api.docs.UHCLeaderboardEntry;
import tc.oc.api.docs.UserId;
import tc.oc.api.docs.virtual.UHCLeaderboardEntryDoc;
import tc.oc.api.model.NullModelService;

public class NullUHCLeaderboardEntryService extends NullModelService<UHCLeaderboardEntry, UHCLeaderboardEntryDoc.Partial> implements UHCLeaderboardEntryService {
    @Override
    public ListenableFuture<UHCLeaderboardEntry> findOrCreate(UserId userId) {
        return Futures.immediateFuture(new UHCLeaderboardEntry() {
            @Override
            public int gold_solo() {
                return 0;
            }

            @Override
            public int gold_teams() {
                return 0;
            }

            @Override
            public int kills_solo() {
                return 0;
            }

            @Override
            public int kills_teams() {
                return 0;
            }

            @Override
            public UserId user() {
                return userId;
            }

            @Override
            public int wins_solo() {
                return 0;
            }

            @Override
            public int wins_teams() {
                return 0;
            }

            @Override
            public String _id() {
                return null;
            }
        });
    }
}
