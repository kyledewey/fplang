package fp_example.parser;

// Used in CallLikeExp.
// Indicates that it's really a named function.
// Holds the name of the function in play.
public class NamedFunctionResolved implements CallLikeResolved {
    public final FunctionName functionName;

    public NamedFunctionResolved(final FunctionName functionName) {
        this.functionName = functionName;
    }

    public int hashCode() {
        return functionName.hashCode();
    }

    public boolean equals(final Object other) {
        return (other instanceof NamedFunctionResolved &&
                functionName.equals(((NamedFunctionResolved)other).functionName));
    }

    public String toString() {
        return "NamedFunctionResolved(" + functionName + ")";
    }
}
