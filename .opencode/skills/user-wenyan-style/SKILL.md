---
name: imitate-wenyan-style
description: |
  根据WenyanNature项目的编码习惯报告与代表性代码片段，按用户本人风格继续写代码。
  基于judou（文言编译器）和src（Minecraft Mod）的全量蒸馏分析。
---

# 模仿用户代码风格

## 使用前先读取
- references/style-profile.md
- references/representative-snippets.md

## 适用场景
- 当用户要求"按我的风格继续写"或"沿用我这个仓库的写法"
- 需要为WenyanNature项目或类似技术栈（Java 17+、Minecraft Mod、编译器）添加代码
- 希望保持与现有代码库一致的命名、结构和错误处理风格

## 执行顺序
1. 先读取风格报告（style-profile.md）了解高置信度习惯
2. 再读取代表性片段（representative-snippets.md）观察具体实现细节
3. 仅应用高置信度规则，避免过度推断
4. 样本不足时执行保守补全（目标语言社区常规写法）

## 仿写决策顺序
1. **最高优先级**：用户给出的近邻文件和目标模块的现有写法
2. **高优先级**：style-profile中目标语言（Java）的高置信度规则
3. **中优先级**：representative-snippets中的局部命名、控制流和错误处理微习惯
4. **低优先级**：跨语言共性（如适用）
5. **最低优先级**：社区常规写法补全（仅当样本不足时）

## 证据优先级
- 目标模块近邻文件 > style-profile中Java的高置信度特征 > representative-snippets的稳定微习惯 > 跨语言共性 > 社区常规补全
- 如果抽象规则与近邻源码冲突，优先服从近邻源码
- 避免将单一文件或目录的特殊写法推广为全局规则

## 跨语言共性
1. **命名质量**：标准英文命名，无拼音/中文混用；标识符语义清晰
2. **结构清晰**：按功能/职责分层，而非严格技术分层
3. **错误处理**：重视错误边界，使用领域特定异常
4. **注释策略**：核心/公共API注释完整，业务逻辑注释稀疏

## 分语言模仿规则

### Java（高置信度）
1. **接口命名**：接口以`I`前缀（`IWenyanValue`, `IWenyanFunction`）
2. **类名前缀**：领域类使用`Wenyan`前缀（`WenyanInteger`, `WenyanCompiler`）
3. **类型选择**：
   - 简单值对象用`record`而非`class`
   - 无状态工具类考虑`enum`实现（单例模式）
4. **控制流**：
   - 偏好`switch`表达式（Java 17+）而非传统`switch`语句
   - 复杂分支用`switch`，简单条件用`if-else`
5. **错误处理**：
   - 定义自定义异常层次（扩展`WenyanException`）
   - 方法显式声明`throws`
   - 特定异常用`try-catch`精细捕获
6. **注解使用**：
   - 广泛使用JetBrains `@NotNull`/`@Nullable`
   - GUI/DTO层使用Lombok `@Getter`/`@Setter`（有限场景）
7. **测试组织**：
   - JUnit 5参数化测试（`@ParameterizedTest`）
   - 测试类继承助手类（`WenyanProgramTestHelper`）
   - 测试数据用`@MethodSource`或`@CsvSource`
8. **包结构**：按功能/职责分层（`compiler/`, `structure/values/`, `runtime/`, `utils/`）

### Python（样本不足，保守规则）
- 样本不足，不提炼专属习惯
- 需模仿时，按Python社区常规写法补全
- 观察到的有限模式：脚本式风格，函数为主，标准库使用

## 运行时约束
- **禁止重新做来源归因**：不在运行时判断"这是否是AI生成"或"是否来自协作者"
- **禁止依赖完整源码背景**：不要求访问完整项目历史或目录结构
- **默认保守**：无法确认来源时，只按近邻文件和高置信度规则续写
- **边界明确**：Lombok仅用于GUI/DTO场景，不迁移到核心算法代码

## 保守补全策略
1. **Java版本**：若目标环境不支持Java 17+，`record`/`switch表达式`退化为`class`/传统`switch`
2. **命名冲突**：当领域前缀冲突时（如多个项目），使用项目特定前缀
3. **新领域术语**：无现有模式时，使用标准英文命名，保持语义清晰
4. **测试框架**：若无JUnit 5，使用项目现有测试框架
5. **Python脚本**：样本不足，完全使用Python社区常规写法

## 禁止误迁移
1. **Lombok范围**：仅在GUI/DTO层使用，不迁移到核心算法/数据结构
2. **注释密度**：业务代码注释稀疏是项目观察，非绝对规则；新代码可适度增加解释性注释
3. **Java版本**：确认目标环境支持Java 17+特性
4. **领域前缀**：`Wenyan`前缀是该项目特定，新项目应使用合适领域前缀

## 快速参考清单
- ✅ 接口：`I`前缀
- ✅ 类名：领域前缀（如`Wenyan`）
- ✅ 值对象：`record`
- ✅ 工具类：`enum`考虑
- ✅ 分支：`switch`表达式优先
- ✅ 异常：自定义层次，显式`throws`
- ✅ 注解：`@NotNull`/`@Nullable`
- ✅ 测试：参数化，继承助手类
- ✅ 包结构：按功能分层
- ❌ 拼音/中文命名
- ❌ 过度注释（业务逻辑）
- ❌ Lombok滥用（非GUI场景）