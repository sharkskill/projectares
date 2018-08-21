package tc.oc.pgm.uhc;

import tc.oc.commons.core.inject.HybridManifest;
import tc.oc.pgm.map.inject.MapBinders;
import tc.oc.pgm.match.inject.MatchBinders;
import tc.oc.pgm.match.inject.MatchModuleFixtureManifest;

public class UHCManifest extends HybridManifest implements MapBinders, MatchBinders {

    @Override
    protected void configure() {
        install(new MatchModuleFixtureManifest<XrayDetectionMatchModule>() {});
    }

}
