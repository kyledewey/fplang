package fp_example.parser;

import java.util.List;

public class MakeTupleExp implements Exp {
    public final List<Exp> exps;

    public MakeTupleExp(final List<Exp> exps) {
        this.exps = exps;
    }

    public int hashCode() { return exps.hashCode(); }

    public boolean equals(final Object other) {
        return (other instanceof MakeTupleExp &&
                exps.equals(((MakeTupleExp)other).exps));
    }

    public String toString() {
        return "MakeTupleExp(" + exps.toString() + ")";
    }
}
