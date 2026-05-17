"""
批量修改 HTML 文件：移除重复的 Header/Tabs/Skip，替换为共享组件引用。
"""
import re
from pathlib import Path

ROOT = Path('d:/Code/WenyanNature')

def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    if re.search(r'<script src="[^"]*components/nav\.js"></script>', content):
        return False  # 已处理

    # 1. 移除 <header> ... </header>（含内部所有内容）
    content = re.sub(
        r'<!--\s*Toggle: drawer\s*-->\s*<input[^>]*?id="__drawer"[^>]*?>\s*'
        r'<!--\s*Toggle: search\s*-->\s*<input[^>]*?id="__search"[^>]*?>\s*'
        r'<!--\s*Overlay\s*-->\s*<label[^>]*?for="__drawer"[^>]*?></label>\s*',
        '', content, flags=re.DOTALL
    )

    # 2. 移除 skip div
    content = re.sub(
        r'<div data-md-component="skip">\s*<a[^>]*?class="md-skip"[^>]*?>[^<]*?</a>\s*</div>\s*',
        '', content, flags=re.DOTALL
    )

    # 3. 移除 announce div (保留空壳)
    content = re.sub(
        r'<div data-md-component="announce">\s*</div>\s*',
        '', content, flags=re.DOTALL
    )

    # 4. 移除整个 <header> 块
    content = re.sub(
        r'<header class="md-header"[^>]*?>.*?</header>\s*',
        '', content, flags=re.DOTALL
    )

    # 5. 移除 <nav class="md-tabs" ...> ... </nav>
    content = re.sub(
        r'<nav class="md-tabs"[^>]*?>.*?</nav>\s*',
        '', content, flags=re.DOTALL
    )

    # 6. 在 md-container 之前注入共享组件所需的标记
    # 插入 toggle inputs、overlay 和 nav.js 脚本
    injection = (
        '  <!-- Toggle: drawer -->\n'
        '  <input class="md-toggle" data-md-toggle="drawer" type="checkbox" id="__drawer" autocomplete="off">\n'
        '  <!-- Toggle: search -->\n'
        '  <input class="md-toggle" data-md-toggle="search" type="checkbox" id="__search" autocomplete="off">\n'
        '  <!-- Overlay -->\n'
        '  <label class="md-overlay" for="__drawer"></label>\n'
    )

    # 在 <div class="md-container" 之前插入
    content = content.replace(
        '<div class="md-container"',
        injection + '<div class="md-container"'
    )

    # 7. 在 </body> 之前添加脚本引用
    # 计算相对路径
    rel = filepath.relative_to(ROOT)
    depth = len(rel.parent.parts) if rel.parent != Path('.') else 0
    prefix = '../' * depth if depth > 0 else ''

    script_tag = f'\n  <script src="{prefix}components/nav.js"></script>\n'
    content = content.replace('</body>', script_tag + '</body>')

    # 8. 清理注释占位符（page_surgery.py 残留下来的空行）
    content = re.sub(r'\n\s*\n\s*<!-- (Skip to content|Announcement|Header|Container|Tabs navigation|Main content) -->\s*\n', '\n', content)
    content = re.sub(r'\n\s*<!-- (Skip to content|Announcement|Header|Container|Tabs navigation|Main content) -->\s*\n', '\n', content)
    content = re.sub(r'\n\s*\n\s*<!-- Sidebar: primary \(navigation\) -->\s*\n', '\n\n', content)
    content = re.sub(r'\n\s*\n\s*<!-- Nav item:.*?-->\s*\n', '\n', content)
    content = re.sub(r'\n\s*<!-- (Github|In game|Judou|Vibe|Diagrams|Functional analysis|Technical) (nested )?-->\s*\n', '\n', content)
    content = re.sub(r'\n\s*<!-- TOC \(secondary sidebar\) -->\s*\n', '\n', content)
    content = re.sub(r'\n\s*<!-- Sidebar: secondary \(table of contents\) -->\s*\n', '\n', content)
    content = re.sub(r'\n\s*<!-- Content area -->\s*\n', '\n', content)
    content = re.sub(r'\n\s*<!-- Hash anchor fix script -->\s*\n', '\n', content)
    content = re.sub(r'\n\s*<!-- Footer -->\s*\n', '\n', content)
    content = re.sub(r'\n\s*<!-- Dialog -->\s*\n', '\n', content)
    content = re.sub(r'\n\s*<!-- Config script -->\s*\n', '\n', content)
    content = re.sub(r'\n\s*<!-- Material JS bundle -->\s*\n', '\n', content)

    # 9. 清理多余空行（连续3个以上空行压缩为2个）
    content = re.sub(r'\n\s*\n\s*\n\s*\n', '\n\n\n', content)
    content = re.sub(r'\n\s*\n\s*\n\s*\n', '\n\n\n', content)

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)

    return True

def main():
    files = list(ROOT.rglob('*.html'))
    # 排除非页面文件
    files = [f for f in files
             if not any(p in f.parts for p in ['assets', 'css', 'components'])
             and f.name.endswith('.html')]

    count = 0
    errors = []
    for f in sorted(files):
        try:
            if process_file(f):
                count += 1
                print(f'  OK  {f.relative_to(ROOT)}')
        except Exception as e:
            errors.append((f, str(e)))
            print(f'  ERR {f.relative_to(ROOT)}: {e}')

    print(f'\nDone: {count} updated, {len(errors)} errors')
    if errors:
        for f, e in errors:
            print(f'  {f.relative_to(ROOT)}: {e}')

if __name__ == '__main__':
    main()