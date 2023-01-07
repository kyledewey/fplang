package fplang.parser;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import fplang.lexer.*;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ParserTest {
    public static Parser mkParser(final Token... tokens) {
        return new Parser(tokens);
    }

    @Test
    public void testParseInteger() throws ParseException {
        assertEquals(new ParseResult<Exp>(new IntLiteralExp(123), 1),
                     mkParser(new IntegerToken(123)).parseExp(0));
    }

    @Test
    public void testParseTrue() throws ParseException {
        assertEquals(new ParseResult<Exp>(new BooleanLiteralExp(true), 1),
                     mkParser(new TrueToken()).parseExp(0));
    }

    @Test
    public void testParseFalse() throws ParseException {
        assertEquals(new ParseResult<Exp>(new BooleanLiteralExp(false), 1),
                     mkParser(new FalseToken()).parseExp(0));
    }

    @Test
    public void testParseVariable() throws ParseException {
        // x
        assertEquals(new ParseResult<Exp>(new VariableExp(new Variable("x")), 1),
                     mkParser(new IdentifierToken("x")).parseExp(0));
    }
    
    @Test
    public void testParsePlusBasic() throws ParseException {
        // 1 + 2
        final ParseResult<Exp> expected =
            new ParseResult<Exp>(new OpExp(new IntLiteralExp(1),
                                           new PlusOp(),
                                           new IntLiteralExp(2)),
                                 3);
        final ParseResult<Exp> received =
            mkParser(new IntegerToken(1),
                     new PlusToken(),
                     new IntegerToken(2)).parseExp(0);
        assertEquals(expected, received);
    }

    @Test
    public void testParsePlusMulti() throws ParseException {
        // 1 + 2 + 3 == (1 + 2) + 3
        final ParseResult<Exp> expected =
            new ParseResult<Exp>(new OpExp(new OpExp(new IntLiteralExp(1),
                                                     new PlusOp(),
                                                     new IntLiteralExp(2)),
                                           new PlusOp(),
                                           new IntLiteralExp(3)),
                                 5);
        final ParseResult<Exp> received =
            mkParser(new IntegerToken(1),
                     new PlusToken(),
                     new IntegerToken(2),
                     new PlusToken(),
                     new IntegerToken(3)).parseExp(0);
        assertEquals(expected, received);
    }

    @Test
    public void testParseLessThanBasic() throws ParseException {
        // 1 < 2
        final ParseResult<Exp> expected =
            new ParseResult<Exp>(new OpExp(new IntLiteralExp(1),
                                           new LessThanOp(),
                                           new IntLiteralExp(2)),
                                 3);
        final ParseResult<Exp> received =
            mkParser(new IntegerToken(1),
                     new LessThanToken(),
                     new IntegerToken(2)).parseExp(0);
        assertEquals(expected, received);
    }

    @Test
    public void testParseLessThanMulti() throws ParseException {
        // 1 < 2 < 3 == (1 < 2) < 3
        final ParseResult<Exp> expected =
            new ParseResult<Exp>(new OpExp(new OpExp(new IntLiteralExp(1),
                                                     new LessThanOp(),
                                                     new IntLiteralExp(2)),
                                           new LessThanOp(),
                                           new IntLiteralExp(3)),
                                 5);
        final ParseResult<Exp> received =
            mkParser(new IntegerToken(1),
                     new LessThanToken(),
                     new IntegerToken(2),
                     new LessThanToken(),
                     new IntegerToken(3)).parseExp(0);
        assertEquals(expected, received);
    }

    @Test
    public void testParseEqualsBasic() throws ParseException {
        // 1 == 2
        final ParseResult<Exp> expected =
            new ParseResult<Exp>(new OpExp(new IntLiteralExp(1),
                                           new EqualsOp(),
                                           new IntLiteralExp(2)),
                                 3);
        final ParseResult<Exp> received =
            mkParser(new IntegerToken(1),
                     new DoubleEqualsToken(),
                     new IntegerToken(2)).parseExp(0);
        assertEquals(expected, received);
    }

    @Test
    public void testParseEqualsMulti() throws ParseException {
        // 1 == 2 == 3: (1 == 2) == 3
        final ParseResult<Exp> expected =
            new ParseResult<Exp>(new OpExp(new OpExp(new IntLiteralExp(1),
                                                     new EqualsOp(),
                                                     new IntLiteralExp(2)),
                                           new EqualsOp(),
                                           new IntLiteralExp(3)),
                                 5);
        final ParseResult<Exp> received =
            mkParser(new IntegerToken(1),
                     new DoubleEqualsToken(),
                     new IntegerToken(2),
                     new DoubleEqualsToken(),
                     new IntegerToken(3)).parseExp(0);
        assertEquals(expected, received);
    }

    @Test
    public void testParseMixed() throws ParseException {
        // 1 + 2 < 3 + 4 == false
        // ((1 + 2) < (3 + 4)) == false
        final Exp onePlusTwo = new OpExp(new IntLiteralExp(1),
                                         new PlusOp(),
                                         new IntLiteralExp(2));
        final Exp threePlusFour = new OpExp(new IntLiteralExp(3),
                                            new PlusOp(),
                                            new IntLiteralExp(4));
        final Exp lessThan = new OpExp(onePlusTwo,
                                       new LessThanOp(),
                                       threePlusFour);
        final ParseResult<Exp> expected =
            new ParseResult<Exp>(new OpExp(lessThan,
                                           new EqualsOp(),
                                           new BooleanLiteralExp(false)),
                                 9);
        final ParseResult<Exp> received =
            mkParser(new IntegerToken(1),
                     new PlusToken(),
                     new IntegerToken(2),
                     new LessThanToken(),
                     new IntegerToken(3),
                     new PlusToken(),
                     new IntegerToken(4),
                     new DoubleEqualsToken(),
                     new FalseToken()).parseExp(0);
        assertEquals(expected, received);
    }

    @Test
    public void testParseParens() throws ParseException {
        // 1 + (2 + 3)
        final ParseResult<Exp> expected =
            new ParseResult<Exp>(new OpExp(new IntLiteralExp(1),
                                           new PlusOp(),
                                           new OpExp(new IntLiteralExp(2),
                                                     new PlusOp(),
                                                     new IntLiteralExp(3))),
                                 7);
        final ParseResult<Exp> received =
            mkParser(new IntegerToken(1),
                     new PlusToken(),
                     new LeftParenToken(),
                     new IntegerToken(2),
                     new PlusToken(),
                     new IntegerToken(3),
                     new RightParenToken()).parseExp(0);
        assertEquals(expected, received);
    }

    @Test
    public void testParseCallNoParams() throws ParseException {
        // x()
        final ParseResult<Exp> expected =
            new ParseResult<Exp>(new CallLikeExp(new VariableExp(new Variable("x")),
                                                 new ArrayList<Exp>()),
                                 3);
        final ParseResult<Exp> received =
            mkParser(new IdentifierToken("x"),
                     new LeftParenToken(),
                     new RightParenToken()).parseExp(0);
        assertEquals(expected, received);
    }

    @Test
    public void testParseCallOneParam() throws ParseException {
        // x(1)
        final ParseResult<Exp> expected =
            new ParseResult<Exp>(new CallLikeExp(new VariableExp(new Variable("x")),
                                                 Arrays.asList(new IntLiteralExp(1))),
                                 4);
        final ParseResult<Exp> received =
            mkParser(new IdentifierToken("x"),
                     new LeftParenToken(),
                     new IntegerToken(1),
                     new RightParenToken()).parseExp(0);
        assertEquals(expected, received);
    }

    @Test
    public void testParseCallTwoParams() throws ParseException {
        // x(1, 2)
        final ParseResult<Exp> expected =
            new ParseResult<Exp>(new CallLikeExp(new VariableExp(new Variable("x")),
                                                 Arrays.asList(new IntLiteralExp(1),
                                                               new IntLiteralExp(2))),
                                 6);
        final ParseResult<Exp> received =
            mkParser(new IdentifierToken("x"),
                     new LeftParenToken(),
                     new IntegerToken(1),
                     new CommaToken(),
                     new IntegerToken(2),
                     new RightParenToken()).parseExp(0);
        assertEquals(expected, received);
    }

    @Test
    public void testChainedCallNoParams() throws ParseException {
        // foo()()
        final Exp fooCall =
            new CallLikeExp(new VariableExp(new Variable("foo")),
                            new ArrayList<Exp>());
        final Exp chainedCall =
            new CallLikeExp(fooCall, new ArrayList<Exp>());
        final ParseResult<Exp> expected =
            new ParseResult<Exp>(chainedCall, 5);
        final ParseResult<Exp> received =
            mkParser(new IdentifierToken("foo"),
                     new LeftParenToken(),
                     new RightParenToken(),
                     new LeftParenToken(),
                     new RightParenToken()).parseExp(0);
        assertEquals(expected, received);
    }

    @Test
    public void testChainedCallWithSingleParams() throws ParseException {
        // foo(1)(2)
        final Exp fooCall =
            new CallLikeExp(new VariableExp(new Variable("foo")),
                            Arrays.asList(new IntLiteralExp(1)));
        final Exp chainedCall =
            new CallLikeExp(fooCall,
                            Arrays.asList(new IntLiteralExp(2)));
        final ParseResult<Exp> expected =
            new ParseResult<Exp>(chainedCall, 7);
        final ParseResult<Exp> received =
            mkParser(new IdentifierToken("foo"),
                     new LeftParenToken(),
                     new IntegerToken(1),
                     new RightParenToken(),
                     new LeftParenToken(),
                     new IntegerToken(2),
                     new RightParenToken()).parseExp(0);
        assertEquals(expected, received);
    }

    @Test
    public void testChainedCallWithMultiParams() throws ParseException {
        // foo(1, x)(2, y)
        final Exp fooCall =
            new CallLikeExp(new VariableExp(new Variable("foo")),
                            Arrays.asList(new IntLiteralExp(1),
                                          new VariableExp(new Variable("x"))));
        final Exp chainedCall =
            new CallLikeExp(fooCall,
                            Arrays.asList(new IntLiteralExp(2),
                                          new VariableExp(new Variable("y"))));
        final ParseResult<Exp> expected =
            new ParseResult<Exp>(chainedCall, 11);
        final ParseResult<Exp> received =
            mkParser(new IdentifierToken("foo"),
                     new LeftParenToken(),
                     new IntegerToken(1),
                     new CommaToken(),
                     new IdentifierToken("x"),
                     new RightParenToken(),
                     new LeftParenToken(),
                     new IntegerToken(2),
                     new CommaToken(),
                     new IdentifierToken("y"),
                     new RightParenToken()).parseExp(0);
        assertEquals(expected, received);
    }

    // type Either[A, B] = Left(A) | Right(B);
    public static final AlgDef EITHER_ALG_DEF =
        new AlgDef(new AlgName("Either"),
                   Arrays.asList(new Typevar("A"),
                                 new Typevar("B")),
                   Arrays.asList(new ConsDef(new ConsName("Left"),
                                             Arrays.asList(new TypevarType(new Typevar("A")))),
                                 new ConsDef(new ConsName("Right"),
                                             Arrays.asList(new TypevarType(new Typevar("B"))))));

    public static final Parser EITHER_PARSER =
        mkParser(new TypeToken(),
                 new IdentifierToken("Either"),
                 new LeftSquareBracketToken(),
                 new IdentifierToken("A"),
                 new CommaToken(),
                 new IdentifierToken("B"),
                 new RightSquareBracketToken(),
                 new SingleEqualsToken(),
                 new IdentifierToken("Left"),
                 new LeftParenToken(),
                 new IdentifierToken("A"),
                 new RightParenToken(),
                 new PipeToken(),
                 new IdentifierToken("Right"),
                 new LeftParenToken(),
                 new IdentifierToken("B"),
                 new RightParenToken(),
                 new SemicolonToken());
    
    @Test
    public void testAlgebraicEither() throws ParseException {
        final ParseResult<AlgDef> expected =
            new ParseResult<AlgDef>(EITHER_ALG_DEF, 18);
        final ParseResult<AlgDef> received =
            EITHER_PARSER.parseAlgDef(0);
        assertEquals(expected, received);
    }

    @Test
    public void testAlgebraicEitherTryParseMulti() throws ParseException {
        final ParseResult<List<AlgDef>> expected =
            new ParseResult<List<AlgDef>>(Arrays.asList(EITHER_ALG_DEF), 18);
        final ParseResult<List<AlgDef>> received =
            EITHER_PARSER.parseAlgDefs(0);
        assertEquals(expected, received);
    }

    // type List[A] = Cons(A, List[A]) | Nil();
    public static final AlgDef LIST_ALG_DEF =
        new AlgDef(new AlgName("List"),
                   Arrays.asList(new Typevar("A")),
                   Arrays.asList(new ConsDef(new ConsName("Cons"),
                                             Arrays.asList(new TypevarType(new Typevar("A")),
                                                           new AlgebraicType(new AlgName("List"),
                                                                             Arrays.asList(new TypevarType(new Typevar("A")))))),
                                 new ConsDef(new ConsName("Nil"),
                                             new ArrayList<Type>())));
    public static final Parser LIST_PARSER =
        mkParser(new TypeToken(),
                 new IdentifierToken("List"),
                 new LeftSquareBracketToken(),
                 new IdentifierToken("A"),
                 new RightSquareBracketToken(),
                 new SingleEqualsToken(),
                 new IdentifierToken("Cons"),
                 new LeftParenToken(),
                 new IdentifierToken("A"),
                 new CommaToken(),
                 new IdentifierToken("List"),
                 new LeftSquareBracketToken(),
                 new IdentifierToken("A"),
                 new RightSquareBracketToken(),
                 new RightParenToken(),
                 new PipeToken(),
                 new IdentifierToken("Nil"),
                 new LeftParenToken(),
                 new RightParenToken(),
                 new SemicolonToken());

    @Test
    public void testAlgebraicList() throws ParseException {
        final ParseResult<AlgDef> expected =
            new ParseResult<AlgDef>(LIST_ALG_DEF, 20);
        final ParseResult<AlgDef> received =
            LIST_PARSER.parseAlgDef(0);
        assertEquals(expected, received);
    }

    @Test
    public void testAlgebraicListTryParseMulti() throws ParseException {
        final ParseResult<List<AlgDef>> expected =
            new ParseResult<List<AlgDef>>(Arrays.asList(LIST_ALG_DEF), 20);
        final ParseResult<List<AlgDef>> received =
            LIST_PARSER.parseAlgDefs(0);
        assertEquals(expected, received);
    }

    @Test
    public void testParseNonGenericFunction() throws ParseException {
        // def add[](x: int, y: int): int = x + y;
        final FunctionDef functionDef =
            new FunctionDef(new FunctionName("add"),
                            new ArrayList<Typevar>(),
                            Arrays.asList(new Vardec(new Variable("x"),
                                                     new IntType()),
                                          new Vardec(new Variable("y"),
                                                     new IntType())),
                            new IntType(),
                            new OpExp(new VariableExp(new Variable("x")),
                                      new PlusOp(),
                                      new VariableExp(new Variable("y"))));
        final ParseResult<FunctionDef> expected =
            new ParseResult<FunctionDef>(functionDef, 20);
        final ParseResult<FunctionDef> received =
            mkParser(new DefToken(),
                     new IdentifierToken("add"),
                     new LeftSquareBracketToken(),
                     new RightSquareBracketToken(),
                     new LeftParenToken(),
                     new IdentifierToken("x"),
                     new ColonToken(),
                     new IntToken(),
                     new CommaToken(),
                     new IdentifierToken("y"),
                     new ColonToken(),
                     new IntToken(),
                     new RightParenToken(),
                     new ColonToken(),
                     new IntToken(),
                     new SingleEqualsToken(),
                     new IdentifierToken("x"),
                     new PlusToken(),
                     new IdentifierToken("y"),
                     new SemicolonToken()).parseFunctionDef(0);
        assertEquals(expected, received);
    }
}

