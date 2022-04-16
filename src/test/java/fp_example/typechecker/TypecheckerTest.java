package fp_example.typechecker;

import java.util.List;
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

    @Test
    public void testLetExp() throws TypeErrorException {
        // let x = 5 in x
        assertExpHasType(new IntType(),
                         new LetExp(new Variable("x"),
                                    new IntLiteralExp(5),
                                    new VariableExp(new Variable("x"))));
    }

    @Test
    public void testMakeFunctionExp() throws TypeErrorException {
        // (x) => x + 1
        final List<Type> paramTypes = new ArrayList<Type>();
        paramTypes.add(new IntType());
        final Type expectedType = new FunctionType(paramTypes, new IntType());
        
        final List<Variable> params = new ArrayList<Variable>();
        params.add(new Variable("x"));
        final Exp exp =
            new MakeHigherOrderFunctionExp(params,
                                           new OpExp(new VariableExp(new Variable("x")),
                                                     new PlusOp(),
                                                     new IntLiteralExp(1)));
        
        assertExpHasType(expectedType, exp);
    }

    @Test
    public void testCallFunctionExp() throws TypeErrorException {
        // ((x) => x + 1)(5)
        final List<Variable> params = new ArrayList<Variable>();
        params.add(new Variable("x"));
        final Exp function =
            new MakeHigherOrderFunctionExp(params,
                                           new OpExp(new VariableExp(new Variable("x")),
                                                     new PlusOp(),
                                                     new IntLiteralExp(1)));
        final List<Exp> callParams = new ArrayList<Exp>();
        callParams.add(new IntLiteralExp(5));
        
        final Exp call =
            new CallHigherOrderFunctionExp(function, callParams);

        assertExpHasType(new IntType(), call);
    }

    @Test
    public void testIfWellTyped() throws TypeErrorException {
        // if (true) 1 else 2
        assertExpHasType(new IntType(),
                         new IfExp(new BooleanLiteralExp(true),
                                   new IntLiteralExp(1),
                                   new IntLiteralExp(2)));
    }

    @Test(expected = TypeErrorException.class)
    public void testIfIllTypedGuard() throws TypeErrorException {
        // if (5) 1 else 2
        assertExpHasType(null,
                         new IfExp(new IntLiteralExp(5),
                                   new IntLiteralExp(1),
                                   new IntLiteralExp(2)));
    }

    @Test(expected = TypeErrorException.class)
    public void testIfIllTypedDifferentBranches() throws TypeErrorException {
        // if (true) 1 else false
        assertExpHasType(null,
                         new IfExp(new BooleanLiteralExp(true),
                                   new IntLiteralExp(1),
                                   new BooleanLiteralExp(false)));
    }

    @Test
    public void testPrintlnExp() throws TypeErrorException {
        // println(5)
        assertExpHasType(new VoidType(),
                         new PrintlnExp(new IntLiteralExp(5)));
    }

    @Test(expected = TypeErrorException.class)
    public void testPrintlnExpIllTyped() throws TypeErrorException {
        // println(true < false)
        assertExpHasType(null,
                         new PrintlnExp(new OpExp(new BooleanLiteralExp(true),
                                                  new LessThanOp(),
                                                  new BooleanLiteralExp(false))));
    }
    
    @Test
    public void testBlock() throws TypeErrorException {
        // { 5; true }
        final List<Exp> exps = new ArrayList<Exp>();
        exps.add(new IntLiteralExp(5));
        exps.add(new BooleanLiteralExp(true));
        assertExpHasType(new BoolType(),
                         new BlockExp(exps));
    }

    // TODO: call named functions
    // TODO: call named functions with generics
    // TODO: constructors
    // TODO: generic constructors
    // TODO: match
}
