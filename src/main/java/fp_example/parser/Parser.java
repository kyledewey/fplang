package fp_example.parser;

import java.util.List;
import java.util.ArrayList;

import fp_example.lexer.*;

public class Parser {
    public final Token[] tokens;

    public Parser(final Token[] tokens) {
        this.tokens = tokens;
    }

    public Token getToken(final int position) throws ParseException {
        try {
            return tokens[position];
        } catch (final ArrayIndexOutOfBoundsException e) {
            throw new ParseException("Invalid token position: " + position);
        }
    }

    public void assertTokenHereIs(final int position, final Token expected) throws ParseException {
        final Token received = getToken(position);
        if (!received.equals(expected)) {
            throw new ParseException("Expected " + expected + "; received: " + received);
        }
    }

    // comma_types ::= [type (`,` type)*]
    public ParseResult<List<Type>> parseCommaTypes(int position) throws ParseException {
        final List<Type> types = new ArrayList<Type>();

        try {
            ParseResult<Type> current = parseType(position);
            types.add(current.result);
            position = current.position;
            boolean shouldRun = true;
            while (shouldRun) {
                try {
                    assertTokenHereIs(position, new CommaToken());
                    current = parseType(position + 1);
                    types.add(current.result);
                    position = current.position;
                } catch (final ParseException e) {
                    shouldRun = false;
                }
            }
        } catch (final ParseException e) {}

        return new ParseResult<List<Type>>(types, position);
    }

    // type ::= `int` | `bool` | `void` | id | `(` comma_types `)` `=>` type | id `[` comma_types `]`                
    public ParseResult<Type> parseType(final int position) throws ParseException {
        final Token token = getToken(position);
        if (token instanceof IntToken) {
            return new ParseResult(new IntType(), position + 1);
        } else if (token instanceof BoolToken) {
            return new ParseResult(new BoolType(), position + 1);
        } else if (token instanceof VoidToken) {
            return new ParseResult(new VoidType(), position + 1);
        } else if (token instanceof LeftParenToken) {
            final ParseResult<List<Type>> paramTypes = parseCommaTypes(position + 1);
            assertTokenHereIs(paramTypes.position, new RightParenToken());
            assertTokenHereIs(paramTypes.position + 1, new ArrowToken());
            final ParseResult<Type> returnType = parseType(paramTypes.position +2);
            return new ParseResult<Type>(new FunctionType(paramTypes.result,
                                                          returnType.result),
                                         returnType.position);
        } else if (token instanceof IdentifierToken) {
            // could be either a type variable or a algebraic type
            // try it as a algebraic, then fall back to identifier
            final String name = ((IdentifierToken)token).identifier;
            try {
                assertTokenHereIs(position + 1, new LeftSquareBracketToken());
                final ParseResult<List<Type>> types = parseCommaTypes(position + 2);
                assertTokenHereIs(types.position, new RightSquareBracketToken());
                return new ParseResult<Type>(new AlgebraicType(new AlgName(name),
                                                               types.result),
                                             types.position + 1);
            } catch (final ParseException e) {
                return new ParseResult<Type>(new TypevarType(new Typevar(name)),
                                             position + 1);
            }
        } else {
            throw new ParseException("Unknown type: " + token);
        }
    }

    public ParseResult<String> parseIdentifier(final int position) throws ParseException {
        final Token token = getToken(position);
        if (token instanceof IdentifierToken) {
            final String name = ((IdentifierToken)token).identifier;
            return new ParseResult<String>(name, position + 1);
        } else {
            throw new ParseException("Expected identifier; received; " + token);
        }
    }
    
    public ParseResult<Variable> parseVariable(final int position) throws ParseException {
        final ParseResult<String> id = parseIdentifier(position);
        return new ParseResult<Variable>(new Variable(id.result), id.position);
    }

    public ParseResult<ConsName> parseConsName(final int position) throws ParseException {
        final ParseResult<String> id = parseIdentifier(position);
        return new ParseResult<ConsName>(new ConsName(id.result), id.position);
    }

    public ParseResult<AlgName> parseAlgName(final int position) throws ParseException {
        final ParseResult<String> id = parseIdentifier(position);
        return new ParseResult<AlgName>(new AlgName(id.result), id.position);
    }

    public ParseResult<FunctionName> parseFunctionName(final int position) throws ParseException {
        final ParseResult<String> id = parseIdentifier(position);
        return new ParseResult<FunctionName>(new FunctionName(id.result), id.position);
    }
    
