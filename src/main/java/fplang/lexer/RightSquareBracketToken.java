package fplang.lexer;

public class RightSquareBracketToken implements Token {
    public int hashCode() { return 5; }
    public boolean equals(final Object other) {
        return other instanceof RightSquareBracketToken;
    }
    public String toString() {
        return "RightSquareBracketToken";
    }
}
