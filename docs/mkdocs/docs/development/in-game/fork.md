# exec function

make it able to run the function(async) at other's fu

## design

User-facing API: new exec function in FormationCoreModuleEntity, other not change
function exec(fu, function) → future

## requirement

1. reference other fu, better to use current impled import, import(fu) → obj fu
2. give a function exec(fu, function) → future to run
3. able to wait future

## impl

1. import behavior of import fu will not return package now. return a fu's object
   fu's object will overwrite getAttr to make turn to package lazy, transparent to user
   fu's content will contain a blockPos?
2. to exec, we need the other's fu's block entity
   need IWenyanScheduler.create() (also isRemoved()), blockpos for communication request
3. the future, can directly use current future

## note

1. fu's object: WenyanCodeWithExecutor warper of `IWenyanXxx super RunnerBlockEntity`, the getAttr
   will lazy call the current import's behavior
2. import xxx, current use getPackage: Either<Package, String> → import function
   to getPackage: IWenyanObject (impl by WenyanPackage/WenyanCodeWithExecutor), current import handler
   might no need anymore
3. RunnerBlockEntity should only be changed for a new newThread(IWenyanBytecode), with same logic

# detail

---

### Overview

The `exec` feature enables a pool (眼/FormationCore) to run functions on a remote fu (符/RunnerBlockEntity) and get a future result.

**User-facing API:**
```
fu = import("otherFu")       // → WenyanCodeWithExecutor (new)
result = 眼.exec(fu, "func")  // → IWenyanValue (future)
await(result)                 // wait for completion
```

---

### File 1: CREATE `IWenyanPackageable.java`

**Location:** `src/main/java/indi/wenyan/content/block/runner/IWenyanPackageable.java`

**Role:** A interface for `WenyanCodeWithExecutor`, impled by `RunnerBlockEntity`

```java
public interface IWenyanPackageable {
    String code();
    void newThread(IWenyanBytecode bytecode);
   ...
}
```

### File 1: CREATE `WenyanCodeWithExecutor.java`

**Location:** `src/main/java/indi/wenyan/interpreter_impl/value/WenyanCodeWithExecutor.java`

**Role:** A lazy wrapper around a remote `RunnerBlockEntity`, implementing `IWenyanObject`. This is what `import("fuName")` returns instead of a compiled `WenyanPackage`.

**Design:**
- `public record WenyanCodeWithExecutor(IWenyanPackageable packageable)` implementing `IWenyanObject`
- Has a static `WenyanType<WenyanCodeWithExecutor> TYPE`
- `getAttribute(String name)` lazily looks up the `RunnerBlockEntity` at `blockPos`, gets its code, returns a function reference that when called, executes on the target fu's scheduler via `IWenyanScheduler.create()`
- The `platformName` is for display/debugging
- Follows the same pattern as `WenyanEntity`/`WenyanBlock` (record, `IWenyanObject`)
- Stores a "future result" that can be polled by the `exec` handler later

Key structure:
```java
public record WenyanCodeWithExecutor(IWenyanPackageable packageable)
        implements IWenyanObject {
    public static final WenyanType<WenyanCodeWithExecutor> TYPE = ...;
    
    @Override public WenyanType<?> type() { return TYPE; }
    
    @Override
    public IWenyanValue getAttribute(String name) throws WenyanException {
        // Lazy: look up RunnerBlockEntity, compile its code,
        // return the exported value/function as a wrapper
    }
}
```

---

### File 2: MODIFY `BlockPackageGetter.java`

**Path:** `src/main/java/indi/wenyan/content/block/runner/BlockPackageGetter.java`

**Changes:**
1. **`getPackage()` return type** (line 20): `Either<WenyanPackage, String>` → `IWenyanObject`
   ```java
   public @Nullable IWenyanObject getPackage(Level level, BlockPos blockPos, String packageName)
   ```

2. **`getWenyanPackageEither()`** (line 31): When target is a `RunnerBlockEntity` (line 35-37), return a `WenyanCodeWithExecutor` instead of `Either.right(code)`:
   ```java
   // OLD: return Either.right(platform.getCode());
   // NEW:
   return new WenyanCodeWithExecutor(level, pos, platform.getPlatformName());
   ```

3. **Return type usages** updated throughout: `Either` import might no longer be needed for return type (mark Deprecated).

---

### File 3: MODIFY `ImportRequest.java`

**Path:** `src/main/java/indi/wenyan/interpreter_impl/ImportRequest.java`

**Changes:**
1. **`ImportFunction` interface** (line 76-83): Return type change
   ```java
   // OLD:
   Either<WenyanPackage, String> getPackage(IHandleContext context, String packageName) throws WenyanException;
   // NEW:
   IWenyanObject getPackage(IHandleContext context, String packageName) throws WenyanException;
   ```

