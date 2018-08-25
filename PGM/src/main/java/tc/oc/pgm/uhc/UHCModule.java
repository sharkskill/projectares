package tc.oc.pgm.uhc;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.jdom2.Document;
import org.jdom2.Element;
import tc.oc.api.docs.virtual.MapDoc;
import tc.oc.pgm.map.MapModule;
import tc.oc.pgm.map.MapModuleContext;
import tc.oc.pgm.map.MapModuleFactory;
import tc.oc.pgm.xml.InvalidXMLException;

import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

public class UHCModule implements MapModule {

    enum Type {SOLO, TEAMS}

    private Element uhcElement;

    public UHCModule(Element uhcElement) {
        this.uhcElement = uhcElement;
    }

    @Override
    public Set<MapDoc.Gamemode> getGamemodes(MapModuleContext context) {
        return uhcElement != null ? Collections.singleton(MapDoc.Gamemode.uhc) : Collections.emptySet();
    }

    @Override
    public BaseComponent getGameName(MapModuleContext context) {
        return new TranslatableComponent("match.scoreboard.uhc.title");
    }

    public static class Factory extends MapModuleFactory<UHCModule> {

        @Override
        public UHCModule parse(MapModuleContext context, Logger logger, Document doc) throws InvalidXMLException {
            if (doc.getRootElement().getChild("uhc") != null) {
                return new UHCModule(doc.getRootElement().getChild("uhc"));
            }
            return null;
        }

    }

}
