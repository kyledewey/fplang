package fplang.typechecker;

import java.util.List;

public class FunctionTypeSubstitution {
    public final List<TypeTerm> params;
    public final TypeTerm returnType;

    public FunctionTypeSubstitution(final List<TypeTerm> params,
                                    final TypeTerm returnType) {
        this.params = params;
        this.returnType = returnType;
    }

    public int hashCode() {
        return params.hashCode() + returnType.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof FunctionTypeSubstitution) {
            final FunctionTypeSubstitution asFunc =
                (FunctionTypeSubstitution)other;
            return (params.equals(asFunc.params) &&
                    returnType.equals(asFunc.returnType));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("FunctionTypeSubstitution(" + params.toString() + ", " +
                returnType.toString());
    }
}
