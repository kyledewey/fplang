package fp_example.parser;

import java.util.List;

public class CallNamedFunctionExp implements Exp {
    public final FunctionName functionName;
    public final List<Exp> params;

    public CallNamedFunctionExp(final FunctionName functionName,
                                final List<Exp> params) {
        this.functionName = functionName;
        this.params = params;
    }

    public int hashCode() {
        return functionName.hashCode() + params.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof CallNamedFunctionExp) {
            final CallNamedFunctionExp asCall =
                (CallNamedFunctionExp)other;
            return (functionName.equals(asCall.functionName) &&
                    params.equals(asCall.params));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("CallNamedFunctionExp(" + functionName.toString() + ", " +
                params.toString() + ")");
    }
}