    // semicolon_exps ::= exp `;` (exp `;`)*
    public ParseResult<List<Exp>> parseSemicolonExps(int position) throws ParseException {
        final List<Exp> exps = new ArrayList<Exp>();
        ParseResult<Exp> current = parseExp(position);
        assertTokenHereIs(current.position, new SemicolonToken());
        exps.add(current.result);
        position = current.position + 1;

        boolean shouldRun = true;
        while (shouldRun) {
            try {
                current = parseExp(position);
                assertTokenHereIs(current.position, new SemicolonToken());
                exps.add(current.result);
                position = current.position + 1;
            } catch (final ParseException e) {
                shouldRun = false;
            }
        }

        return new ParseResult<List<Exp>>(exps, position);
    }
    
    // primary_exp ::= i | id | `true` | `false` |
    //             `let` x `=` exp `in` exp |
    //             `if` `(` exp `)` exp `else` exp |
    //             `match` exp `{` case+ `}` |
    //             `println` `(` exp `)` | `{` semicolon_exps `}` |
    //             `(` exp `)`
    public ParseResult<Exp> parsePrimaryExp(final int position) throws ParseException {
        final Token token = getToken(position);
        if (token instanceof IntegerToken) {
            final int value = ((IntegerToken)token).value;
            return new ParseResult<Exp>(new IntLiteralExp(value),
                                        position + 1);
        } else if (token instanceof IdentifierToken) {
            final String name = ((IdentifierToken)token).identifier;
            return new ParseResult<Exp>(new VariableExp(new Variable(name)),
                                        position + 1);
        } else if (token instanceof TrueToken) {
            return new ParseResult<Exp>(new BooleanLiteralExp(true),
                                        position + 1);
        } else if (token instanceof FalseToken) {
            return new ParseResult<Exp>(new BooleanLiteralExp(false),
                                        position + 1);
        } else if (token instanceof LetToken) {
            final ParseResult<Variable> variable = parseVariable(position + 1);
            assertTokenHereIs(variable.position, new SingleEqualsToken());
            final ParseResult<Exp> initializer = parseExp(variable.position + 1);
            assertTokenHereIs(initializer.position, new InToken());
            final ParseResult<Exp> body = parseExp(initializer.position + 1);
            return new ParseResult<Exp>(new LetExp(variable.result,
                                                   initializer.result,
                                                   body.result),
                                        body.position);
        } else if (token instanceof IfToken) {
            assertTokenHereIs(position + 1, new LeftParenToken());
            final ParseResult<Exp> guard = parseExp(position + 2);
            assertTokenHereIs(guard.position, new RightParenToken());
            final ParseResult<Exp> ifTrue = parseExp(guard.position + 1);
            assertTokenHereIs(ifTrue.position, new ElseToken());
            final ParseResult<Exp> ifFalse = parseExp(ifTrue.position + 1);
            return new ParseResult<Exp>(new IfExp(guard.result,
                                                  ifTrue.result,
                                                  ifFalse.result),
                                        ifFalse.position);
        } else if (token instanceof MatchToken) {
            final ParseResult<Exp> exp = parseExp(position + 1);
            assertTokenHereIs(exp.position, new LeftCurlyBracketToken());
            final ParseResult<List<Case>> cases = parseCases(exp.position + 1);
            assertTokenHereIs(cases.position, new RightCurlyBracketToken());
            return new ParseResult<Exp>(new MatchExp(exp.result, cases.result),
                                        cases.position + 1);
        } else if (token instanceof PrintlnToken) {
            assertTokenHereIs(position + 1, new LeftParenToken());
            final ParseResult<Exp> exp = parseExp(position + 2);
            assertTokenHereIs(exp.position, new RightParenToken());
            return new ParseResult<Exp>(new PrintlnExp(exp.result),
                                        exp.position + 1);
        } else if (token instanceof LeftCurlyBracketToken) {
            final ParseResult<List<Exp>> exps = parseSemicolonExps(position + 1);
            assertTokenHereIs(exps.position, new RightCurlyBracketToken());
            return new ParseResult<Exp>(new BlockExp(exps.result),
                                        exps.position + 1);
        } else if (token instanceof LeftParenToken) {
            final ParseResult<Exp> exp = parseExp(position + 1);
            assertTokenHereIs(exp.position, new RightParenToken());
            return new ParseResult<Exp>(exp.result, exp.position + 1);
        } else {
            throw new ParseException("Expected primary expression; received: " + token);
        }
    }

