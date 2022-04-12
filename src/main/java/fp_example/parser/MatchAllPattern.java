package fp_example.parser;

public class MatchAllPattern implements Pattern {
    public int hashCode() { return 0; }
    public boolean equals(final Object other) {
        return other instanceof MatchAllPattern;
    }
    public String toString() { return "MatchAllPattern"; }
}
