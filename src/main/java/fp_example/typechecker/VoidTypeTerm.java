package fp_example.typechecker;

public class VoidTypeTerm implements TypeTerm {
    public int hashCode() { return 2; }
    public boolean equals(final Object other) {
        return other instanceof VoidTypeTerm;
    }
    public String toString() {
        return "VoidTypeTerm";
    }
}
