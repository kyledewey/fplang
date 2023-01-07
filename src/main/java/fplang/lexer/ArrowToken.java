package fplang.lexer;

public class ArrowToken implements Token {
    public int hashCode() { return 3; }
    public boolean equals(final Object other) {
        return other instanceof ArrowToken;
    }
    public String toString() {
        return "ArrowToken";
    }
}
