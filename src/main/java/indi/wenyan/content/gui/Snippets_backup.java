package indi.wenyan.content.gui;

import java.util.List;

@Deprecated
@SuppressWarnings("unused")
public enum Snippets_backup {;
    public static final SnippetSet VARIABLE_SNIPPET = new SnippetSet("variable",
            SnippetSet.snippet("吾有一數", "吾有一數。曰{DATA}。名之曰{ID}。{NONE}"),
            SnippetSet.snippet("昔之", "昔之{DATA}者。今{DATA}是矣。{NONE}")
    );
    public static final SnippetSet FUNCTION_SNIPPET = new SnippetSet("function",
            SnippetSet.snippet("施", "施{ID}。於{DATA}。{NONE}")
    );
    public static final SnippetSet CONDITION_SNIPPET = new SnippetSet("condition",
            SnippetSet.snippet("若", """
                    若{DATA}者。
                      {STMT}
                    也。{NONE}"""),
            SnippetSet.snippet("若-若非", """
                    若{DATA}者。
                      {STMT}
                    若非。
                      {STMT}
                    也。{NONE}""")
    );
    public static final SnippetSet LOOP_SNIPPET = new SnippetSet("loop",
            SnippetSet.snippet("為是...遍", """
                    為是{DATA}遍。
                      {STMT}
                    云云。{NONE}"""),
            SnippetSet.snippet("恆為是", """
                    恆為是。
                      {STMT}
                    云云。{NONE}"""),
            SnippetSet.snippet("break", "乃止。{NONE}")
    );

    public static final SnippetSet DATA_SNIPPET = new SnippetSet("data",
            SnippetSet.snippet("零", "零"),
            SnippetSet.snippet("一", "一"),
            SnippetSet.snippet("二", "二"),
            SnippetSet.snippet("三", "三")
    );
    public static final SnippetSet BOOL_SNIPPET = new SnippetSet("bool",
            SnippetSet.snippet("陰", "陰"),
            SnippetSet.snippet("陽", "陽"),
            SnippetSet.snippet("大於", "{DATA}大於{DATA}")
    );
    public static final SnippetSet ID_SNIPPET = new SnippetSet("id",
            SnippetSet.snippet("甲", "「甲」"),
            SnippetSet.snippet("乙", "「乙」"),
            SnippetSet.snippet("丙", "「丙」"),
            SnippetSet.snippet("丁", "「丁」"),
            SnippetSet.snippet("戊", "「戊」")
    );

    public static final SnippetSet NONE_SNIPPET = new SnippetSet("none",
            SnippetSet.snippet("new line", "\n")
    );

    public static final List<SnippetSet> STMT_CONTEXT = List.of(
            VARIABLE_SNIPPET,
            FUNCTION_SNIPPET,
            CONDITION_SNIPPET,
            LOOP_SNIPPET
    );
    public static final List<SnippetSet> DATA_CONTEXT = List.of(
            DATA_SNIPPET,
            BOOL_SNIPPET,
            ID_SNIPPET
    );
    public static final List<SnippetSet> DEFAULT_CONTEXT = STMT_CONTEXT;

    public static int contextColor(Context context) {
        return switch (context.value()) {
            case "STMT" -> 0xFFFF0000;
            case "DATA" -> 0xFF00FF00;
            case "ID" -> 0xFF0000FF;
            case "NONE" -> 0x99000000;
            default -> throw new IllegalStateException("Unexpected value: " + context.value());
        };
    }

    public static List<SnippetSet> getSnippets(Context context) {
        return switch (context.value()) {
            case "STMT" -> STMT_CONTEXT;
            case "DATA" -> DATA_CONTEXT;
            case "ID" -> List.of(ID_SNIPPET);
            case "NONE" -> List.of(NONE_SNIPPET);
            default -> throw new IllegalStateException("Unexpected value: " + context.value());
        };
    }

    public record Context(String value) { }
}
