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
def PinEntryFSM(String pin, ArrayList<String> buttonIds) {
  return [
    "pin": pin,
    "pinStack": stack_init(pin),
    "buttonIndexMap": [
      (buttonIds[0]): "1",
      (buttonIds[1]): "2",
      (buttonIds[2]): "3",
      (buttonIds[3]): "4",
    ]
  ]
}

/**********/
/* Events */
/*********/
def PinEntryFSM_reset(pinEntryFSM) {
  pinEntryFSM["pinStack"] = stack_init(pinEntryFSM["pin"])
  return [null, pinEntryFSM]
}

def PinEntryFSM_pushButton(pinEntryFSM, buttonId) {
  def stack = pinEntryFSM['pinStack']
  def buttonIndex = pinEntryFSM['buttonIndexMap'][buttonId]
  def isValid = stack_peek(stack) == buttonIndex

  if(isValid) {
    pinEntryFSM['pinStack'] = stack_pop(stack)
  }

  return [
    [open: stack_isEmpty(pinEntryFSM['pinStack']),
     isValid: isValid],
    pinEntryFSM
  ]
}

def state = PinEntryFSM("1234", ["a", "b", "c", "d"])

// press the correct buttons
(result, state) = PinEntryFSM_pushButton(state, "a")
assert result == [open: false, isValid: true]
(result, state) = PinEntryFSM_pushButton(state, "b")
assert result == [open: false, isValid: true]
(result, state) = PinEntryFSM_pushButton(state, "c")
assert result == [open: false, isValid: true]
(result, state) = PinEntryFSM_pushButton(state, "d")
assert result == [open: true, isValid: true]

// stays open until reset
(result, state) = PinEntryFSM_pushButton(state, "d")
assert result == [open: true, isValid: false]

// reset
(_, state) = PinEntryFSM_reset(state)

// invalid button sequence, then a valid sequence
(result, state) = PinEntryFSM_pushButton(state, "b")
assert result == [open: false, isValid: false]
(result, state) = PinEntryFSM_pushButton(state, "c")
assert result == [open: false, isValid: false]
(result, state) = PinEntryFSM_pushButton(state, "a")
assert result == [open: false, isValid: true]
(result, state) = PinEntryFSM_pushButton(state, "b")
assert result == [open: false, isValid: true]
(result, state) = PinEntryFSM_pushButton(state, "c")
assert result == [open: false, isValid: true]
(result, state) = PinEntryFSM_pushButton(state, "d")
assert result == [open: true, isValid: true]
