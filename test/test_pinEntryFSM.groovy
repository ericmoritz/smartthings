#include "stack.groovy"
#include "pinEntryFSM.groovy"

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

