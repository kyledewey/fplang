package fplang.lexer;

public class RightCurlyBracketToken implements Token {
    public int hashCode() { return 17; }
    public boolean equals(final Object other) {
        return other instanceof RightCurlyBracketToken;
    }
    public String toString() {
        return "RightCurlyBracketToken";
    }
}
