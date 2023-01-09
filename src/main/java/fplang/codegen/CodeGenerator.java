package fplang.codegen;

import fplang.parser.*;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import java.io.PrintWriter;
import java.io.IOException;

public class CodeGenerator {
    public static final String CLOSURE_NAME = "__closure_";
    public static final String FUNCTION_POINTER_NAME = "__func";
    public static final String TEMP_VARIABLE_PREFIX = "__temp_";
    public static final String TEMP_FUNCTION_PREFIX = "__tempfunc_";

    public final Program program;
    public final Map<ConsName, Integer> constructors;
    private int nextTempVariable;
    private int nextTempFunction;
    private final List<List<String>> functions;

    public CodeGenerator(final Program program) {
        this.program = program;
        constructors = labelConstructors(program.algs);
        functions = new ArrayList<List<String>>();
        nextTempVariable = 0;
        nextTempFunction = 0;
    }

    public static Map<ConsName, Integer> labelConstructors(final List<AlgDef> algs) {
        final Map<ConsName, Integer> retval = new HashMap<ConsName, Integer>();
        for (final AlgDef algDef : algs) {
            int label = 0;
            for (final ConsDef consDef : algDef.constructors) {
                retval.put(consDef.consName, new Integer(label++));
            }
        }
        return retval;
    }
    
    public static String opToString(final Op op) throws CodeGeneratorException {
        if (op instanceof PlusOp) {
            return "+";
        } else if (op instanceof LessThanOp) {
            return "<";
        } else if (op instanceof EqualsOp) {
            return "==";
        } else {
            throw new CodeGeneratorException("Unknown op: " + op);
        }
    }

    public Variable freshVariable() {
        return new Variable(TEMP_VARIABLE_PREFIX + (nextTempVariable++));
    }

    public String freshFunctionName() {
        return TEMP_FUNCTION_PREFIX + (nextTempFunction++);
    }
    
    // For generating within a single function
    private class FunctionContext {
        public List<String> lines;

        public FunctionContext() {
            lines = new LinkedList<String>();
        }

        public void addLine(final String stmt) {
            lines.add(stmt);
        }

        public void addLine(final int position, final String stmt) {
            lines.add(position, stmt);
        }
        
        public TranslationResult<Variable> tempVariableVardec(final Scope scope, final String rhs) {
            return vardec(scope, freshVariable(), rhs);
        }

        public TranslationResult<Variable> vardec(final Scope scope,
                                                  final Variable variable,
                                                  final String rhs) {
            addLine("let " + variable.name + " = " + rhs + ";");
            return new TranslationResult<Variable>(variable, scope.newVariable(variable));
        }

        public TranslationResult<Variable> translateIntLiteralExp(final IntLiteralExp exp, final Scope scope) {
            return tempVariableVardec(scope, Integer.toString(exp.value));
        }

        public TranslationResult<Variable> translateVariableExp(final VariableExp exp, final Scope scope) {
            return new TranslationResult<Variable>(exp.variable,
                                                   scope.variableUsed(exp.variable));
        }

        public TranslationResult<Variable> translateBooleanLiteralExp(final BooleanLiteralExp exp, final Scope scope) {
            return tempVariableVardec(scope, Boolean.toString(exp.value));
        }

        public TranslationResult<Variable> translateOpExp(final OpExp exp,
                                                          final Scope scope) throws CodeGeneratorException {
            final TranslationResult<Variable> left = translateExp(exp.left, scope);
            final TranslationResult<Variable> right = translateExp(exp.right, left.scope);
            return tempVariableVardec(right.scope,
                                      (left.result.name + " " +
                                       opToString(exp.op) + " " +
                                       right.result.name));
        }

        // let x = exp1 in exp2
        //
        // ...becomes...
        //
        // exp1_statement1;
        // exp1_statement2;
        // let temp0 = undefined;
        // {
        //   let x = exp1_statement_results;
        //   exp2_statement1;
        //   exp2_statement2;
        //   temp0 = exp2_statement_results;
        // }
        public TranslationResult<Variable> translateLetExp(final LetExp exp, final Scope scope)
            throws CodeGeneratorException {
            final TranslationResult<Variable> initializer = translateExp(exp.initializer, scope);
            final TranslationResult<Variable> result = tempVariableVardec(initializer.scope, "undefined");
            addLine("{ ");
            final TranslationResult<Variable> withUserVariable = vardec(result.scope,
                                                                        exp.variable,
                                                                        initializer.result.name);
            final TranslationResult<Variable> body = translateExp(exp.body, withUserVariable.scope);
            addLine(result.result.name + " = " + body.result.name + ";");
            addLine("}");
            return new TranslationResult<Variable>(result.result,
                                                   result.scope.joinFromNestedScope(body.scope));
        }

