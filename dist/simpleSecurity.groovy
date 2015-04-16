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
  subscribe location, "mode", onMode
  subscribe motionSensors, "motion", onMotion
  subscribe demoSwitch, "switch", onDemoSwitch
  if(state_isArmed()) {
    state_arm()
  } else {
    state_disarm()
  }
}


/******************/
/* Event Handlers */
/******************/
def onDemoSwitch(evt) {
  log.debug "onDemoSwitch $evt.value"
  if(evt.value == "on") {
    setLocationMode(mode)
  } else if (evt.value == "off") {
    setLocationMode(offMode)
  }
}


def onMode(evt) {
  log.debug "onMode $evt.value"
  if(evt.value == armedMode) {
    state_arm()
  } else {
    state_disarm()
  }
  react()
}

def onMotion(evt) {
  log.debug "onMotion $evt.value"
  // never forget intruder once we see one
  state_setIntruderDetected(evt.value == 'active' || state_intruderDetected())
  react()
}

/*************/
/* State API */
/*************/
def state_arm() {
  log.debug "System armed"
  state_setIntruderDetected(false)
}

def state_disarm() {
  log.debug "System disarmed"
  state_setIntruderDetected(false)
}

def state_isArmed() {
  location.currentMode == armedMode || demoSwitch.currentSwitch == "pressed"
}


def state_setIntruderDetected(status) {
  log.debug "Intruder? $status"

  // new intruder detected
  if(status && !state.intruderDetected) {
    state.intruderDetectedAt = now()
  }
  state.intruderDetected = status
}

def state_intruderDetected() {
  state.intruderDetected
}

def state_isWarning() {
  now() - warningTimeout * 1000 < state.intruderDetectedAt
}

def react() {
  if(state_isArmed() && state_intruderDetected()) {
    if(state_isWarning()) {
      alarms.strobe()
      // reschedule react to figure out when the warning period is over
      runIn(10, "react")
    } else {
      alarms.both()
    }
  } else {
    alarms.off()
  }
}
