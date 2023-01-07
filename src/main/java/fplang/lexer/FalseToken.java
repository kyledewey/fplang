package fplang.lexer;

public class FalseToken implements Token {
    public int hashCode() { return 7; }
    public boolean equals(final Object other) {
        return other instanceof FalseToken;
    }
    public String toString() {
        return "FalseToken";
    }
}
