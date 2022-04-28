package fp_example.lexer;

public class PrintlnToken implements Token {
    public int hashCode() { return 18; }
    public boolean equals(final Object other) {
        return other instanceof PrintlnToken;
    }
    public String toString() {
        return "PrintlnToken";
    }
}
