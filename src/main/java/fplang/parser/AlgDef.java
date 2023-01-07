package fplang.parser;

import java.util.List;

public class AlgDef {
    public final AlgName algName;
    public final List<Typevar> typevars;
    public final List<ConsDef> constructors;

    public AlgDef(final AlgName algName,
                  final List<Typevar> typevars,
                  final List<ConsDef> constructors) {
        this.algName = algName;
        this.typevars = typevars;
        this.constructors = constructors;
    }

    public int hashCode() {
        return (algName.hashCode() +
                typevars.hashCode() +
                constructors.hashCode());
    }

    public boolean equals(final Object other) {
        if (other instanceof AlgDef) {
            final AlgDef asAlg = (AlgDef)other;
            return (algName.equals(asAlg.algName) &&
                    typevars.equals(asAlg.typevars) &&
                    constructors.equals(asAlg.constructors));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("AlgDef(" + algName.toString() + ", " +
                typevars.toString() + ", " +
                constructors.toString() + ")");
    }
}
