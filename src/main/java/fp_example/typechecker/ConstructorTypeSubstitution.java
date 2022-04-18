package fp_example.typechecker;

import fp_example.parser.AlgName;

import java.util.List;

public class ConstructorTypeSubstitution {
    public final AlgName algName;
    
    // parameters to the constructor itself
    public final List<TypeTerm> params;

    // generics on the algebraic data type corresponding to the constructor
    public final List<TypeTerm> generics;
    
    public ConstructorTypeSubstitution(final AlgName algName,
                                       final List<TypeTerm> params,
                                       final List<TypeTerm> generics) {
        this.algName = algName;
        this.params = params;
        this.generics = generics;
    }

    public int hashCode() {
        return (algName.hashCode() +
                params.hashCode() +
                generics.hashCode());
    }

    public boolean equals(final Object other) {
        if (other instanceof ConstructorTypeSubstitution) {
            final ConstructorTypeSubstitution asSub =
                (ConstructorTypeSubstitution)other;
            return (algName.equals(algName) &&
                    params.equals(asSub.params) &&
                    generics.equals(asSub.generics));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("ConstructorTypeSubstitution(" + algName.toString() + ", " +
                params.toString() + ", " +
                generics.toString() + ")");
    }
}
