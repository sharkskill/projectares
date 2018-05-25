package tc.oc.pgm.modules;

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

@ModuleDescription(name = "Kill Chests")
public class KillChestModule implements MapModule, MatchModuleFactory<KillChestMatchModule> {

    private final Duration explodeAfter;

    public KillChestModule(Duration explodeAfter) {
        this.explodeAfter = explodeAfter;
    }

    @Override
    public KillChestMatchModule createMatchModule(Match match) {
        return new KillChestMatchModule(match, this.explodeAfter);
    }

    // ---------------------
    // ---- XML Parsing ----
    // ---------------------

    public static KillChestModule parse(MapModuleContext context, Logger logger, Document doc) throws InvalidXMLException {
        Element el = doc.getRootElement().getChild("kill-chest");
        if(el != null) {
            Duration explodeAfter = XMLUtils.parseDuration(Node.fromAttr(el, "explode-after"));
            return new KillChestModule(explodeAfter);
        } else {
            return null;
        }
    }
}