    // comma_exps ::= [exp (`,` exp)*]
    public ParseResult<List<Exp>> parseCommaExps(int position) throws ParseException {
        final List<Exp> exps = new ArrayList<Exp>();

        try {
            ParseResult<Exp> current = parseExp(position);
            exps.add(current.result);
            position = current.position;
            boolean shouldRun = true;
            while (shouldRun) {
                try {
                    assertTokenHereIs(position, new CommaToken());
                    current = parseExp(position + 1);
                    exps.add(current.result);
                    position = current.position;
                } catch (final ParseException e) {
                    shouldRun = false;
                }
            }
        } catch (final ParseException e) {}

        return new ParseResult<List<Exp>>(exps, position);
    }

    // TODO:
    // Remove the following classes:
    // -CallHigherOrderFunctionExp
    // -CallNamedFunctionExp
    // -MakeAlgebraicExp
    //
    // These have all been replaced by CallLike.  The typechecker
    // will need to disambiguate them.

    // call_exp ::= primary_exp (`(` comma_exps `)`)*
    public ParseResult<Exp> parseCallExp(final int position) throws ParseException {
        ParseResult<Exp> retval = parsePrimaryExp(position);
        boolean shouldRun = true;
        while (shouldRun) {
            try {
                assertTokenHereIs(retval.position, new LeftParenToken());
                final ParseResult<List<Exp>> params = parseCommaExps(retval.position + 1);
                assertTokenHereIs(params.position, new RightParenToken());
                retval = new ParseResult<Exp>(new CallLike(retval.result,
                                                           params.result),
                                              params.position + 1);
            } catch (final ParseException e) {
                shouldRun = false;
            }
        }
        return retval;
    }

    // additive_exp ::= call_exp (`+` call_exp)*
    public ParseResult<Exp> parseAdditiveExp(final int position) throws ParseException {
        ParseResult<Exp> retval = parseCallExp(position);
        boolean shouldRun = true;

        while (shouldRun) {
            try {
                assertTokenHereIs(retval.position, new PlusToken());
                final ParseResult<Exp> right = parseCallExp(retval.position + 1);
                retval = new ParseResult<Exp>(new OpExp(retval.result,
                                                        new PlusOp(),
                                                        right.result),
                                              right.position);
            } catch (final ParseException e) {
                shouldRun = false;
            }
        }

        return retval;
    }

    // less_than_exp ::= additive_exp (`<` additive_exp)*
    public ParseResult<Exp> parseLessThanExp(final int position) throws ParseException {
        ParseResult<Exp> retval = parseAdditiveExp(position);
        boolean shouldRun = true;

        while (shouldRun) {
            try {
                assertTokenHereIs(retval.position, new LessThanToken());
                final ParseResult<Exp> right = parseAdditiveExp(retval.position + 1);
                retval = new ParseResult<Exp>(new OpExp(retval.result,
                                                        new LessThanOp(),
                                                        right.result),
                                              right.position);
            } catch (final ParseException e) {
                shouldRun = false;
            }
        }

        return retval;
    }

    // equals_exp ::= less_than_exp (`==` less_than_exp)*
    public ParseResult<Exp> parseEqualsExp(final int position) throws ParseException {
        ParseResult<Exp> retval = parseLessThanExp(position);
        boolean shouldRun = true;

        while (shouldRun) {
            try {
                assertTokenHereIs(retval.position, new DoubleEqualsToken());
                final ParseResult<Exp> right = parseLessThanExp(retval.position + 1);
                retval = new ParseResult<Exp>(new OpExp(retval.result,
                                                        new EqualsOp(),
                                                        right.result),
                                              right.position);
            } catch (final ParseException e) {
                shouldRun = false;
            }
        }

        return retval;
    }

    // exp ::= make_function_exp | equals_exp
    public ParseResult<Exp> parseExp(final int position) throws ParseException {
        try {
            return parseMakeFunctionExp(position);
        } catch (final ParseException e) {
            return parseEqualsExp(position);
        }
    }

    // comma_ids ::= [id (`,` id)*]
    public ParseResult<List<String>> parseCommaIds(int position) throws ParseException {
        final List<String> retval = new ArrayList<String>();
        try {
            ParseResult<String> current = parseIdentifier(position);
            retval.add(current.result);
            position = current.position;

            boolean shouldRun = true;
            while (shouldRun) {
                try {
                    assertTokenHereIs(position, new CommaToken());
                    current = parseIdentifier(position + 1);
                    retval.add(current.result);
                    position = current.position;
                } catch (final ParseException e) {
                    shouldRun = false;
                }
            }
        } catch (final ParseException e) {}

        return new ParseResult<List<String>>(retval, position);
    }

