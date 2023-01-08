package fplang.typechecker;

import fplang.parser.ConsName;

import java.util.Set;

public class MatchAll implements MatchValue {
    public MatchValue join(final MatchValue other,
                           final Set<ConsName> expectedConstructors) throws TypeErrorException {
        return this;
    }
    public boolean isMatchAll() {
        return true;
    }
}
