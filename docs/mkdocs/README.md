# Wenyan Nature — MKDocs Documentation

This directory contains the [MkDocs](https://www.mkdocs.org/) documentation site for the **Wenyan Nature** Minecraft mod (吾有一術), hosted via GitHub Pages at [gyxx-xc.github.io/WenyanNature/](https://gyxx-xc.github.io/WenyanNature/).

## Structure

```
docs/mkdocs/
├── docs/                # Documentation source files (Markdown)
│   ├── usage/          # User guides (quick start, syntax, variables, etc.)
│   ├── in_game/        # In-game block/item documentation
│   ├── modules/        # Module API reference (math, block, entity, etc.)
│   ├── development/    # Developer documentation
│   │   ├── in-game/    # Block implementation notes
│   │   └── judou/      # Runtime / compiler design notes
│   └── css/            # Custom stylesheets
├── site/               # Built static site (output)
├── mc_recipe.py        # MkDocs hook: renders Minecraft crafting recipes from $$$...$$$ syntax
├── mkdocs.yml          # MkDocs configuration
└── requirements.txt    # Python dependencies
```

## Prerequisites

- Python 3.10+
- pip

## Setup & Preview

```bash
# Install dependencies
pip install -r requirements.txt

# Start local development server
mkdocs serve

# Build static site
mkdocs build
```

The local server is available at `http://127.0.0.1:8000` by default.

## Custom Plugin

`mc_recipe.py` defines a custom MkDocs hook (`on_page_markdown`) that transforms recipe markup into styled crafting-grid HTML. Use `$$$item1 item2,item3 ... result$$$` in any Markdown page to render a Minecraft-style crafting table.

## Dependencies

- [mkdocs](https://pypi.org/project/mkdocs/)
- [mkdocs-material](https://pypi.org/project/mkdocs-material/) — Material Design theme
- [mkdocs-nav-weight](https://pypi.org/project/mkdocs-nav-weight/) — Navigation ordering
- [mkdocs-static-i18n](https://pypi.org/project/mkdocs-static-i18n/) — Internationalization support

## Deployment

The site is automatically deployed to GitHub Pages on push to the `master` branch (see `.github/workflows/` for details).
