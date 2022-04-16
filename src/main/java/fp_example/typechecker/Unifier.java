package fp_example.typechecker;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

// basic idea: map placeholders to type terms
// the last value in a lookup chain is the set representative
public class Unifier {
    private final Map<PlaceholderTypeTerm, TypeTerm> map;

    public Unifier() {
        map = new HashMap<PlaceholderTypeTerm, TypeTerm>();
    }

    private TypeTerm setRepresentativeFor(TypeTerm term) {
        TypeTerm nextTerm = null;
        while (term instanceof PlaceholderTypeTerm &&
               (nextTerm = map.get((PlaceholderTypeTerm)term)) != null) {
            term = nextTerm;
        }

        return term;
    }

    private boolean someTermContains(final List<TypeTerm> terms,
                                     final PlaceholderTypeTerm placeholder) {
        for (final TypeTerm term : terms) {
            if (termContains(term, placeholder)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean termContains(TypeTerm term,
                                 final PlaceholderTypeTerm placeholder) {
        term = setRepresentativeFor(term);
        if (term instanceof PlaceholderTypeTerm &&
            placeholder.id == ((PlaceholderTypeTerm)term).id) {
            return true;
        } else if (term instanceof FunctionTypeTerm) {
            final FunctionTypeTerm asFunc = (FunctionTypeTerm)term;
            return (someTermContains(asFunc.params, placeholder) ||
                    termContains(asFunc.returnType, placeholder));
        } else if (term instanceof AlgebraicTypeTerm) {
            final AlgebraicTypeTerm asAlg = (AlgebraicTypeTerm)term;
            return someTermContains(asAlg.typeTerms, placeholder);
        } else {
            return false;
        }
    }
    
    // throws an exception if the term to map to contains the placeholder
    // assumes it is working with set representatives
    private void addMapping(final PlaceholderTypeTerm from,
                            final TypeTerm to)
        throws TypeErrorException {
        if (termContains(to, from)) {
            throw new TypeErrorException("attempt to create cyclic term");
        } else {
            map.put(from, to);
        }
    }

    public void unifyMulti(final List<TypeTerm> left,
                           final List<TypeTerm> right) throws TypeErrorException {
        final Iterator<TypeTerm> leftParams = left.iterator();
        final Iterator<TypeTerm> rightParams = right.iterator();

        while (leftParams.hasNext() && rightParams.hasNext()) {
            unify(leftParams.next(), rightParams.next());
        }

        if (leftParams.hasNext() || rightParams.hasNext()) {
            throw new TypeErrorException("Mismatch in number of params");
        }
    }
    
    // returns if they could unify, else throws an exception.
    // left in an inconsistent state if unification fails
    public void unify(TypeTerm left, TypeTerm right)
        throws TypeErrorException {
        left = setRepresentativeFor(left);
        right = setRepresentativeFor(right);

        if (left.equals(right)) {
            // nothing to learn - just return
            // handles all the simple cases
            return;
        } else if (left instanceof PlaceholderTypeTerm) {
            addMapping((PlaceholderTypeTerm)left, right);
        } else if (right instanceof PlaceholderTypeTerm) {
            addMapping((PlaceholderTypeTerm)right, left);
        } else if (left instanceof FunctionTypeTerm &&
                   right instanceof FunctionTypeTerm) {
            final FunctionTypeTerm leftFunc = (FunctionTypeTerm)left;
            final FunctionTypeTerm rightFunc = (FunctionTypeTerm)right;
            unifyMulti(leftFunc.params, rightFunc.params);
            unify(leftFunc.returnType, rightFunc.returnType);
        } else if (left instanceof AlgebraicTypeTerm &&
                   right instanceof AlgebraicTypeTerm) {
            unifyMulti(((AlgebraicTypeTerm)left).typeTerms,
                       ((AlgebraicTypeTerm)right).typeTerms);
        } else {
            throw new TypeErrorException("type mismatch: " + left + ", " + right);
        }
    }
}
