// # start: project.ttl
// @prefix doap: <http://usefulinc.com/ns/doap#> .
// @prefx foaf: <http://xmlns.com/foaf/0.1/> .

// <https://github.com/ericmoritz/smartthings>
//   a doap:Project ;
//   doap:name "Eric Moritz' SmartThings apps" ;
//   doap:homepage <https://github.com/ericmoritz/smartthings> ;
//   doap:description "Various SmartThings apps" ;
//   doap:license <http://www.apache.org/licenses/LICENSE-2.0> ;
//   doap:maintainer <http://eric.themoritzfamily.com/> .

// <http://eric.themoritzfamily.com/>
//   a foaf:Person ;
//   foaf:name "Eric Moritz" ;
//   foaf:homepage <http://eric.themoritzfamily.com/> .
// # end: project.ttl
/**
 *  simple security
 *
 *  Copyright 2015 Eric Moritz
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the
 *  License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an "AS
 *  IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the License for the specific language
 *  governing permissions and limitations under the License.
 *
 */
definition(
    name: "simple security",
    namespace: "",
    author: "Eric Moritz",
    description: "If the system is armed, any motion will set off the alarm",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
  section("Armed mode") {
    input("armedMode", "mode")
  }
  section("Motion Sensors") {
    input("motionSensors", "capability.motionSensor", multiple: true)
  }
  section("Alarms") {
    input("alarms", "capability.alarm", multiple: true)
  }
  section("Settings") {
    input("warningTimeout", "number", title: "Warning period in seconds")
  }
  section("Demo Switch") {
    input("demoSwitch", "capability.switch")
    input("offMode", "mode")
  }
}

def installed() {
  log.debug "Installed with settings: ${settings}"
  initialize()
}

def updated() {
  log.debug "Updated with settings: ${settings}"
  unsubscribe()
  initialize()
}

def initialize() {
  state.fsm = SimpleSecurityFSM(warningTimeout)

  subscribe location, "mode", onMode
  subscribe motionSensors, "motion.active", onMotion
  subscribe demoSwitch, "switch", onDemoSwitch
}


/******************/
/* Event Handlers */
/******************/
def onDemoSwitch(evt) {
  log.debug "onDemoSwitch $evt.value"
  if(evt.value == "on") {
    state.fsm = SimpleSecurityFSM_arm(state.fsm)
    log.trace "arm($state.fsm)"
  } else if (evt.value == "off") {
    state.fsm = SimpleSecurityFSM_disarm(state.fsm)
    log.trace "disarm($state.fsm)"
  }
  react()
}

def onMode(evt) {
  log.debug "onMode $evt.value"
  if(evt.value == armedMode) {
    state.fsm = SimpleSecurityFSM_arm(state.fsm)
    log.trace "arm($state.fsm)"
  } else {
    state.fsm = SimpleSecurityFSM_disarm(state.fsm)
    log.trace "disarm($state.fsm)"
  }
  react()
}

def onMotion(evt) {
  state.fsm = SimpleSecurityFSM_intrude(state.fsm, now())
  react()
}

def react() {
  if(SimpleSecurityFSM_isWarning(state.fsm, now())) {
    alarms.strobe()
    runIn(60, "react")
  } else if (SimpleSecurityFSM_isAlarm(state.fsm, now())) {
    alarms.both()
  } else {
    alarms.off()
  }
}


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

