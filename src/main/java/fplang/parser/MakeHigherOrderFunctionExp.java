package fplang.parser;

import java.util.List;

public class MakeHigherOrderFunctionExp implements Exp {
    public final List<Variable> params;
    public final Exp body;

    public MakeHigherOrderFunctionExp(final List<Variable> params,
                                      final Exp body) {
        this.params = params;
        this.body = body;
    }

    public int hashCode() {
        return params.hashCode() + body.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof MakeHigherOrderFunctionExp) {
            final MakeHigherOrderFunctionExp asFunc =
                (MakeHigherOrderFunctionExp)other;
            return (params.equals(asFunc.params) &&
                    body.equals(asFunc.body));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("MakeHigherOrderFunctionExp(" + params.toString() + ", " +
                body.toString() + ")");
    }
}

