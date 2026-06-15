---
name: judou-runtime
description: 描述 judou 运行时的架构、行为、接口契约与集成方式。适用于需要理解或修改 judou 核心逻辑时的参考。
---

# Judou 运行时 Class 关系图

## 目录结构

| 文件 | 职责 |
|-|-|
| `IWenyanRunner.java` | 暴露给字节码/handler的 runner 接口 |
| `IThreadHolder.java` | 暴露给调度器的 runner 接口（代理模式） |
| `IWenyanScheduler.java` | 调度器接口 |
| `IFrameManager.java` | 帧栈管理器接口 |
| `IEffectCapability.java` | 视觉效果接口 |
| `RunnerCreator.java` | 静态工厂 |
| `WenyanRunner.java` | 旧版 runner（虚方法调用） |
| `WenyanSwitchInlineRunner.java` | 新版 runner（switch 硬编码内联） |
| `WenyanFrame.java` | 栈帧 |
| `WenyanResultStack.java` | 结果栈 |
| `FrameManagerImpl.java` | 帧栈管理实现 |
| `WenyanSchedularImpl.java` | 调度器实现 |
| `IRunner.java` | 内部接口，被 API 层 IWenyanRunner 和 IThreadHolder 共同继承 |

## 继承/实现关系

```
IRunner (api/IRunner.java)
  ├── IWenyanRunner (api/IWenyanRunner.java)
  │     └── 新增: getGlobalResolver(), getFrameManager(), getCurrentRuntime()
  │     └── 静态方法: dieWithException()
  │
  └── IThreadHolder (api/IThreadHolder.java)
        └── 新增: setThread(), getThread(), run(int), pause()
        └── default 方法: 将 block/yield/die/create/platform 全部委托给 program() (即 scheduler)

IFrameManager<T> (api/IFrameManager.java)
  └── FrameManagerImpl<T = WenyanFrame> (impl/FrameManagerImpl.java)

IWenyanScheduler<T> (api/IWenyanScheduler.java)
  └── WenyanSchedularImpl<T = PCB> (impl/WenyanSchedularImpl.java)
        └── 同时也 implements IEffectCapability

WenyanRunner<T> (impl/WenyanRunner.java)
  └── implements IWenyanRunner, IThreadHolder<T>
WenyanSwitchInlineRunner<T> (impl/WenyanSwitchInlineRunner.java)
  └── implements IWenyanRunner, IThreadHolder<T>
```

## 持有关系

```
WenyanRunner / WenyanSwitchInlineRunner
  ├── FrameManagerImpl frameManager          ← 帧栈管理器
  └── IGlobalResolver globalResolver         ← 全局变量/函数解析器

FrameManagerImpl
  ├── WenyanFrame currentRuntime             ← 当前栈帧（可为 null，表示 main 已返回）
  └── int recursionDepth                     ← 递归深度计数器

WenyanFrame
  ├── IWenyanBytecode bytecode               ← 当前函数字节码
  ├── int programCounter                     ← 指令指针
  ├── WenyanFrame returnRuntime              ← 调用者的帧（单向链表链接）
  ├── List<IWenyanValue> locals              ← 局部变量表
  ├── List<IWenyanValue> references          ← 闭包引用捕获表
  ├── WenyanResultStack resultStack          ← 结果栈（操作数栈，有界）
  ├── Deque<IWenyanValue> processStack       ← 处理栈（中间值，无界）
  ├── boolean PCFlag                         ← 指令是否已修改 PC（如 JMP）
  └── ReturnBehavior returnBehavior          ← 返回回调策略

WenyanResultStack
  └── Deque<IWenyanValue> stack              ← 实际栈（ArrayDeque，有 maxSize 限制）

WenyanSchedularImpl
  ├── Semaphore stepLock                     ← 步数信号量
  ├── volatile int accumulatedSteps          ← 累积可用步数
  ├── Collection<PCB> allThreads             ← 所有文言线程（ConcurrentHashMap 安全 Set）
  ├── ThreadPoolExecutor executor             ← 底层线程池（core=1, max=1）
  ├── IWenyanPlatform platform               ← 平台集成接口
  └── PCB (内部类, 实现 IWenyanThread)
        ├── IThreadHolder<PCB> runner        ← 指向所属 runner
        ├── IWenyanScheduler<PCB> program    ← 指向所属 scheduler
        ├── State state                      ← READY / BLOCKED / DYING
        └── ScheduledFuture<?> watchdog      ← 看门狗定时器
```

## 调用链（运行时数据流）

### 主循环

