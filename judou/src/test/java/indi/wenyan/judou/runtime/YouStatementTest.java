package indi.wenyan.judou.runtime;

import indi.wenyan.judou.structure.WenyanException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class YouStatementTest extends WenyanProgramTestHelper {
    private static Stream<Arguments> testData() {
        return Stream.of(
                resultArgs("有數一書之\n", 1),
                resultArgs("有數五書之\n", 5),
                resultArgs("有言一書之\n", "一"),
                resultArgs("有言「「aaa」」書之\n", "aaa"),
                resultArgs("有爻一書之\n", true)
        );
    }

    @ParameterizedTest
    @MethodSource("testData")
    void testNormal(String code, Object... output) throws WenyanException {
        assertResult(code, output);
    }

    @ParameterizedTest
    @CsvSource({
            "有數「「aaa」」",
            "有列一書之"
    })
    void testRuntimeError(String code) {
        assertRuntimeError(code);
    }
}
