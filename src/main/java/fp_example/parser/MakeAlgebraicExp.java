package fp_example.parser;

import java.util.List;

public class MakeAlgebraicExp implements Exp {
    public final ConsName consName;
    public final List<Exp> exps;

    public MakeAlgebraicExp(final ConsName consName,
                            final List<Exp> exps) {
        this.consName = consName;
        this.exps = exps;
    }

    public int hashCode() {
        return consName.hashCode() + exps.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof MakeAlgebraicExp) {
            final MakeAlgebraicExp asMake = (MakeAlgebraicExp)other;
            return (consName.equals(asMake.consName) &&
                    exps.equals(asMake.exps));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("MakeAlgebraicExp(" + consName.toString() + ", " +
                exps.toString() + ")");
    }
}
