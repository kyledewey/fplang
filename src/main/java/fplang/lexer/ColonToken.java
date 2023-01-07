package fplang.lexer;

public class ColonToken implements Token {
    public int hashCode() { return 24; }
    public boolean equals(final Object other) {
        return other instanceof ColonToken;
    }
    public String toString() {
        return "ColonToken";
    }
}
