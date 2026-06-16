---
name: judou-api-info
description: >
  judou（文言编译器）API 参考。提供 judou 模块所有公开接口的签名、用途、使用示例。
  当用户问 "judou API"、"文言编译器接口"、"Wenyan API"、"judou 怎么用"、"如何在 Java 中调用文言"等时触发。
  不用于普通 Minecraft Mod 业务逻辑开发。
---

# Judou API Info Skill

judou 是 WenyanNature 项目中的文言（Classical Chinese）编程语言编译器与运行时。
本 Skill 提供 `judou/src/main/java/indi/wenyan/judou/api/` 下所有公开 API 的完整参考。

## 包结构总览

```
indi.wenyan.judou.api
├── compile/       # 编译器接口
├── runtime/       # 运行时接口（线程调度、帧管理）
├── exec/          # 异步执行请求体系（跨线程通信）
│   ├── request/   # 请求接口
│   └── structure/ # 执行队列/平台/上下文
├── values/        # 值系统（所有文言类型的 Java 表示）
│   ├── exception/ # 异常体系（WenyanException 等）
│   └── primitive/ # 基本类型
├── language/      # 本地化/异常文本/符号常量
├── utils/         # 工具类、配置、值工厂、包构建器
└── WenyanType.java               # 类型描述
```

## 文件索引

| 文件 | 覆盖内容 |
|------|----------|
| `references/api-compile-runtime.md` | compile + runtime 包：编译文言代码、创建 Runner、调度执行 |
| `references/api-exec.md` | exec 包：IRequestCallHandler、请求队列、平台接口 |
| `references/api-values.md` | values 包 + 顶层异常：值体系、基本类型、异常层次 |
| `references/api-utils.md` | utils + language + WenyanType + WenyanPackageBuilder |

## 使用方式

1. 用户提出关于 judou API 的问题
2. 根据问题涉及的包，读取对应的 references 文件
3. 按 references 中的接口签名和示例回答用户

## 核心流程速查

严格禁止任何硬编码string

```
文言源码 → WenyanCompiler.compile() → IWenyanBytecode → RunnerCreator.newRunner()
→ IWenyanRunner → IWenyanScheduler.step() 逐次执行
```

```
注册外部函数 → WenyanPackageBuilder → WenyanPackage → IGlobalResolver → 传入 Runner
```

```
异步请求 → IRequestCallHandler → IHandleableRequest → IExecQueue → platform.handle()
```
