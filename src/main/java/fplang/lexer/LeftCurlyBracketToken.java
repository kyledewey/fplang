package fplang.lexer;

public class LeftCurlyBracketToken implements Token {
    public int hashCode() { return 16; }
    public boolean equals(final Object other) {
        return other instanceof LeftCurlyBracketToken;
    }
    public String toString() {
        return "LeftCurlyBracketToken";
    }
}
