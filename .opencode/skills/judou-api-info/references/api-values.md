# values 包 + 顶层异常

---

## 值体系总览

```
IWenyanValue（根接口）
├── IWenyanNumber             # 数字标记接口
├── IWenyanComputable         # 可计算（加减乘除）
├── IWenyanComparable         # 可比较（compareTo）
├── IWenyanFunction           # 可调用函数
│   ├── IWenyanObjectType     # 对象类型（可创建实例）
│   └── ICrossFunctionExecutable（在 exec 包）
├── IWenyanObject             # 对象（有属性）
├── IWenyanWarperValue<T>     # 包装 Java 值
│
├── ★ WenyanNull              # 空值（枚举单例）
├── ★ WenyanLeftValue         # 变量（左值，可赋值）
├── ★ WenyanPackage           # 包（变量集合，record）
│
└── primitive/
    ├── WenyanInteger          # 整数
    ├── WenyanDouble           # 浮点数
    ├── WenyanBoolean          # 布尔
    ├── WenyanString           # 字符串
    └── WenyanList             # 列表
```

---

## 根接口 — `IWenyanValue`

**路径**: `judou/src/main/java/indi/wenyan/judou/api/values/IWenyanValue.java`

所有文言值的基接口。

```java
public interface IWenyanValue {
    WenyanType<?> type();                       // 值的类型
    <T extends IWenyanValue> T as(WenyanType<T> type);       // 转型（失败抛 WenyanTypeException）
    boolean is(WenyanType<?> type);             // 是否能转型到目标类型
    <T extends IWenyanValue> Optional<T> tryAs(WenyanType<T> type);  // 出于性能考虑，不抛出异常时总是使用is

    // 静态算术方法（自动类型提升）
    static IWenyanValue add(IWenyanValue self, IWenyanValue other);
    static IWenyanValue sub(IWenyanValue self, IWenyanValue other);
    static IWenyanValue mul(IWenyanValue self, IWenyanValue other);
    static IWenyanValue div(IWenyanValue self, IWenyanValue other);
    static WenyanInteger mod(IWenyanValue self, IWenyanValue other);
    static boolean equals(IWenyanValue self, IWenyanValue other);
    static int compareTo(IWenyanValue self, IWenyanValue other);
    static IWenyanValue emptyOf(ParsableType type);
}
```

**类型提升规则**：
- 算术：`String > Double > Integer`（当一方是 String 时提升为 String）
- 比较：`Double > Integer`

---

## 值类型接口

### IWenyanNumber

**路径**: `judou/src/main/java/indi/wenyan/judou/api/values/IWenyanNumber.java`

```java
public interface IWenyanNumber extends IWenyanValue {
    WenyanType<IWenyanNumber> TYPE = new WenyanType<>(...);
}
```

### IWenyanComputable

**路径**: `judou/src/main/java/indi/wenyan/judou/api/values/IWenyanComputable.java`

```java
public interface IWenyanComputable extends IWenyanValue {
    WenyanType<IWenyanComputable> TYPE = ...;
    IWenyanValue add(IWenyanValue other);
    IWenyanValue subtract(IWenyanValue other);
    IWenyanValue multiply(IWenyanValue other);
    IWenyanValue divide(IWenyanValue other);
}
```

### IWenyanComparable

**路径**: `judou/src/main/java/indi/wenyan/judou/api/values/IWenyanComparable.java`

```java
public interface IWenyanComparable extends IWenyanValue {
    WenyanType<IWenyanComparable> TYPE = ...;
    int compareTo(IWenyanValue other);
}
```

### IWenyanFunction

**路径**: `judou/src/main/java/indi/wenyan/judou/api/values/IWenyanFunction.java`

```java
public interface IWenyanFunction extends IWenyanValue {
    WenyanType<IWenyanFunction> TYPE = ...;
    void callWithReturn(@Nullable IWenyanValue self, IWenyanRunner thread,
                        List<IWenyanValue> argsList,
                        Consumer<IWenyanValue> onReturn);
}
```

### IWenyanObject

**路径**: `judou/src/main/java/indi/wenyan/judou/api/values/IWenyanObject.java`

