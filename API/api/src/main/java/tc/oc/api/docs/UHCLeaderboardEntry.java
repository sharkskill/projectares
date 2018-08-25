package tc.oc.api.docs;

import tc.oc.api.docs.virtual.UHCLeaderboardEntryDoc;
import tc.oc.api.model.ModelName;

@ModelName(value = "UHCLeaderboardEntry", singular = "uhc_leaderboard_entry", plural = "uhc_leaderboard_entries")
public interface UHCLeaderboardEntry extends UHCLeaderboardEntryDoc.Complete {}
