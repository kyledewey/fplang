package fp_example.typechecker;

import java.util.List;

public class ConstructorTypeSubstitution {
    // parameters to the constructor itself
    public final List<TypeTerm> params;

    // generics on the algebraic data type corresponding to the constructor
    public final List<TypeTerm> generics;
    
    public ConstructorTypeSubstitution(final List<TypeTerm> params,
                                       final List<TypeTerm> generics) {
        this.params = params;
        this.generics = generics;
    }

    public int hashCode() {
        return params.hashCode() + generics.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof ConstructorTypeSubstitution) {
            final ConstructorTypeSubstitution asSub =
                (ConstructorTypeSubstitution)other;
            return (params.equals(asSub.params) &&
                    generics.equals(asSub.generics));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("ConstructorTypeSubstitution(" + params.toString() + ", " +
                generics.toString() + ")");
    }
}
