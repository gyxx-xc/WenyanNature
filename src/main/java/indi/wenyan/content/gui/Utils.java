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
            Snippet.createSnippet("吾有一數")
                    .content("""
                            吾有一數。曰{DATA}。名之曰{ID}。{NONE}""")
                    .create(),
            Snippet.createSnippet("昔之")
                    .content("""
                            昔之{DATA}者。今{DATA}是矣。{NONE}""")
                    .create()
    );

    public static final SnippetSet FUNCTION_SNIPPET = new SnippetSet("function",
            Snippet.createSnippet("function")
                    .content("""
                            施{ID}。於{DATA}。{NONE}""")
                    .create()
    );

    public static final SnippetSet CONDITION_SNIPPET = new SnippetSet("condition",
            Snippet.createSnippet("if")
                    .content("""
                            若{DATA}者。
                              {STMT}
                            也。{NONE}""")
                    .create(),
            Snippet.createSnippet("if-else")
                    .content("""
                            若{DATA}者。
                              {STMT}
                            若非。
                              {STMT}
                            也。{NONE}""")
                    .create()
    );

    public static final SnippetSet LOOP_SNIPPET = new SnippetSet("condition",
            Snippet.createSnippet("for")
                    .content("""
                            為是{DATA}遍。
                              {STMT}
                            云云。{NONE}""")
                    .create(),
            Snippet.createSnippet("while")
                    .content("""
                            恆為是。
                              {STMT}
                            云云。{NONE}""")
                    .create(),
            Snippet.createSnippet("break")
                    .content("乃止。{NONE}")
                    .create()
    );

    public static final List<SnippetSet> STMT_SNIPPETS = List.of(
            VARIABLE_SNIPPET,
            FUNCTION_SNIPPET,
            CONDITION_SNIPPET,
            LOOP_SNIPPET
    );

    public static final SnippetSet DATA_SNIPPET = new SnippetSet("data",
            Snippet.createSnippet("0")
                    .content("零")
                    .create(),
            Snippet.createSnippet("1")
                    .content("一")
                    .create(),
            Snippet.createSnippet("2")
                    .content("二")
                    .create(),
            Snippet.createSnippet("3")
                    .content("三")
                    .create(),
            Snippet.createSnippet("false")
                    .content("陰")
                    .create(),
            Snippet.createSnippet("gt")
                    .content("{DATA}大於{DATA}")
                    .create()
    );

    public static final SnippetSet ID_SNIPPET = new SnippetSet("data",
            Snippet.createSnippet("a")
                    .content("「甲」")
                    .create(),
            Snippet.createSnippet("b")
                    .content("「乙」")
                    .create()
    );

    public static final List<SnippetSet> DEFAULT_SNIPPET = STMT_SNIPPETS;
}
