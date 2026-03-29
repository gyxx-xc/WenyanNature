# Project Structure

## Directory Structure

```
WenyanNature/
├── src/                                # Source code root
│   ├── main/                           # Common/server-side code
│   │   └── java/indi/wenyan/           # Main mod source code
│   │       ├── content/
│   │       │   ├── block/                      # Block implementations
│   │       │   │   ├── additional_module/      # Additional functional blocks
│   │       │   │   ├── crafting_block/         # Crafting interface blocks
│   │       │   │   ├── pedestal/               # Pedestal block
│   │       │   │   ├── power/                  # Power system blocks
│   │       │   │   ├── runner/                 # Code execution runner blocks
│   │       │   │   └── writing_block/          # Code writing blocks
│   │       │   ├── entity/                     # Entity definitions
│   │       │   ├── item/                       # Custom items
│   │       │   ├── recipe/                     # Crafting recipes
│   │       │   └── gui_impl/                   # Server-side GUI implementations
│   │       ├── interpreter_impl/       # Wenyan language runtime integration
│   │       │   └── value/              # Minecraft-specific value types
│   │       └── setup/
│   │           ├── datagen/            # Data generation providers
│   │           │   ├── Language/       # Language file generation
│   │           │   ├── loot/           # Loot table generation
│   │           │   ├── model/          # Model generation
│   │           │   ├── recipe/         # Recipe generation
│   │           │   └── tags/           # Tag generation
│   │           ├── definitions/        # Registry definitions
│   │           ├── event/              # Event handlers
│   │           ├── language/           # Localization
│   │           └── network/            # Network packet handling
│   └── client/
│       └── java/indi/wenyan/client/
│           ├── gui/
│           │   ├── behaviour/          # GUI behavior handlers
│           │   ├── code_editor/        # Code editor GUI components
│           │   │   ├── _generator_py/          # Python code generation utilities
│           │   │   ├── backend/                # Backend logic for code editing
│           │   │   └── widget/                 # GUI widgets
│           │   └── float_note/         # Floating note UI
│           └── renderer/
│               ├── block/              # Block renderers
│               │   └── utils/          # Renderer utilities
│               └── entity/             # Entity renderers
├── judou/src/main/java/indi/wenyan/judou/
│   ├── antlr/                          # ANTLR grammar and error handling
│   ├── compiler/                       # Bytecode generation and compilation
│   ├── runtime/                        # Core runtime and thread management
│   ├── structure/                      # Data structures and value representations
│   ├── exec_interface/                 # Execution interfaces (non-thread-safe)
│   └── utils/                          # Language processing utilities
│       ├── config/                     # Configuration utilities
│       ├── function/                   # Function utilities
│       └── language/                   # Language translation utilities
├── language_processor/                 # Annotation processor
└── docs/                               # Project documentation
    ├── mkdocs/docs/                    # MkDocs documentation site
    │   ├── development/                # Development guides
    │   ├── modules/                    # Module documentation
    │   ├── patchouli/                  # In-game book documentation
    │   ├── usage/                      # Usage guides
    │   └── css/                        # Custom CSS styles
    ├── README.zh_CHS.md                # Simplified Chinese README
    ├── README.zh_CHT.md                # Traditional Chinese README
    └── requirements.txt                # Python dependencies
```
