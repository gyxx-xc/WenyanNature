package indi.wenyan.judou.runtime;

import indi.wenyan.judou.exec_interface.structure.IHandleContext;
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
                        """, 12)
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
