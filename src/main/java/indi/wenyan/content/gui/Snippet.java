package indi.wenyan.content.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Snippet(String title, List<String> lines, List<Placeholder> insert) {
    public record Placeholder(Context context, int row, int colum) { }
    public enum Context {
        STMT,
        DATA,
        ID
    }

    public static SnippetBuilder createSnippet(String title) {
        return new SnippetBuilder(title);
    }

    @SuppressWarnings("unused")
    public static class SnippetBuilder {
        private final String title;
        private StringBuilder sb;
        private final List<Placeholder> insert = new ArrayList<>();
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
                    insert.add(new Placeholder(context, lines.size(), sb.length()));
                    lastEnd = matcher.end();
                }
                sb.append(s, lastEnd, s.length());
                lines.add(sb.toString());
                sb = new StringBuilder();
            }
            return this;
        }

        public Snippet create() {
            return new Snippet(title, List.copyOf(lines), List.copyOf(insert));
        }
    }
}
