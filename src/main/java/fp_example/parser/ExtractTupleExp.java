package fp_example.parser;

public class ExtractTupleExp implements Exp {
    public final Exp tuple;
    public final int index; // assumed to be 1-indexed

    public ExtractTupleExp(final Exp tuple,
                           final int index) {
        this.tuple = tuple;
        this.index = index;
    }

    public int hashCode() {
        return tuple.hashCode() + index;
    }

    public boolean equals(final Object other) {
        if (other instanceof ExtractTupleExp) {
            final ExtractTupleExp asTup = (ExtractTupleExp)other;
            return (tuple.equals(asTup.tuple) &&
                    index == asTup.index);
        } else {
            return false;
        }
    }

    public String toString() {
        return ("ExtractTupleExp(" + tuple.toString() + ", " +
                index + ")");
    }
}
