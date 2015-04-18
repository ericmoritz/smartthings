#include "stack.groovy"


def x = stack_init("123")
assert stack_peek(x) == "1"
assert stack_isEmpty(x) == false

x = stack_pop(x)
assert stack_peek(x) == "2"
assert stack_isEmpty(x) == false

x = stack_pop(x)
assert stack_peek(x) == "3"
assert stack_isEmpty(x) == false

x = stack_pop(x)
assert stack_peek(x) == ""
assert stack_isEmpty(x) == true
assert stack_pop(x) == ""
