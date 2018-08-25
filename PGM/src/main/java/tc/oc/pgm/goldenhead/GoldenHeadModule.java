package tc.oc.pgm.goldenhead;

import org.jdom2.Document;
import tc.oc.pgm.map.MapModule;
import tc.oc.pgm.map.MapModuleContext;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchModuleFactory;
import tc.oc.pgm.module.ModuleDescription;
import tc.oc.pgm.xml.InvalidXMLException;

import java.util.logging.Logger;

@ModuleDescription(name = "Golden Head")
public class GoldenHeadModule implements MapModule, MatchModuleFactory<GoldenHeadMatchModule> {

    public GoldenHeadModule() {

    }

    public static GoldenHeadModule parse(MapModuleContext context, Logger logger, Document doc) throws InvalidXMLException {
        return doc.getRootElement().getChild("goldenhead") != null ? new GoldenHeadModule() : null;
    }

    // ---------------------
    // ---- XML Parsing ----
    // ---------------------

    @Override
    public GoldenHeadMatchModule createMatchModule(Match match) {
        return new GoldenHeadMatchModule(match);
    }
}
