type List[A] = Cons(A, List[A]) | Nil();
def length[A](list: List[A]): int =
  match list {
    case Cons(head, tail): 1 + length(tail)
    case Nil(): 0
  };
println(length(Cons(true, Cons(false, Nil()))))
