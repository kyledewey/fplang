package fplang.parser;

public class VariablePattern implements Pattern {
    public final Variable variable;
    public VariablePattern(final Variable variable) {
        this.variable = variable;
    }
    public boolean equals(final Object other) {
        return (other instanceof VariablePattern &&
                variable.equals(((VariablePattern)other).variable));
    }
    public int hashCode() {
        return variable.hashCode();
    }
    public String toString() {
        return ("VariablePattern(" + variable.toString() + ")");
    }
}
