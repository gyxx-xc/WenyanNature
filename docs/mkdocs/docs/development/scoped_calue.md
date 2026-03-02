# Design of the value capture

## behavour

var's value is referenced from the scope at compile time, regardless of the runtime situation.

!!! note language
use python as code demo, but it is not act as same as python

```python
i = 0  # i0


def a():
    # i = i0
    i = 1  # i1
    # i = i1
    if True:
        # i = i1
        i = 2  # i2
        # i = i2

    def b():
        # i = i1
        i = 3  # i3
        # i = i3


# ...

# if run b here (outside a)
# i still reference as the comment above (i1, i3)
b()
```

It's also need to conider that the value can be assigned, while all reference to that value is also
changed.
However, when the varibles is defined again, varibles then is reference to the local scope's
varibles.

```python
i = 0


def a():
    set
    i = 1  # i.e. 昔之i者 今一是矣; reference to i0
    i = 2  # i.e. 夫二 名之曰i
    print(i)  # reference to i2


def b():
    print(i)  # reference to i0


a()
b()  # print 1
```

## Design

### overview

Implement by devied into three parts: local, ref, and global. local and ref is impl as list in
frame, where local store the vars defined in this function, ref store the vars referenced from other
scope. global is impl as a map in global scope, maped from string to value.

compiler will handle which id to which index of local or ref list at compile time, so when runtime,
the index get the value directly.

When make function ref will capture the needed vars in local scope, and stored in function's(
`WenyanBuiltinFunction`) list. When return, frame will destroy, the local list will free (by gc),
values referenced will not as they stored in function's list After that when function is called, the
function's list will be copied to frame's ref list.

### compiler

Compiler will:

1. find the variables from which scope.
2. gen instruction.

For locals, compiler will matain a scope stack, which Stack of Scope(int stackBase, Map<String,
int>)

#### scope

When enter a scope, push a scope to stack, and pop when exited. To find a variable, compiler will
find the scope stack from top to bottom, find the first scope which contains the variable. If not
find any vars from stack, find it from the parent scope and local it as reference.

For refs, compiler will recursively find the scope stack as local. When finded, will produce two
record ScopedVar and CapturedVar. ScopedVar used as sign of this var, when same var loaded again,
this will prevent the var captured into ref list again. CapturedVar used to set index of ref, for
how to set, see below.

Otherwise, if not find, will load it as global in string, and find it runtime.

#### instruction

For locals id to int mapping, when new id defined, set it to localVariblesCounter ++, and store into
map. when the scope is exited, counter will reset to the scope's stackBase. As scope is exit the
varibles in it is no longer needed, the new vars later will overwrite the old place from stackBase.
e.g.

```python
if True:
    a = 0  # use store 0
b = 1  # use store 0 and overwrite the old vars
```

For refs, compiler will store which vars need to capture, thus form a ordered list stored refs. and
then can know the index of the refs' list is get right value. To capture, a var is either from local
of a reference. If a function is defined inside another, compiler will update refs of all parent to
load the ref in chain. So, when loaded, the recursively call will do two things to CapturedVar.
First, add this var to itselves ref list, then update and return the index of ref to its ref list.

For globals, bytecode matain a symbol table, to map int in bytecode to string.

### runtime

for runtime, make function will store refs to frame's ref list. when called, the frame's ref list
will be set to function'sref list.