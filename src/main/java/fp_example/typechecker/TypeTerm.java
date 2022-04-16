package fp_example.typechecker;

// TypeTerms represent a type that can participate in unification.
// Unknown information is represented with a PlaceholderTypeTerm.
// typeterm ::= PlaceholderTypeTerm(i) |
//              IntTypeTerm |
//              BoolTypeTerm |
//              VoidTypeTerm |
//              TypevarTypeTerm(typevar) |
//              FunctionTypeTerm(typeterm*, typeterm) |
//              AlgebraicTypeTerm(algname, typeterm*)
public interface TypeTerm {}
