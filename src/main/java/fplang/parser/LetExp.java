package fplang.parser;

public class LetExp implements Exp {
    public final Variable variable;
    public final Exp initializer;
    public final Exp body;

    public LetExp(final Variable variable,
                  final Exp initializer,
                  final Exp body) {
        this.variable = variable;
        this.initializer = initializer;
        this.body = body;
    }

    public int hashCode() {
        return (variable.hashCode() +
                initializer.hashCode() +
                body.hashCode());
    }

    public boolean equals(final Object other) {
        if (other instanceof LetExp) {
            final LetExp asLet = (LetExp)other;
            return (variable.equals(asLet.variable) &&
                    initializer.equals(asLet.initializer) &&
                    body.equals(asLet.body));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("LetExp(" + variable.toString() + ", " +
                initializer.toString() + ", " +
                body.toString() + ")");
    }
}

                
