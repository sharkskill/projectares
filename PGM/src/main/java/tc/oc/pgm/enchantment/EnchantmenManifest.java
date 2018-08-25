package tc.oc.pgm.enchantment;

import tc.oc.commons.core.inject.HybridManifest;
import tc.oc.commons.core.inject.Keys;
import tc.oc.pgm.map.inject.MapBinders;
import tc.oc.pgm.match.inject.MatchBinders;
import tc.oc.pgm.match.inject.MatchModuleFixtureManifest;

public class EnchantmenManifest extends HybridManifest implements MapBinders, MatchBinders {
    @Override
    protected void configure() {
        bindRootElementParser(Keys.optional(EnchantmentProperties.class))
                .to(EnchantParser.class);

        install(new MatchModuleFixtureManifest<EnchantmentMatchModule>() {});
    }
}

