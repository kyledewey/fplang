# Functional Programming Example

This is specifically for typechecking and code generation.
Key languages:

- Higher-order functions
- Generic tuples
- Type inference

Assumptions:
- `void` is a special value (Scala/Swift semantics)

```
x is a variable
i is an integer
typevar is a type variable
functionname is a function name
type ::= int | bool | void | typevar | (type*) => type | (type+)
op ::= + | < | ==
exp ::= i | x | true | false | exp op exp |
        let x = exp in exp |
        (exp+) | exp._i |
        (x*) => exp | exp(exp*) | functionname(exp*) |
        if (exp) exp else exp |
        println(exp) | { exp+ } // semicolon-separated expressions, returns last one
vardec ::= x: type
functiondef ::= def functionname[typevar*](vardec*): type = exp
program ::= functiondef* exp // exp is the entry point
```
