package indi.wenyan.content.gui;

import java.util.List;

public class Utils {
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
                            if ({DATA}) {
                                {STMT}
                            }""")
                    .create(),
            Snippet.createSnippet("while")
                    .content("""
                            while ({DATA}) {
                                {STMT}
                            }""")
                    .create(),
            Snippet.createSnippet("for")
                    .content("""
                            for ({ID} : {DATA}) {
                                {STMT}
                            }""")
                    .create());
}
