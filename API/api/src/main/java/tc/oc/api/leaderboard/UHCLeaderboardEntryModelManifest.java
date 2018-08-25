package tc.oc.api.leaderboard;

import com.google.inject.multibindings.OptionalBinder;
import tc.oc.api.docs.UhcLeaderboardEntry;
import tc.oc.api.docs.virtual.UhcLeaderboardEntryDoc;
import tc.oc.api.model.ModelBinders;
import tc.oc.commons.core.inject.HybridManifest;

public class UHCLeaderboardEntryModelManifest extends HybridManifest implements ModelBinders {

    @Override
    protected void configure() {
        bindModel(UhcLeaderboardEntry.class, UhcLeaderboardEntryDoc.Partial.class, model -> {
            model.bindService().to(UHCLeaderboardEntryService.class);
        });

        OptionalBinder.newOptionalBinder(publicBinder(), UHCLeaderboardEntryService.class)
                .setDefault().to(NullUHCLeaderboardEntryService.class);
    }
}
