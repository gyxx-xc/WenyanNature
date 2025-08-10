package indi.wenyan.content.gui;

import lombok.Setter;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.NonFinal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public record SnippetPlaceholder(Snippets.Context context, int row, int colum) { }

    public static Snippet snippet(String title, String content) {
        List<SnippetPlaceholder> insert = new ArrayList<>();
        List<String> lines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        var strings = content.split("\\n", -1);
        Pattern pattern = Pattern.compile("\\{([^}]*)}");
        for (var s : strings) {
            Matcher matcher = pattern.matcher(s);
            int lastEnd = 0;
            while (matcher.find()) {
                sb.append(s, lastEnd, matcher.start());
                Snippets.Context context = new Snippets.Context(matcher.group(1));
                insert.add(new SnippetPlaceholder(context, lines.size(), sb.length()));
                lastEnd = matcher.end();
            }
            sb.append(s, lastEnd, s.length());
            lines.add(sb.toString());
            sb = new StringBuilder();
        }

        return new Snippet(title, List.copyOf(lines), List.copyOf(insert));
    }
}
