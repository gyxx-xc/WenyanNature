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
            Snippet.createSnippet("if")
                    .content("""
                            if ({DATA})
                                {STMT}
                            """)
                    .create(),
            Snippet.createSnippet("while")
                    .content("""
                            while ({DATA})
                                {STMT}
                            """)
                    .create(),
            Snippet.createSnippet("for")
                    .content("""
                            for ({ID} : {DATA})
                                {STMT}
                            """)
                    .create());

    public static final List<Snippet> DATA_SNIPPET = List.of(
            Snippet.createSnippet("true")
                    .content("true")
                    .create(),
            Snippet.createSnippet("false")
                    .content("false")
                    .create()
    );

    public static final List<Snippet> ID_SNIPPET = List.of(
            Snippet.createSnippet("int")
                    .content("int {ID}")
                    .create()
    );
}
