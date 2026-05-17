#!/usr/bin/env python3
"""
WenyanNature HTML batch formatter.
Unifies indentation (2 spaces) and cleans redundant blank lines.
Preserves content inside <pre>, <script>, <style> blocks.
Truncates at first </html> to remove trailing garbage.
"""
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


def strip_trailing_blank_lines(lines):
    while lines and lines[-1].strip() == "":
        lines.pop()
    return lines


def cleanup_blank_lines(lines):
    """Merge consecutive blank lines into one."""
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
    return result


def format_html_file(filepath: Path):
    with open(filepath, "r", encoding="utf-8") as f:
        raw = f.read()

    # Fix broken <html> tag (missing > before newline)
    raw = re.sub(r'(<html[^>]*?)\s*\n\s*', r'\1>\n', raw, count=1)

    # Truncate at first </html>
    end_match = re.search(r'</html>', raw)
    if end_match:
        raw = raw[:end_match.end()]

    lines = raw.splitlines(keepends=False)

    # Strip leading blank lines
    while lines and lines[0].strip() == "":
        lines.pop(0)

    # ---- Pass 1: tokenize ----
    tokens = []
    in_pre = False
    in_raw_block = False  # <style> or <script>
    raw_block_tag = None
    raw_block_lines = []

    for line in lines:
        stripped = line.strip()
        if not stripped:
            tokens.append({"type": "blank"})
            continue

        # <pre> blocks – verbatim
        if not in_pre and re.search(r'<pre\b', stripped, re.IGNORECASE):
            in_pre = True
        if in_pre:
            tokens.append({"type": "verbatim", "value": line.rstrip()})
            if re.search(r'</pre>', stripped, re.IGNORECASE):
                in_pre = False
            continue

        # <style> / <script> blocks – collect whole block
        if not in_raw_block:
            m = re.search(r'<(style|script)\b', stripped, re.IGNORECASE)
            if m:
                in_raw_block = True
                raw_block_tag = m.group(1).lower()
                raw_block_lines = [line.rstrip()]
                if f'</{raw_block_tag}>' in stripped:
                    tokens.append({"type": "raw_block", "lines": list(raw_block_lines)})
                    in_raw_block = False
                continue
        else:
            raw_block_lines.append(line.rstrip())
            if f'</{raw_block_tag}>' in stripped:
                tokens.append({"type": "raw_block", "lines": list(raw_block_lines)})
                in_raw_block = False
            continue

        # Normal line – count tag opens/closes
        opens = re.findall(r'<(\w+)', stripped)
        closes = re.findall(r'</(\w+)', stripped)
        oc = sum(1 for t in opens if t.lower() not in VOID_ELEMENTS)
        cc = sum(1 for t in closes if t.lower() not in VOID_ELEMENTS)
        starts_close = stripped.startswith("</")

        tokens.append({
            "type": "normal",
            "value": stripped,
            "oc": oc,
            "cc": cc,
            "starts_close": starts_close,
        })

    # ---- Pass 2: emit with indentation ----
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

        if t == "raw_block":
            # Keep style/script block content as-is, just prefix opening/closing tag
            blk_lines = tok["lines"]
            prefix = "  " * indent
            output_lines.append(prefix + blk_lines[0])
            # Inner lines keep their original indentation
            for inner in blk_lines[1:-1]:
                output_lines.append(inner)
            if len(blk_lines) > 1:
                output_lines.append(prefix + blk_lines[-1])
            continue

        # Normal line
        stripped = tok["value"]
        oc = tok["oc"]
        cc = tok["cc"]
        starts_close = tok["starts_close"]

        if starts_close:
            indent = max(0, indent - cc)
            output_lines.append("  " * indent + stripped)
        else:
            output_lines.append("  " * indent + stripped)
            indent += oc
        indent = max(0, indent - cc)

    output_lines = cleanup_blank_lines(output_lines)
    output_lines = strip_trailing_blank_lines(output_lines)
    result = "\n".join(output_lines)
    if not result.endswith("\n"):
        result += "\n"

    with open(filepath, "w", encoding="utf-8", newline="\n") as f:
        f.write(result)


def main():
    print("WenyanNature HTML Formatter")
    print("=" * 50)

    html_files = sorted(ROOT.rglob("*.html"))
    print(f"Found {len(html_files)} HTML files\n")

    print("Creating backups (.bak)...")
    for fp in html_files:
        try:
            backup_file(fp)
        except Exception as e:
            print(f"  WARN backup {fp}: {e}")

    print("\nFormatting...")
    ok = 0
    errors = []
    for fp in html_files:
        try:
            format_html_file(fp)
            ok += 1
            print(f"  OK  {fp.relative_to(ROOT)}")
        except Exception as e:
            print(f"  ERR {fp.relative_to(ROOT)}: {e}")
            errors.append((fp, str(e)))

    print(f"\nDone: {ok} formatted, {len(errors)} errors")
    if errors:
        for fp, msg in errors:
            print(f"  - {fp.relative_to(ROOT)}: {msg}")


if __name__ == "__main__":
    main()