2. **`handle()` method** (line 47-69): Simplify — remove the compilation+execution branch entirely, since `WenyanCodeWithExecutor` handles lazy compilation:
   ```java
   public boolean handle(IHandleContext context) throws WenyanException {
       var result = getPackage.getPackage(context, packageName);
       if (result != null) {
           onReturn.accept(result);
       } else {
           throw new WenyanException(...);
       }
       thread().unblock();
       return true;
   }
   ```
   This means `WenyanCompiler`, `WenyanFrame`, and related compilation imports can be removed from this file.

---

### File 4: MODIFY `RunnerBlockEntity.java`

**Path:** `src/main/java/indi/wenyan/content/block/runner/RunnerBlockEntity.java`

**Changes:**
1. **Add `newThread(IWenyanBytecode)` overload** (after line 235): Same logic as `newThread(String)` but skips compilation:
   ```java
   public boolean newThread(IWenyanBytecode bytecode) {
       try {
           RunnerCreator.createThread(lazyProgram, bytecode, this.initEnvironment());
       } catch (WenyanException e) {
           handleError(e.getMessage());
           return false;
       }
       return true;
   }
   ```

2. **Update `initEnvironment()` import handler** (line 139-143): Since `blockPackageGetter.getPackage()` now returns `IWenyanObject` directly, the handler lambda changes from:
   ```java
   ImportRequest.handlerOf((_, name) -> {
       var either = blockPackageGetter.getPackage(level, getBlockPos(), name);
       ...
       return either;
   })
   ```
   to returning `IWenyanObject` directly (no `Either` wrapping).

---

### File 5: MODIFY `FormationCoreModuleEntity.java`

**Path:** `src/main/java/indi/wenyan/content/block/additional_module/block/FormationCoreModuleEntity.java`

**Changes:**
1. **Add `exec` handler** in the `execPackage` builder chain (after the `CORE_JOIN` handler at line 106):
   ```java
   .description(FunctionMetaText.CoreExec.string())
   .handler(WenyanSymbol.CORE_EXEC, (_, request, onReturn) -> {
       // args: [fu_obj (WenyanCodeWithExecutor), functionName (WenyanString)]
       // 1. Validate args (size == 2)
       // 2. Extract WenyanCodeWithExecutor and function name
       // 3. Look up RunnerBlockEntity from the stored BlockPos
       // 4. Get the fu's code, compile the function, create a thread
       // 5. Track the started execution (like startedPlatforms) for future polling
       // 6. Return false to indicate not done yet; return true + onReturn when done
   })
   ```

2. **New field** for tracking exec futures: The handler needs to track running exec tasks. This could be a new `Map<String, FutureTask>` similar to `startedPlatforms`.

3. **New imports**: `WenyanCodeWithExecutor`, `IWenyanBytecode`, `WenyanCompiler`, `RunnerCreator`

---

### File 6: MODIFY `WenyanSymbol.java`

**Path:** `src/main/java/indi/wenyan/interpreter_impl/WenyanSymbol.java`

**Changes:**
1. **Add new symbol** after `CORE_JOIN` (line 8):
   ```java
   public static final String CORE_EXEC = "「執」";
   ```

---

### File 7: MODIFY `FunctionMetaText.java`

**Path:** `src/main/java/indi/wenyan/setup/language/FunctionMetaText.java`

**Changes:**
1. **Add `CoreExec`** to the enum (line 16):
   ```java
   CoreStart, CoreJoin, CoreStatus, CoreExec,
   ```

---

### File 8: POTENTIALLY MODIFY `ExceptionText.java`

**Path:** `src/main/java/indi/wenyan/setup/language/ExceptionText.java`

If needed, add a new error message for exec failures (e.g., target fu not found at stored position).

---

### Dependency flow

```
WenyanCodeWithExecutor (new)
    ↑ uses
BlockPackageGetter (modified to return WenyanCodeWithExecutor for fu targets)
    ↑ called by
ImportRequest (simplified - no more compilation)
RunnerBlockEntity.initEnvironment() (calls BlockPackageGetter)
    
FormationCoreModuleEntity (adds exec handler that uses WenyanCodeWithExecutor)
RunnerBlockEntity (adds newThread(IWenyanBytecode))
```

### Key design points

1. **Lazy compilation**: `WenyanCodeWithExecutor.getAttribute()` compiles the fu's code on first access, not at import time. The "current import handler (compilation branch)" moves into `WenyanCodeWithExecutor`.

2. **No `Either` return type**: `ImportFunction.getPackage()` returns `IWenyanObject` directly since `WenyanPackage` already implements `IWenyanObject` (via `IWenyanObjectType extends IWenyanObject`).

3. **Async exec pattern**: Uses the same return-`false`-until-complete pattern as `CORE_JOIN`. The `exec` handler stores a reference to the started thread and polls `isRunning()` on subsequent ticks.

4. **Future mechanism**: Leverages the existing request-blocking infrastructure — the calling thread blocks (`request.thread().block()`) and gets unblocked when the remote execution completes.
