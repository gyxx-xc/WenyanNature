"""
修复调色板颜色方案恢复顺序问题。

根因：nav.js 中的调色板恢复脚本在 bundle.js 之后执行，
      bundle.js 初始化时读到的是默认白色方案，无法响应后续的属性变化。

方案：
 1. 从 nav.js 中移除调色板恢复逻辑
 2. 在所有页面中，在 bundle.js 之前注入内联调色板恢复脚本
"""
import re
from pathlib import Path

ROOT = Path('d:/Code/WenyanNature')

# ---- 1. 从 nav.js 移除调色板恢复逻辑 ----
nav_js_path = ROOT / 'components' / 'nav.js'
nav_content = nav_js_path.read_text(encoding='utf-8')

# 移除调色板恢复脚本块（从注释 "---- 调色板初始化" 到最后的 "})();" 之前）
old_palette_block = r'''
  // ---- 调色板初始化（必须在 header 注入之后执行） ----
  var palette = __md_get\("__palette"\);
  if \(palette && palette\.color\) \{
    if \("\(prefers-color-scheme\)" === palette\.color\.media\) \{
      var media = matchMedia\("\(prefers-color-scheme: light\)"\);
      var selector = media\.matches
        \? "\[data-md-color-media='\(prefers-color-scheme: light\)'\]"
        : "\[data-md-color-media='\(prefers-color-scheme: dark\)'\]";
      var input = document\.querySelector\(selector\);
      palette\.color\.media  = input\.getAttribute\("data-md-color-media"\);
      palette\.color\.scheme = input\.getAttribute\("data-md-color-scheme"\);
      palette\.color\.primary = input\.getAttribute\("data-md-color-primary"\);
      palette\.color\.accent = input\.getAttribute\("data-md-color-accent"\);
    \}
    var keys = Object\.keys\(palette\.color\);
    for \(var i = 0; i < keys\.length; i\+\+\) \{
      var key = keys\[i\];
      document\.body\.setAttribute\("data-md-color-" \+ key, palette\.color\[key\]\);
    \}
  \}
'''

nav_content = re.sub(old_palette_block, '', nav_content, flags=re.DOTALL)
nav_js_path.write_text(nav_content, encoding='utf-8')
print(f'[1/2] nav.js: 已移除调色板恢复逻辑')

# ---- 2. 定义调色板恢复脚本（内联版本） ----
palette_script = '''  <script>
    var __palette = __md_get("__palette");
    if (__palette && __palette.color) {
      if ("(prefers-color-scheme)" === __palette.color.media) {
        var __media = matchMedia("(prefers-color-scheme: light)");
        var __input = document.querySelector(__media.matches
          ? "[data-md-color-media='(prefers-color-scheme: light)']"
          : "[data-md-color-media='(prefers-color-scheme: dark)']");
        __palette.color.media  = __input.getAttribute("data-md-color-media");
        __palette.color.scheme = __input.getAttribute("data-md-color-scheme");
        __palette.color.primary = __input.getAttribute("data-md-color-primary");
        __palette.color.accent = __input.getAttribute("data-md-color-accent");
      }
      for (var __key in __palette.color) {
        document.body.setAttribute("data-md-color-" + __key, __palette.color[__key]);
      }
    }
  </script>'''

# ---- 3. 批量在 bundle.js 之前注入 ----
pattern_bundle = re.compile(r'(  <script src="[^"]*/bundle\.[^"]+\.min\.js"></script>)')

pages = [f for f in sorted(ROOT.rglob('*.html'))
         if not any(p in f.parts for p in ['assets', 'css', 'components'])
         and f.name.endswith('.html')]

patched = 0
for page in pages:
    content = page.read_text(encoding='utf-8')
    if '__md_get("__palette")' in content:
        # 页面已有内联调色板脚本，跳过
        # 但如果是在 <header> 注释后的旧占位，需要清理
        pass

    if pattern_bundle.search(content):
        # 在 bundle.js 之前插入
        new_content = pattern_bundle.sub(palette_script + '\n\\1', content)
        page.write_text(new_content, encoding='utf-8')
        patched += 1
        print(f'  OK  {page.relative_to(ROOT)}')
    else:
        print(f'  --  {page.relative_to(ROOT)} (no bundle.js)')

print(f'\n[2/2] 共 {patched} 个页面已注入调色板恢复脚本')