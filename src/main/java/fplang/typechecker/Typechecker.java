package fplang.typechecker;

import fplang.parser.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public class Typechecker {
    // information needed:
    // 1.) what functions are available (by name)
    // 2.) what constructors are available (by name), and what algebraic
    //     data types they correspond to
    // 3.) what constructors correspond to which algebraic data types
    public final Program program;
    public final Map<FunctionName, FunctionDef> funcNameToFuncDef;
    public final Map<AlgName, AlgDef> algNameToAlgDef;
    public final Map<ConsName, AlgDef> consNameToAlgDef;

    // does NOT check if the types within are valid or not
    public static Map<AlgName, AlgDef> makeAlgebraics(final Program program)
        throws TypeErrorException {
        final Map<AlgName, AlgDef> retval = new HashMap<AlgName, AlgDef>();
        for (final AlgDef algDef : program.algs) {
            final AlgName algName = algDef.algName;
            if (retval.containsKey(algName)) {
                throw new TypeErrorException("Duplicate algebraic type name: " + algName);
            }
            retval.put(algName, algDef);
        }

        return retval;
    }

    // checks if the types are valid within
    public static Map<ConsName, AlgDef> makeConstructors(final Map<AlgName, AlgDef> algebraics)
        throws TypeErrorException {
        final Map<ConsName, AlgDef> retval = new HashMap<ConsName, AlgDef>();
        for (final AlgDef algDef : algebraics.values()) {
            final TypeSanitizer sanitizer = TypeSanitizer.makeSanitizer(algebraics, algDef.typevars);
            for (final ConsDef constructor : algDef.constructors) {
                sanitizer.assertTypesOk(constructor.types);
                final ConsName consName = constructor.consName;
                if (retval.containsKey(consName)) {
                    throw new TypeErrorException("Duplicate constructor name: " + consName);
                }
                retval.put(consName, algDef);
            }
        }

        return retval;
    }
    
    public static Map<FunctionName, FunctionDef> makeFunctions(final Map<AlgName, AlgDef> algebraics,
                                                               final Map<ConsName, AlgDef> constructors,
                                                               final Program program)
        throws TypeErrorException {
        final Map<FunctionName, FunctionDef> retval = new HashMap<FunctionName, FunctionDef>();
        for (final FunctionDef functionDef : program.functions) {
            final TypeSanitizer sanitizer = TypeSanitizer.makeSanitizer(algebraics, functionDef.typevars);
            final Set<Variable> variables = new HashSet<Variable>();
            for (final Vardec vardec : functionDef.params) {
                sanitizer.assertTypeOk(vardec.type);
                final Variable variable = vardec.variable;
                if (variables.contains(vardec.variable)) {
                    throw new TypeErrorException("Duplicate variable name: " + variable);
                }
                variables.add(variable);
            }
            sanitizer.assertTypeOk(functionDef.returnType);
            final FunctionName functionName = functionDef.functionName;
            if (retval.containsKey(functionName)) {
                throw new TypeErrorException("Duplicate function name: " + functionName);
            } else if (constructors.containsKey(new ConsName(functionName.name))) {
                throw new TypeErrorException("Function names and constructor names must be distinct: " + functionName);
            }
            retval.put(functionName, functionDef);
        }
        return retval;
    }

    public Typechecker(final Program program) throws TypeErrorException {
        this.program = program;

        algNameToAlgDef = makeAlgebraics(program);
        consNameToAlgDef = makeConstructors(algNameToAlgDef);
        funcNameToFuncDef = makeFunctions(algNameToAlgDef,
                                          consNameToAlgDef,
                                          program);
    }

    public static Map<Variable, TypeTerm> addVarInScope(final Map<Variable, TypeTerm> typeEnvironment,
                                                        final Variable variable,
                                                        final TypeTerm type) {
        final Map<Variable, TypeTerm> retval = new HashMap<Variable, TypeTerm>();
        retval.putAll(typeEnvironment);
        retval.put(variable, type);
        return retval;
    }

    public static Map<Variable, TypeTerm> addVarsInScope(final Map<Variable, TypeTerm> typeEnvironment,
                                                         final Map<Variable, TypeTerm> toAdd) {
        final Map<Variable, TypeTerm> retval = new HashMap<Variable, TypeTerm>();
        retval.putAll(typeEnvironment);
        retval.putAll(toAdd);
        return retval;
    }
    
    public TypeTerm typeofVariableExp(final VariableExp exp,
                                      final Map<Variable, TypeTerm> typeEnvironment)
        throws TypeErrorException {
        final Variable variable = exp.variable;
        final TypeTerm retval = typeEnvironment.get(variable);
        if (retval == null) {
            throw new TypeErrorException("Variable not in scope: " + variable);
        }
        return retval;
    }
                                      
    public TypeTerm typeofOpExp(final OpExp exp,
                                final Map<Variable, TypeTerm> typeEnvironment,
                                final Unifier unifier) throws TypeErrorException {
        final TypeTerm leftType = typeofExp(exp.left, typeEnvironment, unifier);
        final TypeTerm rightType = typeofExp(exp.right, typeEnvironment, unifier);
        if (exp.op instanceof PlusOp) {
            unifier.unify(leftType, new IntTypeTerm());
            unifier.unify(rightType, new IntTypeTerm());
            return new IntTypeTerm();
        } else if (exp.op instanceof LessThanOp) {
            unifier.unify(leftType, new IntTypeTerm());
            unifier.unify(rightType, new IntTypeTerm());
            return new BoolTypeTerm();
        } else if (exp.op instanceof EqualsOp) {
            // semantics: allowed on any two values of the same type
            unifier.unify(leftType, rightType);
            return new BoolTypeTerm();
        } else {
            throw new TypeErrorException("Unsupported operator: " + exp.op.toString());
        }
    }

    // let variable = initializer in body
    public TypeTerm typeofLetExp(final LetExp exp,
                                 final Map<Variable, TypeTerm> typeEnvironment,
                                 final Unifier unifier) throws TypeErrorException {
        final TypeTerm initializerType = typeofExp(exp.initializer,
                                                   typeEnvironment,
                                                   unifier);
        final Map<Variable, TypeTerm> nestedTypeEnvironment =
            addVarInScope(typeEnvironment,
                          exp.variable,
                          initializerType);
        return typeofExp(exp.body, nestedTypeEnvironment, unifier);
    }
                                     
    public TypeTerm typeofMakeHOFExp(final MakeHigherOrderFunctionExp exp,
                                     Map<Variable, TypeTerm> typeEnvironment,
                                     final Unifier unifier) throws TypeErrorException {
        final Set<Variable> variableNames = new HashSet<Variable>();
        final List<TypeTerm> variableTypes = new ArrayList<TypeTerm>();
        // make sure there aren't any duplicate variable names
        // put variables in scope
        for (final Variable variable : exp.params) {
            if (variableNames.contains(variable)) {
                throw new TypeErrorException("Duplicate variable: " + variable);
            }
            variableNames.add(variable);
            // variables aren't annotated with types - these are initially unknown
            final TypeTerm variableType = new PlaceholderTypeTerm();
            variableTypes.add(variableType);
            typeEnvironment = addVarInScope(typeEnvironment,
                                            variable,
                                            variableType);
        }

        final TypeTerm returnType = typeofExp(exp.body, typeEnvironment, unifier);
        return new FunctionTypeTerm(variableTypes, returnType);
    }

    public TypeTerm typeofCallLikeExp(final CallLikeExp exp,
                                      final Map<Variable, TypeTerm> typeEnvironment,
                                      final Unifier unifier) throws TypeErrorException {
        // This could be one of the following:
        // -Calling a higher-order function (exp(exp*))
        // -Calling a named function (functionName(exp*) - really variable(exp*))
        // -Using a constructor for an algebraic data type (consName(exp*) - really variable(exp*))

        if (exp.functionLike instanceof VariableExp) {
            // Could be any of the three alternatives.
            // If this variable is in scope, it's a function.
            // If this variable isn't in scope, try for a named function.
            // If there isn't a named function with this name, then try for constructors.
            // The Typechecker constructor guarantees that there is no overlap between function and
            // constructor names, so there should be no ambiguity there.
            final Variable nameAsVariable = ((VariableExp)exp.functionLike).variable;
            final FunctionName nameAsFunctionName = new FunctionName(nameAsVariable.name);
            final ConsName nameAsConsName = new ConsName(nameAsVariable.name);
            if (typeEnvironment.containsKey(nameAsVariable)) {
                exp.resolution = new HOFResolved();
                return typeofCallHOF(exp.functionLike,
                                     exp.params,
                                     typeEnvironment,
                                     unifier);
            } else if (funcNameToFuncDef.containsKey(nameAsFunctionName)) {
                exp.resolution = new NamedFunctionResolved(nameAsFunctionName);
                return typeofCallNamed(nameAsFunctionName,
                                       exp.params,
                                       typeEnvironment,
                                       unifier);
            } else if (consNameToAlgDef.containsKey(nameAsConsName)) {
                exp.resolution = new MakeAlgebraicResolved(nameAsConsName);
                return typeofMakeAlgebraic(nameAsConsName,
                                           exp.params,
                                           typeEnvironment,
                                           unifier);
            } else {
                throw new TypeErrorException("Call to unknown target: " + nameAsVariable.name);
            }
        } else {
            // if it's an arbitrary expression, syntactically it has
            // to be a higher-order function call
            exp.resolution = new HOFResolved();
            return typeofCallHOF(exp.functionLike,
                                 exp.params,
                                 typeEnvironment,
                                 unifier);
        }
    }

    public TypeTerm typeofCallHOF(final Exp target,
                                  final List<Exp> params,
                                  final Map<Variable, TypeTerm> typeEnvironment,
                                  final Unifier unifier) throws TypeErrorException {
        final List<TypeTerm> actualParamTypes = typeofExps(params,
                                                           typeEnvironment,
                                                           unifier);
        final TypeTerm actualFunctionType = typeofExp(target,
                                                      typeEnvironment,
                                                      unifier);
        final TypeTerm returnType = new PlaceholderTypeTerm();
        unifier.unify(actualFunctionType,
                      new FunctionTypeTerm(actualParamTypes, returnType));
        return returnType;
    }

    public TypeTerm typeofCallNamed(final FunctionName functionName,
                                    final List<Exp> params,
                                    final Map<Variable, TypeTerm> typeEnvironment,
                                    final Unifier unifier) throws TypeErrorException {
        // type substitutions:
        // - Go to the generic signature
        // - Map every type variable to a unique placeholder
        // - Translate every syntactic type for an equivalent TypeTerm, replacing
        //   type variables with the placeholders
        //
        // def map[A, B](list: List[A], f: (A) => B): List[B] = ...
        //
        // def map(list: List[Placeholder(0)], f: (Placeholder(0)) => Placeholder(1)): List[Placeholder(1)]
        //
        // Map(A -> Placeholder(0),
        //     B -> Placeholder(1))
        //
        // substitution: params: List(List[Placeholder(0)],
        //                            (Placeholder(0)) => Placeholder(1))
        //               returnType: List[Placeholder(1)]
        // map(myList, myFunction)
        //
        final FunctionTypeSubstitution substitution = typeSubstitutionForFunction(functionName);
        final List<TypeTerm> actualParamTypes = typeofExps(params, typeEnvironment, unifier);
        unifier.unifyMulti(substitution.params, actualParamTypes);
        return substitution.returnType;
    }

    // Cons(1, Nil()): List[int]
    public TypeTerm typeofMakeAlgebraic(final ConsName consName,
                                        final List<Exp> params,
                                        final Map<Variable, TypeTerm> typeEnvironment,
                                        final Unifier unifier) throws TypeErrorException {
        final ConstructorTypeSubstitution substitution = typeSubstitutionForConstructor(consName);
        final List<TypeTerm> actualParamTypes = typeofExps(params, typeEnvironment, unifier);
        unifier.unifyMulti(substitution.params, actualParamTypes);
        return new AlgebraicTypeTerm(substitution.algName,
                                     substitution.generics);
    }

    public List<TypeTerm> typeofExps(final List<Exp> exps,
                                     final Map<Variable, TypeTerm> typeEnvironment,
                                     final Unifier unifier) throws TypeErrorException {
        final List<TypeTerm> retval = new ArrayList<TypeTerm>();
        for (final Exp exp : exps) {
            retval.add(typeofExp(exp, typeEnvironment, unifier));
        }
        return retval;
    }    

    public static List<TypeTerm> translateTypes(final List<Type> types,
                                                final Map<Typevar, TypeTerm> mapping)
        throws TypeErrorException {
        final List<TypeTerm> retval = new ArrayList<TypeTerm>();
        for (final Type type : types) {
            retval.add(translateType(type, mapping));
        }
        return retval;
    }

    public static TypeTerm translateType(final Type type,
                                         final Map<Typevar, TypeTerm> mapping)
        throws TypeErrorException {
        if (type instanceof IntType) {
            return new IntTypeTerm();
        } else if (type instanceof BoolType) {
            return new BoolTypeTerm();
        } else if (type instanceof VoidType) {
            return new VoidTypeTerm();
        } else if (type instanceof TypevarType) {
            final Typevar typevar = ((TypevarType)type).typevar;
            final TypeTerm retval = mapping.get(typevar);
            if (retval == null) {
                throw new TypeErrorException("Unrecognized typevar: " + typevar);
            }
            return retval;
        } else if (type instanceof FunctionType) {
            final FunctionType asFunc = (FunctionType)type;
            return new FunctionTypeTerm(translateTypes(asFunc.paramTypes, mapping),
                                        translateType(asFunc.returnType, mapping));
        } else if (type instanceof AlgebraicType) {
            final AlgebraicType asAlg = (AlgebraicType)type;
            return new AlgebraicTypeTerm(asAlg.algName,
                                         translateTypes(asAlg.types, mapping));
        } else {
            throw new TypeErrorException("Unsupported type: " + type);
        }
    }

    // basic idea: if you have a type like:
    // MyType[A, B, C]
    // ...then this is expected to be called like:
    // makeTypevarToPlaceholderMapping(List(A, B, C))
    // ...to return a mapping like:
    // mapping = Map(A -> PlaceholderTypeTerm(0),
    //               B -> PlaceholderTypeTerm(1),
    //               C -> PlaceholderTypeTerm(2))
    // Using translateType, we can then call:
    // translateType(MyType[A, B, C], mapping)
    // ...to produce the type:
    // MyType[PlaceholderTypeTerm(0),
    //        PlaceholderTypeTerm(1),
    //        PlaceholderTypeTerm(2)]
    // This enables us to have type inference over generics.
    // This way, the user only needs to write:
    // threeTuple(1, "foo", true)
    // ...instead of something like:
    // threeTuple[Int, String, Boolean](1, "foo", true)
    // That is, it enables type inference over constructors, because
    // these placeholders can be filled in with parameter types as this
    // information becomes available.
    public static Map<Typevar, TypeTerm> makeTypevarToPlaceholderMapping(final List<Typevar> typevars) {
        final Map<Typevar, TypeTerm> mapping = new HashMap<Typevar, TypeTerm>();
        for (final Typevar typevar : typevars) {
            mapping.put(typevar, new PlaceholderTypeTerm());
        }
        return mapping;
    }
    
    public FunctionTypeSubstitution typeSubstitutionForFunction(final FunctionName functionName) throws TypeErrorException {
        final FunctionDef functionDef = funcNameToFuncDef.get(functionName);
        if (functionDef == null) {
            throw new TypeErrorException("call to non-existent function: " + functionName);
        }
        final Map<Typevar, TypeTerm> mapping =
            makeTypevarToPlaceholderMapping(functionDef.typevars);
        return new FunctionTypeSubstitution(translateTypes(functionDef.paramTypes(),
                                                           mapping),
                                            translateType(functionDef.returnType,
                                                          mapping));
    }

    // Tuple3[A, B, C] = MakeTuple3(A, B, C)
    // mapping: Map(A -> Placeholder(0),
    //              B -> Placeholder(1),
    //              C -> Placeholder(2))
    // generics: List(Placeholder(0),
    //                Placeholder(1),
    //                Placeholder(2))
    //
    // params: List(Placeholder(0), Placeholder(1), Placeholder(2))
    // generics: List(Placeholder(0), Placeholder(1), Placeholder(2))
    //
    // Either[A, B] = Left(A) | Right(B)
    // Left(...)
    // mapping: Map(A -> Placeholder(0), B -> Placeholder(1))
    // generics: List(Placeholder(0), Placeholder(1))
    // params: List(Placeholder(0))
    //
    // Right(...)
    // mapping: Map(A -> Placeholder(0), B -> Placeholder(1))
    // generics: List(Placeholder(0), Placeholder(1))
    // params: List(Placeholder(1))
    public ConstructorTypeSubstitution typeSubstitutionForConstructor(final ConsName consName) throws TypeErrorException {
        final AlgDef algDef = consNameToAlgDef.get(consName);
        if (algDef == null) {
            throw new TypeErrorException("Unknown constructor name: " + consName);
        }
        final Map<Typevar, TypeTerm> mapping =
            makeTypevarToPlaceholderMapping(algDef.typevars);
        final List<TypeTerm> generics = new ArrayList<TypeTerm>();
        for (final Typevar typevar : algDef.typevars) {
            generics.add(mapping.get(typevar));
        }
        
        for (final ConsDef consDef : algDef.constructors) {
            if (consName.equals(consDef.consName)) {
                return new ConstructorTypeSubstitution(algDef.algName,
                                                       translateTypes(consDef.types, mapping),
                                                       generics);
            }
        }

        throw new TypeErrorException("Unknown constructor: " + consName);
    }
    
    public AlgDef algDefForConstructorName(final ConsName consName) throws TypeErrorException {
        final AlgDef candidate = consNameToAlgDef.get(consName);
        if (candidate == null) {
            throw new TypeErrorException("Undeclared constructor: " + consName);
        } else {
            return candidate;
        }
    }
                                           
    public TypeTerm typeofIfExp(final IfExp exp,
                                final Map<Variable, TypeTerm> typeEnvironment,
                                final Unifier unifier) throws TypeErrorException {
        final TypeTerm guardType = typeofExp(exp.guard, typeEnvironment, unifier);
        unifier.unify(guardType, new BoolTypeTerm());
        final TypeTerm trueType = typeofExp(exp.ifTrue, typeEnvironment, unifier);
        final TypeTerm falseType = typeofExp(exp.ifFalse, typeEnvironment, unifier);
        // both sides should be of the same type
        unifier.unify(trueType, falseType);
        // trueType should now be false type - doesn't matter which is returned
        return trueType;
    }

    public MatchValue patternToMatchValue(final Pattern pattern) throws TypeErrorException {
        if (pattern instanceof UnderscorePattern ||
            pattern instanceof VariablePattern) {
            return new MatchAll();
        } else if (pattern instanceof ConsPattern) {
            final ConsPattern consPattern = (ConsPattern)pattern;
            final List<MatchValue> subMatches = new ArrayList<MatchValue>();
            for (final Pattern subPattern : consPattern.patterns) {
                subMatches.add(patternToMatchValue(subPattern));
            }
            final Map<ConsName, List<MatchValue>> map = new HashMap<ConsName, List<MatchValue>>();
            map.put(consPattern.consName, subMatches);
            return new MatchSome(algDefForConstructorName(consPattern.consName).algName,
                                 map);
        } else {
            throw new TypeErrorException("Unknown pattern: " + pattern.toString());
        }
    }

    public void exhaustivityCheck(final List<Case> cases,
                                  final Set<ConsName> constructors) throws TypeErrorException {
        MatchValue curValue = new MatchNone();
        for (final Case theCase : cases) {
            if (curValue.isMatchAll()) {
                throw new TypeErrorException("Unreachable case: " + theCase.toString());
            }
            curValue = curValue.join(patternToMatchValue(theCase.pattern),
                                     constructors);
        }
        if (!curValue.isMatchAll()) {
            throw new TypeErrorException("Non-exhaustive pattern match: " + curValue.toString());
        }
    }

    public AlgDef getAlgDef(final AlgName algName) throws TypeErrorException {
        final AlgDef retval = algNameToAlgDef.get(algName);
        if (retval == null) {
            throw new TypeErrorException("No such algdef with name: " + algName.toString());
        } else {
            return retval;
        }
    }
    
    public Set<ConsName> expectedConstructors(TypeTerm type,
                                              final Unifier unifier) throws TypeErrorException {
        type = unifier.transitiveSetRepresentativeFor(type);
        if (type instanceof AlgebraicTypeTerm) {
            final Set<ConsName> constructors = new HashSet<ConsName>();
            for (final ConsDef consDef : getAlgDef(((AlgebraicTypeTerm)type).algName).constructors) {
                constructors.add(consDef.consName);
            }
            return constructors;
        } else {
            throw new TypeErrorException("Algebraic type expected; received: " + type.toString());
        }
    }
    
    // newVarsFromPattern holds the variables this pattern introduces, and is
    // mutated directly.  We intentionally don't modify typeEnvironment (yet),
    // as all these variables are added at the same scope level, so this allows
    // us to make sure everything is unique at this scope level
    public TypeTerm typeofPattern(final Pattern pattern,
                                  final Map<Variable, TypeTerm> newVarsFromPattern,
                                  final Map<Variable, TypeTerm> typeEnvironment,
                                  final Unifier unifier) throws TypeErrorException {
        if (pattern instanceof UnderscorePattern) {
            return new PlaceholderTypeTerm();
        } else if (pattern instanceof VariablePattern) {
            final TypeTerm retval = new PlaceholderTypeTerm();
            final Variable variable = ((VariablePattern)pattern).variable;
            if (newVarsFromPattern.containsKey(variable)) {
                throw new TypeErrorException("Duplicate variable name in pattern: " + variable.toString());
            } else {
                newVarsFromPattern.put(((VariablePattern)pattern).variable, retval);
                return retval;
            }
        } else if (pattern instanceof ConsPattern) {
            final ConsPattern asCons = (ConsPattern)pattern;
            final ConstructorTypeSubstitution sub = typeSubstitutionForConstructor(asCons.consName);
            final Iterator<TypeTerm> expectedTypes = sub.params.iterator();
            final Iterator<Pattern> subPatterns = asCons.patterns.iterator();
            while (expectedTypes.hasNext() && subPatterns.hasNext()) {
                unifier.unify(expectedTypes.next(),
                              typeofPattern(subPatterns.next(),
                                            newVarsFromPattern,
                                            typeEnvironment,
                                            unifier));
            }
                
            return new AlgebraicTypeTerm(sub.algName, sub.generics);
        } else {
            throw new TypeErrorException("Unrecognized pattern: " + pattern.toString());
        }
    }

    public TypeTerm typeofMatchExp(final MatchExp exp,
                                   final Map<Variable, TypeTerm> typeEnvironment,
                                   final Unifier unifier) throws TypeErrorException {
        final TypeTerm discriminantType = typeofExp(exp.exp,
                                                    typeEnvironment,
                                                    unifier);
        final TypeTerm retval = new PlaceholderTypeTerm();
        for (final Case theCase : exp.cases) {
            final Map<Variable, TypeTerm> newVarsFromPattern = new HashMap<Variable, TypeTerm>();
            final TypeTerm patternType = typeofPattern(theCase.pattern,
                                                       newVarsFromPattern,
                                                       typeEnvironment,
                                                       unifier);
            unifier.unify(discriminantType, patternType);
            unifier.unify(retval, typeofExp(theCase.body,
                                            addVarsInScope(typeEnvironment,
                                                           newVarsFromPattern),
                                            unifier));
        }
        exhaustivityCheck(exp.cases,
                          expectedConstructors(discriminantType,
                                               unifier));
        return retval;
    }
    
    public TypeTerm typeofPrintlnExp(final PrintlnExp exp,
                                     final Map<Variable, TypeTerm> typeEnvironment,
                                     final Unifier unifier) throws TypeErrorException {
        // we can print anything, but we still need to make sure the
        // expression itself is well-typed
        typeofExp(exp.exp, typeEnvironment, unifier);
        return new VoidTypeTerm();
    }

    public TypeTerm typeofBlockExp(final BlockExp exp,
                                   final Map<Variable, TypeTerm> typeEnvironment,
                                   final Unifier unifier) throws TypeErrorException {
        TypeTerm returnType = null;
        for (final Exp bodyExp : exp.body) {
            returnType = typeofExp(bodyExp, typeEnvironment, unifier);
        }

        if (returnType == null) {
            throw new TypeErrorException("Block with empty body");
        } else {
            return returnType;
        }
    }
    
    public TypeTerm typeofExp(final Exp exp,
                              final Map<Variable, TypeTerm> typeEnvironment,
                              final Unifier unifier) throws TypeErrorException {
        if (exp instanceof IntLiteralExp) {
            return new IntTypeTerm();
        } else if (exp instanceof VariableExp) {
            return typeofVariableExp((VariableExp)exp, typeEnvironment);
        } else if (exp instanceof BooleanLiteralExp) {
            return new BoolTypeTerm();
        } else if (exp instanceof OpExp) {
            return typeofOpExp((OpExp)exp, typeEnvironment, unifier);
        } else if (exp instanceof LetExp) {
            return typeofLetExp((LetExp)exp, typeEnvironment, unifier);
        } else if (exp instanceof MakeHigherOrderFunctionExp) {
            return typeofMakeHOFExp((MakeHigherOrderFunctionExp)exp,
                                    typeEnvironment,
                                    unifier);
        } else if (exp instanceof CallLikeExp) {
            return typeofCallLikeExp((CallLikeExp)exp,
                                     typeEnvironment,
                                     unifier);
        } else if (exp instanceof IfExp) {
            return typeofIfExp((IfExp)exp, typeEnvironment, unifier);
        } else if (exp instanceof MatchExp) {
            return typeofMatchExp((MatchExp)exp,
                                  typeEnvironment,
                                  unifier);
        } else if (exp instanceof PrintlnExp) {
            return typeofPrintlnExp((PrintlnExp)exp, typeEnvironment, unifier);
        } else if (exp instanceof BlockExp) {
            return typeofBlockExp((BlockExp)exp, typeEnvironment, unifier);
        } else {
            throw new TypeErrorException("Unrecognized expression: " + exp);
        }
    }

    // From makeFunctions, we know that we don't have duplicate type variables
    // or duplicate variable names.
    //
    // In the context of a generic function, we don't want to replace the generics
    // with placeholders.  Rather, we should leave the type variables in-place. If
    // we were to replace these with placeholders, it'd allow nasty things like:
    //
    // def myFunction[A](a: A): A = 123
    //
    // ...since the return type would become a placeholder, and integers are allowed
    // to unify with placeholders.  Instead, we want to leave A in-place.
    public void assertFunctionTypechecks(final FunctionDef functionDef)
        throws TypeErrorException {
        // The function parameters still need to be translated into unifiable
        // types.  To this end, make a mapping that just maps type variables
        // to their TypeTerm equivalents.
        final Map<Typevar, TypeTerm> mapping =
            new HashMap<Typevar, TypeTerm>();
        for (final Typevar typevar : functionDef.typevars) {
            mapping.put(typevar, new TypevarTypeTerm(typevar));
        }
        
        // Since nothing else uses the initial map, it's safe to modify it
        // in place.  Put the function parameters in scope.
        final Map<Variable, TypeTerm> typeEnvironment =
            new HashMap<Variable, TypeTerm>();
        for (final Vardec param : functionDef.params) {
            // TypevarType(Typevar(A)) -> TypevarTypeTerm(Typevar(A))
            // TypevarType(Typevar(B)) -> TypevarTypeTerm(Typevar(B))
            final TypeTerm translatedType = translateType(param.type, mapping);
            typeEnvironment.put(param.variable, translatedType);
        }

        // typecheck the body
        final Unifier unifier = new Unifier();
        final TypeTerm expectedReturnType = translateType(functionDef.returnType,
                                                          mapping);
        final TypeTerm actualReturnType = typeofExp(functionDef.body,
                                                    typeEnvironment,
                                                    unifier);
        unifier.unify(expectedReturnType, actualReturnType);
    }

    public void assertProgramTypechecks() throws TypeErrorException {
        for (final FunctionDef functionDef : program.functions) {
            assertFunctionTypechecks(functionDef);
        }
        typeofExp(program.entryPoint,
                  new HashMap<Variable, TypeTerm>(),
                  new Unifier());
    }

    public static void assertProgramTypechecks(final Program program) throws TypeErrorException {
        new Typechecker(program).assertProgramTypechecks();
    }
}
