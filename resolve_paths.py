import os
import re
import posixpath

MOVED_FOLDERS = ["usage", "development", "in_game"]

def get_old_path(new_path):
    parts = new_path.split("/")
    if parts[0] == "content" and len(parts) > 1 and parts[1] in MOVED_FOLDERS:
        return "/".join(parts[1:])
    return new_path

def get_new_path(old_path):
    parts = old_path.split("/")
    if parts[0] in MOVED_FOLDERS:
        return "content/" + old_path
    return old_path

def resolve_target_old_path(current_old_path, relative_url):
    hash_idx = relative_url.find("#")
    query_idx = relative_url.find("?")
    
    end_idx = len(relative_url)
    if hash_idx != -1:
        end_idx = min(end_idx, hash_idx)
    if query_idx != -1:
        end_idx = min(end_idx, query_idx)
        
    base_url = relative_url[:end_idx]
    suffix = relative_url[end_idx:]
    
    if not base_url:
        return current_old_path, suffix
        
    current_dir = posixpath.dirname(current_old_path)
    if base_url.startswith("/"):
        # Not a relative URL in the strict sense, but if it happens, handle it
        target_old_path = posixpath.normpath(base_url[1:])
    else:
        target_old_path = posixpath.normpath(posixpath.join(current_dir, base_url))
    
    if base_url.endswith("/") and not target_old_path.endswith("/"):
        if target_old_path == ".":
            target_old_path = ""
        else:
            target_old_path += "/"
    elif base_url == "." or base_url == "..":
        if target_old_path == ".":
            target_old_path = ""
        else:
            target_old_path += "/"
        
    return target_old_path, suffix

def get_relative_path(from_path, to_path):
    from_dir = posixpath.dirname(from_path)
    if not to_path:
        to_path = "."
    if not from_dir:
        rel = to_path
    else:
        rel = posixpath.relpath(to_path, from_dir)
        
    rel = rel.replace("\\", "/")
    
    if to_path.endswith("/") and not rel.endswith("/"):
        if rel == ".":
            rel = "./"
        else:
            rel += "/"
            
    if to_path == "" and rel == ".":
        rel = "./"
        
    return rel

def process_file(filepath):
    rel_filepath = os.path.relpath(filepath, ".").replace("\\", "/")
    old_path = get_old_path(rel_filepath)
    
    with open(filepath, "r", encoding="utf-8") as f:
        content = f.read()
        
    def replacer(match):
        full_match = match.group(0)
        attr = match.group(1)
        url = match.group(2)
        
        if url.startswith(("http:", "https:", "mailto:", "data:", "//", "javascript:")):
            return full_match
            
        target_old, suffix = resolve_target_old_path(old_path, url)
        target_new = get_new_path(target_old)
        new_url = get_relative_path(rel_filepath, target_new) + suffix
        
        if new_url.startswith("./") and not url.startswith("./"):
            new_url = new_url[2:]
            if new_url == "":
                new_url = "."
                
        return f'{attr}="{new_url}"'

    def replacer_md_scope(match):
        url = match.group(1)
        if url.startswith(("http:", "https:", "mailto:", "data:", "//", "javascript:")):
            return match.group(0)
            
        target_old, suffix = resolve_target_old_path(old_path, url)
        target_new = get_new_path(target_old)
        new_url = get_relative_path(rel_filepath, target_new) + suffix
        
        if new_url.startswith("./") and not url.startswith("./"):
            new_url = new_url[2:]
            if new_url == "":
                new_url = "."
                
        return f'new URL("{new_url}", location)'

    content = re.sub(r'(href|src)="([^"]+)"', replacer, content)
    content = re.sub(r'new URL\("([^"]+)",\s*location\)', replacer_md_scope, content)
    
    with open(filepath, "w", encoding="utf-8") as f:
        f.write(content)

count = 0
for root, dirs, files in os.walk("."):
    if ".git" in root:
        continue
    for file in files:
        if file.endswith(".html"):
            process_file(os.path.join(root, file))
            count += 1

print(f"Processed {count} HTML files.")
