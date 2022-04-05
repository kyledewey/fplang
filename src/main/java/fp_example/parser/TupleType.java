package fp_example.parser;

import java.util.List;

public class TupleType implements Type {
    public final List<Type> types;

    public TupleType(final List<Type> types) {
        this.types = types;
    }

    public int hashCode() { return types.hashCode(); }

    public boolean equals(final Object other) {
        return (other instanceof TupleType &&
                types.equals(((TupleType)other).types));
    }

    public String toString() {
        return "TupleType(" + types.toString() + ")";
    }
}

                
