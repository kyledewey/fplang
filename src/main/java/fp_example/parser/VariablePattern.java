package fp_example.parser;

public class VariablePattern implements Pattern {
    public final Variable variable;

    public VariablePattern(final Variable variable) {
        this.variable = variable;
    }

    public int hashCode() {
        return variable.hashCode();
    }

    public boolean equals(final Object other) {
        return (other instanceof VariablePattern &&
                variable.equals(((VariablePattern)other).variable));
    }

    public String toString() {
        return "VariablePattern(" + variable.toString() + ")";
    }
}
