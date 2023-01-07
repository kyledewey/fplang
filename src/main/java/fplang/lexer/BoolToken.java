package fplang.lexer;

public class BoolToken implements Token {
    public int hashCode() { return 1; }
    public boolean equals(final Object other) {
        return other instanceof BoolToken;
    }
    public String toString() {
        return "BoolToken";
    }
}
