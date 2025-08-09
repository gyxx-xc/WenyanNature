package indi.wenyan.content.gui;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Snippet(String title, List<String> lines, List<SnippetPlaceholder> insert) {
    public record SnippetPlaceholder(Context context, int row, int colum) { }
    @Accessors(fluent = true)
    public enum Context {
        STMT(0xFFFF0000),
        DATA(0xFF00FF00),
        ID(0xFF0000FF),
        NONE(0x99000000);
        @Getter
        private final int color;

        Context(int color) {
            this.color = color;
        }
    }

    public static SnippetBuilder createSnippet(String title) {
        return new SnippetBuilder(title);
    }

    @SuppressWarnings("unused")
    public static class SnippetBuilder {
        private final String title;
        private StringBuilder sb;
        private final List<SnippetPlaceholder> insert = new ArrayList<>();
        private final List<String> lines = new ArrayList<>();

        private SnippetBuilder(String title) {
            this.title = title;
            sb = new StringBuilder();
        }

        // Theoretically, this should be only executed once when construct statically, so...
        public SnippetBuilder content(String content) {
            var strings = content.split("\\n",-1);
            Pattern pattern = Pattern.compile("\\{([^}]*)}");
            for (var s : strings) {
                Matcher matcher = pattern.matcher(s);
                int lastEnd = 0;
                while (matcher.find()) {
                    sb.append(s, lastEnd, matcher.start());
                    Context context = Context.valueOf(matcher.group(1));
                    insert.add(new SnippetPlaceholder(context, lines.size(), sb.length()));
                    lastEnd = matcher.end();
                }
                sb.append(s, lastEnd, s.length());
                lines.add(sb.toString());
                sb = new StringBuilder();
            }
            return this;
        }

        public Snippet create() {
            // sort for just in case placeholders are not in order
            insert.sort((a, b) -> {
                if (a.row != b.row) {
                    return Integer.compare(a.row, b.row);
                }
                return Integer.compare(a.colum, b.colum);
            });
            return new Snippet(title, List.copyOf(lines), List.copyOf(insert));
        }
    }
}
