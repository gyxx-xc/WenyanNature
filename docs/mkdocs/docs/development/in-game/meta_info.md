# meta function info/WenyanDoc

## user story (?)

1. a user is importing a function from a package, however he don't know how to use it, so he go to
   the wenyan wiki to find out how to use it. this process is time-consuming and break the overall
   game experience that need to rely on information outside the game.
2. a user is using a function, he first type `shi` ten move hand to mouse click the function which
   only help he input the function name. this process cause somewhat of a delay, it would be much
   better if the click can just behave like the snippet that help he to input whole function
   statement.

## design requirement

1. user able to access the function desc, args require, return for a function.
2. program able to gen correct snippet for a function.
3. this system fit for the dynamic builtin code which can only gen after **wy** program run.
4. metadata is client only
5. the new system should make as less change as possible to follow solid
6. memory/efficiency matter. As a very basic system, it will be used very wildly.

function may diff while meta is same, e.g. package for block entity in different location, how to
make memory not grow if I put 999999 block module.

99% time only need 1% of function, metadata's lifecycle should be same as the function.

metadata access chain:
1. package snippet widget
2. passed by behavior
3. getPackageSnippets
4. level.getCapability(pos) (IWenyanDevice) <- put here
5. IWenyanDevice.exec
6. exec.function/var (Map<String, Value>)
7. value.type

the access of metadata suitable for all package:
- package from getter (IWD)
- package of wenyan function

### Iter 1:
IWD → map<String, metadata>
need change:
- access
IWD add interface metadata, add failback if no interface.
```java
interface xxx {Meta getMeta();}
```
- add
the simplest AP
package builder:
build → record(package, meta)
+ buildMeta
+ build function

WenyanFunction → client got string → parser code get ClientAST → getMeta

### iter 2:
IWD too much impl, put it to Raw package, which has same life cycle as IWD.