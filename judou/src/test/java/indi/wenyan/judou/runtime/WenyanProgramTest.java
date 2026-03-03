package indi.wenyan.judou.runtime;

import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.exec_interface.structure.IHandleContext;
import indi.wenyan.judou.runtime.function_impl.WenyanProgramImpl;
import indi.wenyan.judou.runtime.function_impl.WenyanRunner;
import indi.wenyan.judou.runtime.test_utils.TestPlatform;
import indi.wenyan.judou.runtime.test_utils.generated_WenyanProgramTestData;
import indi.wenyan.judou.structure.WenyanCompileException;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.utils.LanguageManager;
import indi.wenyan.judou.utils.LoggerManager;
import indi.wenyan.judou.utils.WenyanValues;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

// all test are repeated as the program run in muti thread
// one time run might cause coincident pass
// TODO: load language part only
class WenyanProgramTest {

    static {
        try {
            LanguageManager.registerLanguageProvider(s -> s);
            LoggerManager.registerLogger(LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME));
        } catch (IllegalStateException ignore) {}
    }

    @Test
    void testNormal() throws WenyanException {
        assertResult("""
                吾有一術。名之曰「歐幾里得法」。
                欲行是術。必先得二數。
                曰「甲」。曰「乙」。
                乃行是術曰。
                		吾有一數。名之曰「回」。
                		若「乙」等於零者。乃得「甲」。
                		若非
                			吾有一數。名之曰「削除」。
                			除「甲」以「乙」所餘幾何。昔之「削除」者。今其是矣。
                			施「歐幾里得法」於「乙」。於「削除」。
                			昔之「回」者。今其是矣。
                		也
                		乃得「回」。
                是謂「歐幾里得法」之術也。
                
                吾有一術。名之曰「互質」。
                欲行是術。必先得二數。
                曰「甲」。曰「乙」。
                乃行是術曰。
                		吾有一數。名之曰「回」。
                		施「歐幾里得法」於「甲」。於「乙」。昔之「回」者。今其是矣。
                		若「回」等於一者。乃得陽。若非。乃得陰。也。
                是謂「互質」之術也。
                
                吾有一術。名之曰「歐拉餘數」。
                欲行是術。必先得一數。
                曰「甲」。
                乃行是術曰。
                	注曰。「「非最優解矣。吾算術及數論廢也」」
                	吾有二數。曰二。曰一。名之曰「埃」。曰「積」
                	恆為是。
                		若「甲」不大於「埃」者。乃止。也。
                		吾有一爻。名之曰「回」。
                		施「互質」於「甲」。於「埃」。昔之「回」者。今其是矣。
                		若「回」者加「積」以一。昔之「積」者。今其是矣。也。
                		加「埃」以一。昔之「埃」者。今其是矣。
                	云云
                	乃得「積」。
                是謂「歐拉餘數」之術也。
                
                施「歐幾里得法」於一千零七十一於四百六十二。書之。
                施「歐幾里得法」於一百二十三於四。書之。
                
                施「互質」於一百二十三於四。書之。
                施「互質」於四於二。書之。
                
                施「歐拉餘數」於二。書之。
                施「歐拉餘數」於十二。書之。
                施「歐拉餘數」於十三。書之。
                施「歐拉餘數」於十六。書之。
                施「歐拉餘數」於二百五十五。書之。""", 21,
                1,
                true,
                false,
                1,
                4,
                12,
                8,
                128);
    }

    @ParameterizedTest
    @FieldSource("indi.wenyan.judou.runtime.test_utils.generated_WenyanProgramTestData#TEST_DATA")
    void testExamples(generated_WenyanProgramTestData.TestData testData) throws WenyanException {
        TestPlatform testPlatform = new TestPlatform();
        assertDoesNotThrow(() -> createAndRun(testData.code(), testPlatform));
        assertNull(testPlatform.error);
        assertEquals(testData.output().size(), testPlatform.output.size(), testData.output() + testPlatform.output.toString());
        for (int i = 0; i < testData.output().size(); i++) {
            assertTrue(IWenyanValue.equals(testData.output().get(i), testPlatform.output.get(i)),
                    testData.output() + " and " + testPlatform.output.toString() + " differ at " + i + "\n" +
                            testData.output().get(i) + " and " + testPlatform.output.get(i));
        }
    }

    @Nested
    class FuStatement {
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
        @CsvSource({"夫「「一」」大於一書之"})
        void testRuntimeError(String code) {
            assertRuntimeError(code);
        }
    }

    @Nested
    class WuyouStatement {
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

    @Nested
    class YouStatement {
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

    @Nested
    class VariableStatement {
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
                "昔之一者今二是矣\n"
        })
        void testRuntimeError(String code) {
            assertRuntimeError(code);
        }
    }

    @Nested
    class AwaitStatement {
        private static Stream<Arguments> testData() {
            return Stream.of(
                    timedArgs("待一\n", 3),
                    timedArgs("待十\n", 12),
                    timedArgs("待二十\n", 22),
                    timedArgs("待一待一待一\n", 7),
                    timedArgs("待十待一\n", 14)
            );
        }

        @ParameterizedTest
        @MethodSource("testData")
        void testNormal(String code, int ticks) throws WenyanException, InterruptedException {
            TestPlatform testPlatform = new TestPlatform();
            IWenyanProgram wenyanProgram = new WenyanProgramImpl(testPlatform);
            wenyanProgram.create(WenyanRunner.ofCode(code, testPlatform.initEnvironment()));
            int cnt = 0;
            while (wenyanProgram.isRunning()) {
                wenyanProgram.step(1000);
                testPlatform.handle(IHandleContext.NONE);
                cnt++;
                //noinspection BusyWait
                Thread.sleep(5);
            }
            assertNull(testPlatform.error);
            assertEquals(ticks, cnt);
        }

        private static Arguments timedArgs(String code, int ticks) {
            return Arguments.of(code, ticks);
        }

        @ParameterizedTest
        @CsvSource({
                "待一以一\n",
                "待「「a」」\n",
                "待千兆\n",
        })
        void testRuntimeError(String code) {
            assertRuntimeError(code);
        }
    }

    @Nested
    class VaribleScopeStatement {
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

    private static Arguments resultArgs(String code, Object... output) {
        return Arguments.of(code, output);
    }

    private void assertResult(String code, Object... output) throws WenyanException {
        TestPlatform testPlatform = new TestPlatform();
        assertDoesNotThrow(() -> createAndRun(code, testPlatform));
        assertNull(testPlatform.error);
        assertEquals(output.length, testPlatform.output.size(), Arrays.toString(output) + testPlatform.output);
        for (int i = 0; i < output.length; i++) {
            assertTrue(IWenyanValue.equals(wenyanValueFromObject(output[i]), testPlatform.output.get(i)),
                    Arrays.toString(output) + " and " + testPlatform.output.toString() + " differ at " + i + "\n" +
                            output[i] + " and " + testPlatform.output.get(i));
        }
    }

    private void assertCompileError(String code) {
        TestPlatform testPlatform = new TestPlatform();
        assertThrows(WenyanCompileException.class, () -> createAndRun(code, testPlatform));
    }

    private void assertRuntimeError(String code) {
        TestPlatform testPlatform = new TestPlatform();
        assertDoesNotThrow(() -> createAndRun(code, testPlatform));
        assertNotNull(testPlatform.error, code);
    }

    private static IWenyanValue wenyanValueFromObject(Object o) {
        return switch (o) {
            case null -> WenyanNull.NULL;
            case Integer i -> WenyanValues.of(i);
            case Long l -> WenyanValues.of(l);
            case Boolean b -> WenyanValues.of(b);
            case Float f -> WenyanValues.of(f);
            case Double d -> WenyanValues.of(d);
            case String s -> WenyanValues.of(s);
            case List<?> l ->
                    WenyanValues.of(l.stream().map(WenyanProgramTest::wenyanValueFromObject).toList());
            default -> throw new RuntimeException("unsupported type: " + o.getClass());
        };
    }

    private void createAndRun(String code, IWenyanPlatform testPlatform) throws WenyanException, InterruptedException {
        IWenyanProgram wenyanProgram = new WenyanProgramImpl(testPlatform);
        wenyanProgram.create(WenyanRunner.ofCode(code, testPlatform.initEnvironment()));
        while (wenyanProgram.isRunning()) {
            wenyanProgram.step(8000);
            testPlatform.handle(IHandleContext.NONE);
            //noinspection BusyWait
            Thread.sleep(20);
        }
    }
}
