package tc.oc.pgm.rejoin;

import tc.oc.commons.core.inject.HybridManifest;
import tc.oc.commons.core.inject.Keys;
import tc.oc.pgm.map.inject.MapBinders;
import tc.oc.pgm.match.MatchUserFacetBinder;
import tc.oc.pgm.match.inject.MatchBinders;

public class RejoinManifest extends HybridManifest implements MapBinders, MatchBinders {
    @Override
    protected void configure() {
        bindRootElementParser(Keys.optional(RejoinRules.class))
            .to(RejoinParser.class);


        installUserModule(binder -> new MatchUserFacetBinder(binder).register(RejoinUserFacet.class));
    }
}

