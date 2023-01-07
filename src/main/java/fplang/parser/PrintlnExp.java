package fplang.parser;

public class PrintlnExp implements Exp {
    public final Exp exp;

    public PrintlnExp(final Exp exp) {
        this.exp = exp;
    }

    public int hashCode() {
        return exp.hashCode();
    }

    public boolean equals(final Object other) {
        return (other instanceof PrintlnExp &&
                exp.equals(((PrintlnExp)other).exp));
    }

    public String toString() {
        return "PrintlnExp(" + exp.toString() + ")";
    }
}
