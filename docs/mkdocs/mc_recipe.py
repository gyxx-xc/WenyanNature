import re

def replace_content(match):
    # Example replacement: uppercase the content between $$$
    l = match.group(1).split()
    result = l[0]
    item = [i for sub in l[1:] for i in sub.split(',')]
    pattle = '<div class="container"> <div class="crafting-grid"> <div class="slot">{}</div> <div class="slot">{}</div> <div class="slot">{}</div> <div class="slot">{}</div> <div class="slot">{}</div> <div class="slot">{}</div> <div class="slot">{}</div> <div class="slot">{}</div> <div class="slot">{}</div> </div> <div class="arrow">→</div> <div class="result">{}</div> </div>'
    return pattle.format(*item, result)

def on_page_markdown(markdown, **kwargs):
    return re.sub(r'\$\$\$(.*?)\$\$\$', replace_content, markdown, flags=re.DOTALL)
