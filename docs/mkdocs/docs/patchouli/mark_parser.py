import json
import marko
from marko.ast_renderer import ASTRenderer


def convert_markdown_to_patchouli(markdown_text):
    """Convert markdown to Patchouli JSON format"""
    # Parse markdown to AST
    md_ast = marko.Markdown(renderer=ASTRenderer)
    doc = md_ast(markdown_text)

    # Create base structure for Patchouli format
    patchouli_doc = {
        "pages": []
    }

    current_page = []

    # Process each element in the document
    for child in doc["children"]:
        element_type = child["element"]

        # Skip blank lines
        if element_type == "blank_line":
            continue

        # Process headings
        elif element_type == "heading":
            heading_text = extract_text(child)
            level = child.get("level", 1)

            # H1 headings start a new page
            if level == 1 and current_page:
                patchouli_doc["pages"].append(current_page)
                current_page = []

            current_page.append({
                "type": "header",
                "text": heading_text,
                "level": level
            })

        # Process paragraphs
        elif element_type == "paragraph":
            paragraph_text = extract_text(child)
            current_page.append({
                "type": "text",
                "text": paragraph_text
            })

        # Process thematic breaks
        elif element_type == "thematic_break":
            current_page.append({
                "type": "separator"
            })

        # Process code blocks
        elif element_type == "fenced_code":
            code_text = child.get("children", "")
            language = child.get("lang", "")
            current_page.append({
                "type": "code",
                "text": code_text,
                "language": language
            })

        # Process lists
        elif element_type == "list":
            list_items = []
            for item in child.get("children", []):
                if item["element"] == "list_item":
                    list_items.append(extract_text(item))

            current_page.append({
                "type": "list",
                "items": list_items
            })

    # Add the final page
    if current_page:
        patchouli_doc["pages"].append(current_page)

    return patchouli_doc

def extract_text(node):
    """Extract text from a node, handling nested elements"""
    if "children" not in node:
        return ""

    if isinstance(node["children"], str):
        return node["children"]

    result = ""
    for child in node["children"]:
        if isinstance(child, str):
            result += child
        elif isinstance(child, dict):
            if child["element"] == "raw_text":
                result += child.get("children", "")
            else:
                result += extract_text(child)

    return result

# Example usage
if __name__ == "__main__":
    with open("input.md", "r") as f:
        markdown_text = f.read()

    patchouli_json = convert_markdown_to_patchouli(markdown_text)

    with open("output.json", "w") as f:
        json.dump(patchouli_json, f, indent=2)