def SimpleSecurityFSM(warningTimeout) {
  __SimpleSecurityFSM_reset(
    [
      "warningTimeout": warningTimeout
    ]
  )
}

/***********/
/* Queries */
/***********/
def SimpleSecurityFSM_isWarning(state, timestamp) {
  (
    state['isArmed'] &&
    state['intruderDetectedAt'] &&
    timestamp - state['intruderDetectedAt'] < state['warningTimeout']
  )
}

def SimpleSecurityFSM_isAlarm(state, timestamp) {
  (
    state['isArmed'] &&
    state['intruderDetectedAt'] &&
    timestamp - state['intruderDetectedAt'] >= state['warningTimeout']
  )
}

/***********/
/* Actions */
/***********/
def SimpleSecurityFSM_intrude(state, timestamp) {
  if(state['isArmed']) {
    // If an intruder wasn't detected before, record the detection timestamp
    if(!state['intruderDetectedAt']) {
      state['intruderDetectedAt'] = timestamp
    }
  }
  state
}

def SimpleSecurityFSM_arm(state) {
  state = __SimpleSecurityFSM_reset(state)
  state['isArmed'] = true
  state
}

def SimpleSecurityFSM_disarm(state) {
  state = __SimpleSecurityFSM_reset(state)
  state
}

def __SimpleSecurityFSM_reset(state) {
  state['intruderDetectedAt'] = null
  state['isArmed'] = false
  state
}

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
