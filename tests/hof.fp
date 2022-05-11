def add[](x: int): (int) => int =
  (y) => x + y;

let f1 = add(1) in
let f2 = add(2) in
{
  println(f1(2));
  println(f2(3));
}
