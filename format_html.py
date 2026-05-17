#!/usr/bin/env python3
"""
WenyanNature HTML batch formatter – v5.
Unifies indentation (2 spaces), cleans blank lines, formats CSS/JS/JSON.
v5: Truncate at first </html>, fix CSS regex (DOTALL), split JS statements.
"""
import json
import re
import shutil
from pathlib import Path

ROOT = Path(r"d:\Code\WenyanNature")

VOID_ELEMENTS = {
    "area", "base", "br", "col", "embed", "hr", "img", "input",
    "link", "meta", "param", "source", "track", "wbr",
}


def backup_file(filepath: Path):
    bak = filepath.with_suffix(filepath.suffix + ".bak")
    shutil.copy2(filepath, bak)


def strip_leading_blank_lines(lines):
    while lines and lines[0].strip() == "":
        lines.pop(0)
    return lines


def cleanup_blank_lines(lines):
    result = []
    prev_blank = False
    for line in lines:
        if line.strip() == "":
            if not prev_blank:
                result.append("")
            prev_blank = True
        else:
            result.append(line)
            prev_blank = False
    while result and result[-1].strip() == "":
        result.pop()
    return result


# ---------------------------------------------------------------------------
# CSS helpers
# ---------------------------------------------------------------------------
def format_css_block(inner_css: str, prefix: str) -> list[str]:
    """Format compressed CSS rules inside a <style> block."""
    if not inner_css.strip():
        return []

    rules = []
    depth = 0
    current = ""
    for ch in inner_css:
        current += ch
        if ch == "{":
            depth += 1
        elif ch == "}":
            depth -= 1
            if depth == 0:
                rules.append(current.strip())
                current = ""

    output = []
    for rule in rules:
        # v5: add re.DOTALL so .* matches newlines inside rule body
        m = re.match(r"^([^{]+)\{(.*)\}$", rule, re.DOTALL)
        if m:
            selector = m.group(1).strip()
            props_str = m.group(2).strip()
            props = [p.strip().rstrip(";") for p in props_str.split(";") if p.strip()]
            output.append(prefix + "  " + selector + " {")
            for p in props:
                output.append(prefix + "    " + p + ";")
            output.append(prefix + "  }")
    return output


# ---------------------------------------------------------------------------
# JSON helpers
# ---------------------------------------------------------------------------
def format_json_block(inner_json: str, prefix: str) -> list[str]:
    try:
        data = json.loads(inner_json)
        formatted = json.dumps(data, indent=2, ensure_ascii=False)
        lines = formatted.split("\n")
        return [prefix + "  " + l for l in lines]
    except (json.JSONDecodeError, ValueError):
        return [prefix + "  " + inner_json]


# ---------------------------------------------------------------------------
# JS helpers – v5: split compressed statements, then brace-indent
# ---------------------------------------------------------------------------
def preprocess_js(inner_js: str) -> str:
    """
    Split a compressed single-line JS into multiple lines at statement boundaries.
    Specifically handles mkdocs-material's boilerplate patterns like:
      __md_scope=..., __md_hash=..., __md_get=..., __md_set=...
    """
    # Strategy: walk through characters tracking brace/string depth.
    # At depth 0, when we see , or ; followed by a word character,
    # replace the separator with newline (keeping the separator).
    result = []
    depth = 0
    string_char = None
    escape_next = False

    # First check if this is already multi-line (has significant newlines)
    if "\n" in inner_js.strip():
        return inner_js  # Already has line breaks

    for ch in inner_js:
        if escape_next:
            escape_next = False
            result.append(ch)
            continue
        if ch == "\\":
            escape_next = True
            result.append(ch)
            continue
        if string_char:
            result.append(ch)
            if ch == string_char:
                string_char = None
            continue
        if ch in ('"', "'", "`"):
            string_char = ch
            result.append(ch)
            continue
        if ch == "{":
            depth += 1
        elif ch == "}":
            depth -= 1
        elif depth == 0 and ch == ",":
            # Split at top-level comma (between statements)
            result.append(",\n")
            continue
        elif depth == 0 and ch == ";":
            result.append(";\n")
            continue
        result.append(ch)

    return "".join(result)


