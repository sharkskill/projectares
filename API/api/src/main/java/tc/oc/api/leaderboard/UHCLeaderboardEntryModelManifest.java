package tc.oc.api.leaderboard;

import com.google.inject.multibindings.OptionalBinder;
import tc.oc.api.docs.UHCLeaderboardEntry;
import tc.oc.api.docs.virtual.UHCLeaderboardEntryDoc;
import tc.oc.api.model.ModelBinders;
import tc.oc.commons.core.inject.HybridManifest;

public class UHCLeaderboardEntryModelManifest extends HybridManifest implements ModelBinders {

    @Override
    protected void configure() {
        bindModel(UHCLeaderboardEntry.class, UHCLeaderboardEntryDoc.Partial.class, model -> {
            model.bindService().to(UHCLeaderboardEntryService.class);
        });

        OptionalBinder.newOptionalBinder(publicBinder(), UHCLeaderboardEntryService.class)
                .setDefault().to(NullUHCLeaderboardEntryService.class);
    }
}
