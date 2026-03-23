package indi.wenyan.judou.runtime;

import indi.wenyan.judou.structure.WenyanException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class FuStatementTest extends WenyanProgramTestHelper {
    private static Stream<Arguments> testData() {
        return Stream.of(
                resultArgs("夫一書之\n", 1),
                resultArgs("夫零書之\n", 0),
                resultArgs("夫「「aaa」」書之\n", "aaa"),
                resultArgs("夫一又五分書之\n", 1.5),
                resultArgs("夫陽書之\n", true),
                resultArgs("夫一夫其書之\n", 1),
                resultArgs("夫一大於二書之\n", false),
                resultArgs("夫二等於二書之\n", true),
                resultArgs("夫陽等於一書之", false)
        );
    }

    @ParameterizedTest
    @MethodSource("testData")
    void testNormal(String code, Object... output) throws WenyanException {
        assertResult(code, output);
    }

    @ParameterizedTest
    @CsvSource("夫「「一」」大於一書之")
    void testRuntimeError(String code) {
        assertRuntimeError(code);
    }
}
