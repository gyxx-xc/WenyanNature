package indi.wenyan.content.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Snippet(String title, List<String> lines, List<SnippetPlaceholder> insert) {
    public record SnippetPlaceholder(Context context, int row, int colum) { }

    public record Context(String value) {

        public static int color(Context context) {
            return switch (context.value()) {
                case "STMT" -> 0xFFFF0000;
                case "DATA" -> 0xFF00FF00;
                case "ID" -> 0xFF0000FF;
                case "NONE" -> 0x99000000;
                default -> throw new IllegalStateException("Unexpected value: " + context.value());
            };
        }

        public static Context of(String name) {
            return new Context(name);
        }

        public static List<Utils.SnippetSet> getSnippets(Context context) {
            return switch (context.value()) {
                case "STMT" -> Utils.STMT_SNIPPETS;
                case "DATA" -> List.of(Utils.DATA_SNIPPET, Utils.ID_SNIPPET);
                case "ID" -> List.of(Utils.ID_SNIPPET);
                case "NONE" -> Utils.DEFAULT_SNIPPET;
                default -> throw new IllegalStateException("Unexpected value: " + context.value());
            };
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
                    Context context = Context.of(matcher.group(1));
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
