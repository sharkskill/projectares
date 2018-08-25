package tc.oc.pgm.uhc;

public class UHCProperties {

    final Type type;

    public UHCProperties(Type type) {
        this.type = type;
    }

    public enum Type {SOLO, TEAMS}
}
