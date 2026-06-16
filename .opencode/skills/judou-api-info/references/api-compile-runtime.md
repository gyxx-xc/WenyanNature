# compile + runtime 包

完整流程：`编译 → Bytecode → Runner → Scheduler 调度`

---

## compile 包 — `indi.wenyan.judou.api.compile`

### WenyanCompiler

**路径**: `judou/src/main/java/indi/wenyan/judou/api/compile/WenyanCompiler.java`

文言代码的入口编译器。将文言源码编译成可执行的字节码。

```java
public final class WenyanCompiler {
    public WenyanCompiler(boolean debug) { ... }
    public WenyanCompiler() { ... }  // debug=false

    public BytecodeWithExportedValues compile(String sourceCode) { ... }

    public record BytecodeWithExportedValues(
        IWenyanBytecode bytecode,
        List<String> exportedValues
    ) {}
}
```

- `compile(sourceCode)` — 编译文言源码，返回字节码 + 导出的变量名列表
- 内部流程：预处理 → 中间代码生成 → 优化 → 验证
- 编译失败抛出 `WenyanCompileException`

**示例**:
```java
bytecode = new WenyanCompiler().compile(code).bytecode();
```

### IWenyanBytecode

**路径**: `judou/src/main/java/indi/wenyan/judou/api/compile/IWenyanBytecode.java`

编译后的字节码表示, 不应直接使用。

```java
public interface IWenyanBytecode {
    ...
}
```

---

## runtime 包 — `indi.wenyan.judou.api.runtime`

### IRunner（基础接口）

不应直接使用。

```java
public interface IRunner {
    IWenyanPlatform platform();          // 所属平台
    void block() throws WenyanUnreachedException;    // 阻塞当前线程
    void unblock() throws WenyanUnreachedException;  // 解除阻塞
    void yield() throws WenyanUnreachedException;    // 让出执行权
    void die() throws WenyanUnreachedException;      // 终止线程
    <T extends IWenyanScheduler.IWenyanThread> void create(IThreadHolder<T> newThread);
}
```

### IWenyanRunner（线程运行器）

**路径**: `judou/src/main/java/indi/wenyan/judou/api/runtime/IWenyanRunner.java`

```java
public interface IWenyanRunner extends IRunner {
    IGlobalResolver getGlobalResolver();           // 全局解析器（变量查找）
    IFrameManager<WenyanFrame> getFrameManager();  // 帧栈管理器
    WenyanFrame getCurrentRuntime();               // 当前栈帧（快捷方法）
}
```

### IFrameManager

**路径**: `judou/src/main/java/indi/wenyan/judou/api/runtime/IFrameManager.java`

管理函数调用的帧栈。

```java
public interface IFrameManager<T> {
    void call(T runtime);                           // 入栈（调用函数）
    void ret() throws WenyanUnreachedException;     // 出栈（返回）
    T getCurrentRuntimeException();                 // 取当前帧（无帧时报错）
    @Nullable T getCurrentRuntime();                // 取当前帧（可能为 null）
}
```

### IWenyanScheduler

**路径**: `judou/src/main/java/indi/wenyan/judou/api/runtime/IWenyanScheduler.java`

文言线程调度器。管理多个文言协程的执行，在游戏 tick 中逐步推进。

```java
public interface IWenyanScheduler<T extends IWenyanScheduler.IWenyanThread> {
    boolean isAvailable();                // 是否可继续使用
    void step();                          // 分配执行步数（非线程安全，需在主线程调用）
    IWenyanPlatform getPlatform();
    boolean isRunning();
    void stop();
    void create(IThreadHolder<T> runner); // 创建新线程
    void block(IThreadHolder<T> runner);
    void unblock(IThreadHolder<T> runner);
    void yield(IThreadHolder<T> runner);
    void die(IThreadHolder<T> runner);

    static <T extends IWenyanScheduler.IWenyanThread> IWenyanScheduler<T> defaultImpl(
        IWenyanPlatform platform, int step) { ... }
}
```

- `step()` — 每次调用推进一个 tick，是调度器的核心
- 默认实现: `WenyanSchedularImpl`
- `step` 参数控制每次分配多少条指令

### IEffectCapability

**路径**: `judou/src/main/java/indi/wenyan/judou/api/runtime/IEffectCapability.java`

```java
public interface IEffectCapability {
    boolean remainSteps();  // 是否还有剩余步骤（不精确，仅用于 effect 判断）
}
```

### RunnerCreator（工厂）

**路径**: `judou/src/main/java/indi/wenyan/judou/api/runtime/RunnerCreator.java`

创建 Runner 和线程的静态工厂。

```java
public enum RunnerCreator {
    static <T extends IWenyanScheduler.IWenyanThread> void createThread(
        Supplier<IWenyanScheduler<T>> scheduler,
        IWenyanBytecode mainRuntime,
        IGlobalResolver globalResolver);
}
```

## 完整编译→运行示例

```java
// 运行平台中
bytecode = new WenyanCompiler().compile(pages).bytecode();
RunnerCreator.createThread(lazyProgram, bytecode, this.initEnvironment());
```
