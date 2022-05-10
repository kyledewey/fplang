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
public class CallLikeExp implements Exp {
    public final Exp functionLike;
    public final List<Exp> params;

    // filled in by the typechecker
    public CallLikeResolved resolution = null;
    
    public CallLikeExp(final Exp functionLike,
                       final List<Exp> params) {
        this.functionLike = functionLike;
        this.params = params;
    }

    public int hashCode() {
        return functionLike.hashCode() + params.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof CallLikeExp) {
            final CallLikeExp asCall = (CallLikeExp)other;
            return (functionLike.equals(asCall.functionLike) &&
                    params.equals(asCall.params));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("CallLikeExp(" + functionLike + ", " +
                params + ")");
    }
}
