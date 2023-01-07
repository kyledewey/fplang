package fplang.parser;

public class AlgName {
    public final String name;

    public AlgName(final String name) {
        this.name = name;
    }

    public int hashCode() { return name.hashCode(); }

    public boolean equals(final Object other) {
        return (other instanceof AlgName &&
                name.equals(((AlgName)other).name));
    }

    public String toString() {
        return "AlgName(" + name + ")";
    }
}
