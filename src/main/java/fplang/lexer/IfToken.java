package fplang.lexer;

public class IfToken implements Token {
    public int hashCode() { return 11; }
    public boolean equals(final Object other) {
        return other instanceof IfToken;
    }
    public String toString() {
        return "IfToken";
    }
}