```java
public interface IWenyanObject extends IWenyanValue {
    WenyanType<IWenyanObject> TYPE = ...;
    IWenyanValue getAttribute(String name);
}
```

### IWenyanObjectType

**路径**: `judou/src/main/java/indi/wenyan/judou/api/values/IWenyanObjectType.java`

```java
public interface IWenyanObjectType extends IWenyanFunction {
    WenyanType<IWenyanObjectType> TYPE = ...;
    IWenyanValue getAttribute(String name);
}
```

### IWenyanWarperValue

**路径**: `judou/src/main/java/indi/wenyan/judou/api/values/IWenyanWarperValue.java`

```java
public interface IWenyanWarperValue<T> extends IWenyanValue {
    T value();  // 获取包装的 Java 原始值
}
```

---

## 具体值实现

### WenyanNull

**路径**: `judou/src/main/java/indi/wenyan/judou/api/values/WenyanNull.java`

```java
public enum WenyanNull implements IWenyanValue {
    NULL;   // 唯一实例
    static WenyanType<WenyanNull> TYPE = ...;
}
```

### WenyanLeftValue（变量/左值）

**路径**: `judou/src/main/java/indi/wenyan/judou/api/values/WenyanLeftValue.java`

表示可赋值的变量。所有非左值赋值时会自动包装。

```java
public class WenyanLeftValue implements IWenyanValue {
    IWenyanValue getValue();
    void setValue(IWenyanValue value);
    static IWenyanValue varOf(IWenyanValue value);  // 包装为左值
}
```

### WenyanPackage

**路径**: `judou/src/main/java/indi/wenyan/judou/api/values/WenyanPackage.java`

变量/函数的集合（record）。同时实现了 `IWenyanObjectType`（可作为类型）和 `IGlobalResolver`（全局变量查找）。

```java
public record WenyanPackage(Map<String, IWenyanValue> variables)
    implements IWenyanObjectType, IGlobalResolver {
    static WenyanType<WenyanPackage> TYPE = ...;
    void combine(WenyanPackage other);  // 合并另一个包
    void put(String name, IWenyanValue value);  // 放入变量
    IWenyanValue getGlobal(String name);
    IWenyanValue getAttribute(String name);
}
```

### 基本类型（primitive 子包）

| 类 | 说明 | TYPE 常量 |
|----|------|-----------|
| `WenyanInteger` | 整数（基于 BigInteger） | `WenyanInteger.TYPE` |
| `WenyanDouble` | 浮点数（double 包装） | `WenyanDouble.TYPE` |
| `WenyanBoolean` | 布尔（TRUE / FALSE 枚举） | `WenyanBoolean.TYPE` |
| `WenyanString` | 字符串 | `WenyanString.TYPE` |
| `WenyanList` | 列表（基于 List<IWenyanValue>） | `WenyanList.TYPE` |

**WenyanInteger** 的常用方法：
```java
BigInteger value();              // 获取 BigInteger 值
WenyanInteger mod(WenyanInteger other);
static WenyanInteger valueOf(long l);
static WenyanInteger valueOf(BigInteger bi);
```

**WenyanBoolean** 是枚举：
```java
public enum WenyanBoolean implements IWenyanValue {
    TRUE, FALSE;
    boolean value();
}
```

**WenyanList**：
```java
List<IWenyanValue> value();      // 内部列表
@Override String toString();     // 格式化为文言列表字符串
```

---

## 异常体系

### WenyanException

**路径**: `judou/src/main/java/indi/wenyan/judou/api/values/exception/WenyanException.java`

所有文言执行期异常的基类。`extends Exception`（已检查异常）。

```java
public class WenyanException extends Exception {
    // 构造
    public WenyanException(String message);
    public WenyanException(String message, ParserRuleContext ctx);
    public WenyanException(WenyanException e, ParserRuleContext ctx);

    // 错误处理
    public void handle(Consumer<String> output, Logger logger,
                       @Nullable ErrorContext context);

    // 子类型
    public static class WenyanNumberException extends WenyanException { ... }
    public static class WenyanDataException extends WenyanException { ... }
    public static class WenyanVarException extends WenyanException { ... }
    public static class WenyanTypeException extends WenyanException { ... }
    public static class WenyanCheckerError extends WenyanException { ... }

    public record ErrorContext(int line, int column, String segment) {}
}
```

