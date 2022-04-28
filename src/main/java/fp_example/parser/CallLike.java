package fp_example.parser;

import java.util.List;

// This represents the following:
// exp(exp*)
// functionname(exp*)
// consname(exp*)
//
// We cannot unambigiously differentiate these without type
// information, so it's the responsibility of the typechecker
// to figure this out.
public class CallLike implements Exp {
    public final Exp functionLike;
    public final List<Exp> params;

    public CallLike(final Exp functionLike,
                    final List<Exp> params) {
        this.functionLike = functionLike;
        this.params = params;
    }

    public int hashCode() {
        return functionLike.hashCode() + params.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof CallLike) {
            final CallLike asCall = (CallLike)other;
            return (functionLike.equals(asCall.functionLike) &&
                    params.equals(asCall.params));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("CallLike(" + functionLike + ", " +
                params + ")");
    }
}
