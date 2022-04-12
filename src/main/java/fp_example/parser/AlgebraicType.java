package fp_example.parser;

import java.util.List;

public class AlgebraicType implements Type {
    public final AlgName algName;
    public final List<Type> types;

    public AlgebraicType(final AlgName algName,
                         final List<Type> types) {
        this.algName = algName;
        this.types = types;
    }

    public int hashCode() {
        return algName.hashCode() + types.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof AlgebraicType) {
            final AlgebraicType asAlg = (AlgebraicType)other;
            return (algName.equals(asAlg.algName) &&
                    types.equals(asAlg.types));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("AlgebraicType(" + algName.toString() + ", " +
                types.toString() + ")");
    }
}