    public ParseResult<List<Variable>> parseCommaVariables(final int position) throws ParseException {
        final ParseResult<List<String>> ids = parseCommaIds(position);
        final List<Variable> retval = new ArrayList<Variable>();
        for (final String id : ids.result) {
            retval.add(new Variable(id));
        }
        return new ParseResult<List<Variable>>(retval, ids.position);
    }

    public ParseResult<List<Typevar>> parseCommaTypevars(final int position) throws ParseException {
        final ParseResult<List<String>> ids = parseCommaIds(position);
        final List<Typevar> retval = new ArrayList<Typevar>();
        for (final String id : ids.result) {
            retval.add(new Typevar(id));
        }
        return new ParseResult<List<Typevar>>(retval, ids.position);
    }
    
    // make_function_exp ::= `(` comma_ids `)` `=>` exp
    public ParseResult<Exp> parseMakeFunctionExp(final int position) throws ParseException {
        assertTokenHereIs(position, new LeftParenToken());
        final ParseResult<List<Variable>> params = parseCommaVariables(position + 1);
        assertTokenHereIs(params.position, new RightParenToken());
        assertTokenHereIs(params.position + 1, new ArrowToken());
        final ParseResult<Exp> body = parseExp(params.position + 2);
        return new ParseResult<Exp>(new MakeHigherOrderFunctionExp(params.result,
                                                                   body.result),
                                    body.position);
    }

    // vardec ::= id `:` type
    public ParseResult<Vardec> parseVardec(final int position) throws ParseException {
        final ParseResult<Variable> variable = parseVariable(position);
        assertTokenHereIs(variable.position, new ColonToken());
        final ParseResult<Type> type = parseType(variable.position + 1);
        return new ParseResult<Vardec>(new Vardec(variable.result,
                                                  type.result),
                                       type.position);
    }

    // comma_vardecs ::= [vardec (`,` vardec)*]
    public ParseResult<List<Vardec>> parseCommaVardecs(int position) throws ParseException {
        final List<Vardec> vardecs = new ArrayList<Vardec>();

        try {
            ParseResult<Vardec> current = parseVardec(position);
            vardecs.add(current.result);
            position = current.position;

            boolean shouldRun = true;
            while (shouldRun) {
                try {
                    assertTokenHereIs(position, new CommaToken());
                    current = parseVardec(position + 1);
                    vardecs.add(current.result);
                    position = current.position;
                } catch (final ParseException e) {
                    shouldRun = false;
                }
            }
        } catch (final ParseException e) {}

        return new ParseResult<List<Vardec>>(vardecs, position);
    }

    // case ::= `case` id `(` comma_ids `)` `:` exp
    public ParseResult<Case> parseCase(final int position) throws ParseException {
        assertTokenHereIs(position, new CaseToken());
        final ParseResult<ConsName> consName = parseConsName(position + 1);
        assertTokenHereIs(consName.position, new LeftParenToken());
        final ParseResult<List<Variable>> variables = parseCommaVariables(consName.position + 1);
        assertTokenHereIs(variables.position, new RightParenToken());
        assertTokenHereIs(variables.position + 1, new ColonToken());
        final ParseResult<Exp> body = parseExp(variables.position + 2);
        return new ParseResult<Case>(new Case(consName.result,
                                              variables.result,
                                              body.result),
                                     body.position);
    }

    public ParseResult<List<Case>> parseCases(int position) throws ParseException {
        final List<Case> cases = new ArrayList<Case>();
        boolean shouldRun = true;
        while (shouldRun) {
            try {
                final ParseResult<Case> theCase = parseCase(position);
                cases.add(theCase.result);
                position = theCase.position;
            } catch (final ParseException e) {
                shouldRun = false;
            }
        }

        return new ParseResult<List<Case>>(cases, position);
    }

    // consdef ::= id `(` comma_types `)`
    public ParseResult<ConsDef> parseConsDef(final int position) throws ParseException {
        final ParseResult<ConsName> consName = parseConsName(position);
        assertTokenHereIs(consName.position, new LeftParenToken());
        final ParseResult<List<Type>> types = parseCommaTypes(consName.position + 1);
        assertTokenHereIs(types.position, new RightParenToken());
        return new ParseResult<ConsDef>(new ConsDef(consName.result, types.result),
                                        types.position + 1);
    }

    // bar_consdefs ::= consdef (`|` consdef)*
    public ParseResult<List<ConsDef>> parsePipeConsDefs(int position) throws ParseException {
        final List<ConsDef> consDefs = new ArrayList<ConsDef>();
        ParseResult<ConsDef> current = parseConsDef(position);
        consDefs.add(current.result);
        position = current.position;

        boolean shouldRun = true;
        while (shouldRun) {
            try {
                assertTokenHereIs(position, new PipeToken());
                current = parseConsDef(position + 1);
                consDefs.add(current.result);
                position = current.position;
            } catch (final ParseException e) {
                shouldRun = false;
            }
        }

        return new ParseResult<List<ConsDef>>(consDefs, position);
    }

