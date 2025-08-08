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
                    .string("if (").hold(Snippet.Context.DATA).stringLine(") {")
                    .string("\t").hold(Snippet.Context.STMT).newLine()
                    .stringLine("}")
                    .create(),
            Snippet.createSnippet("while")
                    .string("while (").hold(Snippet.Context.DATA).stringLine(") {")
                    .string("\t").hold(Snippet.Context.STMT).newLine()
                    .stringLine("}")
                    .create(),
            Snippet.createSnippet("for")
                    .string("for (").hold(Snippet.Context.ID).string(" : ")
                    .hold(Snippet.Context.DATA).stringLine(") {")
                    .string("\t").hold(Snippet.Context.STMT).newLine()
                    .stringLine("}")
                    .create());
}
