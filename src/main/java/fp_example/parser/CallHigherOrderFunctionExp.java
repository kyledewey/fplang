package fp_example.parser;

import java.util.List;

public class CallHigherOrderFunctionExp implements Exp {
    public final Exp function;
    public final List<Exp> params;

    public CallHigherOrderFunctionExp(final Exp function,
                                      final List<Exp> params) {
        this.function = function;
        this.params = params;
    }

    public int hashCode() {
        return function.hashCode() + params.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof CallHigherOrderFunctionExp) {
            final CallHigherOrderFunctionExp asFunc =
                (CallHigherOrderFunctionExp)other;
            return (function.equals(asFunc.function) &&
                    params.equals(asFunc.params));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("CallHigherOrderFunctionExp(" + function.toString() + ", " +
                params.toString() + ")");
    }
}