def format_js_block(inner_js: str, prefix: str) -> list[str]:
    """
    Format JavaScript inside a <script> block.
    Pre-splits compressed statements, then applies brace-based indentation.
    """
    # Pre-process to split compressed statements
    inner_js = preprocess_js(inner_js)

    lines = inner_js.split("\n")

    # Strip leading/trailing blank lines
    while lines and lines[0].strip() == "":
        lines.pop(0)
    while lines and lines[-1].strip() == "":
        lines.pop(0)

    if not lines:
        return []

    output = []
    depth = 0

    for raw_line in lines:
        striped = raw_line.strip()
        if not striped:
            output.append("")
            continue

        # Count net brace open/close for this line
        line_depth_change = 0
        string_char = None
        escape_next = False

        for ch in striped:
            if escape_next:
                escape_next = False
                continue
            if ch == "\\":
                escape_next = True
                continue
            if string_char:
                if ch == string_char:
                    string_char = None
                continue
            if ch in ('"', "'", "`"):
                string_char = ch
                continue
            if ch == "{":
                line_depth_change += 1
            elif ch == "}":
                line_depth_change -= 1

        # Lines that start with } decrease indent before output
        if striped.startswith("}") and line_depth_change < 0:
            depth = max(0, depth + line_depth_change)

        # Build the output line
        indent_str = prefix + "  " + ("  " * depth)
        output.append(indent_str + striped)

        # Adjust depth for next line
        if not striped.startswith("}"):
            depth = max(0, depth + line_depth_change)
        elif line_depth_change >= 0:
            depth = max(0, depth + line_depth_change)

    return output


