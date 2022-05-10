package fp_example.parser;

// Used in CallLikeExp.
// Indicates that this is really making an algebraic data type
// via a constructor.  Holds the constructor name
public class MakeAlgebraicResolved implements CallLikeResolved {
    public final ConsName consName;

    public MakeAlgebraicResolved(final ConsName consName) {
        this.consName = consName;
    }

    public int hashCode() {
        return consName.hashCode();
    }

    public boolean equals(final Object other) {
        return (other instanceof MakeAlgebraicResolved &&
                consName.equals(((MakeAlgebraicResolved)other).consName));
    }

    public String toString() {
        return "MakeAlgebraicResolved(" + consName + ")";
    }
}
