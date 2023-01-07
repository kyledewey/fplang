package fplang.lexer;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Tokenizer {
    public static final Map<String, Token> RESERVED_WORDS;
    static {
        RESERVED_WORDS = new HashMap<String, Token>();
        RESERVED_WORDS.put("int", new IntToken());
        RESERVED_WORDS.put("bool", new BoolToken());
        RESERVED_WORDS.put("void", new VoidToken());
        RESERVED_WORDS.put("true", new TrueToken());
        RESERVED_WORDS.put("false", new FalseToken());
        RESERVED_WORDS.put("let", new LetToken());
        RESERVED_WORDS.put("in", new InToken());
        RESERVED_WORDS.put("if", new IfToken());
        RESERVED_WORDS.put("else", new ElseToken());
        RESERVED_WORDS.put("match", new MatchToken());
        RESERVED_WORDS.put("println", new PrintlnToken());
        RESERVED_WORDS.put("type", new TypeToken());
        RESERVED_WORDS.put("def", new DefToken());
        RESERVED_WORDS.put("case", new CaseToken());
    }

    // tries them in the same order they are listed as
    public static final List<Symbol> SYMBOLS;
    static {
        SYMBOLS = new ArrayList<Symbol>();
        SYMBOLS.add(new Symbol("(", new LeftParenToken()));
        SYMBOLS.add(new Symbol(")", new RightParenToken()));
        SYMBOLS.add(new Symbol("=>", new ArrowToken()));
        SYMBOLS.add(new Symbol("[", new LeftSquareBracketToken()));
        SYMBOLS.add(new Symbol("]", new RightSquareBracketToken()));
        SYMBOLS.add(new Symbol("==", new DoubleEqualsToken()));
        SYMBOLS.add(new Symbol("=", new SingleEqualsToken()));
        SYMBOLS.add(new Symbol("{", new LeftCurlyBracketToken()));
        SYMBOLS.add(new Symbol("}", new RightCurlyBracketToken()));
        SYMBOLS.add(new Symbol("+", new PlusToken()));
        SYMBOLS.add(new Symbol("<", new LessThanToken()));
        SYMBOLS.add(new Symbol(";", new SemicolonToken()));
        SYMBOLS.add(new Symbol(",", new CommaToken()));
        SYMBOLS.add(new Symbol(":", new ColonToken()));
        SYMBOLS.add(new Symbol("|", new PipeToken()));
    }
    
    public final String input;
    private int position;

    public Tokenizer(final String input) {
        this.input = input;
        position = 0;
    }

    public void skipWhitespace() {
        while (position < input.length() &&
               Character.isWhitespace(input.charAt(position))) {
            position++;
        }
    }

    // assumes it's starting on a non-whitespace character
    // returns null if it couldn't read an integer
    private IntegerToken tryTokenizeInteger() {
        String asString = "";
        char current = '\0';
        
        while (position < input.length() &&
               Character.isDigit(current = input.charAt(position))) {
            asString += current;
            position++;
        }

        if (asString.length() == 0) {
            return null;
        } else {
            return new IntegerToken(Integer.parseInt(asString));
        }
    }

    // assumes it's starting on a non-whitespace character
    // returns null if it couldn't read a reserved word
    // or an identifier
    private Token tryTokenizeReservedWordOrIdentifier() {
        String name = "";
        char current = '\0';

        if (position < input.length() &&
            Character.isLetter(current = input.charAt(position))) {
            name += current;
            position++;

            while (position < input.length() &&
                   Character.isLetterOrDigit(current = input.charAt(position))) {
                name += current;
                position++;
            }

            Token retval = RESERVED_WORDS.get(name);
            if (retval != null) {
                return retval;
            } else {
                return new IdentifierToken(name);
            }
        } else {
            return null;
        }
    }

    // assumes it's starting on a non-whitespace character
    // returns null if it couldn't read in a symbol
    private Token tryTokenizeSymbol() {
        for (final Symbol symbol : SYMBOLS) {
            if (input.startsWith(symbol.asString, position)) {
                position += symbol.asString.length();
                return symbol.asToken;
            }
        }
        return null;
    }

    // returns null if it couldn't read in anything
    private Token tryTokenizeSingle() {
        skipWhitespace();
        Token retval = tryTokenizeInteger();
        if (retval != null) {
            return retval;
        }
        retval = tryTokenizeReservedWordOrIdentifier();
        if (retval != null) {
            return retval;
        }
        retval = tryTokenizeSymbol();
        return retval;
    }

    public List<Token> tokenize() throws TokenizerException {
        final List<Token> retval = new ArrayList<Token>();
        position = 0;
        skipWhitespace();

        while (position < input.length()) {
            final Token token = tryTokenizeSingle();
            if (token == null) {
                throw new TokenizerException("Unexpected character: " +
                                             input.charAt(position));
            }
            retval.add(token);
            skipWhitespace();
        }

        return retval;
    }

    public static Token[] tokenize(final String input) throws TokenizerException {
        final List<Token> tokens = new Tokenizer(input).tokenize();
        return tokens.toArray(new Token[tokens.size()]);
    }
}
