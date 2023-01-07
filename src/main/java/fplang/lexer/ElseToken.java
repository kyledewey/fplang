package fplang.lexer;

public class ElseToken implements Token {
    public int hashCode() { return 14; }
    public boolean equals(final Object other) {
        return other instanceof ElseToken;
    }
    public String toString() {
        return "ElseToken";
    }
}
