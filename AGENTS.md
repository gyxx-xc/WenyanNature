# Agent Guidelines for Wenyan Nature

This document provides guidelines for agentic coding agents working on the Wenyan Nature Minecraft mod project.

## Project Overview

Wenyan Nature is a Minecraft mod inspired by the Wenyan language (文言文编程语言). It allows players to create magical effects by writing Wenyan language code that interacts with Minecraft world mechanics. The project uses Java 25, NeoForge (Minecraft modding framework), and follows modern Java development practices.

## Code Style Guidelines

### 1. Language and Naming Conventions
- **All code must be written in English**
- **Class names**: `UpperCamelCase` (e.g., `PistonModuleEntity`, `WenyanRunner`)
- **Method/variable names**: `lowerCamelCase` (e.g., `getBasePackageName`, `currentRuntime`)
- **Constants**: `UPPER_SNAKE_CASE` (e.g., `MAX_LOGIN_ATTEMPTS`)
- **Package structure**: Follows `indi.wenyan.*` hierarchy with clear separation:
  - `content/` - Minecraft block/entity implementations
  - `judou/` - Wenyan language runtime and interpreter
  - `client/` - Client-side rendering and UI
  - `setup/` - Registration and initialization

### 2. Type System and Annotations
- Use `@NonNull` (jspecify) or `@NotNull` (JetBrains) for non-null parameters
- Use `@Getter`, `@Setter` (Lombok) for boilerplate getters/setters
- Use `@Contract` annotations for method contracts
- Follow interface-implementation pattern (e.g., `IWenyanRunner` interface with `WenyanRunner` implementation)

### 3. Error Handling
- Use checked exceptions for recoverable errors (`WenyanException`)
- Use unchecked exceptions for programming errors (`WenyanUnreachedException`)
- Always include meaningful error messages
- Log errors using `WenyanProgramming.LOGGER` (never `System.out.println`)
- Example pattern:
  ```java
  try {
      // operation
      if (checkFailed())
          throw new WenyanException("User-friedly exception message");
  } catch (SomeRuntimeException e) {
      LOGGER.error("Failed to perform operation", e);
      throw new WenyanUnreachedException();
  }
  ```

### 4. Documentation and Comments
- **Method-level documentation**: Use Javadoc format for public methods
- **TODO comments**: Mark incomplete features with `// TODO: description`
- **Inline comments**: Only when code logic is not self-explanatory
- **Avoid over-commenting**: Let code express intent where possible

## Architectural Principles

### 1. SOLID Principles
- **Single Responsibility**: Each class has one clear purpose
- **Open/Closed**: Extend via interfaces, not modification
- **Liskov Substitution**: Subtypes must be substitutable
- **Interface Segregation**: Many client-specific interfaces
- **Dependency Inversion**: Depend on abstractions, not concretions

### 2. Design Patterns in Use
- **Strategy Pattern**: Used in module handlers and executors
- **Template Method**: `AbstractModuleEntity` base class
- **Builder Pattern**: `HandlerPackageBuilder` for fluent API creation
- **Factory Pattern**: `ScreenOpenerFactory` for UI creation
- **Observer Pattern**: Event handlers in `ClientSetup`

### 3. Package Structure Conventions
```
Project Structure:
├── src/main/java/indi/wenyan/          # Main mod implementation
│   ├── content/                        # Minecraft content implementations
│   │   ├── block/                      # Block entities and logic
│   │   ├── entity/                     # Entity implementations
│   │   ├── gui_api/                    # GUI interfaces and APIs
│   │   ├── item/                       # Item implementations
│   │   └── recipe/                     # Crafting recipes
│   ├── interpreter_impl/               # Wenyan interpreter implementations
│   └── setup/                          # Registration and setup
│       ├── config/                     # Configuration management
│       ├── datagen/                    # Data generation utilities
│       ├── definitions/                # Block/item/entity definitions
│       ├── event/                      # Event handlers
│       └── network/                    # Network packet handling
├── src/client/java/indi/wenyan/client/ # Client-side code
│   ├── block/                          # Block renderers and client logic
│   └── gui/                            # GUI screens and rendering
├── judou/src/main/java/indi/wenyan/judou/  # Wenyan language runtime module
│   ├── antlr/                          # ANTLR grammar and parsing
│   ├── compiler/                       # Wenyan bytecode compilation
│   ├── exec_interface/                 # Execution interface and handlers
│   │   ├── handler/                    # Request handlers
│   │   └── structure/                  # Interface data structures
│   ├── runtime/                        # Execution engine
│   │   ├── executor/                   # Code executors
│   │   └── function_impl/              # Function implementations
│   ├── structure/                      # Data structures and types
│   │   └── values/                     # Wenyan value implementations
│   └── utils/                          # Utility classes
└── language_processor/src/main/java/indi/wenyan/annotation_processor/  # Annotation processor
    ├── annotation/                     # Wenyan annotation definitions
    └── utils/                          # Processor utilities
```

## Build and Development

Do not even try to build/run the project.

## Agent-Specific Instructions

### 1. When Making Changes
1. **Analyze existing patterns** in similar files before implementing
2. **Follow the interface-first approach** for new functionality

### 2. Code Review Checklist
- [ ] Follows naming conventions
- [ ] Uses appropriate annotations
- [ ] Handles errors properly
- [ ] Only includes necessary documentation
- [ ] Maintains backward compatibility
- [ ] No new warnings introduced

### 3. Common Pitfalls to Avoid
- ❌ Using Chinese or other non-English text in code
- ❌ Direct `System.out.println` for logging
- ❌ Breaking existing module interfaces
- ❌ Ignoring null safety annotations
- ❌ Creating circular dependencies
- ❌ Over-engineering simple solutions

---

*Last updated: March 19, 2026*  
*For questions, refer to existing code patterns or consult project maintainers.*