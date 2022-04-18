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
