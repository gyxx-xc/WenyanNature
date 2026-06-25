package indi.wenyan.judou.test_statement;

import indi.wenyan.judou.api.values.exception.WenyanException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class LambdaStatementTest extends WenyanProgramTestHelper {
    private static Stream<Arguments> declaredLambdaTestData() {
        return Stream.of(
                // Declared lambda with one arg, named and called
                resultArgs("""
                        吾有一術 需數曰「甲」
                            加「甲」以一書之
                        之術也
                        名之曰「加一」
                        施「加一」於五
                        """, 6),

                // Declared lambda with multiple args
                resultArgs("""
                        吾有一術 需數曰「甲」 數曰「乙」
                            加「甲」以「乙」書之
                        之術也
                        名之曰「和」
                        施「和」於三於四
                        """, 7),

                // Declared lambda with string arg
                resultArgs("""
                        吾有一術 需言曰「甲」
                            書「甲」
                        之術也
                        名之曰「印」
                        施「印」於「「hello」」
                        """, "hello"),

                // Declared lambda without args
                resultArgs("""
                        吾有一術 書「「ok」」 之術也
                        施之
                        """, "ok"),

                // Declared lambda with return value
                resultArgs("""
                        吾有一術 需數曰「甲」
                            乘「甲」以二乃得之
                        之術也
                        名之曰「倍」
                        施「倍」於五書之
                        """, 10)
        );
    }

    @ParameterizedTest
    @MethodSource("declaredLambdaTestData")
    void testDeclaredLambda(String code, Object... output) throws WenyanException {
        assertResult(code, output);
    }

    private static Stream<Arguments> simpleLambdaTestData() {
        return Stream.of(
                // Simple lambda with one arg
                resultArgs("""
                        有術 需數曰「甲」
                            加「甲」以一書之
                        之術也
                        名之曰「加一」
                        施「加一」於三
                        """, 4),

                // Simple lambda with two args
                resultArgs("""
                        有術 需數曰「甲」 數曰「乙」
                            乘「甲」以「乙」書之
                        之術也
                        名之曰「積」
                        施「積」於六於七
                        """, 42),

                // Simple lambda without args
                resultArgs("""
                        有術
                            書「「lambda」」
                        之術也
                        名之曰「f」
                        施「f」
                        """, "lambda"),

                // Simple lambda with return
                resultArgs("""
                        有術 需數曰「甲」
                            減「甲」以一乃得之
                        之術也
                        名之曰「前驅」
                        施「前驅」於十書之
                        """, 9)
        );
    }

    @ParameterizedTest
    @MethodSource("simpleLambdaTestData")
    void testSimpleLambda(String code, Object... output) throws WenyanException {
        assertResult(code, output);
    }

    private static Stream<Arguments> lambdaAsValueTestData() {
        return Stream.of(
                // Lambda returned from a named function
                resultArgs("""
                        吾有一術名之曰「造加器」是術曰
                            有術 需數曰「甲」
                                加「甲」以一乃得之
                            之術也
                            乃得之
                        是謂「造加器」之術也
                        施「造加器」名之曰「加一」
                        施「加一」於五書之
                        """, 6),

                // Lambda passed as argument to another function
                resultArgs("""
                        吾有一術名之曰「應用」
                        欲行是術必先得一數曰「甲」一術曰「函」是術曰
                            施「函」於「甲」書之
                        是謂「應用」之術也
                        有術 需數曰「乙」
                            加「乙」以一乃得之
                        之術也
                        施「應用」於十於其
                        """, 11),

                // Lambda with closure
                resultArgs("""
                        夫十名之曰「基」
                        有術 需數曰「甲」
                            加「甲」以「基」書之
                        之術也
                        名之曰「加基」
                        施「加基」於五
                        """, 15)
        );
    }

    @ParameterizedTest
    @MethodSource("lambdaAsValueTestData")
    void testLambdaAsValue(String code, Object... output) throws WenyanException {
        assertResult(code, output);
    }

    private static Stream<Arguments> asyncLambdaTestData() {
        return Stream.of(
                // Async declared lambda
                resultArgs("""
                        同有一術 需數曰「甲」
                            加「甲」以一書之
                        之術也
                        名之曰「加一」
                        施「加一」於五
                        """, 6),

                // Async lambda without args
                resultArgs("""
                        同有一術
                            書「「async」」
                        之術也
                        名之曰「f」
                        施「f」
                        """, "async")
        );
    }

    @ParameterizedTest
    @MethodSource("asyncLambdaTestData")
    void testAsyncLambda(String code, Object... output) throws WenyanException {
        assertResult(code, output);
    }

    private static Stream<Arguments> lambdaCompilerErrorTestData() {
        return Stream.of(
                // Syntax: missing FUNCTION_TYPE after 有
                Arguments.of("有需數曰「甲」 之術也"),

                // Syntax: missing 之術也 terminator after body
                Arguments.of("吾有一術 需數曰「甲」 加「甲」以一")
        );
    }

    @ParameterizedTest
    @MethodSource("lambdaCompilerErrorTestData")
    void testLambdaCompileError(String code) {
        assertCompileError(code);
    }
}
