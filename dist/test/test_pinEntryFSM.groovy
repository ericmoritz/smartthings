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

/***********/
/* Queries */
/**********/
def PinEntryFSM_isOpen(pinEntryFSM) {
  stack_isEmpty(pinEntryFSM['pinStack'])
}


/***********/
/* Events */
/***********/
def PinEntryFSM_reset(pinEntryFSM) {
  pinEntryFSM["pinStack"] = stack_init(pinEntryFSM["pin"])
  return pinEntryFSM
}

def PinEntryFSM_pushButton(pinEntryFSM, buttonId) {
  def stack = pinEntryFSM['pinStack']
  def buttonIndex = pinEntryFSM['buttonIndexMap'][buttonId]
  def isValid = stack_peek(stack) == buttonIndex

  if(isValid) {
    pinEntryFSM['pinStack'] = stack_pop(stack)
  }
  return pinEntryFSM
}

def state = PinEntryFSM("1234", ["a", "b", "c", "d"])

// press the correct buttons
state = PinEntryFSM_pushButton(state, "a")
assert PinEntryFSM_isOpen(state) == false

state = PinEntryFSM_pushButton(state, "b")
assert PinEntryFSM_isOpen(state) == false

state = PinEntryFSM_pushButton(state, "c")
assert PinEntryFSM_isOpen(state) == false

state = PinEntryFSM_pushButton(state, "d")
assert PinEntryFSM_isOpen(state) == true

// stays open until reset
state = PinEntryFSM_pushButton(state, "d")
assert PinEntryFSM_isOpen(state) == true



// reset
state = PinEntryFSM_reset(state)

// press the incorrect buttons, and then press the correct pin
state = PinEntryFSM_pushButton(state, "c")
assert PinEntryFSM_isOpen(state) == false

state = PinEntryFSM_pushButton(state, "d")
assert PinEntryFSM_isOpen(state) == false

state = PinEntryFSM_pushButton(state, "a")
assert PinEntryFSM_isOpen(state) == false

state = PinEntryFSM_pushButton(state, "a")
assert PinEntryFSM_isOpen(state) == false

state = PinEntryFSM_pushButton(state, "b")
assert PinEntryFSM_isOpen(state) == false

state = PinEntryFSM_pushButton(state, "c")
assert PinEntryFSM_isOpen(state) == false

state = PinEntryFSM_pushButton(state, "d")
assert PinEntryFSM_isOpen(state) == true
