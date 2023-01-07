package fplang.typechecker;

import java.util.List;

public class FunctionTypeTerm implements TypeTerm {
    public final List<TypeTerm> params;
    public final TypeTerm returnType;

    public FunctionTypeTerm(final List<TypeTerm> params,
                            final TypeTerm returnType) {
        this.params = params;
        this.returnType = returnType;
    }

    public int hashCode() {
        return params.hashCode() + returnType.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof FunctionTypeTerm) {
            final FunctionTypeTerm otherFunc = (FunctionTypeTerm)other;
            return (params.equals(otherFunc.params) &&
                    returnType.equals(otherFunc.returnType));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("FunctionTypeTerm(" + params.toString() + ", " +
                returnType.toString() + ")");
    }
}
