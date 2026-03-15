package indi.wenyan.judou.runtime;

import indi.wenyan.judou.runtime.function_impl.WenyanProgramImpl;
import indi.wenyan.judou.runtime.function_impl.WenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanRuntime;
import indi.wenyan.judou.runtime.test_utils.TestPlatform;
import indi.wenyan.judou.runtime.test_utils.generated_WenyanProgramTestData;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

import static org.junit.jupiter.api.Assertions.*;

// all test are repeated as the program run in muti thread
// one time run might cause coincident pass
// TODO: load language part only
class WenyanProgramBasicTest extends WenyanProgramTestHelper {

    @SuppressWarnings("unused")
    // @Test
    void testNormal() throws WenyanException, InterruptedException {
        String code = """
                吾有一術。名之曰「甲」。是術曰
                  加二以一
                是謂「甲」之術也。
                
                恆為是
                施「甲」
                云云
                """;
//        String code = """
//                有數一名之曰「a」
//                恆為是
//                加「a」以一名之曰「a」
//                云云
//                """;
        TestPlatform testPlatform = new TestPlatform();
        IWenyanProgram wenyanProgram = new WenyanProgramImpl(testPlatform);
        wenyanProgram.create(WenyanRunner.of(WenyanRuntime.ofCode(code), testPlatform.initEnvironment()));
        long start = System.nanoTime();
        wenyanProgram.step(1000000000);
        while (wenyanProgram.isRunning()) {
            //noinspection BusyWait
            Thread.sleep(20);
        }
        long t = System.nanoTime() - start;
        System.out.println(t);
        System.out.println(testPlatform.error);
        System.out.println(testPlatform.output);
//        while (wenyanProgram.isRunning()) {
//            wenyanProgram.step(10000000);
//            testPlatform.handle(IHandleContext.NONE);
//            //noinspection BusyWait
//            Thread.sleep(20);
//        }
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
}
