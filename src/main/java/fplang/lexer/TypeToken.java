package fplang.lexer;

public class TypeToken implements Token {
    public int hashCode() { return 25; }
    public boolean equals(final Object other) {
        return other instanceof TypeToken;
    }
    public String toString() {
        return "TypeToken";
    }
}