| 子类型 | 用途 |
|--------|------|
| `WenyanNumberException` | 数值错误（除零、溢出等） |
| `WenyanDataException` | 数据处理错误 |
| `WenyanVarException` | 变量错误（未定义、数量不对等） |
| `WenyanTypeException` | 类型转换错误（由 `as()` 抛出） |
| `WenyanCheckerError` | 代码验证错误 |

### WenyanCompileException

**路径**: `judou/src/main/java/indi/wenyan/judou/api/values/exception/WenyanCompileException.java`

编译期异常。`extends RuntimeException`（非检查异常），由 `WenyanCompiler.compile()` 抛出。

```java
public class WenyanCompileException extends RuntimeException {
    public WenyanCompileException(String message);
    public WenyanCompileException(String message, Throwable cause, ParserRuleContext ctx);
    public WenyanCompileException(String message, ParserRuleContext ctx);
    public WenyanCompileException(WenyanException e, ParserRuleContext ctx);
}
```

### WenyanUnreachedException

**路径**: `judou/src/main/java/indi/wenyan/judou/api/values/exception/WenyanUnreachedException.java`

理论上"不可达"的异常（通常是内部 bug）。`extends WenyanException`。

```java
public class WenyanUnreachedException extends WenyanException {
    public WenyanUnreachedException();
    public WenyanUnreachedException(String message);

    public static class WenyanUnexceptedException extends WenyanUnreachedException {
        public final Throwable cause;
        public WenyanUnexceptedException(Throwable e);
    }
}
```

---

## WenyanType — 类型描述

**路径**: `judou/src/main/java/indi/wenyan/judou/api/WenyanType.java`

```java
public class WenyanType<T extends IWenyanValue> {
    public final Class<T> tClass;  // 对应的 Java 类

    public WenyanType(String name, Class<T> tClass);

    // 类型提升
    static WenyanType<? extends IWenyanComputable> computeWiderType(WenyanType<?> a, WenyanType<?> b);
    static WenyanType<? extends IWenyanComparable> compareWiderType(WenyanType<?> a, WenyanType<?> b);
}
```

**类型常量定义位置**：
| 常量 | 定义在 |
|------|--------|
| `IWenyanValue` 各子接口的 TYPE | 各接口自身 |
| `WenyanInteger.TYPE` | `WenyanInteger` |
| `WenyanDouble.TYPE` | `WenyanDouble` |
| `WenyanBoolean.TYPE` | `WenyanBoolean` |
| `WenyanString.TYPE` | `WenyanString` |
| `WenyanList.TYPE` | `WenyanList` |
| `WenyanNull.TYPE` | `WenyanNull` |
| `WenyanPackage.TYPE` | `WenyanPackage` |


### WenyanValues（值工厂）

**路径**: `judou/src/main/java/indi/wenyan/judou/api/utils/WenyanValues.java`

创建文言值的静态工厂。**必用工具**。

```java
public enum WenyanValues {
    static WenyanNull of();
    static WenyanInteger of(long l);
    static WenyanInteger of(BigInteger bi);
    static WenyanDouble of(double d);
    static WenyanBoolean of(boolean b);
    static WenyanString of(String s);
    static WenyanList of(List<IWenyanValue> l);
    static WenyanList of(IWenyanValue... l);
    static IWenyanFunction of(BuiltinFunction function);

    static boolean checkArgsType(List<IWenyanValue> args, WenyanType<?>... types);
}
```

**BuiltinFunction** 函数式接口：
```java
@FunctionalInterface
public interface BuiltinFunction {
    IWenyanValue apply(IWenyanValue self, List<IWenyanValue> args);
}
```

**注意**：`BuiltinFunction` **必须线程安全**（在文言线程执行），如果需要主线程执行请使用 `IRequestCallHandler`。
