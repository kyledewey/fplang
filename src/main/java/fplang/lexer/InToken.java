package fplang.lexer;

public class InToken implements Token {
    public int hashCode() { return 19; }
    public boolean equals(final Object other) {
        return other instanceof InToken;
    }
    public String toString() {
        return "InToken";
    }
}
