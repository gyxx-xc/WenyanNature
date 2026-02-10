package indi.wenyan.content.gui.code_editor.backend;

import lombok.Setter;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.NonFinal;

import java.util.List;

@Accessors(fluent = true)
@Value
public class SnippetSet {
    String name;
    List<Snippet> snippets;
    @NonFinal @Setter boolean fold = false;

    public SnippetSet(String name, Snippet... snippets) {
        this.name = name;
        this.snippets = List.of(snippets);
    }

    public record Snippet(String title, List<String> lines, List<SnippetPlaceholder> insert) { }

    public record SnippetPlaceholder(generated_Snippets.Context context, int row, int colum) { }
}
