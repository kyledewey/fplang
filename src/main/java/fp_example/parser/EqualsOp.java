package fp_example.parser;

public class EqualsOp implements Op {
    public int hashCode() { return 2; }
    public boolean equals(final Object other) {
        return other instanceof EqualsOp;
    }
    public String toString() {
        return "EqualsOp";
    }
}
