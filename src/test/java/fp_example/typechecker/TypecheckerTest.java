package fp_example.typechecker;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import static org.junit.Assert.fail;
import org.junit.Test;

import fp_example.parser.*;

public class TypecheckerTest {
    public static Typechecker emptyTypechecker() throws TypeErrorException {
        return new Typechecker(new Program(new ArrayList<AlgDef>(),
                                           new ArrayList<FunctionDef>(),
                                           new IntLiteralExp(0)));
    }

    public static void assertExpHasType(final Type expectedType,
                                        final Map<Variable, TypeTerm> typeEnvironment,
                                        final Exp exp) throws TypeErrorException {
        final Unifier unifier = new Unifier();
        final TypeTerm receivedType =
            emptyTypechecker().typeofExp(exp, typeEnvironment, unifier);
        if (expectedType != null) {
            final TypeTerm expectedTypeAsTerm =
                Typechecker.translateType(expectedType,
                                          new HashMap<Typevar, TypeTerm>());
            unifier.unify(expectedTypeAsTerm, receivedType);
        } else {
            fail("Expected exception to be thrown; received type: " +
                 unifier.transitiveSetRepresentativeFor(receivedType));
        }
    }

    public static void assertExpHasType(final Type expectedType,
                                        final Exp exp) throws TypeErrorException {
        assertExpHasType(expectedType, new HashMap<Variable, TypeTerm>(), exp);
    }
    
    @Test
    public void testIntLiteralExp() throws TypeErrorException {
        assertExpHasType(new IntType(),
                         new IntLiteralExp(0));
    }
}