        // (x*) => exp
        //
        // ...becomes...
        //
        // function __temp_function_0(closure, x*) {
        //   let closed_over_variable1 = closure.closed_over_variable1;
        //   let closed_over_variable2 = closure.closed_over_variable2;
        //   return exp;
        // }
        //
        // { '__func: __temp_function_0, 'closed_over_variable1': ..., 'closed_over_variable2': ... }
        public TranslationResult<Variable> translateHOFExp(final MakeHigherOrderFunctionExp exp,
                                                           final Scope scope)
            throws CodeGeneratorException {
            final FunctionContext context = new FunctionContext();
            final StringBuilder header = new StringBuilder("function ");
            final String functionName = freshFunctionName();
            header.append(functionName);
            header.append("(");
            header.append(CLOSURE_NAME);
            for (final Variable param : exp.params) {
                header.append(", ");
                header.append(param.name);
            }
            header.append(") {");
            context.addLine(header.toString());

            // Basic idea: translate the body.  Any variables that the body uses that aren't
            // immediately available will be noted in the variables that must be closed over.
            // After translating the body, use this list to figure out which variables must have
            // been put in scope
            final TranslationResult<Variable> body =
                context.translateExp(exp.body, Scope.enteringNewFunction(exp.params));
            for (final Variable closedOver : body.scope.mustCloseOver) {
                context.addLine(1, "let " + closedOver.name + " = " + CLOSURE_NAME + "." + closedOver.name + ";");
            }
            context.addLine("return " + body.result.name + ";");
            context.addLine("}");
            functions.add(context.lines);

            // create the closure itself
            final StringBuilder closure = new StringBuilder("{ '");
            closure.append(FUNCTION_POINTER_NAME);
            closure.append("': ");
            closure.append(functionName);
            for (final Variable closedOver : body.scope.mustCloseOver) {
                closure.append(", '");
                closure.append(closedOver.name);
                closure.append("': ");
                closure.append(closedOver.name);
            }
            closure.append(" }");

            return tempVariableVardec(scope.joinFromNestedScope(body.scope),
                                      closure.toString());
        }

        public int constructorId(final ConsName consName) throws CodeGeneratorException {
            final Integer retval = constructors.get(consName);
            if (retval == null) {
                throw new CodeGeneratorException("Typechecker failed to catch undefined constructor name: " +
                                                 consName.name);
            } else {
                return retval.intValue();
            }
        }
        
        public TranslationResult<Variable> translateCallLikeExp(final CallLikeExp exp,
                                                                final Scope scope)
            throws CodeGeneratorException {
            if (exp.resolution == null) {
                throw new CodeGeneratorException("Resolution has not been set by the typechecker");
            } else if (exp.resolution instanceof HOFResolved) {
                final TranslationResult<Variable> function = translateExp(exp.functionLike, scope);
                final TranslationResult<List<Variable>> params = translateExps(exp.params, function.scope);
                final StringBuilder call = new StringBuilder(function.result.name);
                call.append(".");
                call.append(FUNCTION_POINTER_NAME);
                call.append("(");
                call.append(function.result.name);
                for (final Variable param : params.result) {
                    call.append(", ");
                    call.append(param.name);
                }
                call.append(")");
                return tempVariableVardec(params.scope, call.toString());
            } else if (exp.resolution instanceof NamedFunctionResolved) {
                final FunctionName functionName = ((NamedFunctionResolved)exp.resolution).functionName;
                final TranslationResult<List<Variable>> params = translateExps(exp.params, scope);
                final StringBuilder call = new StringBuilder(functionName.name);
                call.append("(");
                final Iterator<Variable> paramIterator = params.result.iterator();
                if (paramIterator.hasNext()) {
                    call.append(paramIterator.next().name);
                    while (paramIterator.hasNext()) {
                        call.append(", ");
                        call.append(paramIterator.next().name);
                    }
                }
                call.append(")");
                return tempVariableVardec(params.scope, call.toString());
            } else if (exp.resolution instanceof MakeAlgebraicResolved) {
                final StringBuilder obj = new StringBuilder("{ 'id': ");
                final ConsName consName = ((MakeAlgebraicResolved)exp.resolution).consName;
                obj.append(constructorId(consName));
                final TranslationResult<List<Variable>> params = translateExps(exp.params, scope);
                int paramId = 0;
                for (final Variable param : params.result) {
                    obj.append(", ");
                    obj.append("'_");
                    obj.append(paramId++);
                    obj.append("': ");
                    obj.append(param.name);
                }
                obj.append(" }");
                return tempVariableVardec(params.scope, obj.toString());
            } else {
                throw new CodeGeneratorException("Unknown resolution: " + exp.resolution);
            }
        }

