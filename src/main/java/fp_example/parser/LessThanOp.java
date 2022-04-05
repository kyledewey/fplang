package fp_example.parser;

public class LessThanOp implements Op {
    public int hashCode() { return 1; }
    public boolean equals(final Object other) {
        return other instanceof LessThanOp;
    }
    public String toString() {
        return "LessThanOp";
    }
}
