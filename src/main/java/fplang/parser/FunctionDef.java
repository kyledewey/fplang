package fplang.parser;

import java.util.List;
import java.util.ArrayList;

public class FunctionDef {
    public final FunctionName functionName;
    public final List<Typevar> typevars;
    public final List<Vardec> params;
    public final Type returnType;
    public final Exp body;

    public FunctionDef(final FunctionName functionName,
                       final List<Typevar> typevars,
                       final List<Vardec> params,
                       final Type returnType,
                       final Exp body) {
        this.functionName = functionName;
        this.typevars = typevars;
        this.params = params;
        this.returnType = returnType;
        this.body = body;
    }

    public List<Type> paramTypes() {
        final List<Type> retval = new ArrayList<Type>();
        for (final Vardec vardec : params) {
            retval.add(vardec.type);
        }
        return retval;
    }
    
    public int hashCode() {
        return (functionName.hashCode() +
                typevars.hashCode() +
                params.hashCode() +
                returnType.hashCode() +
                body.hashCode());
    }

    public boolean equals(final Object other) {
        if (other instanceof FunctionDef) {
            final FunctionDef asFunc = (FunctionDef)other;
            return (functionName.equals(asFunc.functionName) &&
                    typevars.equals(asFunc.typevars) &&
                    params.equals(asFunc.params) &&
                    returnType.equals(asFunc.returnType) &&
                    body.equals(asFunc.body));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("FunctionDef(" + functionName.toString() + ", " +
                typevars.toString() + ", " +
                params.toString() + ", " +
                returnType.toString() + ", " +
                body.toString() + ")");
    }
}