        public TranslationResult<List<Variable>> translateExps(final List<Exp> exps,
                                                               Scope scope)
            throws CodeGeneratorException {
            final List<Variable> variables = new ArrayList<Variable>();
            for (final Exp exp : exps) {
                final TranslationResult<Variable> current = translateExp(exp, scope);
                variables.add(current.result);
                scope = current.scope;
            }
            return new TranslationResult<List<Variable>>(variables, scope);
        }

        // if exp1 exp2 exp3
        //
        // let temp0 = undefined;
        // ...
        public TranslationResult<Variable> translateIfExp(final IfExp exp, final Scope scope)
            throws CodeGeneratorException {
            final TranslationResult<Variable> guard = translateExp(exp.guard, scope);
            final TranslationResult<Variable> result = tempVariableVardec(guard.scope, "undefined");
            addLine("if (" + guard.result.name + ") {");
            final TranslationResult<Variable> ifTrue = translateExp(exp.ifTrue, result.scope);
            addLine(result.result.name + " = " + ifTrue.result.name + ";");
            addLine("} else {");
            final TranslationResult<Variable> ifFalse = translateExp(exp.ifFalse, ifTrue.scope);
            addLine(result.result.name + " = " + ifFalse.result.name + ";");
            addLine("}");
            return new TranslationResult<Variable>(result.result, ifFalse.scope);
        }

        // public TranslationResult<Class<Void>> translateCase(final Variable discriminator,
        //                                                     final Variable matchResult,
        //                                                     final Case theCase,
        //                                                     final Scope initialScope)
        //     throws CodeGeneratorException {
        //     final Integer id = constructors.get(theCase.consName);
        //     if (id == null) {
        //         throw new CodeGeneratorException("Typechecker missed unknown case in pattern match");
        //     }
        //     addLine("case " + id.intValue() + ": {");
        //     int index = 0;
        //     Scope scope = initialScope;
        //     for (final Variable variable : theCase.variables) {
        //         scope = vardec(scope, variable, discriminator.name + "._" + (index++)).scope;
        //     }
        //     final TranslationResult<Variable> bodyResult = translateExp(theCase.body, scope);
        //     addLine(matchResult.name + " = " + bodyResult.result.name + ";");
        //     addLine("break;");
        //     addLine("}");
        //     return new TranslationResult<Class<Void>>(Void.TYPE,
        //                                               initialScope.joinFromNestedScope(bodyResult.scope));
        // }        
        
        // match exp { ... }
        //
        // let temp0 = undefined;
        // switch (...) { case ...: temp0 = ...; }
        // public TranslationResult<Variable> translateMatchExp(final MatchExp exp, final Scope initialScope)
        //     throws CodeGeneratorException {
        //     final TranslationResult<Variable> result = tempVariableVardec(initialScope, "undefined");
        //     final TranslationResult<Variable> discriminator = translateExp(exp.exp, result.scope);
        //     addLine("switch (" + discriminator.result.name + ".id) {");
        //     Scope scope = discriminator.scope;
        //     for (final Case theCase : exp.cases) {
        //         scope = translateCase(discriminator.result,
        //                               result.result,
        //                               theCase,
        //                               scope).scope;
        //     }
        //     addLine("}");
        //     return new TranslationResult<Variable>(result.result, scope);
        // }

