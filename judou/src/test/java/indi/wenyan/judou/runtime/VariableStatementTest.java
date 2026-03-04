package indi.wenyan.judou.runtime;

import indi.wenyan.judou.structure.WenyanException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class VariableStatementTest extends WenyanProgramBasicTest {
    private static Stream<Arguments> testData() {
        return Stream.of(
                resultArgs("夫一名之曰「a」書之\n", 1),
                resultArgs("夫一名之曰「a」書「a」\n", 1),
                resultArgs("夫一名之曰「aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa」書「aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa」", 1),
                resultArgs("夫一名之曰「」書「」\n", 1),
                resultArgs("夫一名之曰「 」書「 」\n", 1),
                resultArgs("夫一名之曰「\u200b」書「\u200b」\n", 1),
                resultArgs("夫一名之曰「\uD83D\uDE22」書「\uD83D\uDE22」\n", 1),
                resultArgs("夫一名之曰「a a a」書「a a a」\n", 1),
                resultArgs("夫一名之曰「a\na\n」書「a\na\n」\n", 1),
                resultArgs("夫「「a」」名之曰「a」書「a」\n", "a"),
                resultArgs("夫一名之曰「a」昔之「a」者今二是矣書之\n", 1),
                resultArgs("夫一名之曰「a」昔之「a」者今二是矣書「a」\n", 2),
                resultArgs("夫一名之曰「a」夫二予之以「a」書「a」\n", 2),
                resultArgs("夫「「a」」名之曰「a」昔之「a」者今二是矣書「a」\n", "二"),
                resultArgs("夫一名之曰「a」名之曰「b」書「b」\n", 1),
                resultArgs("夫一名之曰「a」名之曰「b」昔之「a」者今二是矣書「b」\n", 1),
                resultArgs("夫一名之曰「a」夫二名之曰「a」書「a」\n", 2),
                resultArgs("夫一名之曰「a」夫「a」名之曰「a」書「a」\n", 1),
                resultArgs("夫一名之曰「b」名之曰「a」昔之「b」者今三是矣昔之「a」者今二是矣夫「b」名之曰「a」書「b」", 3),
                resultArgs("夫一名之曰「a」昔之「a」者今不復存矣書「a」", (Object) null)
        );
    }

    @ParameterizedTest
    @MethodSource("testData")
    void testNormal(String code, Object... output) throws WenyanException {
        assertResult(code, output);
    }

    @ParameterizedTest
    @CsvSource({
            "夫「a」名之曰一",
            "夫一名之曰「aaa's 」 is xxx」",
            "夫一名之曰書"
    })
    void testCompileError(String code) {
        assertCompileError(code);
    }

    @ParameterizedTest
    @CsvSource({
            "書「a」\n",
            "昔之一者今二是矣\n",
            "予之以「a」",
    })
    void testRuntimeError(String code) {
        assertRuntimeError(code);
    }
}
