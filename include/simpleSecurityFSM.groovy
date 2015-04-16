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