        // ---BEGIN EVERYTHING FOR NEW MATCH---
        public void patternToBooleanExp(final String base,
                                        final Pattern pattern,
                                        final StringBuffer buffer,
                                        final boolean initial)
            throws CodeGeneratorException {
            if (pattern instanceof ConsPattern) {
                final ConsPattern asCons = (ConsPattern)pattern;
                if (!initial) {
                    buffer.append(" && ");
                }
                buffer.append(base);
                buffer.append(".id == ");
                buffer.append(constructorId(asCons.consName));
                int patternNum = 0;
                for (final Pattern subPattern : asCons.patterns) {
                    patternToBooleanExp(base + "._" + patternNum,
                                        subPattern,
                                        buffer,
                                        false);
                }
            } else if (!(pattern instanceof UnderscorePattern ||
                         pattern instanceof VariablePattern)) {
                throw new CodeGeneratorException("Unrecognized pattern: " + pattern.toString());
            }
        }

        public String patternToBooleanExp(final String base,
                                          final Pattern pattern)
            throws CodeGeneratorException {
            final StringBuffer retval = new StringBuffer();
            patternToBooleanExp(base, pattern, retval, true);
            final String asString = retval.toString();
            // as an optimization, we intentionally avoid true until this moment
            if (asString.length() == 0) {
                return "true";
            } else {
                return asString;
            }
        }

        public TranslationResult<Class<Void>> introducePatternVariables(final String base,
                                                                        final Pattern pattern,
                                                                        Scope scope)
            throws CodeGeneratorException {
            if (pattern instanceof VariablePattern) {
                scope = vardec(scope,
                               ((VariablePattern)pattern).variable,
                               base).scope;
            } else if (pattern instanceof ConsPattern) {
                int patternNum = 0;
                for (final Pattern subPattern : ((ConsPattern)pattern).patterns) {
                    scope = introducePatternVariables(base + "._" + patternNum,
                                                      subPattern,
                                                      scope).scope;
                }
            } else if (!(pattern instanceof UnderscorePattern)) {
                throw new CodeGeneratorException("Unrecognized pattern: " + pattern.toString());
            }
            return new TranslationResult<Class<Void>>(Void.TYPE, scope);
        }

        public TranslationResult<Class<Void>> translateCase(final Case theCase,
                                                            final Variable discriminant,
                                                            final Variable result,
                                                            final Scope initialScope,
                                                            final boolean firstCase)
            throws CodeGeneratorException {
            final String header = (firstCase) ? "if (" : "} else if (";
            final String condition = patternToBooleanExp(discriminant.name, theCase.pattern);
            addLine(header + condition + ") {");
            final Scope innerScope = introducePatternVariables(discriminant.name,
                                                               theCase.pattern,
                                                               initialScope).scope;
            final TranslationResult<Variable> bodyResult = translateExp(theCase.body, innerScope);
            addLine(result.name + " = " + bodyResult.result.name + ";");
            addLine("}");
            return new TranslationResult<Class<Void>>(Void.TYPE,
                                                      initialScope.joinFromNestedScope(bodyResult.scope));
        }
        
        //
        // match exp { ... }
        //
        // exp_statement;
        // let temp0 = undefined;
        // if (exp_statement_results == ...) {
        //   temp0 = ...;
        // } else if (exp_statement_results == ...) {
        //   temp0 = ...;
        // } ...
        // 
        public TranslationResult<Variable> translateMatchExp(final MatchExp exp, final Scope initialScope)
            throws CodeGeneratorException {
            final TranslationResult<Variable> discriminant = translateExp(exp, initialScope);
            final TranslationResult<Variable> result = tempVariableVardec(discriminant.scope, "undefined");
            Scope scope = result.scope;
            boolean firstCase = true;
            for (final Case theCase : exp.cases) {
                scope = translateCase(theCase,
                                      discriminant.result,
                                      result.result,
                                      scope,
                                      firstCase).scope;
                if (firstCase) {
                    firstCase = false;
                }
            }
            return new TranslationResult<Variable>(result.result, scope);
        }                
        // ---END EVERYTHING FOR NEW MATCH---
        
        public TranslationResult<Variable> translatePrintlnExp(final PrintlnExp exp, final Scope scope)
            throws CodeGeneratorException {
            final TranslationResult<Variable> toPrint = translateExp(exp.exp, scope);
            addLine("console.log(" + toPrint.result.name + ");");
            return tempVariableVardec(toPrint.scope, "\"void\"");
        }

