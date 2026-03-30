# furnace

## needs

just funerence, but can burn any things (smoking, smelting, blasting).

has two process function: add one progress, mutiply by two.
and one get function of get progress.

the player excepted to used in two ways.

one is always to add one progress, this can have two small advances, no need burning item, and a
little faster when batched burn.

another is to use the mutiply by two, which is much faster and able to beat other mod like Create's
blaster.

if the process is over the need process, the item will be burned out(disappear), so player need to
control the process to extactly meet the process. This feature will be only applied to muti by 2, so
if user always add one, it will behaive same as orginal game.

the progress is decided by the item's speed. while with the batch increase, the process will
increase at same scale, while del some random number (50% most, mean 25%) to force player to get the
progress, as well as make the process faster.

## design

a block with two container, up for input, down for output.

a gui that almost same as the original game, when hover on progress bar, show the progress(s) in
tooltip.

module impl three function:

1. void burn()
2. void doubleBurn()
3. int getProgress()

## impls

blockentity has two itemstackResource handler, gui container menu.
after user put in the item stack/itemstack changed, check recipe and set the neededProgress = time
x count x reduce. reduce = 1 - random.double(0, min(count/64, 1) * 0.5), neededProgress = max(
neededprogress, 2). set the progress to 1.

for all three function first check if has recipe, if no, throw error.

1. burn function
    - progress >= neededProgress, check if the output slot is empty/able to put. if yes, empty the
      stack, add output to the output slot (meanwhile, since the input slot changed, the progress
      will update), else do nothing.
    - else, add one progress.
2. doubleBurn function
    - progress == needed, same as burn,
    - progress > needed, empty the slot only
    - else, double progress. (need add a checker for intger overflow)
3. getProgress function, return progress.