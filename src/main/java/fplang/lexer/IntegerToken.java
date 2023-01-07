package fplang.lexer;

public class IntegerToken implements Token {
    public final int value;

    public IntegerToken(final int value) {
        this.value = value;
    }

    public int hashCode() {
        return value;
    }

    public boolean equals(final Object other) {
        return (other instanceof IntegerToken &&
                value == ((IntegerToken)other).value);
    }

    public String toString() {
        return "IntegerToken(" + value + ")";
    }
}
