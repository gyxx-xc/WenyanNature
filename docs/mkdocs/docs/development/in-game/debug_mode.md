# debug mode
静滞墨（暂定）

Currently, not sure if this function's helpful, so make it mvp.

in this mode, the player can open the gui of debug.
gui contain: 
- step,
- ~continue~,
- ~make breakpoint~,
- ~check one var's value when mouse hover~.
- exit (direct to edit mode)

customer story:
someone write a program, but it's too fast so he don't know which step wrong.
1. make sure there's no program running
2. set the fu to debug mode.
3. open gui
4. step and see what function provide result.
5. switch into edit mode, edit
6. try the program again.


# impl
change the way that step do it, require the program give the context to step.
inject the step function, maintain a pc that will stop the program, provide min(step, remain until pc) step
if click step, pc = get next context.getEndPc