```
  Minecraft tick (20 tick/s)
    → scheduler.step()                       [Semaphore.release()]
    → submitThread(runner)
        → executor.execute()
            → stepLock.acquire()              [获取步数配额]
            → accumulatedSteps = step
            → startWatchdog(thread)
            → runner.run(sliceStep)           [↓ 进入 runner]
```

### Runner 执行循环

```
  WenyanRunner.run(step)
    ┌─────────────────────────────────────────────────┐
    │ for (i = 0; i < step; i++) {                    │
    │   runtime = frameManager.getCurrentRuntime()     │
    │   runtime == null → die(), return               │
    │   validateRuntimeState(runtime)                 │
    │   code = bytecode.getCode(programCounter)        │
    │   code.getCode().exec(arg, this)   [虚方法调度]   │
    │   updateProgramCounter(runtime)                  │
    │    → !PCFlag? PC++ : PCFlag=false               │
    │    → willPause? return                          │
    │ }                                                │
    │ this.yield()                                     │
    └─────────────────────────────────────────────────┘

  WenyanSwitchInlineRunner.run(step)
    ┌─────────────────────────────────────────────────────┐
    │ for (i = 0; i < step; i++) {                        │
    │   runtime = frameManager.getCurrentRuntime()         │
    │   runtime == null → die(), return                   │
    │   ordinal = bytecode.getCodeOrdinal(programCounter)  │
    │   switch (ordinal) ...  [switch 硬编码内联, 无虚调用] │
    │   !pcFlag? PC++ : pcFlag=false                      │
    │   willPause? return                                  │
    │ }                                                    │
    │ this.yield()                                         │
    └─────────────────────────────────────────────────────┘
```

### IThreadHolder 代理模式

指令内调用 `block()` / `yield()` / `die()` 时，通过 IThreadHolder 的 default method 桥接到 scheduler：

```
  runner.block()
    → IThreadHolder.block()    [default method]
      → program().block(this)
        → scheduler.block(runner)          [WenyanSchedularImpl]
          → thread.setState(BLOCKED)
          → runner.pause()
  runner.yield()
    → IThreadHolder.yield()    [default method]
      → program().yield(this)
        → scheduler.yield(runner)
          → submitThread(runner)            [重新入队等待下次调度]
          → runner.pause()
  runner.die()
    → IThreadHolder.die()      [default method]
      → program().die(this)
        → scheduler.die(runner)
          → allThreads.remove(thread)
          → thread.setState(DYING)
          → runner.pause()
```

### 异常处理

```
  catch (WenyanException e)
    → IWenyanRunner.dieWithException(this, e)
      → frame.getErrorContext(e, logger)    [获取源码上下文]
      → e.handle(platform.handleError, logger, errorContext)
      → runner.die()
```

## RunnerCreator 工厂逻辑

```java
RunnerCreator.newRunner(mainRuntime, globalResolver)
  → config.

useLegacyRunner()?
        → true:new WenyanRunner<>(mainRuntime,globalResolver)
        → false:new WenyanSwitchInlineRunner<>(mainRuntime,globalResolver)

        RunnerCreator.

createThread(scheduler, bytecode, globalResolver)
  → scheduler.

create(newRunner(WenyanFrame.ofCode(bytecode),globalResolver))
```

## 关键设计点总结

| 设计点 | 说明 |
|-|-|
| **双 runner 并存** | `WenyanRunner` 走虚方法 `exec(arg, this)`；`WenyanSwitchInlineRunner` 走 `switch(ordinal)`，消除虚调用，性能更优 |
| **帧栈是单向链表** | `FrameManagerImpl` 仅持 `currentRuntime` 指针，通过 `WenyanFrame.returnRuntime` 回溯，非传统数组栈 |
| **双栈设计** | 每帧有 `resultStack`（有界，存最终结果）和 `processStack`（无界，存指令间中间值），指令在两者间搬运数据 |
| **IThreadHolder 桥接** | 字节码指令调用 `runner.block/yield/die` 时，经 `IThreadHolder.default method` 委托给 `scheduler`，实现逻辑解耦 |
| **单线程轮转** | `ThreadPoolExecutor(1,1)` 串行执行所有文言线程，天然线程安全，不需要锁竞争 |
| **Semaphore 步数控制** | `stepLock` + `accumulatedSteps` 实现 time-slice 协作式调度，每 tick 放行固定步数 |
| **看门狗保底** | 每次执行启动 `ScheduledFuture`，超时强行 `stop()` 并通知 `platform.handleError()` |
| **IFrameManager 双向契约** | `getCurrentRuntime()` 返回 nullable（main 返回后为 null），`getCurrentRuntimeException()` 抛异常——调用者按需选择 |
