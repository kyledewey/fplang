package fp_example.parser;

public class Vardec {
    public final Variable variable;
    public final Type type;

    public Vardec(final Variable variable,
                  final Type type) {
        this.variable = variable;
        this.type = type;
    }

    public int hashCode() {
        return variable.hashCode() + type.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof Vardec) {
            final Vardec asVar = (Vardec)other;
            return (variable.equals(asVar.variable) &&
                    type.equals(asVar.type));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("Vardec(" + variable.toString() + ", " +
                type.toString() + ")");
    }
}
