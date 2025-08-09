package indi.wenyan.content.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Snippet(String title, List<String> lines, List<SnippetPlaceholder> insert) {
    public record SnippetPlaceholder(Context context, int row, int colum) { }

    public record Context(String value) {
        public static Context of(String name) {
            return new Context(name);
        }

        public static int color(Context context) {
            return switch (context.value()) {
                case "STMT" -> 0xFFFF0000;
                case "DATA" -> 0xFF00FF00;
                case "ID" -> 0xFF0000FF;
                case "NONE" -> 0x99000000;
                default -> throw new IllegalStateException("Unexpected value: " + context.value());
            };
        }

        public static List<Utils.SnippetSet> getSnippets(Context context) {
            return switch (context.value()) {
                case "STMT" -> Utils.STMT_SNIPPETS;
                case "DATA" -> Utils.DATA_SNIPPETS;
                case "ID" -> List.of(Utils.ID_SNIPPET);
                case "NONE" -> Utils.DEFAULT_SNIPPET;
                default -> throw new IllegalStateException("Unexpected value: " + context.value());
            };
        }
    }

    public static Snippet of(String title, String content) {
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
                Context context = Context.of(matcher.group(1));
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
