package fp_example.lexer;

public class DefToken implements Token {
    public int hashCode() { return 26; }
    public boolean equals(final Object other) {
        return other instanceof DefToken;
    }
    public String toString() {
        return "DefToken";
    }
}
