package fp_example.parser;

public class ParseResult<A> {
    public final A result;
    public final int position;

    public ParseResult(final A result,
                       final int position) {
        this.result = result;
        this.position = position;
    }

    public int hashCode() {
        return result.hashCode() + position;
    }

    public boolean equals(final Object other) {
        if (other instanceof ParseResult) {
            final ParseResult<A> asResult = (ParseResult<A>)other;
            return (result.equals(asResult.result) &&
                    position == asResult.position);
        } else {
            return false;
        }
    }

    public String toString() {
        return ("ParseResult(" + result.toString() + ", " +
                position + ")");
    }
}
