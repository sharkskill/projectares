package tc.oc.pgm.uhc;

import org.jdom2.Element;
import tc.oc.pgm.utils.XMLUtils;
import tc.oc.pgm.xml.InvalidXMLException;
import tc.oc.pgm.xml.Node;
import tc.oc.pgm.xml.parser.ElementParser;

import java.util.Optional;

public class UHCParser implements ElementParser<Optional<UHCProperties>> {

    @Override
    public Optional<UHCProperties> parseElement(Element element) throws InvalidXMLException {
        Element elUHC = element.getChild("uhc");
        if (elUHC != null) {
            return Optional.of(new UHCProperties(XMLUtils.parseEnum(Node.fromRequiredAttr(elUHC, "type"), UHCProperties.Type.class, "uhc type")));
        }
        return Optional.empty();
    }
}
