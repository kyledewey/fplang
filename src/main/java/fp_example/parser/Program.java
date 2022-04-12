package fp_example.parser;

import java.util.List;

public class Program {
    public final List<AlgDef> algs;
    public final List<FunctionDef> functions;
    public final Exp entryPoint;

    public Program(final List<AlgDef> algs,
                   final List<FunctionDef> functions,
                   final Exp entryPoint) {
        this.algs = algs;
        this.functions = functions;
        this.entryPoint = entryPoint;
    }

    public int hashCode() {
        return (algs.hashCode() +
                functions.hashCode() +
                entryPoint.hashCode());
    }

    public boolean equals(final Object other) {
        if (other instanceof Program) {
            final Program otherProgram = (Program)other;
            return (algs.equals(otherProgram.algs) &&
                    functions.equals(otherProgram.functions) &&
                    entryPoint.equals(otherProgram.entryPoint));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("Program(" + algs.toString() + ", " +
                functions.toString() + ", " +
                entryPoint.toString() + ")");
    }
}
