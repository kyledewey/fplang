package fp_example.lexer;

public class SemicolonToken implements Token {
    public int hashCode() { return 22; }
    public boolean equals(final Object other) {
        return other instanceof SemicolonToken;
    }
    public String toString() {
        return "SemicolonToken";
    }
}
