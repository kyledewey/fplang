package fp_example.code_generator;

public class TranslationResult<A> {
    public final A result;
    public final Scope scope;

    public TranslationResult(final A result,
                             final Scope scope) {
        this.result = result;
        this.scope = scope;
    }

    public int hashCode() {
        return result.hashCode() + scope.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof TranslationResult) {
            final TranslationResult<A> otherResult = (TranslationResult<A>)other;
            return (result.equals(otherResult.result) &&
                    scope.equals(otherResult.scope));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("TranslationResult(" + result.toString() + ", " +
                scope.toString() + ")");
    }
}
