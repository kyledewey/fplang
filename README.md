# Functional Programming Example

This is specifically for typechecking and code generation.
Key features:

- Higher-order functions
- Generics
- Type inference
- Algebraic data types
- Pattern matching

Assumptions:
- `void` is a special value (Scala/Swift semantics)

```
x is a variable
i is an integer
typevar is a type variable
functionname is a function name
algname is the name of an algebraic data type
consname is the name of a constructor
type ::= int | bool | void | typevar | (type*) => type | algname[type*]
op ::= + | < | ==
exp ::= i | x | true | false | exp op exp |
        let x = exp in exp |
        (x*) => exp | exp(exp*) | functionname(exp*) |
        if (exp) exp else exp |
        consname(exp*) | match exp { case+ } |
        println(exp) | { exp+ } // semicolon-separated expressions, returns last one
case ::= pattern: exp
pattern ::= x | _ | consname(pattern*)
vardec ::= x: type
consdef ::= consname(type*)
algdef ::= algname[typevar*] = consdef+
functiondef ::= def functionname[typevar*](vardec*): type = exp
program ::= algdef* functiondef* exp // exp is the entry point
```
