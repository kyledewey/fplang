package fp_example.lexer;

public class CaseToken implements Token {
    public int hashCode() { return 28; }
    public boolean equals(final Object other) {
        return other instanceof CaseToken;
    }
    public String toString() {
        return "CaseToken";
    }
}
