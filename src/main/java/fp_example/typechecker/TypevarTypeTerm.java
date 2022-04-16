package fp_example.typechecker;

import fp_example.parser.Typevar;

public class TypevarTypeTerm implements TypeTerm {
    public final Typevar typevar;

    public TypevarTypeTerm(final Typevar typevar) {
        this.typevar = typevar;
    }

    public int hashCode() {
        return typevar.hashCode();
    }

    public boolean equals(final Object other) {
        return (other instanceof TypevarTypeTerm &&
                typevar.equals(((TypevarTypeTerm)other).typevar));
    }

    public String toString() {
        return "TypevarTypeTerm(" + typevar.toString() + ")";
    }
}
