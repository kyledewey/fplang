package fp_example.lexer;

public class MatchToken implements Token {
    public int hashCode() { return 15; }
    public boolean equals(final Object other) {
        return other instanceof MatchToken;
    }
    public String toString() {
        return "MatchToken";
    }
}
