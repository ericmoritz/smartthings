# Eric Moritz' smartthings

This is the source code for Eric Moritz' smartthings SmartApps

## Framework

Because smartthings SmartApps are kind of a pain to test, this project provides
a basic framework for unittesting and uses the C preprocessor to include
libraries.

### Includes

In the directory `include/` are `.groovy` files that your SmartApps can
use `#include "{filename}"` to include.  This allows you to create
reusable libraries for your SmartApps.

### Testing

By using includes, you can seperate out your logic into easy to test
finite state machines.

For example, for my `pinEntry` SmartApp, I have a
`include/pinEntryFSM.groovy` library for managing the state of the
`pinEntry` system while isolated from the SmartThings IDE.

To test this FSM, there is a `test/pinEntryFSM.groovy` test script:

```groovy

#include "stack.groovy"
#include "pinEntryFSM.groovy"

def state = PinEntryFSM("1234", ["a", "b", "c", "d"])

// press the correct buttons
(result, state) = PinEntryFSM_pushButton(state, "a")
assert result == [open: false, isValid: true]

```
running `make -B test` will run the tests

Writing the actual SmartApp is just a matter of defining the metadata,
preferences and wiring up the event handlers to the FSM and reacting to
the FSM's state changes.

