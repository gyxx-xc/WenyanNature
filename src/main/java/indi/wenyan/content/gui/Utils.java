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

    public static final List<Snippet> STATEMENT_SNIPPET = List.of(
            Snippet.createSnippet("吾有一數")
                    .content("""
                            吾有一數。曰{DATA}。名之曰{ID}。{NONE}""")
                    .create(),
            Snippet.createSnippet("昔之")
                    .content("""
                            昔之{DATA}者。今{DATA}是矣。{NONE}""")
                    .create(),
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
                    .create(),
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
                    .create(),
            Snippet.createSnippet("function")
                    .content("""
                            施{ID}。於{DATA}。{NONE}""")
                    .create()
    );

    public static final List<Snippet> DATA_SNIPPET = List.of(
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

    public static final List<Snippet> ID_SNIPPET = List.of(
            Snippet.createSnippet("a")
                    .content("「甲」")
                    .create(),
            Snippet.createSnippet("b")
                    .content("「乙」")
                    .create()
    );

    public static final List<Snippet> DEFAULT_SNIPPET = STATEMENT_SNIPPET;
}
