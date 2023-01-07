package fplang.typechecker;

public class PlaceholderTypeTerm implements TypeTerm {
    private static int currentPlaceholder = 0;

    public final int id;
    
    public PlaceholderTypeTerm(final int id) {
        this.id = id;
    }

    public PlaceholderTypeTerm() {
        this(currentPlaceholder++);
    }

    public int hashCode() {
        return id;
    }

    public boolean equals(final Object other) {
        return (other instanceof PlaceholderTypeTerm &&
                id == ((PlaceholderTypeTerm)other).id);
    }

    public String toString() {
        return "PlaceholderTypeTerm(" + id + ")";
    }
}
