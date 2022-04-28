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
        if (exp) exp else exp |  // (exp) ? exp : exp
        consname(exp*) | match exp { case+ } |
        println(exp) | { exp+ } // semicolon-separated expressions, returns last one
case ::= consname(x*): exp
vardec ::= x: type
consdef ::= consname(type*)
algdef ::= algname[typevar*] = consdef+
functiondef ::= def functionname[typevar*](vardec*): type = exp
program ::= algdef* functiondef* exp // exp is the entry point
```

Refactored grammar:
```
id is an identifier
i is an integer
type ::= `int` | `bool` | `void` | id | `(` comma_types `)` `=>` type | id `[` comma_types `]`
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
exp ::= make_function_exp | equals_exp
make_function_exp ::= `(` comma_ids `)` `=>` exp
semicolon_exps ::= exp `;` (exp `;`)*
comma_exps ::= [exp (`,` exp)*]
comma_ids ::= [id (`,` id)*]
comma_types ::= [type (`,` type)*]
vardec ::= id `:` type
comma_vardecs ::= [vardec (`,` vardec)*]
case ::= `case` id `(` comma_ids `)` `:` exp
consdef ::= id `(` comma_types `)`
pipe_consdefs ::= consdef (`|` consdef)*
algdef ::= `type` id `[` comma_ids `]` `=` pipe_consdefs `;`
functiondef ::= `def` id `[` comma_ids `]` `(` comma_vardecs `)` `:` type `=` exp
program ::= algdef* functiondef* exp // exp is the entry point
```

Syntactically, `consname(exp*)` and `functionname(exp*)` are both special cases of `exp(exp*)`, which cannot be differentiated until typechecking.
This is because an identifier can be an expression, and variables in an expression context cannot unambiguously be resolved (is this a variable?  A constructor name?  A function name?) without type information.


Example code from typechecking videos:
```
List[A] = Cons(A, List[A]) | Nil()

Cons(1, Cons(1, Nil())): List[int]
Cons(true, Cons(false, Cons(true, Nil()))): List[bool]

BinaryTree[A] = Leaf() | Node(BinaryTree[A], A, BinaryTree[A])

       +: (int, int) => int
       <: (int, int) => bool
       ==: (A, A) => bool

let a = 1 in a

let x = 5 in
let y = x + x in
y < x

(x, y) => x + y: (int, int) => int

let f = (x, y) => x + y in
f(1, 2)

((x, y) => x + y)(1, 2)

def length[A](list: List[A]): int =
  match list {
    Cons(head, tail): 1 + length(tail)
    Nil(): 0
  }

map(Cons(1, Cons(2, Cons(3, Nil()))), (x) => x + 1): Cons(2, Cons(3, Cons(4, Nil())))
map(Cons(1, Cons(2, Cons(3, Nil()))), (x) => x < 2): Cons(true, Cons(false, Cons(false, Nil())))

def map[A, B](list: List[A], f: (A) => B): List[B] =
  match list {
    Cons(head, tail): Cons(f(head), map(tail, f))
    Nil(): Nil()
  }
```
