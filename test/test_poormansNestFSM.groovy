#include "poormansNestFSM.groovy"

def fsm = FSM([:])
fsm = FSM_setTemp(fsm, 0, 1, 65)
fsm = FSM_setTemp(fsm, 0, 1, 72)
fsm = FSM_setTemp(fsm, 0, 1, 75)
assert FSM_suggestTemp(fsm, 0, 1) == 70
assert FSM_suggestTemp(fsm, 0, 2) == null
                   
