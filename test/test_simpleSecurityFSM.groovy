#include "simpleSecurityFSM.groovy"

// State of disarmed
state = SimpleSecurityFSM(10)
assert SimpleSecurityFSM_isWarning(state, 0) == false
assert SimpleSecurityFSM_isAlarm(state, 0) == false

// State of armed but no intruder
state = SimpleSecurityFSM_arm(state)
assert SimpleSecurityFSM_isWarning(state, 0) == false
assert SimpleSecurityFSM_isAlarm(state, 0) == false

// Intrude
state = SimpleSecurityFSM_intrude(state, 1)

// armed and before the warning timeout expired
assert SimpleSecurityFSM_isWarning(state, 2) == true
assert SimpleSecurityFSM_isAlarm(state, 2) == false

// armed and after the warning timeout expired
assert SimpleSecurityFSM_isWarning(state, 11) == false
assert SimpleSecurityFSM_isAlarm(state, 11) == true

// disarm 
state = SimpleSecurityFSM_disarm(state)

// disarmed should no longer have an active alert
assert SimpleSecurityFSM_isWarning(state, 11) == false
assert SimpleSecurityFSM_isAlarm(state, 11) == false

// rearming should continue to not have an active alert
state = SimpleSecurityFSM_arm(state)
assert SimpleSecurityFSM_isWarning(state, 11) == false
assert SimpleSecurityFSM_isAlarm(state, 11) == false
