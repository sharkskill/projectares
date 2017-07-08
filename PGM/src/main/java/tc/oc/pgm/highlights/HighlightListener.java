package tc.oc.pgm.highlights;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tc.oc.commons.bukkit.chat.PlayerComponent;
import tc.oc.commons.bukkit.nick.IdentityProvider;
import tc.oc.commons.bukkit.tokens.TokenUtil;
import tc.oc.commons.core.chat.Component;
import tc.oc.pgm.Config;
import tc.oc.pgm.destroyable.DestroyableContribution;
import tc.oc.pgm.events.MatchEndEvent;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.MatchScheduler;
import tc.oc.pgm.playerstats.StatsUserFacet;

import javax.inject.Inject;

public class HighlightListener implements Listener {

    private final MatchScheduler scheduler;
    private final IdentityProvider identityProvider;

    @Inject
    HighlightListener(MatchScheduler scheduler, IdentityProvider identityProvider) {
        this.scheduler = scheduler;
        this.identityProvider = identityProvider;
    }

    @EventHandler
    public void matchEnd(MatchEndEvent event) {
        if (Config.MVP.enabled()) {
            StatsUserFacet bestPlayerStats = null;
            MatchPlayer bestPlayer = null;
            double bestPlayerPoints = 0;

            if (event.getMatch().getParticipatingPlayers().size() < Config.MVP.playersRequired()) {
                return;
            }

            for (MatchPlayer player : event.getMatch().getParticipatingPlayers()) {
                StatsUserFacet facet = player.getUserContext().facet(StatsUserFacet.class);

                double points = 0;
                points += facet.matchKills();
                points -= facet.deaths();
                points -= facet.teammatesKilled();
                for (long wool : facet.getWoolCaptureTimes()) {
                    int woolPoints = (int) ((wool * 2.25) - 2);
                    points += Math.min(Math.max(woolPoints, 0), 120);
                }

                for (long core : facet.getCoreLeakTimes()) {
                    int corePoints = (int) ((core * 2.25) - 2);
                    points += Math.min(Math.max(corePoints, 0), 120);
                }

                for (DestroyableContribution destroyable : facet.getDestroyableDestroyTimes().keySet()) {
                    int destroyablePoints = (int) ((facet.getDestroyableDestroyTimes().get(destroyable) * 2.25 * destroyable.getPercentage()) - 2);
                    points += Math.min(Math.max(destroyablePoints, 0), 120);
                }

                for (long flag : facet.getFlagCaptureTimes()) {
                    int flagPoints = (int) (flag / 1.75);
                    points += Math.min(Math.max(flagPoints, 0), 120);
                }

                points += (facet.getBlocksBroken() / 30);

                if (bestPlayerStats == null || points > bestPlayerPoints) {
                    bestPlayerStats = facet;
                    bestPlayer = player;
                    bestPlayerPoints = points;
                }
            }

            if (bestPlayer != null) {
                BaseComponent title = new Component(ChatColor.AQUA, ChatColor.BOLD).translate("broadcast.gameOver.mvp");
                BaseComponent subtitle;

                if (Config.Token.enabled() && Math.random() < Config.Token.mvpChance()) {
                    if (Math.random() > 0.25) {
                        TokenUtil.giveMutationTokens(TokenUtil.getUser(bestPlayer.getBukkit()), 1);
                        subtitle = new Component(ChatColor.YELLOW).translate("broadcast.gameOver.mvp.token.subtitle", new PlayerComponent(identityProvider.createIdentity(bestPlayer.getBukkit())), new Component(ChatColor.YELLOW).translate("mvp.token.type.mutation"));
                    } else {
                        TokenUtil.giveMapTokens(TokenUtil.getUser(bestPlayer.getBukkit()), 1);
                        subtitle = new Component(ChatColor.YELLOW).translate("broadcast.gameOver.mvp.token.subtitle", new PlayerComponent(identityProvider.createIdentity(bestPlayer.getBukkit())), new Component(ChatColor.YELLOW).translate("mvp.token.type.map"));
                    }
                } else {
                    subtitle = new PlayerComponent(identityProvider.createIdentity(bestPlayer.getBukkit()));
                }

                for (MatchPlayer viewer : event.getMatch().getPlayers()) {
                    scheduler.createDelayedTask(100L, () -> {
                        viewer.showTitle(title, subtitle, 0, 60, 60);
                    });
                }
            }
        }
    }
}
