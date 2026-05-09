package indi.wenyan.judou.test_statement;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.compile.IWenyanBytecode;
import indi.wenyan.judou.api.compile.WenyanCompiler;
import indi.wenyan.judou.api.exec.structure.IHandleContext;
import indi.wenyan.judou.api.runtime.IWenyanScheduler;
import indi.wenyan.judou.api.runtime.RunnerCreator;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.runtime.function_impl.WenyanSchedularImpl;
import indi.wenyan.judou.test_utils.TestPlatform;
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
        IWenyanScheduler<WenyanSchedularImpl.PCB> wenyanProgram = new WenyanSchedularImpl(testPlatform, 1000);
        IWenyanBytecode bytecode = new WenyanCompiler().compile(code).bytecode();
        wenyanProgram.create(RunnerCreator.newRunner(WenyanFrame.ofCode(bytecode), testPlatform.initEnvironment()));
        int cnt = 0;
        while (wenyanProgram.isRunning()) {
            wenyanProgram.step();
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
