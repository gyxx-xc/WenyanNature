# exec 包 — 跨线程异步请求体系

## 设计目标

judou 的文言代码在独立线程上执行（性能和安全性），但 Minecraft 游戏操作线程不安全，必须在主线程执行。
exec 包提供了"请求"机制：文言代码发起请求 → 请求入队 → 主线程 tick 时处理 → 返回结果。

---

## 包文档 — `package-info.java`

```java
/// Since Judou exec the code on a separated thread for performance and safety concern,
/// it provided a way to async the code with game thread, making developing function just like in tick().
///
/// To archive this, the core structure is IHandleableRequest request,
/// which will:
/// - define the function of how to exec(handle)
/// - store the calling context until exec
/// - contain data across tick call
///
/// the request will be adopted with IRequestCallHandler handler as IWenyanFunction function,
/// the detailed process is:
/// 1. program got handler as function and call it
/// 2. handler create a IHandleableRequest request with context
/// 3. handler add request to the IExecQueue belong to IWenyanPlatform and block
/// 4. platform will handle the request when ticking
```

---

## 核心接口

### IRequestCallHandler

**路径**: `judou/src/main/java/indi/wenyan/judou/api/exec/IRequestCallHandler.java`

异步请求的入口。当文言代码调用某个函数时，此接口负责创建请求并排队。

```java
public interface IRequestCallHandler extends IJavacallHandler, ICrossFunctionExecutable {
    IHandleableRequest newRequest(IWenyanRunner thread, IWenyanValue self,
                                  List<IWenyanValue> argsList,
                                  Consumer<IWenyanValue> onReturn);

    @Override
    default void callWithReturn(IWenyanValue self, IWenyanRunner thread,
                                List<IWenyanValue> argsList,
                                Consumer<IWenyanValue> onReturn) {
        thread.platform().receive(newRequest(thread, self, argsList, onReturn));
        thread.block();
    }
}
```

- `newRequest()` — 创建请求对象（包含处理逻辑）
- `callWithReturn()` — 默认实现：请求入队 + 阻塞线程等待结果
- 实现此接口的函数**不是线程安全**的，因为会切换到主线程执行

### ICrossFunctionExecutable

**路径**: `judou/src/main/java/indi/wenyan/judou/api/exec/ICrossFunctionExecutable.java`

标记接口：需要调用者在外部（scheduler/game tick）执行的函数。

```java
public interface ICrossFunctionExecutable extends IWenyanFunction { }
```

---

## request 子包 — `indi.wenyan.judou.api.exec.request`

### IHandleableRequest

**路径**: `judou/src/main/java/indi/wenyan/judou/api/exec/request/IHandleableRequest.java`

请求最顶层接口。

```java
public interface IHandleableRequest {
    IWenyanRunner thread();              // 发起请求的线程
    boolean run(IWenyanPlatform platform, IHandleContext context);  // 执行请求
}
```

- `run()` 返回 `true` 表示请求完成，`false` 表示需要继续排队

### IBaseHandleableRequest

**路径**: `judou/src/main/java/indi/wenyan/judou/api/exec/request/IBaseHandleableRequest.java`

简化实现：只需要实现 `handle()`，异常处理自动完成。

```java
public interface IBaseHandleableRequest extends IHandleableRequest {
    boolean handle(IHandleContext context);  // 你的处理逻辑

    @Override
    default boolean run(IWenyanPlatform platform, IHandleContext context) {
        try {
            return handle(context);
        } catch (WenyanException e) {
            IWenyanRunner.dieWithException(thread(), e);
            return true;
        } catch (RuntimeException e) {
            IWenyanRunner.dieWithException(thread(),
                new WenyanUnreachedException.WenyanUnexceptedException(e));
            return true;
        }
    }
}
```

### IArgsRequest

**路径**: `judou/src/main/java/indi/wenyan/judou/api/exec/request/IArgsRequest.java`

带参数和 self 的请求。

```java
public interface IArgsRequest extends IHandleableRequest {
    IWenyanValue self();
    List<IWenyanValue> args();
}
```

---

## structure 子包 — `indi.wenyan.judou.api.exec.structure`

### IWenyanPlatform

**路径**: `judou/src/main/java/indi/wenyan/judou/api/exec/structure/IWenyanPlatform.java`

平台接口。Minecraft 或其他宿主实现此接口来承载文言运行时。

```java
public interface IWenyanPlatform extends IExecReceiver {
    static WenyanPackage initEnvironment() {
        var environment = new WenyanPackage(new HashMap<>());
        environment.combine(WenyanPackages.WENYAN_BASIC_PACKAGES);
        return environment;
    }

    String getPlatformName();
    void handleError(String error);  // 错误处理
}
```

- `initEnvironment()` — 创建包含所有内置函数/类型的全局环境

### IExecQueue

**路径**: `judou/src/main/java/indi/wenyan/judou/api/exec/structure/IExecQueue.java`

执行队列。接收请求，在 tick 时逐个处理。

```java
public interface IExecQueue {
    void receive(IHandleableRequest request);  // 入队
    void handle(IHandleContext context);        // 处理队列中的所有请求

    static IExecQueue create(IWenyanPlatform platform) { ... }
}
```

### IExecReceiver

**路径**: `judou/src/main/java/indi/wenyan/judou/api/exec/structure/IExecReceiver.java`

可接收请求的对象（平台、设备等）。

```java
public interface IExecReceiver {
    IExecQueue getExecQueue();
    default void receive(IHandleableRequest request) { getExecQueue().receive(request); }
    default void handle(IHandleContext context) { getExecQueue().handle(context); }
}
```

### IHandleContext

**路径**: `judou/src/main/java/indi/wenyan/judou/api/exec/structure/IHandleContext.java`

标记接口。携带对 javacall 可能有帮助的上下文信息。

```java
public interface IHandleContext {
    IHandleContext NONE = new IHandleContext() {};  // 空上下文
}
```

### RawHandlerPackage

**路径**: `judou/src/main/java/indi/wenyan/judou/api/exec/structure/RawHandlerPackage.java`

批量注册函数和处理器的便捷容器。

```java
public record RawHandlerPackage(
    Map<String, IWenyanValue> variables,
    Map<String, Supplier<IRawRequest>> functions
) {
    @FunctionalInterface
    public interface IRawRequest {
        boolean handle(IHandleContext context, IArgsRequest request,
                       Consumer<IWenyanValue> onReturn);
    }
}
```

---

## 实现自定义异步函数示例

```java
// 1. 创建 IRequestCallHandler（通常是匿名类或 lambda 辅助）
public class MyClickHandler implements IRequestCallHandler {
    @Override
    public IHandleableRequest newRequest(IWenyanRunner thread, IWenyanValue self,
                                          List<IWenyanValue> args,
                                          Consumer<IWenyanValue> onReturn) {
        return context -> {
            // 在主线程执行
            Player player = ...;
            player.click();
            onReturn.accept(WenyanValues.of(true));
            return true;  // 请求完成
        };
    }
}

// 2. 用 WenyanPackageBuilder 注册
WenyanPackageBuilder.create()
    .function("點", new MyClickHandler())  // 「點」在文言中调用
    .build();
```
