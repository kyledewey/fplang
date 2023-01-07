package fplang.parser;

import java.util.List;

public class ConsDef {
    public final ConsName consName;
    public final List<Type> types;

    public ConsDef(final ConsName consName,
                   final List<Type> types) {
        this.consName = consName;
        this.types = types;
    }

    public int hashCode() {
        return consName.hashCode() + types.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof ConsDef) {
            final ConsDef asCons = (ConsDef)other;
            return (consName.equals(asCons.consName) &&
                    types.equals(asCons.types));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("ConsDef(" + consName.toString() + ", " +
                types.toString() + ")");
    }
}
