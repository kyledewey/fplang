package fp_example.parser;

import java.util.List;

public class Case {
    public final ConsName consName;
    public final List<Variable> variables;
    public final Exp body;

    public Case(final ConsName consName,
                final List<Variable> variables,
                final Exp body) {
        this.consName = consName;
        this.variables = variables;
        this.body = body;
    }

    public int hashCode() {
        return (consName.hashCode() +
                variables.hashCode() +
                body.hashCode());
    }

    public boolean equals(final Object other) {
        if (other instanceof Case) {
            final Case asCase = (Case)other;
            return (consName.equals(asCase.consName) &&
                    variables.equals(asCase.variables) &&
                    body.equals(asCase.body));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("Case(" + consName.toString() + ", " +
                variables.toString() + ", " +
                body.toString() + ")");
    }
}
