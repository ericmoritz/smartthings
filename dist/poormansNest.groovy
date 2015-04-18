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
 *  Poor Man's Nest
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
    name: "Poor Man's Nest",
    namespace: "",
    author: "Eric Moritz",
    description: "A poor man's learning themostat",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
  section("Thermostat") {
    input "themostat", "capability.thermostat"
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
  state.fsm = FSM(state.fsm)
  subscribe thermostat, "capability.thermostat.coolingSetpoint", onTempSet
}

def onTempSet(evt) {
  key = tempKey(new Date())
  def day = key[0]
  def hour = key[1]
  log.trace "onTempSet(.value=$evt.value) { day=$day; hour=$hour }"
  state.fsm = FSM_setTemp(state.fsm, day, hour, evt.value)
}

def onSuggestTemp(evt) {
  key = tempKey(new Date())
  def day = key[0]
  def hour = key[1]
  def temp = FSM_suggestTemp(state.fsm, day, hour)
  log.trace "FSM_suggestTemp(_, $day, $hour) -> $temp"
  if(temp != null) {
    themostat.setCoolingSetpoint(temp)
  }
}

private tempKey(dt) {
  [dt.getDay(), dt.getHours()]
}


def FSM(oldfsm) {
  oldfsm ?: [:]
}

/**********/
/* Events */
/**********/
def FSM_setTemp(fsm, dayOfWeek, hour, temp) {
  def key = [dayOfWeek, hour]
  def entry = fsm[key] ?: ["total": 0, "count": 0, "mean": 0]
  entry["total"] += temp
  entry["count"] += 1
  fsm[key] = entry
  fsm
}
/***********/
/* Queries */
/***********/
def FSM_suggestTemp(fsm, dayOfWeek, hour) {
  def key = [dayOfWeek, hour]
  def entry = fsm[key]
  if(entry != null) {
    (entry["total"] / entry["count"]).longValue()
  }
}

