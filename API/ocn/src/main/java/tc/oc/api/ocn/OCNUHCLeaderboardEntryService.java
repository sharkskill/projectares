package tc.oc.api.ocn;

import com.damnhandy.uri.template.UriTemplate;
import com.google.common.util.concurrent.ListenableFuture;
import tc.oc.api.docs.UhcLeaderboardEntry;
import tc.oc.api.docs.UserId;
import tc.oc.api.docs.virtual.UhcLeaderboardEntryDoc;
import tc.oc.api.http.HttpOption;
import tc.oc.api.leaderboard.UHCLeaderboardEntryService;
import tc.oc.api.model.HttpModelService;

import javax.inject.Singleton;

@Singleton
class OCNUHCLeaderboardEntryService extends HttpModelService<UhcLeaderboardEntry, UhcLeaderboardEntryDoc.Partial> implements UHCLeaderboardEntryService {

    @Override
    public ListenableFuture<UhcLeaderboardEntry> findOrCreate(UserId user) {
        final String uri = UriTemplate.fromTemplate("/{model}/get_or_create/{user_id}")
                .set("model", "uhc_leaderboard_entries")
                .set("user_id", user.player_id())
                .expand();
        return client().get(uri, UhcLeaderboardEntry.class, HttpOption.INFINITE_RETRY);
    }
}
