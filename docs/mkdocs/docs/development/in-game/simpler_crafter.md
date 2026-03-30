# simpler crafting block

# require

## needed

this block is for

1. make player get more familiour to the crafting form of calling function
2. provide some more intergrations/qol to the original game

## design

can call the function with specific logic to speed up the crafting.

we can got many blocks for different crafting. each block has different ways to speedup.

so we can get

### funerence

just funerence, but can burn any things (smoking, smelting, blasting).

has two process function: add one progress, mutiply by two.
and one get function of get progress.

the player excepted to used in two ways. 

one is always to add one progress, this can have two small advances, no need burning item, and a little faster when batched burn. 

another is to use the mutiply by two, which is much faster and able to beat other mod like Create's blaster.

if the process is over the need process, the item will be burned out(disappear), so player need to control the process to extactly meet the process. This feature will be only applied to muti by 2, so if user always add one, it will behaive same as orginal game.

the progress is decided by the item's speed. while with the batch increase, the process will increase at same scale, while del some random number (50% most, mean 25%) to force player to get the progress, as well as make the process faster.
