package fp_example.typechecker;

import fp_example.parser.AlgName;

import java.util.List;

public class AlgebraicTypeTerm implements TypeTerm {
    public final AlgName algName;
    public final List<TypeTerm> typeTerms;

    public AlgebraicTypeTerm(final AlgName algName,
                             final List<TypeTerm> typeTerms) {
        this.algName = algName;
        this.typeTerms = typeTerms;
    }

    public int hashCode() {
        return algName.hashCode() + typeTerms.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof AlgebraicTypeTerm) {
            final AlgebraicTypeTerm asAlg = (AlgebraicTypeTerm)other;
            return (algName.equals(asAlg.algName) &&
                    typeTerms.equals(asAlg.typeTerms));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("AlgebraicTypeTerms(" + algName.toString() + ", " +
                typeTerms.toString() + ")");
    }
}
