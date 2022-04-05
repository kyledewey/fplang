package fp_example.parser;

public class IfExp implements Exp {
    public final Exp guard;
    public final Exp ifTrue;
    public final Exp ifFalse;

    public IfExp(final Exp guard,
                 final Exp ifTrue,
                 final Exp ifFalse) {
        this.guard = guard;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }

    public int hashCode() {
        return (guard.hashCode() +
                ifTrue.hashCode() +
                ifFalse.hashCode());
    }

    public boolean equals(final Object other) {
        if (other instanceof IfExp) {
            final IfExp asIf = (IfExp)other;
            return (guard.equals(asIf.guard) &&
                    ifTrue.equals(asIf.ifTrue) &&
                    ifFalse.equals(asIf.ifFalse));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("IfExp(" + guard.toString() + ", " +
                ifTrue.toString() + ", " +
                ifFalse.toString() + ")");
    }
}
