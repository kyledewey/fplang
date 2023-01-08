package fplang.parser;

import java.util.List;

public class ConsPattern implements Pattern {
    public final ConsName consName;
    public final List<Pattern> patterns;

    public ConsPattern(final ConsName consName,
                       final List<Pattern> patterns) {
        this.consName = consName;
        this.patterns = patterns;
    }

    public boolean equals(final Object other) {
        if (other instanceof ConsPattern) {
            final ConsPattern asCons = (ConsPattern)other;
            return (consName.equals(asCons.consName) &&
                    patterns.equals(asCons.patterns));
        } else {
            return false;
        }
    }

    public int hashCode() {
        return consName.hashCode() + patterns.hashCode();
    }

    public String toString() {
        return ("ConsPattern(" +
                consName.toString() + ", " +
                patterns.toString() + ")");
    }
}
