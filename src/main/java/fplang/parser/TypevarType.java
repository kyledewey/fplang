package fplang.parser;

public class TypevarType implements Type {
    public final Typevar typevar;

    public TypevarType(final Typevar typevar) {
        this.typevar = typevar;
    }

    public int hashCode() { return typevar.hashCode(); }

    public boolean equals(final Object other) {
        return (other instanceof TypevarType &&
                typevar.equals(((TypevarType)other).typevar));
    }

    public String toString() {
        return "TypevarType(" + typevar.toString() + ")";
    }
}
