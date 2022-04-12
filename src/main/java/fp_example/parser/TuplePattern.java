package fp_example.parser;

import java.util.List;

public class TuplePattern implements Pattern {
    public final List<Pattern> patterns;

    public TuplePattern(final List<Pattern> patterns) {
        this.patterns = patterns;
    }

    public int hashCode() {
        return patterns.hashCode();
    }

    public boolean equals(final Object other) {
        return (other instanceof TuplePattern &&
                patterns.equals(((TuplePattern)other).patterns));
    }

    public String toString() {
        return "TuplePattern(" + patterns.toString() + ")";
    }
}
