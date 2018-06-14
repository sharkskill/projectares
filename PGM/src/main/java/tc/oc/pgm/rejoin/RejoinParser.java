package tc.oc.pgm.rejoin;

import org.jdom2.Element;
import tc.oc.pgm.utils.XMLUtils;
import tc.oc.pgm.xml.InvalidXMLException;
import tc.oc.pgm.xml.Node;
import tc.oc.pgm.xml.parser.ElementParser;

import java.time.Duration;
import java.util.Optional;

public class RejoinParser implements ElementParser<Optional<RejoinRules>> {

    @Override
    public Optional<RejoinRules> parseElement(Element element) throws InvalidXMLException {
        Element elRejoin = element.getChild("rejoin");
        if (elRejoin != null) {
            return Optional.of(new RejoinRules(XMLUtils.parseDuration(Node.fromAttr(elRejoin, "max-offline-time"), Duration.ofMinutes(2)),
                    XMLUtils.parseNumber(Node.fromAttr(elRejoin, "max-rejoins"), Integer.class, 3)));
        }
        return Optional.empty();
    }
}
