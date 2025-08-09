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
            Snippet.of("施", "施{ID}。於{DATA}。{NONE}")
    );

    public static final SnippetSet CONDITION_SNIPPET = new SnippetSet("condition",
            Snippet.of("若", """
                    若{DATA}者。
                      {STMT}
                    也。{NONE}"""),
            Snippet.of("若-若非", """
                    若{DATA}者。
                      {STMT}
                    若非。
                      {STMT}
                    也。{NONE}""")
    );

    public static final SnippetSet LOOP_SNIPPET = new SnippetSet("loop",
            Snippet.of("為是...遍", """
                    為是{DATA}遍。
                      {STMT}
                    云云。{NONE}"""),
            Snippet.of("恆為是", """
                    恆為是。
                      {STMT}
                    云云。{NONE}"""),
            Snippet.of("break", "乃止。{NONE}")
    );

    public static final SnippetSet DATA_SNIPPET = new SnippetSet("data",
            Snippet.of("零", "零"),
            Snippet.of("一", "一"),
            Snippet.of("二", "二"),
            Snippet.of("三", "三")
    );

    public static final SnippetSet BOOL_SNIPPET = new SnippetSet("bool",
            Snippet.of("陰", "陰"),
            Snippet.of("陽", "陽"),
            Snippet.of("大於", "{DATA}大於{DATA}")
    );

    public static final SnippetSet ID_SNIPPET = new SnippetSet("id",
            Snippet.of("甲", "「甲」"),
            Snippet.of("乙", "「乙」"),
            Snippet.of("丙", "「丙」"),
            Snippet.of("丁", "「丁」"),
            Snippet.of("戊", "「戊」")
    );

    public static final List<SnippetSet> STMT_SNIPPETS = List.of(
            VARIABLE_SNIPPET,
            FUNCTION_SNIPPET,
            CONDITION_SNIPPET,
            LOOP_SNIPPET
    );

    public static final List<SnippetSet> DATA_SNIPPETS = List.of(
            DATA_SNIPPET,
            BOOL_SNIPPET,
            ID_SNIPPET
    );

    public static final List<SnippetSet> DEFAULT_SNIPPET = STMT_SNIPPETS;
}
