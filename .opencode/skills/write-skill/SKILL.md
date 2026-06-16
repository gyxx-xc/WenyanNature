---
name: write-skill
description: 按 8 步 CoT 构建 opencode Skill。用户说"写 skill"、"创建 skill"、"new skill"、"编写 skill"、"写一个 skill"等时触发。不要在回答普通代码问题时触发。
---

# Write Skill — 元 Skill（写 Skill 的 Skill）

按结构化 CoT 流程生成高质量的 `SKILL.md` 及其配套文件。

## 强制 CoT 推理规则

在开始执行本 Skill 的任何步骤之前，你必须激活**严格逐步推理模式**。以下规则**必须无条件遵守**，任何违反将被视为无效输出：

### 系统角色
你是一个**逐步推理引擎**。你的所有输出必须经过完整的结构化推理，不得跳过任何步骤直接给出结论。

---

## 输入与输出

**输入**: 用户描述 Skill 要实现的功能（一句话到一段话），可能附带示例代码或参考链接。

**输出**:
- `.opencode/skills/<skill-name>/SKILL.md`（核心指令）
- `.opencode/skills/<skill-name>/references/examples.md`（示例）
- `.opencode/skills/<skill-name>/scripts/`（可选脚本）
- 若有需要，修改 `opencode.json` 注册非默认路径（仅在默认路径不被扫描时）

## 执行步骤

### 第 1 步：明确 Skill 目标与触发场景

与用户确认：
1. 一句话核心价值
2. 触发条件：哪些关键词/文件/动作触发？哪些场景**不该**触发？
3. 输入格式（用户给什么？文字、文件路径、API 密钥？）
4. 输出格式（纯文本、JSON、文件修改、表格？）

产出：skill-name、SKILL.md 的 description 字段初稿。

### 第 2 步：决定自由度等级

| 等级 | 示例 | 策略 |
|------|------|------|
| 低 | 数据库迁移 | 直接给可执行脚本，AI 只负责调用 |
| 中 | 生成会议纪要 | 给模板+必填字段，允许措辞调整 |
| 高 | 代码重构建议 | 给原则+示例，让 AI 自主推导 |

产出：`# Freedom Level` 标注 + 选择原因。

### 第 3 步：确定目录结构

```
<skill-name>/
├── SKILL.md
├── scripts/          # 可运行脚本（Python/Bash）
├── references/       # 背景知识、详细示例、长篇指南
└── assets/           # 模板、字体、图片
```

决策要点：
- 哪些放 SKILL.md（流程、条件、步骤）
- 哪些放 references/（背景知识、长文档）
- 是否需要 scripts/（固定操作直接脚本化）

产出：完整目录树。

### 第 5 步：补充参考文献与脚本

- 若 SKILL.md 超过 500 行，拆出内容到 references/
- 对重复性操作（curl、数据处理）直接写 scripts/ 并让 AI 执行
- 用明确指令告诉 AI 何时读取哪些文件

### 第 6 步：编写示例

在 `references/examples.md` 中包含：
1. 1 个最小输入示例 + 期望输出
2. 1 个边缘情况示例（缺失信息、错误格式）+ 处理方式

### 第 7 步：自我验证

逐项检查（无遗漏即成功）：
- [ ] name 与文件夹名一致，仅含小写字母、数字、连字符
- [ ] description 包含"使用场景"和"不使用场景"
- [ ] SKILL.md ≤ 500 行
- [ ] 每个 references/ 文件在一层深度内引用
- [ ] 至少包含一个失败路径
- [ ] 成功标准可验证
- [ ] 若含命令行或 API，已脚本化

### 第 8 步：收尾

1. 在 SKILL.md 末尾追加 `## Changelog`
2. 告知用户需要**重启 opencode** 才能加载新 Skill
3. 运行 `opencode --list-skills`（若可用）确认 Skill 被识别

## 失败处理

1. **用户描述过于模糊**
   → 回退：按最通用的"中自由度"设计，留出扩展空间，在 SKILL.md 开头注明假设

2. **Skill 因 description 不准确未触发**
   → 回退：检查 description 是否包含用户实际使用的关键词，更新后重启

3. **SKILL.md 远超 500 行**
   → 回退：将长篇示例、背景参考拆到 references/，SKILL.md 中只保留流程和引用

## 成功标准

- 生成的 Skill 在重启 opencode 后可通过 `opencode --list-skills` 见到
- 当用户输入匹配 description 中的触发条件时，该 Skill 被加载
- 所有文件格式正确，无 YAML 解析错误

## Gotchas

1. **description 必须同时包含"使用场景"和"不使用场景"**，否则 AI 可能在无关问题中触发。gate 用 "Use ONLY when..."
2. **name 必须全小写字母+数字+连字符**，与文件夹名一致。opencode 对大写字母容忍度低
3. **重启才能生效**：opencode 不热加载 Skill，忘记提示用户会导致"为什么没生效"的疑惑
4. **目录扫描深度**：opencode 扫描 `**/SKILL.md`，但 `skills.paths` 如果配置了，只扫描指定路径。如果用户曾改过 `skills.paths`，新 Skill 可能不在扫描范围内
5. **description 宜短**：过长会被截断，触发关键词前置
6. **子步骤不超过 7 个**：超过则说明粒度太粗，应拆分或组合

## Changelog

- v1.0 (2026-06-16): 初始版本，按 8 步 CoT 构建
