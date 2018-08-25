package tc.oc.api.leaderboard;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import tc.oc.api.docs.UhcLeaderboardEntry;
import tc.oc.api.docs.UserId;
import tc.oc.api.docs.virtual.UhcLeaderboardEntryDoc;
import tc.oc.api.model.NullModelService;

public class NullUHCLeaderboardEntryService extends NullModelService<UhcLeaderboardEntry, UhcLeaderboardEntryDoc.Partial> implements UHCLeaderboardEntryService {
    @Override
    public ListenableFuture<UhcLeaderboardEntry> findOrCreate(UserId userId) {
        return Futures.immediateFuture(new UhcLeaderboardEntry() {
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
