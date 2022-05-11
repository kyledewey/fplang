package fp_example.parser;

// Used in CallLikeExp.
// indicates that this is really a HOF.
// The state is already on the CallLikeExp, so no need
// to duplicate anything here.
public class HOFResolved implements CallLikeResolved {
    public int hashCode() { return 0; }
    public boolean equals(final Object other) {
        return other instanceof HOFResolved;
    }
    public String toString() {
        return "HOFResolved";
    }
}

