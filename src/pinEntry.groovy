/**
 *  Pin Entry
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
    name: "Pin Entry",
    namespace: "",
    author: "Eric Moritz",
    description: "This smart app lets you treat switches as a PIN when each are switch on.",
    
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
        section("Mode") {
          input "newMode", "mode", title: "Mode",
                         description: "mode to switch to when triggered"
        }

        section("Pin") {
          input "pin", "password", title: "Pin", description: "A string of 1-4 digits, ex: 11234 that represent each switch"
        }

	section("Switch 1") {
          input "switch1", "capability.switch"
        }

	section("Switch 2") {
          input "switch2", "capability.switch"
        }

	section("Switch 3") {
          input "switch3", "capability.switch"
        }

	section("Switch 4") {
          input "switch4", "capability.switch"
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
      switch1.id,
      switch2.id,
      switch3.id,
      switch4.id
    ]
  )
  subscribe(switch1, "switch.on", onSwitch)
  subscribe(switch2, "switch.on", onSwitch)
  subscribe(switch3, "switch.on", onSwitch)
  subscribe(switch4, "switch.on", onSwitch)
}


/******************/
/* Event handlers */
/******************/
def onSwitch(evt) {
  state.pinEntryFSM = PinEntryFSM_pushButton(
    state.pinEntryFSM,
    evt.deviceId
  )
  if(PinEntryFSM_isOpen(state.pinEntryFSM)) {
    log.debug "opened"
    setLocationMode(newMode);
    state.pinEntryFSM = PinEntryFSM_reset(state.pinEntryFSM)
  }
}

// Includes
#include "stack.groovy"
#include "pinEntryFSM.groovy"
