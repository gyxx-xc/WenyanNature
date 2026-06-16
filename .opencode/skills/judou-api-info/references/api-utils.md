# utils + language + WenyanPackageBuilder

基础设施层：配置、工具方法、本地化、包构建。

---

## utils 包 — `indi.wenyan.judou.api.utils`

### UtilManager（全局管理器）

**路径**: `judou/src/main/java/indi/wenyan/judou/api/utils/UtilManager.java`

全局单例管理器（enum 无实例模式）。存储日志、配置、语言提供者。

```java
public enum UtilManager {
    static Logger getLogger();
    static void setLogger(Logger logger);

    static IConfigProvider getConfig();
    static void setConfig(IConfigProvider config);

    static ILanguageProvider getLanguage();
    static void setLanguage(ILanguageProvider language);
}
```

**使用方式**：in Main mod class constructor:
```java
UtilManager.setLogger(yourLogger);
UtilManager.setConfig(yourConfigImpl);
UtilManager.setLanguage(yourLanguageImpl);
```

### IConfigProvider（配置接口）

**路径**: `judou/src/main/java/indi/wenyan/judou/api/utils/IConfigProvider.java`

```java
public interface IConfigProvider {
    ... see code for detail ...
}
```

### ChineseUtils（中文工具）

**路径**: `judou/src/main/java/indi/wenyan/judou/api/utils/ChineseUtils.java`

数字转文言汉字、简繁转换。

```java
public enum ChineseUtils {
    static String toChinese(BigInteger i);      // 整数转文言数字
    static String toChinese(double value);      // 浮点数转文言数字（含「又」「分」「釐」等）
    static String toSimplifiedVar(String s);     // 变量名简繁转换
    static String bracketOf(String string);      // 加「」括号

    enum SymbolFormat { TRADITIONAL, SIMPLIFIED, BOTH }
}
```

### Either（左右值容器）

same as mojang's

---

## language 包 — `indi.wenyan.judou.api.language`

### JudouLocalizationEnum（本地化基接口）

**路径**: `judou/src/main/java/indi/wenyan/judou/api/language/JudouLocalizationEnum.java`

```java
public interface JudouLocalizationEnum {
    String getTranslationKey();                     // 获取 i18n key
    default String string();                        // 获取翻译文本（无参数）
    default String string(Object... args);          // 获取翻译文本（带参数）
}
```

所有可本地化的枚举实现此接口，通过 `UtilManager.getLanguage()` 获取翻译。

### ILanguageProvider（语言提供者接口）

**路径**: `judou/src/main/java/indi/wenyan/judou/api/language/ILanguageProvider.java`

```java
public interface ILanguageProvider {
    String getTranslation(String key);                     // 按 key 获取翻译
    default String getTranslation(String key, Object... args);  // 带参数
}
```

默认实现：`indi.wenyan.judou.utils.RawLanguageProvider`

### JudouExceptionText（异常文本枚举）

**路径**: `judou/src/main/java/indi/wenyan/judou/api/language/JudouExceptionText.java`

所有异常消息的 i18n key。`implements JudouLocalizationEnum`。

```java
public enum JudouExceptionText implements JudouLocalizationEnum {
    // ...（共 ~37 个，见源文件）
}
```

**key 格式**: see Minecraft's component

错误使用方式，严格禁止任何硬编码string：
```java
throw new WenyanException(
    JudouExceptionText.CannotCast.string("整数", "字符串"));
```
使用方式：
```java
throw new WenyanException(
    JudouExceptionText.CannotCast.string(WenyanInteger.TYPE.toString(), WenyanString.TYPE.toString()));
```

---

## WenyanPackageBuilder（包构建器）

**路径**: `judou/src/main/java/indi/wenyan/judou/api/utils/WenyanPackageBuilder.java`

向文言环境注册外部函数/变量的 DSL 构建器。

```java
public final class WenyanPackageBuilder {
    static WenyanPackageBuilder create();

    // 引入已有环境
    WenyanPackageBuilder environment(WenyanPackage environment);

    // 注册常量
    WenyanPackageBuilder constant(String name, IWenyanValue value);

    // 注册函数（BuiltinFunction，**必须线程安全**）
    WenyanPackageBuilder function(String name, WenyanValues.BuiltinFunction function);
    WenyanPackageBuilder function(String[] name, WenyanValues.BuiltinFunction function);

    // 注册异步函数（IRequestCallHandler，在主线程执行）
    WenyanPackageBuilder function(String name, IJavacallHandler javacall);

    // 注册对象类型
    WenyanPackageBuilder object(String name, IWenyanObjectType objectType);

    // 便捷方法：double/int 函数（自动类型转换）
    WenyanPackageBuilder doubleFunction(String name, ThrowFunction<List<Double>, Double> function);
    WenyanPackageBuilder intFunction(String name, ThrowFunction<List<Integer>, Integer> function);

    // 构建
    WenyanPackage build();
}
```

### 内置辅助工厂方法

```java
// 二元运算归约：加、乘等
static BuiltinFunction reduceWith(ReduceFunction function);

// 布尔二元运算：且、或等
static BuiltinFunction boolBinaryOperation(ThrowBiFunction<Boolean, Boolean, Boolean> function);

// 比较操作：大於、小於等
static BuiltinFunction compareOperation(CompareFunction function);
```

### 完整示例

```java
// 注册自定义函数
WenyanPackage customPackage = WenyanPackageBuilder.create()
    .constant(WenyanSymbol.A_CONST_INT, WenyanValues.of(1))
    .function(WenyanSymbol.SAY_HELLO, (self, args) -> {
        return WenyanValues.of("你好，世界！");
    })
    .doubleFunction(WenyanSymbol.PI, (args) -> Math.PI)
    .intFunction(WenyanSymbol.GET_MINECRAFT, (args) ->
    // unable to impl, use exec package instead        
    )
    .environment(IWenyanPlatform.initEnvironment())  // 包含内置函数
    .build();
```
