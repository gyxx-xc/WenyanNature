---
name: judou-compiler
description: 描述 Judou（句读）文言编译器的架构、行为、接口契约与集成方式。适用于需要理解或修改 judou 核心逻辑时的参考。
---

# Judou（句读）行为描述

## 概述

Judou 是一个**文言编程语言（Wenyan）的编译器 + 栈式虚拟机（Stack-based VM）**，用 Java 实现。它接受文言文源代码，经过
ANTLR 词法/语法分析、多阶段 Visitor 编译为自定义字节码，再在协作式调度 VM 上执行。

**核心入口**: `WenyanCompiler.compile(String sourceCode)` → `IWenyanBytecode`

---

## 架构分层

### 1. 编译前端 (Compiler Frontend)

| 阶段 | 组件 | 行为 |
|-|-|-|
| 预处理 | `WenyanPreprocessor.java` | 简繁转换、格式归一化 |
| 词法分析 | `WenyanLexer.g4` (ANTLR)  | 文言源码 → Token 流 |
| 语法分析 | `WenyanParser.g4` (ANTLR) | Token 流 → AST |
| 语义分析 | `WenyanMainVisitor` 编排 4 个子 Visitor | AST → 字节码指令序列 |
| 校验 | `WenyanVerifier.java` | 验证字节码合法性（标签、类型等） |

5 个 Visitor 的分工：

- `WenyanCandyVisitor` — 语法糖展开（如 `之` 调用语法糖）
- `WenyanControlVisitor` — 控制流（条件、循环、分支）
- `WenyanDataVisitor` — 数据声明（变量、类型定义）
- `WenyanExprVisitor` — 表达式（算术、逻辑、函数调用）
- `WenyanMainVisitor` — 编排上述 Visitor，按 AST 节点类型分发

### 2. 字节码格式

每条指令 = `WenyanCodes` 枚举 + `int arg`（约 30 条指令）。

**指令类别**：

- **数据操作**: `PUSH`, `POP`, `LOAD`, `STORE`, `CAST`
- **栈操作**: `DUP`, `SWAP`, `ROT`
- **控制流**: `JMP`, `BRANCH_TRUE`, `BRANCH_FALSE`
- **函数**: `CALL`, `CALL_ATTR`, `CREATE_FUNCTION`, `RET`, `CALL_BUILTIN_FUNCTION`
- **对象**: `LOAD_ATTR`, `STORE_ATTR`, `CREATE_TYPE`, `CREATE_LIST`
- **循环**: `FOR_ITER`, `FOR_NUM`
- **调试**: `BREAKPOINT`

字节码附加数据结构：常量表、标识符表、标签表、调试上下文表。

`IWenyanBytecode`（编译产物，性能需求，线程安全）。

### 3. 值系统 (Value System)

所有值实现 `IWenyanValue` 接口，属性包括 `getType()`、`asString()`、`asInt()` 等。

**类型层次**：

```
IWenyanValue
├── IWenyanNumber (算术运算接口)
│   ├── WenyanInteger (int 包装)
│   └── WenyanDouble (double 包装)
├── WenyanBoolean (true/false)
├── WenyanString (String 包装)
├── WenyanList (有序列表，支持索引操作)
├── IWenyanFunction
│   └── WenyanFunction (用户定义的文言函数)
├── WenyanBuiltinFunction (Java 注册的内置函数)
├── WenyanBuiltinObject (对象实例)
├── WenyanNull (空值单例)
└── WenyanPackage (包/命名空间)
```

**关键行为**：

- `IWenyanComparable` — 比较操作（`大于`、`小于`、`等于`）
- `IWenyanComputable` — 计算操作（`加`、`减`、`乘`、`除`、`模`）
- `WenyanLeftValue` — 左值包装，支持赋值操作
- `IWenyanWarperValue` — Java 对象包装器（用于 mod 端传递 Minecraft 对象）

---

## 关键接口契约

| 接口 | 位置 | 行为描述 |
|-|-|-|
| `IWenyanPlatform`| `api/exec/structure/` | 平台抽象：提供 String 输出、随机数、类型注册等。Mod 端 `RunnerBlockEntity` / `ThrowRunnerEntity` 实现 |
| `IExecQueue`| `api/exec/structure/` | 请求队列：`push(IHandleableRequest)` 入队，`popAll()` 批量取出消费|
| `ICrossFunctionExecutable` | `api/exec/`| 跨函数调用执行器：支持从外部调用文言函数|
| `IRequestCallHandler` | `api/exec/` | 请求处理器：处理 `IHandleableRequest` 的分发逻辑 |
| `IFrameManager` | `api/runtime/` | 帧栈管理器：`callFrame()` / `returnFrame()` 管理调用栈 |
| `IConfigProvider` | `api/utils/` | 配置提供：最大执行步数、超时时间、线程池大小等 |

---

## 编译流程详解

```
文言源码
  ↓ WenyanPreprocessor.preprocess()  简繁转换、格式清洗
  ↓ WenyanLexer                       词法分析 → Token 流
  ↓ WenyanParser                      语法分析 → AST (ParseTree)
  ↓ WenyanMainVisitor.visit()
    ├── WenyanDataVisitor.visit()     处理变量/类型声明
    ├── WenyanCandyVisitor.visit()    展开语法糖
    ├── WenyanControlVisitor.visit()  处理控制流
    └── WenyanExprVisitor.visit()     处理表达式
  ↓ WenyanVerifier.verify()           校验字节码
  ↓ WenyanCompilerEnvironment         管理符号表、作用域
  ↓ WenyanImmutableBytecode           最终不可变字节码
```

---

## 异常层次

```
WenyanException (基类)
├── WenyanCompileException   // 编译期错误（语法、语义、类型）
└── WenyanUnreachedException // 运行时不可达代码（断言）
```

所有异常携带中文错误信息（通过 `ILanguageProvider` 国际化）。

---

## 与 Minecraft Mod 的集成点

1. **平台实现**: `RunnerBlockEntity` / `ThrowRunnerEntity` → `IWenyanPlatform`
2. **请求队列**: `ExecQueue` → `BlockRequest` / `ImportRequest` / `SimpleRequest`
3. **内置函数**: `RawHandlerPackage` → Minecraft 能力（红石、破坏方块、通信）
4. **配方校验**: Checker 类 → `IWenyanValue` / `WenyanValues`
5. **编辑器高亮**: 客户端 → `WenyanLexer` 词法规则

---

## 行为约束

1. **从不阻塞**: 调度器确保每次执行有限步数，适合嵌入游戏主循环
2. **线程安全**: `ExecQueue`、`WenyanImmutableBytecode` 线程安全；运行时帧栈不跨线程共享
3. **确定性**: 相同源码 + 相同平台 → 相同字节码（调试用）
4. **协作式**: 文言程序不可抢占，需主动让出或等待时间片耗尽
5. **国际化的错误信息**: 所有异常通过 `ILanguageProvider` 提供中文文本
