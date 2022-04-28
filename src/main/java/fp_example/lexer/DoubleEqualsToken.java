package fp_example.lexer;

public class DoubleEqualsToken implements Token {
    public int hashCode() { return 10; }
    public boolean equals(final Object other) {
        return other instanceof DoubleEqualsToken;
    }
    public String toString() {
        return "DoubleEqualsToken";
    }
}
