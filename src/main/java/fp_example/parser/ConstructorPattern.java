package fp_example.parser;

import java.util.List;

public class ConstructorPattern implements Pattern {
    public final ConsName consName;
    public final List<Pattern> patterns;

    public ConstructorPattern(final ConsName consName,
                              final List<Pattern> patterns) {
        this.consName = consName;
        this.patterns = patterns;
    }

    public int hashCode() {
        return consName.hashCode() + patterns.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof ConstructorPattern) {
            final ConstructorPattern asCons = (ConstructorPattern)other;
            return (consName.equals(asCons.consName) &&
                    patterns.equals(asCons.patterns));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("ConstructorPattern(" + consName.toString() + ", " +
                patterns.toString() + ")");
    }
}
