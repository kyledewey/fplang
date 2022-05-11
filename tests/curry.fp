def curry[](): (int) => (int) => (int) => (int) => int =
  (a) => (b) => (c) => (d) => a + b + c + d;

println(curry()(1)(2)(3)(4))
