package fplang.typechecker;

import fplang.parser.AlgName;
import fplang.parser.ConsName;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class MatchSome implements MatchValue {
    public final AlgName forType;
    public final Map<ConsName, List<MatchValue>> matchedSoFar;
    
    public MatchSome(final AlgName forType,
                     final Map<ConsName, List<MatchValue>> matchedSoFar) {
        this.forType = forType;
        this.matchedSoFar = matchedSoFar;
    }

    public static List<MatchValue> joinLists(final List<MatchValue> list1,
                                             final List<MatchValue> list2,
                                             final Set<ConsName> expectedConstructors) {
        final List<MatchValue> retval = new ArrayList<MatchValue>();
        final Iterator<MatchValue> list1Iterator = list1.iterator();
        final Iterator<MatchValue> list2Iterator = list2.iterator();
        while (list1Iterator.hasNext() &&
               list2Iterator.hasNext()) {
            retval.add(list1Iterator.next().join(list2Iterator.next(),
                                                 expectedConstructors));
        }
        assert(!list1Iterator.hasNext());
        assert(!list2Iterator.hasNext());
        return retval;
    }

    public static boolean allHandled(final Set<ConsName> expectedConstructors,
                                     final AlgName forType,
                                     final Map<ConsName, List<MatchValue>> map) {
        if (map.keySet().equals(expectedConstructors)) {
            for (final List<MatchValue> list : map.values()) {
                for (final MatchValue matchValue : list) {
                    if (!matchValue.isMatchAll()) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }
                
    public static Map<ConsName, List<MatchValue>> joinMaps(final Map<ConsName, List<MatchValue>> map1,
                                                           final Map<ConsName, List<MatchValue>> map2,
                                                           final Set<ConsName> expectedConstructors) {
        // for any constructor that's in both, those lists need to be joined
        // otherwise, we just include it in the result
        final Map<ConsName, List<MatchValue>> retval = new HashMap<ConsName, List<MatchValue>>();
        for (final Map.Entry<ConsName, List<MatchValue>> pair : map1.entrySet()) {
            final ConsName consName = pair.getKey();
            final List<MatchValue> subValues = pair.getValue();
            final List<MatchValue> otherList = map2.get(consName);
            final List<MatchValue> listForThis =
                (otherList != null) ? joinLists(subValues,
                                                otherList,
                                                expectedConstructors) : subValues;
            retval.put(consName, listForThis);
        }
        for (final Map.Entry<ConsName, List<MatchValue>> pair : map2.entrySet()) {
            if (!map1.containsKey(pair.getKey())) {
                retval.put(pair.getKey(), pair.getValue());
            }
        }
        return retval;
    }
    
    public MatchValue join(final MatchValue other,
                           final Set<ConsName> expectedConstructors) throws TypeErrorException {
        if (other instanceof MatchAll) {
            return other;
        } else if (other instanceof MatchNone) {
            return this;
        } else if (other instanceof MatchSome) {
            final MatchSome otherSome = (MatchSome)other;
            if (!forType.equals(otherSome.forType)) {
                throw new TypeErrorException("Type mismatch in pattern for: " + forType.toString());
            }
            final Map<ConsName, List<MatchValue>> resultMap = joinMaps(matchedSoFar,
                                                                       otherSome.matchedSoFar,
                                                                       expectedConstructors);
            if (allHandled(expectedConstructors, forType, resultMap)) {
                return new MatchAll();
            } else {
                return new MatchSome(forType, resultMap);
            }
        } else {
            throw new TypeErrorException("Unknown match value: " + other.toString());
        }
    }

    public boolean isMatchAll() {
        return false;
    }
}

