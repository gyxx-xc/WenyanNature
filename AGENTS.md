# AGENTS.md — Wenyan Nature

> 激进的使用skill, 有很多有用的其他信息

## 项目概览
Minecraft NeoForge 模组，让玩家用文言文语言在游戏中编写魔法程序。
- **Mod ID**: `wenyan_programming`  |  **Group**: `indi.wenyan`
- **Java 25**  |  **Minecraft 26.1**  |  **NeoForge 26.1.2**

## 模块结构
```
:root          src/{main,client,gameTest,test}      — Minecraft Mod
:judou         judou/                                — 文言编译器 + 运行时
:language_processor  language_processor/             — 注解处理器 (JavaPoet)
```

- `judou` 既是项目依赖，也通过 **jarJar** 打包（shaded）进模组。
- `src/main` = 通用+服务端代码；`src/client` = 客户端 GUI/渲染。
- `src/generated/resources` 是**数据生成产出**——切勿手动编辑。

## 构建与运行

**禁止直接执行 `./gradlew`。** 始终使用 MCP 工具（`local-server_build_project` / `local-server_execute_run_configuration`）。

```bash
# 验证编译——用 MCP 构建（等价于 ./gradlew build，但用工具）
local-server_build_project  # 编译整个项目

# 仅编译改动的文件
local-server_build_project(filesToRebuild=["path/to/ChangedFile.java"])
```

- **数据生成**：运行 `clientData` 配置（`local-server_execute_run_configuration(configurationName="clientData")`）。
- **游戏测试**：运行 `gameTestServer` 配置。
- `directWorld` 运行配置需要预先创建一个名为 `GametestWorld` 的单人存档。

## 测试

### 单元测试 (JUnit 6.0)
- 根项目 `src/test/`：测试位于 `indi.wenyan.content.recipe.answering.checker.wyquestion.*`
- `judou/src/test/`：编译器/运行时测试位于 `indi.wenyan.judou.test_statement`
- 使用 `@ParameterizedTest` + `@FieldSource`/`@MethodSource`——项目约定。
- 共享逻辑提取到 `test_utils` 包，由测试类继承。

### 游戏测试
- 位于 `src/gameTest/`。入口：`WenyanTest.java`。
- 使用 NeoForge `testframework`，需要游戏测试服务端。

## 代码风格

- **接口命名**：`I` 前缀（`IWenyanValue`、`IWenyanFunction`）
- **领域类命名**：`Wenyan` 前缀（`WenyanCompiler`、`WenyanFrame`）
- **值对象**：优先使用 `record`，而非 class
- **Switch**：使用 switch 表达式（Java 17+），不用传统 switch 语句
- **Nullability**：参数和返回值使用 `@NotNull`/`@Nullable`（JetBrains）
- **Lombok**：`@Getter`/`@Setter` 仅限 GUI/DTO 代码，核心逻辑中禁用
- **异常**：定义自定义异常层次；方法显式声明 `throws`
- **注释**：核心库（judou）对公开 API 写 Javadoc；业务代码（src/main）注释稀疏
- **命名**：标准英文，标识符中不含拼音/中文
- **包结构**：按功能领域划分（compiler, runtime, utils），而非技术分层

## 关键陷阱

- **严禁使用 `bash` 工具执行 `./gradlew`**。所有构建必须通过 MCP 工具（`local-server_build_project`、`local-server_execute_run_configuration`）。
- `judou` 使用 ANTLR 生成语法解析器——生成源码在 `build/generated-src/antlr/`。IDE 报错时先用 MCP 构建 `judou` 模块（`local-server_build_project(filesToRebuild=["judou/"])`）。
- 客户端源码集（`src/client`）依赖 `main` 编译输出。`main` 编译不过则 `client` 也会失败。
- 数据生成产出必须运行并提交：运行 `clientData` 配置 → 从 `src/generated/resources/` 复制输出。
