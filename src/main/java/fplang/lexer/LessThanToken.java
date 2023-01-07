package fplang.lexer;

public class LessThanToken implements Token {
    public int hashCode() { return 21; }
    public boolean equals(final Object other) {
        return other instanceof LessThanToken;
    }
    public String toString() {
        return "LessThanToken";
    }
}
