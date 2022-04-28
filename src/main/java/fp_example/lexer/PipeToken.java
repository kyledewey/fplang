package fp_example.lexer;

public class PipeToken implements Token {
    public int hashCode() { return 27; }
    public boolean equals(final Object other) {
        return other instanceof PipeToken;
    }
    public String toString() {
        return "PipeToken";
    }
}
