package fplang.parser;

public class OpExp implements Exp {
    public final Exp left;
    public final Op op;
    public final Exp right;

    public OpExp(final Exp left,
                 final Op op,
                 final Exp right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public int hashCode() {
        return (left.hashCode() +
                op.hashCode() +
                right.hashCode());
    }

    public boolean equals(final Object other) {
        if (other instanceof OpExp) {
            final OpExp asOp = (OpExp)other;
            return (left.equals(asOp.left) &&
                    op.equals(asOp.op) &&
                    right.equals(asOp.right));
        } else {
            return false;
        }
    }

    public String toString() {
        return ("OpExp(" + left.toString() + ", " +
                op.toString() + ", " +
                right.toString() + ")");
    }
}
