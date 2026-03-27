package indi.wenyan.judou.runtime;

import indi.wenyan.judou.exec_interface.structure.IHandleContext;
import indi.wenyan.judou.runtime.function_impl.RunnerCreater;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.runtime.function_impl.WenyanProgramImpl;
import indi.wenyan.judou.runtime.test_utils.TestPlatform;
import indi.wenyan.judou.structure.WenyanException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AwaitStatementTest extends WenyanProgramTestHelper {
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
        IWenyanProgram<WenyanProgramImpl.PCB> wenyanProgram = new WenyanProgramImpl(testPlatform);
        wenyanProgram.create(RunnerCreater.newRunner(WenyanFrame.ofCode(code), testPlatform.initEnvironment()));
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
