package indi.wenyan.judou.runtime;

import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.exec_interface.structure.IHandleContext;
import indi.wenyan.judou.runtime.function_impl.WenyanProgramImpl;
import indi.wenyan.judou.runtime.function_impl.WenyanThread;
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
import org.slf4j.helpers.NOPLogger;

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
            LoggerManager.registerLogger(NOPLogger.NOP_LOGGER);
            LanguageManager.registerLanguageProvider(s -> s);
        } catch (IllegalStateException ignore) { // already registered by other class
        }
    }

    @Test
    void testNormal() throws WenyanException {
        assertResult("""
                書一。""", 1);
    }

    @ParameterizedTest
    @FieldSource("indi.wenyan.judou.runtime.test_utils.generated_WenyanProgramTestData#TEST_DATA")
    void testExamples(generated_WenyanProgramTestData.TestData testData) throws WenyanException {
        TestPlatform testPlatform = new TestPlatform();
        assertDoesNotThrow(() -> createAndRun(testData.code(), testPlatform));
        assertNull(testPlatform.error);
        assertEquals(testData.output().size(), testPlatform.output.size(),
                testData.output() + testPlatform.output.toString());
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
                    resultArgs("夫陽等於一書之", false));
        }

        @ParameterizedTest
        @MethodSource("testData")
        void testNormal(String code, Object... output) throws WenyanException {
            assertResult(code, output);
        }

        @ParameterizedTest
        @CsvSource({ "夫「「一」」大於一書之" })
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
                    resultArgs("吾有一列書之", List.of()));
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
                    resultArgs("有爻一書之\n", true));
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
                    resultArgs(
                            "夫一名之曰「aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa」書「aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa」",
                            1),
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
                    resultArgs("夫一名之曰「a」昔之「a」者今不復存矣書「a」", (Object) null));
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
                    timedArgs("待十待一\n", 14));
        }

        @ParameterizedTest
        @MethodSource("testData")
        void testNormal(String code, int ticks) throws WenyanException, InterruptedException {
            TestPlatform testPlatform = new TestPlatform();
            IWenyanProgram wenyanProgram = new WenyanProgramImpl(testPlatform);
            wenyanProgram.create(WenyanThread.ofCode(code, testPlatform.initEnvironment()));
            int cnt = 0;
            while (wenyanProgram.isRunning()) {
                wenyanProgram.step(1000);
                testPlatform.handle(IHandleContext.NONE);
                cnt++;
                // noinspection BusyWait
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
        wenyanProgram.create(WenyanThread.ofCode(code, testPlatform.initEnvironment()));
        while (wenyanProgram.isRunning()) {
            wenyanProgram.step(1000);
            testPlatform.handle(IHandleContext.NONE);
            // noinspection BusyWait
            Thread.sleep(20);
        }
    }
}
