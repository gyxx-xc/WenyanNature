package indi.wenyan.judou.runtime;

import indi.wenyan.judou.structure.WenyanException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class VaribleScopeStatementTest extends WenyanProgramTestHelper {
    private static Stream<Arguments> basicScopeTestData() {
        return Stream.of(
                // Basic function scope - variables inside function don't affect outside
                resultArgs("""
                        夫一名之曰「甲」
                        吾有一術名之曰「法」是術曰
                            夫二名之曰「甲」
                            書「甲」
                        是謂「法」之術也
                        施「法」
                        書「甲」
                        """, 2, 1),

                // Variable assignment in function affects outer scope
                resultArgs("""
                        夫一名之曰「甲」
                        吾有一術名之曰「法」是術曰
                            昔之「甲」者今二是矣
                            書「甲」
                        是謂「法」之術也
                        施「法」
                        書「甲」
                        """, 2, 2),

                // Function parameter shadows outer variable
                resultArgs("""
                        夫一名之曰「甲」
                        吾有一術名之曰「法」
                        欲行是術必先得一數曰「甲」是術曰
                            書「甲」
                        是謂「法」之術也
                        施「法」於二
                        書「甲」
                        """, 2, 1),

                // Nested scope - if statement doesn't create new scope
                resultArgs("""
                        夫一名之曰「甲」
                        若陽者
                            夫二名之曰「甲」
                        云云
                        書「甲」
                        """, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("basicScopeTestData")
    void testBasicScope(String code, Object... output) throws WenyanException {
        assertResult(code, output);
    }

    private static Stream<Arguments> closureTestData() {
        return Stream.of(
                // Closure captures outer variable
                resultArgs("""
                        夫一名之曰「甲」
                        吾有一術名之曰「法」是術曰
                            吾有一術名之曰「內法」是術曰
                                書「甲」
                            是謂「內法」之術也
                            施「內法」
                        是謂「法」之術也
                        施「法」
                        """, 1),

                // Closure with assignment
                resultArgs("""
                        夫一名之曰「甲」
                        吾有一術名之曰「法」是術曰
                            吾有一術名之曰「內法」是術曰
                                昔之「甲」者今二是矣
                                書「甲」
                            是謂「內法」之術也
                            施「內法」
                        是謂「法」之術也
                        施「法」
                        書「甲」
                        """, 2, 2),

                // Multiple closures sharing same captured variable
                resultArgs("""
                        夫零名之曰「甲」
                        吾有一術名之曰「加」是術曰
                            加「甲」以一昔之「甲」者今其是矣
                            書「甲」
                        是謂「加」之術也
                        吾有一術名之曰「減」是術曰
                            減「甲」以一昔之「甲」者今其是矣
                            書「甲」
                        是謂「減」之術也
                        施「加」
                        施「加」
                        施「減」
                        書「甲」
                        """, 1, 2, 1, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("closureTestData")
    void testClosure(String code, Object... output) throws WenyanException {
        assertResult(code, output);
    }

    private static Stream<Arguments> nestedFunctionTestData() {
        return Stream.of(
                // Nested function definition with closure
                resultArgs("""
                        夫一名之曰「甲」
                        吾有一術名之曰「外法」是術曰
                            夫二名之曰「乙」
                            吾有一術名之曰「內法」是術曰
                                書「甲」
                                書「乙」
                            是謂「內法」之術也
                            施「內法」
                        是謂「外法」之術也
                        施「外法」
                        """, 1, 2),

                // Inner function returned and called later
                resultArgs("""
                        夫五名之曰「甲」
                        吾有一術名之曰「造函」是術曰
                            吾有一術名之曰「內法」是術曰
                                書「甲」
                            是謂「內法」之術也
                            乃得「內法」
                        是謂「造函」之術也
                        施「造函」名之曰「函」
                        夫十名之曰「甲」
                        施「函」
                        """, 5),

                // Deep nested closure chain
                resultArgs("""
                        夫一名之曰「甲」
                        吾有一術名之曰「外」是術曰
                            夫二名之曰「乙」
                            吾有一術名之曰「中」是術曰
                                夫三名之曰「丙」
                                吾有一術名之曰「內」是術曰
                                    加「甲」以「乙」加之以「丙」書之
                                是謂「內」之術也
                                施「內」
                            是謂「中」之術也
                            施「中」
                        是謂「外」之術也
                        施「外」
                        """, 6)
        );
    }

    @ParameterizedTest
    @MethodSource("nestedFunctionTestData")
    void testNestedFunction(String code, Object... output) throws WenyanException {
        assertResult(code, output);
    }

    private static Stream<Arguments> shadowingTestData() {
        return Stream.of(
                // Variable shadowing in nested function
                resultArgs("""
                        夫一名之曰「甲」
                        吾有一術名之曰「法」是術曰
                            夫二名之曰「甲」
                            吾有一術名之曰「內法」是術曰
                                書「甲」
                            是謂「內法」之術也
                            施「內法」
                            書「甲」
                        是謂「法」之術也
                        施「法」
                        書「甲」
                        """, 2, 2, 1),

                // Parameter shadows captured variable
                resultArgs("""
                        夫一名之曰「甲」
                        吾有一術名之曰「法」
                        欲行是術必先得一數曰「甲」是術曰
                            吾有一術名之曰「內法」是術曰
                                書「甲」
                            是謂「內法」之術也
                            施「內法」
                        是謂「法」之術也
                        施「法」於二
                        書「甲」
                        """, 2, 1),

                // Re-declaration vs assignment in conditionals
                resultArgs("""
                        夫一名之曰「甲」
                        若陽者
                            夫二名之曰「甲」
                        云云
                        書「甲」
                        若陽者
                            昔之「甲」者今三是矣
                        云云
                        書「甲」
                        """, 1, 3)
        );
    }

    @ParameterizedTest
    @MethodSource("shadowingTestData")
    void testShadowing(String code, Object... output) throws WenyanException {
        assertResult(code, output);
    }

    private static Stream<Arguments> loopScopeTestData() {
        return Stream.of(
                // Loop variable scope
                resultArgs("""
                        吾有一列名之曰「列」充之以一以二以三
                        凡「列」中之「元」
                            書「元」
                        云云
                        """, 1, 2, 3)

                // Loop variable persists after loop
//                    resultArgs("""
//                            吾有一列名之曰「列」充之以一
//                            凡「列」中之「元」
//                                書「元」
//                            云云
//                            書「元」
//                            """, 1, 1),
        );
    }

    @ParameterizedTest
    @MethodSource("loopScopeTestData")
    void testLoopScope(String code, Object... output) throws WenyanException {
        assertResult(code, output);
    }

    private static Stream<Arguments> complexScopeTestData() {
        return Stream.of(
                // Complex closure with multiple variables
                resultArgs("""
                        夫十名之曰「甲」
                        夫二十名之曰「乙」
                        吾有一術名之曰「加」是術曰
                            加「甲」以「乙」書之
                        是謂「加」之術也
                        吾有一術名之曰「改」是術曰
                            昔之「甲」者今五十是矣
                            昔之「乙」者今六十是矣
                        是謂「改」之術也
                        施「加」
                        施「改」
                        施「加」
                        """, 30, 110),

                // Function returning function with captured state
                resultArgs("""
                        吾有一術名之曰「造加」
                        欲行是術必先得一數曰「基」是術曰
                            吾有一術名之曰「加」
                            欲行是術必先得一數曰「值」是術曰
                                加「基」以「值」書之
                            是謂「加」之術也
                            乃得「加」
                        是謂「造加」之術也
                        施「造加」於十名之曰「加十」
                        施「造加」於二十名之曰「加二十」
                        施「加十」以五
                        施「加二十」以五
                        """, 15, 25)
        );
    }

    @ParameterizedTest
    @MethodSource("complexScopeTestData")
    void testComplexScope(String code, Object... output) throws WenyanException {
        assertResult(code, output);
    }
}
