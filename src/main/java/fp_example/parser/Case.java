package fp_example.parser;

public class Case {
    public final Pattern pattern;
    public final Exp exp;

    public Case(final Pattern pattern,
                final Exp exp) {
        this.pattern = pattern;
        this.exp = exp;
    }

    public int hashCode() {
        return pattern.hashCode() + exp.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof Case) {
            final Case asCase = (Case)other;
            return (pattern.equals(asCase.pattern) &&
                    exp.equals(asCase.exp));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("Case(" + pattern.toString() + ", " +
                exp.toString() + ")");
    }
}
