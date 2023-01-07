package fplang.parser;

import java.util.List;

public class FunctionType implements Type {
    public final List<Type> paramTypes;
    public final Type returnType;

    public FunctionType(final List<Type> paramTypes,
                        final Type returnType) {
        this.paramTypes = paramTypes;
        this.returnType = returnType;
    }

    public int hashCode() {
        return paramTypes.hashCode() + returnType.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof FunctionType) {
            final FunctionType otherFunc = (FunctionType)other;
            return (paramTypes.equals(otherFunc.paramTypes) &&
                    returnType.equals(otherFunc.returnType));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("FunctionType(" + paramTypes.toString() + ", " +
                returnType.toString() + ")");
    }
}
