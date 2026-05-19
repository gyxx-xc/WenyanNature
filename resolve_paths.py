import os
import re
import posixpath

ROOT = "."
ROOT_ABS = os.path.abspath(ROOT)


def is_within_project(target_path):
    """Check if the resolved path stays within the project root directory."""
    full = os.path.normpath(os.path.abspath(os.path.join(ROOT, target_path)))
    return full.startswith(ROOT_ABS + os.sep) or full == ROOT_ABS


def target_exists(target_path):
    """Check if a path (relative to ROOT) exists as a file, directory, or directory with index.html."""
    if not is_within_project(target_path):
        return False
    if os.path.exists(os.path.join(ROOT, target_path)):
        return True
    if os.path.exists(os.path.join(ROOT, target_path + ".html")):
        return True
    if os.path.isdir(os.path.join(ROOT, target_path)):
        return True
    if os.path.exists(os.path.join(ROOT, target_path, "index.html")):
        return True
    return False


def resolve_and_fix(url, file_dir):
    """
    Resolve a possibly-broken URL to the correct project path by stripping
    excess ../ prefixes, then return the correct relative URL from file_dir.
    """
    # Resolve relative to file_dir
    if url.startswith("/"):
        resolved = url.lstrip("/")
    else:
        resolved = posixpath.normpath(posixpath.join(file_dir, url)).replace("\\", "/")

    # Handle trailing slash
    trailing_slash = url.endswith("/")
    resolved_clean = resolved.rstrip("/")

    # Try the resolved path first
    if target_exists(resolved_clean):
        target = resolved_clean
    else:
        # The path might be broken (too many ../). Strip leading ../ one at a time.
        target = resolved_clean
        while target.startswith(".."):
            if target.startswith("../"):
                target = target[3:]
            elif target == "..":
                target = ""
            else:
                break  # something like "..foo" - don't strip
            if target_exists(target):
                break
        else:
            # Could not find target, return original URL unchanged
            return url

    # Calculate correct relative path
    rel_target = target if target else "."
    new_url = posixpath.relpath(rel_target, file_dir).replace("\\", "/")

    if trailing_slash:
        if new_url == ".":
            new_url = "./"
        elif not new_url.endswith("/"):
            new_url += "/"

    return new_url


def process_html_file(filepath):
    rel_filepath = os.path.relpath(filepath, ROOT).replace("\\", "/")
    file_dir = posixpath.dirname(rel_filepath)

    with open(filepath, "r", encoding="utf-8") as f:
        content = f.read()

    def fix_url(match):
        full = match.group(0)
        attr = match.group(1)
        url = match.group(2)

        # Skip non-relative URLs and empty URLs
        if not url or url.startswith(
            ("http:", "https:", "mailto:", "data:", "//", "javascript:")
        ):
            return full

        # Preserve hash and query suffix
        base_url = url
        suffix = ""
        hash_idx = url.find("#")
        query_idx = url.find("?")
        cut_idx = len(url)
        if hash_idx != -1:
            cut_idx = min(cut_idx, hash_idx)
        if query_idx != -1:
            cut_idx = min(cut_idx, query_idx)
        if cut_idx < len(url):
            base_url = url[:cut_idx]
            suffix = url[cut_idx:]

        # Skip pure hash/query refs
        if not base_url:
            return full

        new_base = resolve_and_fix(base_url, file_dir)
        new_url = new_base + suffix

        return f'{attr}="{new_url}"'

    def fix_md_scope(match):
        url = match.group(1)
        if not url or url.startswith(("http:", "https:", "//", "javascript:")):
            return match.group(0)

        new_url = resolve_and_fix(url, file_dir)
        return f'new URL("{new_url}", location)'

    content = re.sub(r'(href|src)="([^"]*)"', fix_url, content)
    content = re.sub(r'new URL\("([^"]+)",\s*location\)', fix_md_scope, content)

    with open(filepath, "w", encoding="utf-8", newline="\n") as f:
        f.write(content)


count = 0
for root_dir, dirs, files in os.walk(ROOT):
    if ".git" in root_dir:
        continue
    for file in files:
        if file.endswith(".html"):
            process_html_file(os.path.join(root_dir, file))
            count += 1

print(f"Processed {count} HTML files.")