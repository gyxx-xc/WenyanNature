a much faster step limiting system for the next gen WenyanRunner.

Currently, the step limiting system has two ting wrong:
function and blocking. function still costy in the situation that need instruct level optimization,
while the blocking(using java's lock) very conflict to the current impl that use the content switch
in thread pool as blocking.

also need to consider the basic requirement of the step limiting:

1. the step runned <= step limit, while should be as closed as possible
2. should be thread safe and not lead progrom to unexceped behavior.

behavior should be run the runner with slice of min(slice, steps), minus step with step runned after
end of this step, and not run when steps == 0. the step will be added 1. when no process is
waiting/running (i.e. no steps left / no running threads) 2. when steps running, the step will be
added after the current process is ended or / as same as the start of the next process, if no next
process, it becomes case 1.

For thread safty prove, consider all the cases

1. no running, just add it's the only that access/write the steps.
2. running, i.e. step add inside the slice. the steps runned between the two call of the added step
   max is (max of one slice from last steps) + (current steps) = max two steps call.

this can be futher impled by matain a bool to mark if add step is called, add steps as the new queue
offer. proven by: if no running no one access to steps, the first access is the next running, so
change just before the running is same as change when the called happens. if runnings, is the same
as the behvior above.

the running process's condition can then get to be: if has step left, run it. if no step left,
block. and the call of the addStep will wake it. the acquired step use in other place now considered
as same but not changed when running.

When step used up, the queue should no longer submit the thread for more efficent and no busy wait.
impl: a new blocking queue, that block until new steps arrive. should impl as a warpper of the
current blocking queue.

for design, come up with the idea of combine the step with the current system of slice. where the
step token used up, same as the slice ended.

meanwhile, the runner might early stop due to the slice ended/block. need to sync the info about the
step used. it read the step at the start of the thread, and write when slice end. this shoud be
warpped by the proramImpl, as it(steps) should be matained by the program.
