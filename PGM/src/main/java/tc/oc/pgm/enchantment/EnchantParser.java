package tc.oc.pgm.enchantment;

import com.google.api.client.util.Sets;
import org.bukkit.enchantments.Enchantment;
import org.jdom2.Element;
import tc.oc.pgm.utils.XMLUtils;
import tc.oc.pgm.xml.InvalidXMLException;
import tc.oc.pgm.xml.Node;
import tc.oc.pgm.xml.parser.ElementParser;

import java.util.Optional;
import java.util.Set;

public class EnchantParser implements ElementParser<Optional<EnchantmentProperties>> {

    @Override
    public Optional<EnchantmentProperties> parseElement(Element element) throws InvalidXMLException {
        Element config = element.getChild("enchantments");
        if (config != null) {
            Set<Enchantment> enchants = Sets.newHashSet();
            for (Element disable : config.getChildren("disable")) {
                enchants.add(XMLUtils.parseEnchantment(new Node(disable)));
            }
            return Optional.of(new EnchantmentProperties(enchants));
        }
        return Optional.empty();
    }
}
