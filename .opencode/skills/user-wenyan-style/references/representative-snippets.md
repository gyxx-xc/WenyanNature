# 代表性代码片段集

## snippet-java-record-01
- 语言：Java
- 路径：judou/src/main/java/indi/wenyan/judou/structure/values/primitive/WenyanString.java
- 用途：使用record定义不可变值对象，体现record偏好与简洁性
- 建议上下文：5行

```java
package indi.wenyan.judou.api.values.primitive;

public record WenyanString(String value)
        implements IWenyanComparable, IWenyanComputable {

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
```

## snippet-java-interface-01
- 语言：Java
- 路径：judou/src/main/java/indi/wenyan/judou/structure/values/IWenyanValue.java（代表性接口）
- 用途：接口使用I前缀，定义核心领域接口，体现接口命名习惯
- 建议上下文：6行

```java
package indi.wenyan.judou.api.values;

import indi.wenyan.judou.api.WenyanType;

public interface IWenyanValue {
    WenyanType<?> type();

    default boolean is(WenyanType<?> type) {
        return type().equals(type);
    }
}
```

## snippet-java-switch-01
- 语言：Java
- 路径：src/client/java/indi/wenyan/client/gui/code_editor/backend/behaviour/CodeField.java
- 用途：使用switch表达式处理键盘事件，体现Java 17+特性偏好
- 建议上下文：8行

```java
private boolean handleIgnoreModifiers(int keyCode) {
    switch (keyCode) {
        case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
            insertText("\n");
            return true;
        }
        default -> {
            return false;
        }
    }
}
```

## snippet-java-exception-01
- 语言：Java
- 路径：judou/src/main/java/indi/wenyan/judou/structure/WenyanException.java
- 用途：自定义异常层次结构，体现错误处理模式
- 建议上下文：10行

```java
package indi.wenyan.judou.structure;

public class WenyanException extends Exception {
    public WenyanException(String message) {
        super(message);
    }

    public static class WenyanNumberException extends WenyanException {
        public WenyanNumberException(String message) {
            super(message);
        }
    }
}
```

## snippet-java-lombok-01
- 语言：Java
- 路径：src/client/java/indi/wenyan/client/gui/code_editor/WritingEditorScreen.java
- 用途：GUI类使用Lombok @Getter注解，体现有限场景的Lombok使用
- 建议上下文：6行

```java
package indi.wenyan.client.gui.code_editor;

import lombok.Getter;

public class WritingEditorScreen extends Screen {
    @Getter
    private CodeEditorWidget textFieldWidget;
    
    private final WritingBlockBackend backend;
}
```

## snippet-java-test-01
- 语言：Java
- 路径：judou/src/test/java/indi/wenyan/judou/test_statement/VariableStatementTest.java
- 用途：JUnit 5参数化测试，体现测试组织习惯
- 建议上下文：8行

```java
class VariableStatementTest extends WenyanProgramTestHelper {
    @ParameterizedTest
    @MethodSource("testData")
    void testNormal(String code, Object... output) throws WenyanException {
        assertResult(code, output);
    }
    
    private static Stream<Arguments> testData() {
        return Stream.of(
            resultArgs("夫一名之曰「a」書之\n", 1)
        );
    }
}
```

## snippet-java-enum-01
- 语言：Java
- 路径：judou/src/main/java/indi/wenyan/judou/compiler/WenyanPreprocessor.java
- 用途：使用enum实现工具类（单例模式），体现enum偏好
- 建议上下文：10行

```java
package indi.wenyan.judou.compiler;

public enum WenyanPreprocessor {
    ;
    private final static Pattern PATTERN = Pattern.compile("「「.*?」」|「.*?」");

    static @NotNull String preprocess(String sourceCode) {
        if (!UtilManager.getConfig().convertCode())
            return sourceCode;
        // ... preprocessing logic
        return result.toString();
    }
}
```

## snippet-java-annotation-01
- 语言：Java
- 路径：judou/src/main/java/indi/wenyan/judou/structure/values/primitive/WenyanInteger.java
- 用途：使用JetBrains注解（@NotNull, @Nullable），体现静态分析辅助习惯
- 建议上下文：6行

```java
package indi.wenyan.judou.api.values.primitive;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class WenyanInteger implements IWenyanComputable {
    public static WenyanInteger valueOf(@NotNull BigInteger i) {
        try {
            return valueOf(i.intValueExact());
        } catch (ArithmeticException e) {
            return new WenyanInteger(i);
        }
    }
}
```

## snippet-java-method-01
- 语言：Java
- 路径：judou/src/main/java/indi/wenyan/judou/structure/values/primitive/WenyanInteger.java
- 用途：方法签名显式声明throws，体现错误处理约定
- 建议上下文：5行

```java
public int value() throws WenyanException {
    try {
        return value.intValueExact();
    } catch (ArithmeticException e) {
        throw new WenyanException(JudouExceptionText.IntegerOverflow.string());
    }
}
```

## snippet-python-generator-01
- 语言：Python
- 路径：judou/src/test/java/indi/wenyan/judou/test_utils/generator.py
- 用途：Python生成脚本，体现脚本式风格与代码生成模式
- 建议上下文：8行

```python
import json

def java_string(s):
    if type(s) == type(1):
        return str(s)
    elif type(s) == type(1.0):
        return str(s)
    elif type(s) == type(""):
        return '"' + s.replace('"', '\\"').replace('\n', '\\n') + '"'
```

## snippet-java-localvar-01
- 语言：Java
- 路径：judou/src/main/java/indi/wenyan/judou/compiler/WenyanPreprocessor.java
- 用途：局部变量命名语义清晰（matcher, result, lastEnd），体现命名习惯
- 建议上下文：8行

```java
static @NotNull String preprocess(String sourceCode) {
    Matcher matcher = PATTERN.matcher(sourceCode);
    StringBuilder result = new StringBuilder();
    int lastEnd = 0;

    while (matcher.find()) {
        String beforeBracket = sourceCode.substring(lastEnd, matcher.start());
        result.append(ZhConverterUtil.toTraditional(beforeBracket));
        result.append(matcher.group());
        lastEnd = matcher.end();
    }
}
```

## snippet-java-packaging-01
- 语言：Java
- 路径：项目整体结构
- 用途：包结构按功能分层（非严格技术分层），体现架构思维
- 建议上下文：N/A（概念性）

```
judou/
├── compiler/          # 编译器前端
├── structure/values/  # 值类型定义
├── runtime/           # 运行时
├── utils/             # 工具函数
└── exec_interface/    # 执行接口

src/
├── client/            # 客户端GUI
├── setup/             # 初始化与网络
├── content/           # 游戏内容
└── interpreter_impl/  # Minecraft集成
```