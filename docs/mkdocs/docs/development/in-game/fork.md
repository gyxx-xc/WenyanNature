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

### File 1: MODIFY `IWenyanPackageable.java`

**Location:** `src/main/java/indi/wenyan/content/block/runner/IWenyanPackageable.java`

Add corresponding method

```java
public interface IWenyanPackageable {
    void newThread(IWenyanBytecode bytecode);
   ...
}
```

### File 2: MODIFY `WenyanCodeWithExecutor.java`

**Location:** `src/main/java/indi/wenyan/interpreter_impl/value/WenyanCodeWithExecutor.java`

---

### File 4: MODIFY `RunnerBlockEntity.java`

**Path:** `src/main/java/indi/wenyan/content/block/runner/RunnerBlockEntity.java`

**Changes:**
1. **Add `newThread(IWenyanBytecode)` overload** (after line 235): Same logic as `newThread(String)` but skips compilation:
   ```java
   public void newThread(IWenyanBytecode bytecode) {
       try {
           RunnerCreator.createThread(lazyProgram, bytecode, this.initEnvironment());
       } catch (WenyanException e) {
           handleError(e.getMessage());
       }
   }
   ```

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
   })
   ```

---

### File 6: MODIFY `WenyanSymbol.java`, `FunctionMetaText.java`

**Path:** `src/main/java/indi/wenyan/interpreter_impl/WenyanSymbol.java`

**Changes:**
1. **Add new symbol**:
   ```java
   public static final String CORE_EXEC = "「執」";
   ```

**Path:** `src/main/java/indi/wenyan/setup/language/FunctionMetaText.java`

**Changes:**
1. **Add `CoreExec`** to the enum
