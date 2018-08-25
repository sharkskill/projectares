package tc.oc.api.leaderboard;

import com.google.common.util.concurrent.ListenableFuture;
import tc.oc.api.docs.UhcLeaderboardEntry;
import tc.oc.api.docs.UserId;
import tc.oc.api.docs.virtual.UhcLeaderboardEntryDoc;
import tc.oc.api.model.ModelService;

public interface UHCLeaderboardEntryService extends ModelService<UhcLeaderboardEntry, UhcLeaderboardEntryDoc.Partial> {

    ListenableFuture<UhcLeaderboardEntry> findOrCreate(UserId userId);
}
