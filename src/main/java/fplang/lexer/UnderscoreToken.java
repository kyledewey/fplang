package fplang.lexer;

public class UnderscoreToken implements Token {
    public int hashCode() { return 29; }
    public boolean equals(final Object other) {
        return other instanceof UnderscoreToken;
    }
    public String toString() {
        return "UnderscoreToken";
    }
}
