def stack_init(str) {
  str
}

def stack_peek(stack) {
  if(!stack_isEmpty(stack)) {
    stack[0]
  } else {
    ""
  }
}

def stack_pop(stack) {
  if(!stack_isEmpty(stack)) {
    stack.subSequence(1, stack.length())
  } else {
    ""
  }
}

def stack_isEmpty(stack) {
  stack.length() == 0
}


def x = stack_init("123")
assert stack_peek(x) == "1"
assert stack_isEmpty(x) == false

print x
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
