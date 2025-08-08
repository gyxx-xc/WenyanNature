package indi.wenyan.content.gui;

import java.util.ArrayList;
import java.util.List;

public record Snippet(String title, String content, List<Placeholder> insert) {
    public record Placeholder(Context context, int place) { }
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
        private final StringBuilder sb = new StringBuilder();
        private final List<Placeholder> insert = new ArrayList<>();

        private SnippetBuilder(String title) {
            this.title = title;
        }

        public SnippetBuilder string(String content) {
            sb.append(content);
            return this;
        }

        public SnippetBuilder stringLine(String content) {
            sb.append(content).append('\n');
            return this;
        }

        public SnippetBuilder newLine() {
            sb.append('\n');
            return this;
        }

        public SnippetBuilder hold(Context context) {
            insert.add(new Placeholder(context, sb.length()));
            return this;
        }

        public Snippet create() {
            return new Snippet(title, sb.toString(), List.copyOf(insert));
        }
    }
}
