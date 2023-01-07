type List[A] = Cons(A, List[A]) | Nil();
type Option[A] = Some(A) | None();

def length[A](list: List[A]): int =
  match list {
    case Cons(head, tail): 1 + length(tail)
    case Nil(): 0
  };

def map[A, B](list: List[A], f: (A) => B): List[B] =
  match list {
    case Cons(head, tail): Cons(f(head), map(tail, f))
    case Nil(): Nil()
  };

def getIndexHelper[A](list: List[A], pos: int, target: int): Option[A] =
  match list {
    case Cons(head, tail):
      if (pos == target)
        Some(head)
      else
        getIndexHelper(tail, pos + 1, target)
    case Nil(): None()
  };

def getIndex[A](list: List[A], index: int): Option[A] =
  getIndexHelper(list, 0, index);

def append[A](list1: List[A], list2: List[A]): List[A] =
  match list1 {
    case Cons(head, tail): Cons(head, append(tail, list2))
    case Nil(): list2
  };

def flatMap[A, B](list: List[A], f: (A) => List[B]): List[B] =
  match list {
    case Cons(head, tail): append(f(head), flatMap(tail, f))
    case Nil(): Nil()
  };

def printOption[A](option: Option[A]): void =
  match option {
    case Some(a): println(a)
    case None(): println(0)
  };

let integers = Cons(1, Cons(2, Cons(3, Nil()))) in
let booleans = map(integers, (x) => x < 2) in
let bigList = flatMap(integers, (x) => Cons(x, Cons(x + 1, Cons(x + 2, Nil())))) in
{
  printOption(getIndex(integers, 0));
  printOption(getIndex(integers, 1));
  printOption(getIndex(integers, 2));

  printOption(getIndex(booleans, 0));
  printOption(getIndex(booleans, 1));
  printOption(getIndex(booleans, 2));

  printOption(getIndex(bigList, 0));
  printOption(getIndex(bigList, 1));
  printOption(getIndex(bigList, 2));
  printOption(getIndex(bigList, 3));
  printOption(getIndex(bigList, 4));
  printOption(getIndex(bigList, 5));
  printOption(getIndex(bigList, 6));
  printOption(getIndex(bigList, 7));
  printOption(getIndex(bigList, 8));
}