# ---------------------------------------------------------------------------
# Main HTML formatter
# ---------------------------------------------------------------------------
def format_html_file(filepath: Path):
    with open(filepath, "r", encoding="utf-8") as f:
        raw = f.read()

    # Fix broken <html> tag (missing > before newline)
    raw = re.sub(r'(<html[^>]*?)\s*\n\s*', r'\1>\n', raw, count=1)

    # v5: Truncate at FIRST </html> to remove any garbled duplicates
    # (Original file had multiple garbled copies appended after </html>)
    end_match = re.search(r'</html>', raw)
    if end_match:
        raw = raw[:end_match.end()]

    lines = raw.splitlines(keepends=False)
    lines = strip_leading_blank_lines(lines)

    # ---- Pass 1: build token stream ----
    tokens = []
    in_pre = False
    in_style = False
    in_script = False
    verbatim_lines = []

    for line in lines:
        stripped = line.strip()
        if not stripped:
            tokens.append({"type": "blank"})
            continue

        # <pre> blocks – keep as-is
        if not in_pre and re.search(r'<pre\b', stripped, re.IGNORECASE):
            in_pre = True
        if in_pre:
            tokens.append({"type": "verbatim", "value": line.rstrip()})
            if re.search(r'</pre>', stripped, re.IGNORECASE):
                in_pre = False
            continue

        # <style> blocks – collect for later formatting
        if not in_style and re.search(r'<style\b', stripped, re.IGNORECASE):
            in_style = True
            verbatim_lines = [line.rstrip()]
            if '</style>' in stripped:
                tokens.append({"type": "style_block", "lines": list(verbatim_lines)})
                in_style = False
            continue
        if in_style:
            verbatim_lines.append(line.rstrip())
            if '</style>' in stripped:
                tokens.append({"type": "style_block", "lines": list(verbatim_lines)})
                in_style = False
            continue

        # <script> blocks – collect for later formatting
        if not in_script and re.search(r'<script\b', stripped, re.IGNORECASE):
            in_script = True
            verbatim_lines = [line.rstrip()]
            if '</script>' in stripped:
                tokens.append({"type": "script_block", "lines": list(verbatim_lines)})
                in_script = False
            continue
        if in_script:
            verbatim_lines.append(line.rstrip())
            if '</script>' in stripped:
                tokens.append({"type": "script_block", "lines": list(verbatim_lines)})
                in_script = False
            continue

        # Normal line – parse tags
        opens = re.findall(r'<(\w+)', stripped)
        closes = re.findall(r'</(\w+)', stripped)

        token = {"type": "normal", "value": stripped}
        token["open_count"] = sum(1 for t in opens if t.lower() not in VOID_ELEMENTS)
        token["close_count"] = sum(1 for t in closes if t.lower() not in VOID_ELEMENTS)
        token["starts_close"] = stripped.startswith("</")
        tokens.append(token)

    # ---- Pass 2: emit with proper indentation ----
    output_lines = []
    indent = 0

    for tok in tokens:
        t = tok["type"]

        if t == "blank":
            output_lines.append("")
            continue

        if t == "verbatim":
            output_lines.append(tok["value"])
            continue

        if t == "style_block":
            blk_lines = tok["lines"]
            prefix = "  " * indent
            content = "\n".join(blk_lines)
            m = re.search(r'(<style[^>]*>)(.*?)(</style>)', content, re.DOTALL | re.IGNORECASE)
            if m:
                before = m.group(1)
                inner = m.group(2).strip()
                after = m.group(3)
                output_lines.append(prefix + before)
                if inner:
                    css_lines = format_css_block(inner, prefix)
                    output_lines.extend(css_lines)
                output_lines.append(prefix + after)
            else:
                output_lines.append(prefix + blk_lines[0])
            continue

        if t == "script_block":
            blk_lines = tok["lines"]
            prefix = "  " * indent
            content = "\n".join(blk_lines)
            m = re.search(r'(<script[^>]*>)(.*?)(</script>)', content, re.DOTALL | re.IGNORECASE)
            if m:
                before = m.group(1)
                inner = m.group(2).strip()
                after = m.group(3)

                # __config JSON
                if 'id="__config"' in before or "id='__config'" in before:
                    output_lines.append(prefix + before)
                    if inner:
                        json_lines = format_json_block(inner, prefix)
                        output_lines.extend(json_lines)
                    output_lines.append(prefix + after)
                else:
                    output_lines.append(prefix + before)
                    if inner:
                        js_lines = format_js_block(inner, prefix)
                        output_lines.extend(js_lines)
                    output_lines.append(prefix + after)
            else:
                output_lines.append(prefix + blk_lines[0])
            continue

        # Normal line
        stripped = tok["value"]
        oc = tok["open_count"]
        cc = tok["close_count"]
        starts_close = tok["starts_close"]

        if starts_close:
            indent = max(0, indent - cc)
            output_lines.append("  " * indent + stripped)
        else:
            output_lines.append("  " * indent + stripped)
            indent += oc
        indent = max(0, indent - cc)

    output_lines = cleanup_blank_lines(output_lines)
    result = "\n".join(output_lines)
    if not result.endswith("\n"):
        result += "\n"

    with open(filepath, "w", encoding="utf-8", newline="\n") as f:
        f.write(result)


def main():
    print("=" * 60)
    print("WenyanNature HTML Batch Formatter v5")
    print("=" * 60)

    html_files = sorted(ROOT.rglob("*.html"))
    print(f"\nFound {len(html_files)} HTML files\n")

    print("Creating backups (.bak)...")
    for fp in html_files:
        try:
            backup_file(fp)
        except Exception as e:
            print(f"  [WARN] Could not backup {fp}: {e}")

    print("\nFormatting HTML files...")
    errors = []
    success = 0
    for fp in html_files:
        try:
            format_html_file(fp)
            success += 1
            print(f"  OK  {fp.relative_to(ROOT)}")
        except Exception as e:
            print(f"  ERR {fp.relative_to(ROOT)}: {e}")
            errors.append((fp, str(e)))

    print(f"\n{'=' * 60}")
    print(f"Done: {success} formatted, {len(errors)} errors")
    if errors:
        print("\nErrors:")
        for fp, msg in errors:
            print(f"  - {fp.relative_to(ROOT)}: {msg}")
    print("=" * 60)


if __name__ == "__main__":
    main()