package fplang.parser;

import java.util.List;

public class Case {
    public final Pattern pattern;
    public final Exp body;

    public Case(final Pattern pattern,
                final Exp body) {
        this.pattern = pattern;
        this.body = body;
    }

    public int hashCode() {
        return (pattern.hashCode() +
                body.hashCode());
    }

    public boolean equals(final Object other) {
        if (other instanceof Case) {
            final Case asCase = (Case)other;
            return (pattern.equals(asCase.pattern) &&
                    body.equals(asCase.body));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("Case(" +
                pattern.toString() + ", " +
                body.toString() + ")");
    }
}
