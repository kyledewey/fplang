package fplang.parser;

public class Typevar {
    public final String name;

    public Typevar(final String name) {
        this.name = name;
    }

    public int hashCode() { return name.hashCode(); }

    public boolean equals(final Object other) {
        return (other instanceof Typevar &&
                name.equals(((Typevar)other).name));
    }

    public String toString() {
        return "Typevar(" + name + ")";
    }
}
