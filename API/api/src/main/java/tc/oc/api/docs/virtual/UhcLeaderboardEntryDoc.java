package tc.oc.api.docs.virtual;

import tc.oc.api.annotations.Serialize;
import tc.oc.api.docs.UserId;
import tc.oc.api.model.ModelName;

public interface UhcLeaderboardEntryDoc {
    interface Partial extends PartialModel {}

    @ModelName(value = "UhcLeaderboardEntry", singular = "uhc_leaderboard_entry", plural = "uhc_leaderboard_entries")
    interface Base extends Partial, Model {
    }

    @Serialize
    interface Complete extends Solo, Teams {
        UserId user();
    }

    @Serialize
    interface Solo extends Base {
        int wins_solo();

        int gold_solo();
        int kills_solo();
    }

    @Serialize
    interface Teams extends Base {
        int wins_teams();

        int gold_teams();

        int kills_teams();
    }
}
