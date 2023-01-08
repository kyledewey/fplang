package fplang.typechecker;

import fplang.parser.ConsName;

import java.util.Set;

public class MatchNone implements MatchValue {
    public MatchValue join(final MatchValue other,
                           final Set<ConsName> expectedConstructors) throws TypeErrorException {
        return other;
    }
    public boolean isMatchAll() {
        return false;
    }
}
