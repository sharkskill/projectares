package tc.oc.pgm.graceperiod;

import org.jdom2.Document;
import org.jdom2.Element;
import tc.oc.pgm.map.MapModule;
import tc.oc.pgm.map.MapModuleContext;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchModuleFactory;
import tc.oc.pgm.module.ModuleDescription;
import tc.oc.pgm.utils.XMLUtils;
import tc.oc.pgm.xml.InvalidXMLException;
import tc.oc.pgm.xml.Node;

import java.time.Duration;
import java.util.logging.Logger;

@ModuleDescription(name = "Grace Period")
public class GracePeriodModule implements MapModule, MatchModuleFactory<GracePeriodMatchModule> {

    private final Duration duration;

    public GracePeriodModule(Duration duration) {
        this.duration = duration;
    }

    @Override
    public GracePeriodMatchModule createMatchModule(Match match) {
        return new GracePeriodMatchModule(match, this.duration);
    }

    // ---------------------
    // ---- XML Parsing ----
    // ---------------------

    public static GracePeriodModule parse(MapModuleContext context, Logger logger, Document doc) throws InvalidXMLException {
        Element el = doc.getRootElement().getChild("graceperiod");
        if(el != null) {
            Duration duration = XMLUtils.parseDuration(Node.fromAttr(el, "duration"));
            return new GracePeriodModule(duration);
        } else {
            return null;
        }
    }
}
