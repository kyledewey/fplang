package fp_example.lexer;

public class SingleEqualsToken implements Token {
    public int hashCode() { return 9; }
    public boolean equals(final Object other) {
        return other instanceof SingleEqualsToken;
    }
    public String toString() {
        return "SingleEqualsToken";
    }
}
