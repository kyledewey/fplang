package fplang.lexer;

public class LeftSquareBracketToken implements Token {
    public int hashCode() { return 4; }
    public boolean equals(final Object other) {
        return other instanceof LeftSquareBracketToken;
    }
    public String toString() {
        return "LeftSquareBracketToken";
    }
}
