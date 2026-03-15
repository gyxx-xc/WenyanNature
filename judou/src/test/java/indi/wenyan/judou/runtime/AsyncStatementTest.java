package indi.wenyan.judou.runtime;

import indi.wenyan.judou.exec_interface.structure.IHandleContext;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.runtime.function_impl.WenyanProgramImpl;
import indi.wenyan.judou.runtime.function_impl.WenyanRunner;
import indi.wenyan.judou.runtime.test_utils.TestPlatform;
import indi.wenyan.judou.structure.WenyanException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AsyncStatementTest extends WenyanProgramTestHelper {
    private static Stream<Arguments> testData() {
        return Stream.of(
                timedArgs("""
                        同有一術名之曰「a」。是術曰。
                        待十
                        是謂「a」之術也。
                        施「a」名之曰「a1」施「a」名之曰「a2」
                        待「a1」待「a2」
                        """, 12),
                // Test multiple async calls with different wait times
                timedArgs("""
                        同有一術名之曰「fast」。是術曰。
                        待五
                        是謂「fast」之術也。
                        同有一術名之曰「slow」。是術曰。
                        待十五
                        是謂「slow」之術也。
                        施「fast」名之曰「f」施「slow」名之曰「s」
                        待「f」待「s」
                        """, 17),

                // Test sequential async calls
                timedArgs("""
                        同有一術名之曰「task」。是術曰。
                        待八
                        是謂「task」之術也。
                        施「task」名之曰「t1」
                        待「t1」
                        施「task」名之曰「t2」
                        待「t2」
                        """, 19),

                // Test nested async calls
                timedArgs("""
                        同有一術名之曰「inner」。是術曰。
                        待三
                        是謂「inner」之術也。
                        同有一術名之曰「outer」。是術曰。
                        施「inner」名之曰「i」
                        待「i」
                        是謂「outer」之術也。
                        施「outer」名之曰「o」
                        待「o」
                        """, 5)
        );
    }

    @ParameterizedTest
    @MethodSource("testData")
    void testNormal(String code, int ticks) throws WenyanException, InterruptedException {
        TestPlatform testPlatform = new TestPlatform();
        IWenyanProgram wenyanProgram = new WenyanProgramImpl(testPlatform);
        wenyanProgram.create(WenyanRunner.of(WenyanFrame.ofCode(code), testPlatform.initEnvironment()));
        int cnt = 0;
        while (wenyanProgram.isRunning()) {
            wenyanProgram.step(1000);
            testPlatform.handle(IHandleContext.NONE);
            cnt++;
            if (cnt > 100)
                System.out.println(1111);
            //noinspection BusyWait
            Thread.sleep(5);
        }
        assertNull(testPlatform.error);
        assertEquals(ticks, cnt);
    }

    private static Arguments timedArgs(String code, int ticks) {
        return Arguments.of(code, ticks);
    }
}