    // algdef ::= `type` id `[` comma_ids `]` `=` pipe_consdefs `;`
    public ParseResult<AlgDef> parseAlgDef(final int position) throws ParseException {
        assertTokenHereIs(position, new TypeToken());
        final ParseResult<AlgName> algName = parseAlgName(position + 1);
        assertTokenHereIs(algName.position, new LeftSquareBracketToken());
        final ParseResult<List<Typevar>> typevars = parseCommaTypevars(algName.position + 1);
        assertTokenHereIs(typevars.position, new RightSquareBracketToken());
        assertTokenHereIs(typevars.position + 1, new SingleEqualsToken());
        final ParseResult<List<ConsDef>> consDefs = parsePipeConsDefs(typevars.position + 2);
        assertTokenHereIs(consDefs.position, new SemicolonToken());
        return new ParseResult<AlgDef>(new AlgDef(algName.result,
                                                  typevars.result,
                                                  consDefs.result),
                                       consDefs.position + 1);
    }

    public ParseResult<List<AlgDef>> parseAlgDefs(int position) throws ParseException {
        final List<AlgDef> retval = new ArrayList<AlgDef>();
        boolean shouldRun = true;
        while (shouldRun) {
            try {
                final ParseResult<AlgDef> algDef = parseAlgDef(position);
                retval.add(algDef.result);
                position = algDef.position;
            } catch (final ParseException e) {
                shouldRun = false;
            }
        }

        return new ParseResult<List<AlgDef>>(retval, position);
    }
    
    // functiondef ::= `def` id `[` comma_ids `]` `(` comma_vardecs `)` `:` type `=` exp
    public ParseResult<FunctionDef> parseFunctionDef(final int position) throws ParseException {
        assertTokenHereIs(position, new DefToken());
        final ParseResult<FunctionName> functionName = parseFunctionName(position + 1);
        assertTokenHereIs(functionName.position, new LeftSquareBracketToken());
        final ParseResult<List<Typevar>> typevars = parseCommaTypevars(functionName.position + 1);
        assertTokenHereIs(typevars.position, new RightSquareBracketToken());
        assertTokenHereIs(typevars.position + 1, new LeftParenToken());
        final ParseResult<List<Vardec>> params = parseCommaVardecs(typevars.position + 2);
        assertTokenHereIs(params.position, new RightParenToken());
        assertTokenHereIs(params.position + 1, new ColonToken());
        final ParseResult<Type> returnType = parseType(params.position + 2);
        assertTokenHereIs(returnType.position, new SingleEqualsToken());
        final ParseResult<Exp> body = parseExp(returnType.position + 1);
        return new ParseResult<FunctionDef>(new FunctionDef(functionName.result,
                                                            typevars.result,
                                                            params.result,
                                                            returnType.result,
                                                            body.result),
                                            body.position);
    }

    public ParseResult<List<FunctionDef>> parseFunctionDefs(int position) throws ParseException {
        final List<FunctionDef> retval = new ArrayList<FunctionDef>();
        boolean shouldRun = true;
        while (shouldRun) {
            try {
                final ParseResult<FunctionDef> functionDef = parseFunctionDef(position);
                retval.add(functionDef.result);
                position = functionDef.position;
            } catch (final ParseException e) {
                shouldRun = false;
            }
        }

        return new ParseResult<List<FunctionDef>>(retval, position);
    }
    
    // program ::= algdef* functiondef* exp
    public ParseResult<Program> parseProgram(final int position) throws ParseException {
        final ParseResult<List<AlgDef>> algDefs = parseAlgDefs(position);
        final ParseResult<List<FunctionDef>> functionDefs = parseFunctionDefs(algDefs.position);
        final ParseResult<Exp> entryPoint = parseExp(functionDefs.position);
        return new ParseResult<Program>(new Program(algDefs.result,
                                                    functionDefs.result,
                                                    entryPoint.result),
                                        entryPoint.position);
    }

    public Program parseProgram() throws ParseException {
        final ParseResult<Program> retval = parseProgram(0);
        if (retval.position != tokens.length) {
            throw new ParseException("Remaining tokens at end");
        } else {
            return retval.result;
        }
    }

    public static Program parseProgram(final Token[] tokens) throws ParseException {
        return new Parser(tokens).parseProgram();
    }
}
