package indi.wenyan.content.gui;

import java.util.List;

public enum Utils {;
    public record BoxInformation(int top, int left, int bottom, int right) {
        public int horizontal() {
            return left + right;
        }

        public int vertical() {
            return top + bottom;
        }
    }

    public record SnippetSet(String name, List<Snippet> snippets) {
        public SnippetSet(String name, Snippet... snippets) {
            this(name, List.of(snippets));
        }
    }

    public static final SnippetSet VARIABLE_SNIPPET = new SnippetSet("variable",
            Snippet.of("吾有一數", "吾有一數。曰{DATA}。名之曰{ID}。{NONE}"),
            Snippet.of("昔之", "昔之{DATA}者。今{DATA}是矣。{NONE}")
    );

    public static final SnippetSet FUNCTION_SNIPPET = new SnippetSet("function",
            Snippet.of("function", "施{ID}。於{DATA}。{NONE}")
    );

    public static final SnippetSet CONDITION_SNIPPET = new SnippetSet("condition",
            Snippet.of("if", """
                    若{DATA}者。
                      {STMT}
                    也。{NONE}"""),
            Snippet.of("if-else", """
                    若{DATA}者。
                      {STMT}
                    若非。
                      {STMT}
                    也。{NONE}""")
    );

    public static final SnippetSet LOOP_SNIPPET = new SnippetSet("condition",
            Snippet.of("for", """
                    為是{DATA}遍。
                      {STMT}
                    云云。{NONE}"""),
            Snippet.of("while", """
                    恆為是。
                      {STMT}
                    云云。{NONE}"""),
            Snippet.of("break", "乃止。{NONE}")
    );

    public static final SnippetSet DATA_SNIPPET = new SnippetSet("data",
            Snippet.of("0", "零"),
            Snippet.of("1", "一"),
            Snippet.of("2", "二"),
            Snippet.of("3", "三"),
            Snippet.of("false", "陰"),
            Snippet.of("gt", "{DATA}大於{DATA}")
    );

    public static final SnippetSet ID_SNIPPET = new SnippetSet("data",
            Snippet.of("a", "「甲」"),
            Snippet.of("b", "「乙」")
    );

    public static final List<SnippetSet> STMT_SNIPPETS = List.of(
            VARIABLE_SNIPPET,
            FUNCTION_SNIPPET,
            CONDITION_SNIPPET,
            LOOP_SNIPPET
    );

    public static final List<SnippetSet> DEFAULT_SNIPPET = STMT_SNIPPETS;
}
