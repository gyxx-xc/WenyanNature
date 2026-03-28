package indi.wenyan.judou.test_statement;

import indi.wenyan.judou.exec_interface.structure.IHandleContext;
import indi.wenyan.judou.runtime.IWenyanProgram;
import indi.wenyan.judou.runtime.function_impl.RunnerCreater;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.runtime.function_impl.WenyanProgramImpl;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanPackage;
import indi.wenyan.judou.test_utils.TestPlatform;
import indi.wenyan.judou.test_utils.generated_WenyanProgramTestData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

// all test are repeated as the program run in muti thread
// one time run might cause coincident pass
class WenyanProgramBasicTest extends WenyanProgramTestHelper {

    @SuppressWarnings("ALL")
//    @Test
    void testNormal() throws WenyanException, InterruptedException, IOException {
        String code = """
                        同有一術名之曰「a」。是術曰。
                        待一
                        是謂「a」之術也。
                        施「a」名之曰「a1」施「a」名之曰「a2」
                        待「a1」待「a2」
                """;

        TestPlatform testPlatform = new TestPlatform();
        IWenyanProgram<WenyanProgramImpl.PCB> wenyanProgram = new WenyanProgramImpl(testPlatform, 1000);
        WenyanPackage globalResolver = testPlatform.initEnvironment();

        while (true) {
            WenyanFrame mainRuntime = WenyanFrame.ofCode(code);
            wenyanProgram.create(RunnerCreater.newRunner(mainRuntime, globalResolver));
            while (wenyanProgram.isRunning()) {
                wenyanProgram.step();
                testPlatform.handle(IHandleContext.NONE);
                //noinspection BusyWait
                Thread.sleep(5);
            }
        }
    }

    @ParameterizedTest
    @FieldSource("indi.wenyan.judou.test_utils.generated_WenyanProgramTestData#TEST_DATA")
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
}
