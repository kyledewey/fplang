package fp_example.typechecker;

public class BoolTypeTerm implements TypeTerm {
    public int hashCode() { return 1; }
    public boolean equals(final Object other) {
        return other instanceof BoolTypeTerm;
    }
    public String toString() {
        return "BoolTypeTerm";
    }
}
