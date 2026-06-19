package indi.wenyan.judou.test_statement;

import indi.wenyan.judou.api.compile.IWenyanBytecode;
import indi.wenyan.judou.api.compile.WenyanCompiler;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.exception.WenyanException;
import indi.wenyan.judou.test_utils.TestPlatform;
import indi.wenyan.judou.test_utils.generated_WenyanProgramTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

// all test are repeated as the program run in muti thread
// one time run might cause coincident pass
class WenyanProgramBasicTest extends WenyanProgramTestHelper {

    @SuppressWarnings("ALL")
    @Test
    void testNormal() throws WenyanException, InterruptedException, IOException {
    }

    static void main() {
        String code = """
                        同有一術名之曰「a」。是術曰。
                        待一
                        是謂「a」之術也。
                        施「a」名之曰「a1」施「a」名之曰「a2」
                        待「a1」待「a2」
                """;
        IWenyanBytecode bytecode = new WenyanCompiler(true).compile(code).bytecode();
        System.out.println(bytecode);
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
                    testData.output() + " and " + testPlatform.output + " differ at " + i + "\n" +
                            testData.output().get(i) + " and " + testPlatform.output.get(i));
        }
    }
}
