package fp_example.parser;

public class ConsName {
    public final String name;

    public ConsName(final String name) {
        this.name = name;
    }

    public int hashCode() { return name.hashCode(); }

    public boolean equals(final Object other) {
        return (other instanceof ConsName &&
                name.equals(((ConsName)other).name));
    }

    public String toString() {
        return "ConsName(" + name + ")";
    }
}
