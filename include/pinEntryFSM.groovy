def PinEntryFSM(String pin, ArrayList<String> buttonIds) {
  return [
    "pin": pin,
    "pinStack": stack_init(pin),
    "buttonIndexMap": [
      (buttonIds[0]): "1",
      (buttonIds[1]): "2",
      (buttonIds[2]): "3",
      (buttonIds[3]): "4",
    ]
  ]
}

/***********/
/* Queries */
/**********/
def PinEntryFSM_isOpen(pinEntryFSM) {
  stack_isEmpty(pinEntryFSM['pinStack'])
}


/***********/
/* Events */
/***********/
def PinEntryFSM_reset(pinEntryFSM) {
  pinEntryFSM["pinStack"] = stack_init(pinEntryFSM["pin"])
  return pinEntryFSM
}

def PinEntryFSM_pushButton(pinEntryFSM, buttonId) {
  def stack = pinEntryFSM['pinStack']
  def buttonIndex = pinEntryFSM['buttonIndexMap'][buttonId]
  def isValid = stack_peek(stack) == buttonIndex

  if(isValid) {
    pinEntryFSM['pinStack'] = stack_pop(stack)
  }
  return pinEntryFSM
}
