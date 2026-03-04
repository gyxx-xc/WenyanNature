package indi.wenyan.judou.runtime;

import indi.wenyan.judou.structure.WenyanException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

class WuyouStatementTest extends WenyanProgramBasicTest {
    private static Stream<Arguments> testData() {
        return Stream.of(
                resultArgs("吾有一數書之", 0),
                resultArgs("吾有二數書之", 0, 0),
                resultArgs("吾有一數曰一書之\n。", 1),
                resultArgs("吾有二數曰一曰二書之", 1, 2),
                resultArgs("吾有一數曰一又五分書之", 1.5),
                resultArgs("吾有一言書之", ""),
                resultArgs("吾有二言曰一曰「「aaa」」書之", "一", "aaa"),
                resultArgs("吾有一言曰陽書之", "陽"),
                resultArgs("吾有一爻書之", false),
                resultArgs("夫一吾有一爻曰其書之", true),
                resultArgs("吾有一列書之", List.of())
        );
    }

    @ParameterizedTest
    @MethodSource("testData")
    void testNormal(String code, Object... output) throws WenyanException {
        assertResult(code, output);
    }

    @ParameterizedTest
    @CsvSource({
            "吾有負一數書之\n",
            "吾有零數書之\n",
            "吾有零數曰一書之\n",
            "吾有二數曰一書之\n",
            "吾有一百萬列書之",
    })
    void testCompileError(String code) {
        assertCompileError(code);
    }

    @ParameterizedTest
    @CsvSource({
            "吾有一數曰「「一」」書之",
            "吾有一列曰一書之\n",
    })
    void testRuntimeError(String code) {
        assertRuntimeError(code);
    }
}
