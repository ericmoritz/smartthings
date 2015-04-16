#include "stack.groovy"
#include "pinEntryFSM.groovy"

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
