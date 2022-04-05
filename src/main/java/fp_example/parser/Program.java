package fp_example.parser;

import java.util.List;

public class Program {
    public final List<FunctionDef> functions;
    public final Exp entryPoint;

    public Program(final List<FunctionDef> functions,
                   final Exp entryPoint) {
        this.functions = functions;
        this.entryPoint = entryPoint;
    }

    public int hashCode() {
        return functions.hashCode() + entryPoint.hashCode();
    }

    public boolean equals(final Object other) {
        if (other instanceof Program) {
            final Program otherProgram = (Program)other;
            return (functions.equals(otherProgram.functions) &&
                    entryPoint.equals(otherProgram.entryPoint));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("Program(" + functions.toString() + ", " +
                entryPoint.toString() + ")");
    }
}
