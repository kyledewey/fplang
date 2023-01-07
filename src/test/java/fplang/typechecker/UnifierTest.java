package fplang.typechecker;

import java.util.List;
import java.util.ArrayList;

import fplang.parser.Typevar;
import fplang.parser.AlgName;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class UnifierTest {
    @Test
    public void testIdenticalPlaceholdersUnify() throws TypeErrorException {
        final Unifier unifier = new Unifier();
        unifier.unify(new PlaceholderTypeTerm(0),
                      new PlaceholderTypeTerm(0));
        assertTrue(unifier.getMapping().isEmpty());
    }

    @Test
    public void testNonIdenticalPlaceholdersUnify() throws TypeErrorException {
        final Unifier unifier = new Unifier();
        final PlaceholderTypeTerm p0 = new PlaceholderTypeTerm(0);
        final PlaceholderTypeTerm p1 = new PlaceholderTypeTerm(1);
        unifier.unify(p0, p1);
        final TypeTerm representative = unifier.transitiveSetRepresentativeFor(p0);
        assertEquals(representative,
                     unifier.transitiveSetRepresentativeFor(p1));
        assertTrue(representative.equals(p0) ||
                   representative.equals(p1));
    }

    @Test
    public void testIntsUnify() throws TypeErrorException {
        final Unifier unifier = new Unifier();
        unifier.unify(new IntTypeTerm(), new IntTypeTerm());
        assertTrue(unifier.getMapping().isEmpty());
    }

    @Test
    public void testBoolsUnify() throws TypeErrorException {
        final Unifier unifier = new Unifier();
        unifier.unify(new BoolTypeTerm(), new BoolTypeTerm());
        assertTrue(unifier.getMapping().isEmpty());
    }

    @Test
    public void testVoidsUnify() throws TypeErrorException {
        final Unifier unifier = new Unifier();
        unifier.unify(new VoidTypeTerm(), new VoidTypeTerm());
        assertTrue(unifier.getMapping().isEmpty());
    }

    @Test
    public void testIdenticalTypevarsUnify() throws TypeErrorException {
        final Unifier unifier = new Unifier();
        unifier.unify(new TypevarTypeTerm(new Typevar("A")),
                      new TypevarTypeTerm(new Typevar("A")));
        assertTrue(unifier.getMapping().isEmpty());
    }

    @Test(expected = TypeErrorException.class)
    public void testNonIdenticalTypevarsDontUnify() throws TypeErrorException {
        final Unifier unifier = new Unifier();
        unifier.unify(new TypevarTypeTerm(new Typevar("A")),
                      new TypevarTypeTerm(new Typevar("B")));
    }

    @Test
    public void testIdenticalFunctionsUnify() throws TypeErrorException {
        // (int, bool) => void
        final List<TypeTerm> firstParams = new ArrayList<TypeTerm>();
        firstParams.add(new IntTypeTerm());
        firstParams.add(new BoolTypeTerm());
        final TypeTerm first = new FunctionTypeTerm(firstParams, new VoidTypeTerm());

        final List<TypeTerm> secondParams = new ArrayList<TypeTerm>();
        secondParams.add(new IntTypeTerm());
        secondParams.add(new BoolTypeTerm());
        final TypeTerm second = new FunctionTypeTerm(secondParams, new VoidTypeTerm());

        final Unifier unifier = new Unifier();
        unifier.unify(first, second);
        assertTrue(unifier.getMapping().isEmpty());
    }

    @Test
    public void testTransitivelyIdenticalFunctionsUnify() throws TypeErrorException {
        // p0 -> (int, p1) => void
        // (p2, bool) => p3

        final Unifier unifier = new Unifier();
        
        final List<TypeTerm> firstParams = new ArrayList<TypeTerm>();
        firstParams.add(new IntTypeTerm());
        firstParams.add(new PlaceholderTypeTerm(1));
        final TypeTerm first = new PlaceholderTypeTerm(0);
        unifier.unify(first, new FunctionTypeTerm(firstParams, new VoidTypeTerm()));

        final List<TypeTerm> secondParams = new ArrayList<TypeTerm>();
        secondParams.add(new PlaceholderTypeTerm(2));
        secondParams.add(new BoolTypeTerm());
        final TypeTerm second = new FunctionTypeTerm(secondParams, new PlaceholderTypeTerm(3));

        unifier.unify(first, second);

        final List<TypeTerm> expectedParams = new ArrayList<TypeTerm>();
        expectedParams.add(new IntTypeTerm());
        expectedParams.add(new BoolTypeTerm());
        final TypeTerm expected = new FunctionTypeTerm(expectedParams, new VoidTypeTerm());
                                                       
        assertEquals(expected,
                     unifier.transitiveSetRepresentativeFor(new PlaceholderTypeTerm(0)));
        assertEquals(new BoolTypeTerm(),
                     unifier.transitiveSetRepresentativeFor(new PlaceholderTypeTerm(1)));
        assertEquals(new IntTypeTerm(),
                     unifier.transitiveSetRepresentativeFor(new PlaceholderTypeTerm(2)));
        assertEquals(new VoidTypeTerm(),
                     unifier.transitiveSetRepresentativeFor(new PlaceholderTypeTerm(3)));
    }

    @Test(expected = TypeErrorException.class)
    public void testNonIdenticalFunctionsDontUnify() throws TypeErrorException {
        // (int) => bool
        // (int) => void

        final List<TypeTerm> firstParams = new ArrayList<TypeTerm>();
        firstParams.add(new IntTypeTerm());

        final List<TypeTerm> secondParams = new ArrayList<TypeTerm>();
        secondParams.add(new IntTypeTerm());

        final Unifier unifier = new Unifier();
        unifier.unify(new FunctionTypeTerm(firstParams, new BoolTypeTerm()),
                      new FunctionTypeTerm(secondParams, new VoidTypeTerm()));
    }

    @Test
    public void testIdenticalAlgebraicsUnify() throws TypeErrorException {
        // Foo[Int, Bool]
        final List<TypeTerm> firstParams = new ArrayList<TypeTerm>();
        firstParams.add(new IntTypeTerm());
        firstParams.add(new BoolTypeTerm());
        
        final List<TypeTerm> secondParams = new ArrayList<TypeTerm>();
        secondParams.add(new IntTypeTerm());
        secondParams.add(new BoolTypeTerm());

        final Unifier unifier = new Unifier();
        unifier.unify(new AlgebraicTypeTerm(new AlgName("Foo"),
                                            firstParams),
                      new AlgebraicTypeTerm(new AlgName("Foo"),
                                            secondParams));
    }

    @Test
    public void testTransitivelyIdenticalAlgebraicsUnify() throws TypeErrorException {
        // p0 -> Foo[int, p1]
        // Foo[p2, bool]
        final Unifier unifier = new Unifier();

        final List<TypeTerm> firstParams = new ArrayList<TypeTerm>();
        firstParams.add(new IntTypeTerm());
        firstParams.add(new PlaceholderTypeTerm(1));
        unifier.unify(new PlaceholderTypeTerm(0),
                      new AlgebraicTypeTerm(new AlgName("Foo"), firstParams));

        final List<TypeTerm> secondParams = new ArrayList<TypeTerm>();
        secondParams.add(new PlaceholderTypeTerm(2));
        secondParams.add(new BoolTypeTerm());
        
        final List<TypeTerm> expectedParams = new ArrayList<TypeTerm>();
        expectedParams.add(new IntTypeTerm());
        expectedParams.add(new BoolTypeTerm());
        final TypeTerm expected = new AlgebraicTypeTerm(new AlgName("Foo"),
                                                        expectedParams);

        unifier.unify(new PlaceholderTypeTerm(0),
                      new AlgebraicTypeTerm(new AlgName("Foo"),
                                            secondParams));

        assertEquals(expected,
                     unifier.transitiveSetRepresentativeFor(new PlaceholderTypeTerm(0)));
        assertEquals(new BoolTypeTerm(),
                     unifier.transitiveSetRepresentativeFor(new PlaceholderTypeTerm(1)));
        assertEquals(new IntTypeTerm(),
                     unifier.transitiveSetRepresentativeFor(new PlaceholderTypeTerm(2)));
    }

    @Test(expected = TypeErrorException.class)
    public void testNonIdenticalAlgebraicsDontUnify_differentNames() throws TypeErrorException {
        // Foo[]
        // Bar[]
        final Unifier unifier = new Unifier();
        unifier.unify(new AlgebraicTypeTerm(new AlgName("Foo"),
                                            new ArrayList<TypeTerm>()),
                      new AlgebraicTypeTerm(new AlgName("Bar"),
                                            new ArrayList<TypeTerm>()));
    }

    @Test(expected = TypeErrorException.class)
    public void testNonIdenticalAlgebraicsDontUnify_differentNumParams() throws TypeErrorException {
        // Foo[]
        // Foo[int]
        final TypeTerm first = new AlgebraicTypeTerm(new AlgName("Foo"),
                                                     new ArrayList<TypeTerm>());

        final List<TypeTerm> secondParams = new ArrayList<TypeTerm>();
        secondParams.add(new IntTypeTerm());

        final Unifier unifier = new Unifier();
        unifier.unify(first,
                      new AlgebraicTypeTerm(new AlgName("Foo"),
                                            secondParams));
    }

    @Test(expected = TypeErrorException.class)
    public void testNonIdenticalAlgebraicsDontUnify_differentParams() throws TypeErrorException {
        // Foo[bool]
        // Foo[int]
        final List<TypeTerm> firstParams = new ArrayList<TypeTerm>();
        firstParams.add(new BoolTypeTerm());

        final List<TypeTerm> secondParams = new ArrayList<TypeTerm>();
        secondParams.add(new IntTypeTerm());

        final Unifier unifier = new Unifier();
        unifier.unify(new AlgebraicTypeTerm(new AlgName("Foo"), firstParams),
                      new AlgebraicTypeTerm(new AlgName("Foo"), secondParams));
    }
}
