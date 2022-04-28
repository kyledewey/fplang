package fp_example.lexer;

public class TrueToken implements Token {
    public int hashCode() { return 6; }
    public boolean equals(final Object other) {
        return other instanceof TrueToken;
    }
    public String toString() {
        return "TrueToken";
    }
}
