package fplang.typechecker;

public class IntTypeTerm implements TypeTerm {
    public int hashCode() { return 0; }
    public boolean equals(final Object other) {
        return other instanceof IntTypeTerm;
    }
    public String toString() {
        return "IntTypeTerm";
    }
}
