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
                        """, 10),

                resultArgs("""
                        吾有一術 需「甲」
                            乘「甲」以二乃得之
                        之術也
                        名之曰「倍」
                        施「倍」於五書之
                        """, 10),

                // Lambda with all three basic types (數, 言, 爻)
                resultArgs("""
                        吾有一術 需數曰「甲」言曰「乙」爻曰「丙」
                            書「甲」書「乙」書「丙」
                        之術也
                        名之曰「混」
                        施「混」於四十二於「「hi」」於陽
                        """, 42, "hi", true),

                // Lambda reused with multiple different arguments
                resultArgs("""
                        吾有一術 需數曰「甲」
                            乘「甲」以二乃得之
                        之術也
                        名之曰「倍」
                        施「倍」於三書之
                        施「倍」於五書之
                        施「倍」於十書之
                        """, 6, 10, 20),

                // Lambda with closure mutation (outer variable modified inside lambda)
                resultArgs("""
                        夫十名之曰「基」
                        吾有一術 需數曰「甲」
                            加「甲」以「基」書之
                            夫二十予之以「基」
                        之術也
                        名之曰「加基」
                        施「加基」於五
                        施「加基」於五
                        """, 15, 25)
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
                        """, 9),

                // Simple lambda with closure capture
                resultArgs("""
                        夫百名之曰「cent」
                        有術 需數曰「甲」
                            加「甲」以「cent」書之
                        之術也
                        名之曰「加百」
                        施「加百」於三
                        """, 103),

                // Simple lambda with local variable
                resultArgs("""
                        有術 需數曰「甲」
                            有數三名之曰「乙」
                            加「甲」以「乙」書之
                        之術也
                        名之曰「加三」
                        施「加三」於七
                        """, 10)
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
                        """, 15),

                // Lambda currying: outer lambda returns inner lambda that captures outer param
                resultArgs("""
                        吾有一術 需數曰「甲」
                            吾有一術 需數曰「乙」
                                乘「乙」以「甲」乃得之
                            之術也
                            乃得之
                        之術也
                        名之曰「造乘器」
                        施「造乘器」於五名之曰「乘五」
                        施「乘五」於六書之
                        """, 30),

                // Higher-order: function applies a lambda twice to a value
                resultArgs("""
                        吾有一術 名之曰「兩次」
                        欲行是術 必先得 一數曰「甲」 一術曰「函」 是術曰
                            施「函」於「甲」
                            施「函」於其書之
                        是謂「兩次」之術也
                        有術 需數曰「乙」
                            加「乙」以一乃得之
                        之術也
                        施「兩次」於五於其
                        """, 7),

                // Multiple lambdas stored and composed: f(g(x))
                resultArgs("""
                        吾有一術 需數曰「甲」
                            乘「甲」以二乃得之
                        之術也
                        名之曰「乘二」
                        吾有一術 需數曰「乙」
                            加「乙」以一乃得之
                        之術也
                        名之曰「加一」
                        施「加一」於三
                        施「乘二」於其書之
                        """, 8),

                // Lambda passed to a named function that calls it conditionally
                resultArgs("""
                        吾有一術 名之曰「選擇」
                        欲行是術 必先得 一數曰「甲」 一術曰「真函」 一術曰「假函」 是術曰
                            若「甲」大於十者
                                施「真函」於「甲」書之
                            若非
                                施「假函」於「甲」書之
                            云云
                        是謂「選擇」之術也
                        夫五
                        有術 需數曰「乙」 加「乙」以十乃得之 之術也
                        有術 需數曰「丙」 乘「丙」以二乃得之 之術也
                        取三以施「選擇」
                        """, 10),

                // Deeply nested lambda (3 levels)
                resultArgs("""
                        吾有一術 需數曰「甲」
                            吾有一術 需數曰「乙」
                                吾有一術
                                    加「乙」以「甲」書之
                                之術也
                                乃得之
                            之術也
                            乃得之
                        之術也
                        名之曰「外」
                        施「外」於十
                        施之於二十
                        施之
                        """, 30)
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
                        """, "async"),

                // Async lambda with 待 (await) inside body
                resultArgs("""
                        同有一術 需數曰「甲」
                            待「甲」
                            加「甲」以一書之
                        之術也
                        名之曰「延遲加一」
                        施「延遲加一」於五
                        """, 6),

                // Multiple async lambdas in sequence
                resultArgs("""
                        同有一術
                            待二
                            書「「a」」
                        之術也
                        名之曰「a」
                        施「a」
                        同有一術
                            書「「b」」
                        之術也
                        名之曰「b」
                        施「b」
                        """, "b", "a")
        );
    }

    @ParameterizedTest
    @MethodSource("asyncLambdaTestData")
    void testAsyncLambda(String code, Object... output) throws WenyanException {
        assertResult(code, output);
    }

    private static Stream<Arguments> lambdaAdvancedErrorTestData() {
        return Stream.of(
                // Duplicate parameter names
                Arguments.of("""
                        吾有一術 需數曰「甲」 需數曰「甲」
                            書「甲」
                        之術也
                        """),

                // Lambda body with no statements and no terminator
                Arguments.of("有術"),

                // Lambda with 須 instead of 需 (typo/malformed param syntax)
                Arguments.of("""
                        吾有一術 須數曰「甲」
                            書「甲」
                        之術也
                        """),

                // Lambda in lambda with broken nesting (missing terminator)
                Arguments.of("""
                        吾有一術 需數曰「甲」
                            吾有一術 需數曰「乙」
                                加「乙」以「甲」乃得之
                            之術也
                        """)
        );
    }

    @ParameterizedTest
    @MethodSource("lambdaAdvancedErrorTestData")
    void testLambdaAdvancedError(String code) {
        assertCompileError(code);
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
