type Tree[] = Internal(Tree[], int, Tree[]) | Leaf();
match Internal(Internal(Leaf(), 1, Leaf()), 2, Internal(Leaf(), 3, Leaf())) {
  case Internal(Internal(_, v1, _), v2, Internal(_, v3, _)):
    println(v1 + v2 + v3)
  case _:
    println(0)
}
