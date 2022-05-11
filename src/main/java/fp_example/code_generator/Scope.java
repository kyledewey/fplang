package fp_example.code_generator;

import fp_example.parser.Variable;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class Scope {
    // a variable is immediately available if either:
    // - It's a parameter to the function we are directly in, or:
    // - It was declared locally in whatever function we are in
    public final Set<Variable> immediatelyAvailable;

    // If we use a variable that isn't in the above set, then we need to
    // close over it
    public final Set<Variable> mustCloseOver;

    public Scope(final Set<Variable> immediatelyAvailable,
                 final Set<Variable> mustCloseOver) {
        this.immediatelyAvailable = immediatelyAvailable;
        this.mustCloseOver = mustCloseOver;
    }

    public Scope newVariable(final Variable variable) {
        if (!immediatelyAvailable.contains(variable)) {
            final Set<Variable> newImmediatelyAvailable = new HashSet<Variable>();
            newImmediatelyAvailable.addAll(immediatelyAvailable);
            newImmediatelyAvailable.add(variable);
            return new Scope(newImmediatelyAvailable, mustCloseOver);
        } else {
            return this;
        }
    }
    
    public Scope variableUsed(final Variable variable) {
        if (immediatelyAvailable.contains(variable) ||
            mustCloseOver.contains(variable)) {
            return this;
        } else {
            final Set<Variable> newMustCloseOver = new HashSet<Variable>();
            newMustCloseOver.addAll(mustCloseOver);
            newMustCloseOver.add(variable);
            return new Scope(immediatelyAvailable, newMustCloseOver);
        }
    }

    public static Scope enteringNewFunction(final List<Variable> params) {
        final Set<Variable> immediatelyAvailable = new HashSet<Variable>();
        immediatelyAvailable.addAll(params);
        return new Scope(immediatelyAvailable, new HashSet<Variable>());
    }

    public Scope joinFromNestedScope(final Scope fromFunction) {
        // we only have variables available from the outer scope
        // however, anything the inner function closed over must now be
        // closed over by the outer function, too
        final Set<Variable> newMustCloseOver = new HashSet<Variable>();
        newMustCloseOver.addAll(mustCloseOver);
        newMustCloseOver.addAll(fromFunction.mustCloseOver);
        return new Scope(immediatelyAvailable, newMustCloseOver);
    }
    
    public int hashCode() {
        return immediatelyAvailable.hashCode() + mustCloseOver.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof Scope) {
            final Scope asScope = (Scope)other;
            return (immediatelyAvailable.equals(asScope.immediatelyAvailable) &&
                    mustCloseOver.equals(asScope.mustCloseOver));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("Scope(" + immediatelyAvailable.toString() + ", " +
                mustCloseOver.toString() + ")");
    }
}
