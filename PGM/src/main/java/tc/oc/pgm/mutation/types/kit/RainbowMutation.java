package tc.oc.pgm.mutation.types.kit;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import tc.oc.commons.bukkit.item.ItemBuilder;
import tc.oc.pgm.kits.FreeItemKit;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.mutation.types.KitMutation;

import java.util.List;
import java.util.WeakHashMap;

public class RainbowMutation extends KitMutation {



    final WeakHashMap<MatchPlayer, List<ItemStack>> armorRemoved;

    public RainbowMutation(Match match, boolean force) {
        super(match, force);
        armorRemoved = new WeakHashMap<>();
    }

    @Override
    public void apply(MatchPlayer matchPlayer) {
        FreeItemKit freeItemKit = new FreeItemKit(new ItemBuilder(item(Material.LEATHER_HELMET)).unbreakable(true).shareable(false).locked(true).get());
    }
}
