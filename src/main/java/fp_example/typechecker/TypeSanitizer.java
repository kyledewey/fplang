package fp_example.typechecker;

import fp_example.parser.*;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

// checks if a given type is valid or not
public class TypeSanitizer {
    public final Map<AlgName, AlgDef> algebraicDataTypes;
    public final Set<Typevar> typevars;

    public TypeSanitizer(final Map<AlgName, AlgDef> algebraicDataTypes,
                         final Set<Typevar> typevars) {
        this.algebraicDataTypes = algebraicDataTypes;
        this.typevars = typevars;
    }

    public static TypeSanitizer makeSanitizer(final Map<AlgName, AlgDef> algebraicDataTypes,
                                              final List<Typevar> typevarsList)
        throws TypeErrorException {
        final Set<Typevar> typevars = new HashSet<Typevar>();
        for (final Typevar typevar : typevarsList) {
            if (typevars.contains(typevar)) {
                throw new TypeErrorException("Duplicate typevar in scope: " + typevar);
            } else {
                typevars.add(typevar);
            }
        }

        return new TypeSanitizer(algebraicDataTypes, typevars);
    }

    public void assertTypesOk(final List<Type> types) throws TypeErrorException {
        for (final Type type : types) {
            assertTypeOk(type);
        }
    }
    
    public void assertTypeOk(final Type type) throws TypeErrorException {
        if (type instanceof IntType ||
            type instanceof BoolType ||
            type instanceof VoidType) {
            return;
        } else if (type instanceof TypevarType &&
                   typevars.contains(((TypevarType)type).typevar)) {
            return;
        } else if (type instanceof FunctionType) {
            final FunctionType asFunc = (FunctionType)type;
            assertTypesOk(asFunc.paramTypes);
            assertTypeOk(asFunc.returnType);
        } else if (type instanceof AlgebraicType) {
            final AlgebraicType asAlgebraic = (AlgebraicType)type;
            final AlgName algName = asAlgebraic.algName;
            final AlgDef algDef = algebraicDataTypes.get(algName);
            if (algDef == null) {
                throw new TypeErrorException("unknown algebraic data type: " + algName);
            } else if (asAlgebraic.types.size() != algDef.typevars.size()) {
                throw new TypeErrorException("Incorrect number of types for algebraic data type");
            }
        } else {
            throw new TypeErrorException("Unsupported type: " + type);
        }
    }
}
