# Hand Runner VB (符咒) - 系统架构设计 (Architecture & Data Models)

## 1. 核心架构概述 (System Architecture)
Hand Runner 的核心是将原本绑定在方块 (Block) 上的代码执行能力，抽象并转移到消耗型物品 (Item) 和短暂存在的实体 (Entity) 上。为了**不冲突且最大程度复用现有代码**，我们将设计一层适配器或抽象公共逻辑处理类。

系统主要分为四个逻辑层模块：
1. **物品与交互层 (Item & Interaction Layer):** 处理玩家的输入（右击）、冷却时间、物品栈扣减以及实体的生成。
2. **生命周期与实体层 (Entity Lifecycle Layer):** 管理 `HandRunnerEntityVB` 的存在时间（Tick）、视觉渲染（着火效果）以及最终的回收。
3. **能力与存储层 (Capabilities & Storage Layer):** 使得物品 (`ItemStack`) 能够像方块的 TileEntity 一样存储代码字典/NBT 数据。
4. **共享执行层 (Execution Shared Logic Layer):** 将执行引擎 (Wenyan/Judou Runner) 的调用逻辑抽离，使得方块和实体均能通过这层接口安全地执行代码。

## 2. 数据模型与接口 (Data Models & APIs)

### 2.1 物品载体: `HandRunnerItemVB`
直接负责和玩家的交互。
- **继承:** `Item` 或现有的基础魔法物品类。
- **核心功能:**
  - `use(Level, Player, InteractionHand)`: 包含物品的消耗逻辑、触发原版 Cooldown 并在玩家位置 `spawn` 出 `HandRunnerEntityVB`。
  - **Capability 绑定:** 重写生成 ItemStack Capability 的方法，附着一个包含执行代码包的新 Capability 实例。

### 2.2 实体类: `HandRunnerEntityVB`
负责代码的执行承载和寿命管理。
- **继承:** `Entity`。
- **核心数据属性:**
  - `int lifeTime`: 当前剩余生命周期 (Tick)，最大值根据设定（如200 tick = 10秒）。
  - 连接至所持有 `Capability` 中的执行包，方便随时调用。
- **核心方法:**
  - `tick()`: 每个 tick `lifeTime--`，检查若 `lifeTime <= 0` 则调用 `discard()`。
  - 处理获取周围方块以及提取自身 NBT 数据的上下文准备工作。

### 2.3 接口约定: `IInvDevVB`
实现对方块（具有 TileEntity）与实体背包/Capability中代码一致存取的约定。
- **继承/组合:** 考虑到要求不修改现有代码，可以新建一个 `IInvDevVB` 去适配或者继承原有的 `IWenyanDevice`（如有）。
- **职责:** 提供 `getDeviceName()`, `getRawExecPackage()` 等标准的数据提取方法。

### 2.4 公共执行适配器: `RunnerExecutionUtilVB`
提取复用代码的关键类。
- **类型:** `public static final class` (工具类)
- **职责:** 
  - 接收来自方块或 `HandRunnerEntityVB` 的执行请求。
  - 处理安全保护逻辑：如果代码在执行后要求消耗，或者试图进行二次无效调用，这里将做拦截（返回 Fail state）。
  - 执行完成后的状态更新（如：从对应的实体 Capability NBT 中抹除已执行完的“符”节点）。

## 3. NBT / Capability 存储方案
为了保证离开合成台后物品依然记得所写入的代码：
- 使用原版的 `Capability<T>` 系统为 `ItemStack` 附加数据。
- **数据结构 (JSON/NBT 描述):**
```json
{
  "VB_DeviceName": "example_fu",
  "VB_Modules": {
    "module_1": "raw_wenyan_code_string...",
    "module_2": "..."
  },
  "VB_IsConsumed": false
}
```

## 4. 安全性与向后兼容 (Constraints Check)
1. **命名规范:** 所有新增类/接口/文件一律带有 `VB` 后缀。
2. **代码侵入性控制:** 
   - 绝不修改现存的 Block Runner 核心执行引擎，仅读取其暴露的 API。
   - 共用逻辑提取为 `RunnerExecutionUtilVB`，如果原 Block Runner 逻辑强耦合，我们会写一层 Wrapper 来调用原方法而不是去修改原函数。
3. **调用越界:** `HandRunnerEntityVB` 被严格限制其生命周期（Ticks），不可能驻留内存引发严重内存泄漏；此外调用背包模块函数具有消耗与状态自检，防止通过一符多用引发死循环。

## 5. Mermaid 架构图生成计划
我们将在 `docs/vibe/diagrams/` 目录下生成：
1. `system-handrunner-architecture-YYYYMMDD-HHMMSS.md` （系统分层交互图）
2. `datamodel-handrunner-capability-YYYYMMDD-HHMMSS.md`（数据 Capability 存储结构图）
3. `sequence-handrunner-execution-YYYYMMDD-HHMMSS.md` （右键触发执行的时间序列图）
