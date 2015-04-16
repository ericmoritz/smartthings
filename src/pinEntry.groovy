/**
 *  Pin Entry
 *
 *  Copyright 2015 Eric Moritz
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Pin Entry",
    namespace: "",
    author: "Eric Moritz",
    description: "Switch to a mode when pressing a combination of buttons",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
        section("Pin") {
          input "pin", "password", description: "A string of 1-4 digits, ex: 11234"
        }
	section("Button 1") {
          input "button1", "capability.switch"
        }
	section("Button 2") {
          input "button2", "capability.switch"
        }
	section("Button 3") {
          input "button3", "capability.switch"
        }
	section("Button 4") {
          input "button4", "capability.switch"
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
  state.pinEntryFSM = PinEntryFSM(
    pin,
    [
      button1.id.toString(),
      button2.id.toString(),
      button3.id.toString(),
      button4.id.toString()
    ]
  )
  subscribe(button1, "switch.on", onButton)
  subscribe(button2, "switch.on", onButton)
  subscribe(button3, "switch.on", onButton)
  subscribe(button4, "switch.on", onButton)
}


/******************/
/* Event handlers */
/******************/
def onButton(evt) {
  def x
  x = PinEntryFSM_pushButton(
    state.pinEntryFSM,
    evt.deviceId
  )
  if(x[0]['open']) {
    log.debug "opened"
    x = PinEntryFSM_reset(state.pinEntryFSM)
    state.pinEntryFSM = x[1]
  }
}

// Includes
#include "stack.groovy"
#include "pinEntryFSM.groovy"
