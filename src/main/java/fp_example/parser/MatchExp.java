package fp_example.parser;

import java.util.List;

public class MatchExp implements Exp {
    public final Exp exp;
    public final List<Case> cases;

    public MatchExp(final Exp exp,
                    final List<Case> cases) {
        this.exp = exp;
        this.cases = cases;
    }

    public int hashCode() {
        return exp.hashCode() + cases.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof MatchExp) {
            final MatchExp asMatch = (MatchExp)other;
            return (exp.equals(asMatch.exp) &&
                    cases.equals(asMatch.cases));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("MatchExp(" + exp.toString() + ", " +
                cases.toString() + ")");
    }
}
