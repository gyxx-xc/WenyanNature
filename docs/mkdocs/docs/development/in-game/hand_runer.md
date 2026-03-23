# Hand Runner

## feat

### require

a low tech, in time, simple code runner, which basicly for situation
that not need too much complex logics. mainly provided some utils and
qol functions. make it a bit expansive to make in contrast to the
block runner. also, the function should really limited. this design of
item will provide some poc features that make the mod runner easier to
run.

### feature

a new item.

right click to use, use will consume one and summon a entity to run
the code. a cd should be apply (like ender eye/snow ball...).

code can be writen in write block.
in crafting table, it can attach the module to the runner, however,
only a few function is impled. the attached module is consumed after
the function belong to that fu is called.

set a time(tick) limit that after this time it will disappear, since
this is only for simple logic, most time totally enough. showing to
player that the entity is on fire (align to the use of fu that burn to
use)

provided some examples for player to fast get started

able to import modules from blocks, has containers that contain fu
only.

## impl design

about the crafting table, design a interface that able it to accept
mutible class and write.

item that override right click, the cd can copy the origin way to
impl, and summon a runner entity. the rest of logic is handled by
entity.

override the tick function to reduce the life(int) every tick, if life
is 0, del itself.

examples in the form of the origin item with well writen code in
component, can be obtained in creative tab or the valliage trade/chest.

### module function impl

import function: find through itself's inv, also find through the
block in range base on itself's pos.

for function in block already have, directly call is safe, the
function will be pretend to called as in block's tick.

for function called in inv, entity's information can be obtained in
context, and so can be futher impled. the warpper request should impl
the calling feature of make the function del the fu in entity's inv,
and check if the inv still has fu if called again.

for inv's module design refer to next section

### inv module

an new interface extend IWenyanDevice of IInvDev something. create a
new Capability's type and get the Device's name and raw exec package
from the capability of the itemstack. name should be held by the
component of the module (access from item stack), while the exec
package should held by the item, as it is same for different itemstack
most of the time.

the impl of the function, should not has too much code redundentcy
with the block module, might need to extract the common part as a
static function to call in both(block and item) impl.
