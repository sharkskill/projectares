package tc.oc.api.leaderboard;

import com.google.common.util.concurrent.ListenableFuture;
import tc.oc.api.docs.UHCLeaderboardEntry;
import tc.oc.api.docs.UserId;
import tc.oc.api.docs.virtual.UHCLeaderboardEntryDoc;
import tc.oc.api.model.ModelService;

public interface UHCLeaderboardEntryService extends ModelService<UHCLeaderboardEntry, UHCLeaderboardEntryDoc.Partial> {

    ListenableFuture<UHCLeaderboardEntry> findOrCreate(UserId userId);
}
