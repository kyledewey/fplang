package fplang.parser;

import java.util.List;

public class BlockExp implements Exp {
    public final List<Exp> body;

    public BlockExp(final List<Exp> body) {
        this.body = body;
        assert(!body.isEmpty());
    }

    public int hashCode() {
        return body.hashCode();
    }
    
    public boolean equals(final Object other) {
        return (other instanceof BlockExp &&
                body.equals(((BlockExp)other).body));
    }

    public String toString() {
        return "BlockExp(" + body.toString() + ")";
    }
}
