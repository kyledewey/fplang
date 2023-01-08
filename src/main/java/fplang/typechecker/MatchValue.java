package fplang.typechecker;

import fplang.parser.ConsName;

import java.util.Set;

// Used for determining if a pattern match is exhaustive.
// Representation of all the values that a given pattern
// handles.
//
// MatchValue ::=
//   MatchAll |
//   MatchNone |
//   MatchSome(AlgName, Map<ConsName, List<MatchValue>>)
//
// MatchAll means that all cases are handled
//
// MatchNone means no cases are handled
//
// MatchSome means something in between.
//   -AlgName is the specific algrebraic data type this is for
//   -The mapping records the patterns that are handled for
//    each of the constructors for this algebraic data type

public interface MatchValue {
    // Used to combine two patterns and form a new one.
    // The resulting MatchValue represents the union of everything covered
    // by this pattern and other.  The use of the word "join" is because
    // this is forming a lattice (https://en.wikipedia.org/wiki/Lattice_(order)),
    // which are commonly used in program analysis.  This is a form of
    // program analysis.
    public MatchValue join(final MatchValue other,
                           final Set<ConsName> expectedConstructors) throws TypeErrorException;
    public boolean isMatchAll();
}
