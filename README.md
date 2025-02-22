# Functional Programming Example

This is specifically for typechecking and code generation.
Key features:

- Higher-order functions
- Generics
- Type inference
- Algebraic data types
- Pattern matching
- Non-trivial syntax

Assumptions:
- `void` is a special value (Scala/Swift semantics)

## Abstract Grammar ##

```
var is a variable
i is an integer
typevar is a type variable
functionname is a function name
algname is the name of an algebraic data type
consname is the name of a constructor
type ::= `int` | `bool` | `void` | typevar |
        `(` type* `)` `=>` type | algname `[` type* `]`
op ::= `+` | `<` | `==`
exp ::= i | var | `true` | `false` | exp op exp |
        `let` var `=` exp `in` exp |
        `(` x* `)` `=>` exp | exp `(` exp* `)` | functionname `(` exp* `)` |
        `if` `(` exp `)` exp `else` exp |  // (exp) ? exp : exp
        consname `(` exp* `)` | `match` exp `{` case+ `}` |
        `println` `(` exp `)` | `{` exp+ `}` // semicolon-separated expressions, returns last one
pattern ::= `_` | var | consname `(` pattern* `)`
case ::= `case` pattern `:` exp
vardec ::= x `:` type
consdef ::= consname `(` type* `)`
algdef ::= `type` algname `[` typevar* `]` `=` consdef+ `;`
functiondef ::= `def` functionname `[` typevar* `]` `(` vardec* `)` `:` type `=` exp `;`
program ::= algdef* functiondef* exp // exp is the entry point
```

## Concrete Grammar ##

```
id is an identifier
i is an integer
type ::= `int` | `bool` | `void` | id |
         `(` comma_types `)` `=>` type | id `[` comma_types `]`
primary_exp ::= i | id | `true` | `false` |
                `let` x `=` exp `in` exp |
                `if` `(` exp `)` exp `else` exp |
                `match` exp `{` case+ `}` |
                `println` `(` exp `)` | `{` semicolon_exps `}` |
                `(` exp `)`
call_exp ::= primary_exp (`(` comma_exps `)`)*
additive_exp ::= call_exp (`+` call_exp)*
less_than_exp ::= additive_exp (`<` additive_exp)*
equals_exp ::= less_than_exp (`==` less_than_exp)*
function_exp ::= (`(` comma_ids `)` `=>`)* equals_exp
exp ::= function_exp
semicolon_exps ::= exp `;` (exp `;`)*
comma_exps ::= [exp (`,` exp)*]
comma_ids ::= [id (`,` id)*]
comma_types ::= [type (`,` type)*]
vardec ::= id `:` type
comma_vardecs ::= [vardec (`,` vardec)*]
pattern ::= `_` | id | id `(` comma_pattern `)`
comma_pattern ::= [pattern (`,` pattern)*]
case ::= `case` pattern `:` exp
consdef ::= id `(` comma_types `)`
pipe_consdefs ::= consdef (`|` consdef)*
algdef ::= `type` id `[` comma_ids `]` `=` pipe_consdefs `;`
functiondef ::= `def` id `[` comma_ids `]` `(` comma_vardecs `)` `:` type `=` exp `;`
program ::= algdef* functiondef* exp // exp is the entry point
```

Syntactically, `consname(exp*)` and `functionname(exp*)` are both special cases of `exp(exp*)`, which cannot be differentiated until typechecking.
This is because an identifier can be an expression, and variables in an expression context cannot unambiguously be resolved (is this a variable?  A constructor name?  A function name?) without type information.

# Code Generation #

- For each algebraic data type, each constructor is given a unique number
- Each expression translates into:
    - A sequence of javascript statements
    - A variable holding the result of the statements
- Given that expressions become a mix of statements and expressions, we'll need to introduce temporary variables along the way
- Higher-order functions are represented with JS objects holding a reference to the function, as well as the closure environment.

```
(x*) => exp
```

...becomes:

```javascript
function __temp_function_0(closure, x*) {
  let closed_over_variable1 = closure.closed_over_variable1;
  let closed_over_variable2 = closure.closed_over_variable2;
  return exp;
}
```

# Running the Compiler

```console
mvn exec:java -Dexec.mainClass="fplang.Compiler" -Dexec.args="examples/list_length.fplang output.js"
```

# Running End-to-End Tests

```console
./run_end_to_end_tests.py
```
