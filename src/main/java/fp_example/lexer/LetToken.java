package fp_example.lexer;

public class LetToken implements Token {
    public int hashCode() { return 8; }
    public boolean equals(final Object other) {
        return other instanceof LetToken;
    }
    public String toString() {
        return "LetToken";
    }
}