        public TranslationResult<Variable> translateBlockExp(final BlockExp exp, final Scope scope)
            throws CodeGeneratorException {
            // blocks in this language are purely for chaining prints
            // they don't have scoping implications (this is what let is for)
            final TranslationResult<List<Variable>> body = translateExps(exp.body, scope);
            final List<Variable> variables = body.result;
            return new TranslationResult<Variable>(variables.get(variables.size() - 1),
                                                   body.scope);
        }

        public TranslationResult<Variable> translateExp(final Exp exp, final Scope scope)
            throws CodeGeneratorException {
            if (exp instanceof IntLiteralExp) {
                return translateIntLiteralExp((IntLiteralExp)exp, scope);
            } else if (exp instanceof VariableExp) {
                return translateVariableExp((VariableExp)exp, scope);
            } else if (exp instanceof BooleanLiteralExp) {
                return translateBooleanLiteralExp((BooleanLiteralExp)exp, scope);
            } else if (exp instanceof OpExp) {
                return translateOpExp((OpExp)exp, scope);
            } else if (exp instanceof LetExp) {
                return translateLetExp((LetExp)exp, scope);
            } else if (exp instanceof MakeHigherOrderFunctionExp) {
                return translateHOFExp((MakeHigherOrderFunctionExp)exp, scope);
            } else if (exp instanceof CallLikeExp) {
                return translateCallLikeExp((CallLikeExp)exp, scope);
            } else if (exp instanceof IfExp) {
                return translateIfExp((IfExp)exp, scope);
            } else if (exp instanceof MatchExp) {
                return translateMatchExp((MatchExp)exp, scope);
            } else if (exp instanceof PrintlnExp) {
                return translatePrintlnExp((PrintlnExp)exp, scope);
            } else if (exp instanceof BlockExp) {
                return translateBlockExp((BlockExp)exp, scope);
            } else {
                throw new CodeGeneratorException("Unknown expression: " + exp);
            }
        }
    } // FunctionContext

    private void translateFunction(final FunctionDef functionDef) throws CodeGeneratorException {
        final List<Variable> params = new ArrayList<Variable>();
        for (final Vardec param : functionDef.params) {
            params.add(param.variable);
        }
        final StringBuilder header = new StringBuilder("function ");
        header.append(functionDef.functionName.name);
        header.append("(");
        final Iterator<Variable> paramsIterator = params.iterator();
        if (paramsIterator.hasNext()) {
            header.append(paramsIterator.next().name);
            while (paramsIterator.hasNext()) {
                header.append(", ");
                header.append(paramsIterator.next().name);
            }
        }
        header.append(") {");
        final FunctionContext context = new FunctionContext();
        context.addLine(header.toString());
        final Scope initialScope = Scope.enteringNewFunction(params);
        final TranslationResult<Variable> body = context.translateExp(functionDef.body, initialScope);
        if (!body.scope.mustCloseOver.isEmpty()) {
            throw new CodeGeneratorException("Typechecker failed to catch an undeclared variable in a function");
        }
        context.addLine("return " + body.result.name + ";");
        context.addLine("}");
        functions.add(context.lines);
    }

    private void reinitialize() {
        functions.clear();
        nextTempVariable = 0;
        nextTempFunction = 0;
    }
    
    public void translateProgram(final PrintWriter writer)
        throws CodeGeneratorException,
               IOException {
        reinitialize();
        
        for (final FunctionDef functionDef : program.functions) {
            translateFunction(functionDef);
        }
        final FunctionContext entryPointContext = new FunctionContext();
        final TranslationResult<Variable> entryPoint =
            entryPointContext.translateExp(program.entryPoint,
                                           Scope.enteringNewFunction(new ArrayList<Variable>()));
        if (!entryPoint.scope.mustCloseOver.isEmpty()) {
            throw new CodeGeneratorException("Typechecker failed to catch an undeclared variable in the entry point");
        }

        for (final List<String> function : functions) {
            for (final String line : function) {
                writer.println(line);
            }
        }

        for (final String line : entryPointContext.lines) {
            writer.println(line);
        }
    }

    public static void generateCode(final Program program,
                                    final PrintWriter output)
        throws CodeGeneratorException,
               IOException {
        new CodeGenerator(program).translateProgram(output);
    }
} // CodeGenerator
