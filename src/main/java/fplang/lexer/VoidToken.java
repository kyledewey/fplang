package fplang.lexer;

public class VoidToken implements Token {
    public int hashCode() { return 2; }
    public boolean equals(final Object other) {
        return other instanceof VoidToken;
    }
    public String toString() {
        return "VoidToken";
    }
}
