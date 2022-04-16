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
        TypeTerm receivedType = emptyTypechecker().typeofExp(exp,
                                                             typeEnvironment,
                                                             unifier);
        receivedType = unifier.transitiveSetRepresentativeFor(receivedType);
        if (expectedType != null) {
            final TypeTerm expectedTypeAsTerm =
                Typechecker.translateType(expectedType,
                                          new HashMap<Typevar, TypeTerm>());
            unifier.unify(expectedTypeAsTerm, receivedType);
        } else {
            fail("Expected exception to be thrown; received type: " + receivedType);
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

    @Test
    public void testVariableInScope() throws TypeErrorException {
        final Map<Variable, TypeTerm> typeEnvironment =
            new HashMap<Variable, TypeTerm>();
        typeEnvironment.put(new Variable("x"), new IntTypeTerm());
        assertExpHasType(new IntType(),
                         typeEnvironment,
                         new VariableExp(new Variable("x")));
    }

    @Test(expected = TypeErrorException.class)
    public void testVariableNotInScope() throws TypeErrorException {
        assertExpHasType(null,
                         new VariableExp(new Variable("x")));
    }

    @Test
    public void testBooleanLiteralExp() throws TypeErrorException {
        assertExpHasType(new BoolType(),
                         new BooleanLiteralExp(true));
    }

    @Test
    public void testPlusExp() throws TypeErrorException {
        assertExpHasType(new IntType(),
                         new OpExp(new IntLiteralExp(1),
                                   new PlusOp(),
                                   new IntLiteralExp(2)));
    }

    @Test
    public void testLessThanExp() throws TypeErrorException {
        assertExpHasType(new BoolType(),
                         new OpExp(new IntLiteralExp(1),
                                   new LessThanOp(),
                                   new IntLiteralExp(2)));
    }

    @Test
    public void testEqualsExpInts() throws TypeErrorException {
        assertExpHasType(new BoolType(),
                         new OpExp(new IntLiteralExp(1),
                                   new EqualsOp(),
                                   new IntLiteralExp(2)));
    }

    @Test
    public void testEqualsExpBools() throws TypeErrorException {
        assertExpHasType(new BoolType(),
                         new OpExp(new BooleanLiteralExp(true),
                                   new EqualsOp(),
                                   new BooleanLiteralExp(false)));
    }

    @Test(expected = TypeErrorException.class)
    public void testEqualsExpIntBool() throws TypeErrorException {
        assertExpHasType(null,
                         new OpExp(new IntLiteralExp(0),
                                   new EqualsOp(),
                                   new BooleanLiteralExp(true)));
    }
}
