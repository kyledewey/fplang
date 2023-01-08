package fplang.parser;

public class UnderscorePattern implements Pattern {
    public boolean equals(final Object other) {
        return other instanceof UnderscorePattern;
    }
    public int hashCode() { return 0; }
    public String toString() {
        return "UnderscorePattern";
    }
}
