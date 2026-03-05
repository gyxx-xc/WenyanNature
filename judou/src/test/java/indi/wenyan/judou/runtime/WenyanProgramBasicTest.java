package indi.wenyan.judou.runtime;

import indi.wenyan.judou.runtime.test_utils.TestPlatform;
import indi.wenyan.judou.runtime.test_utils.generated_WenyanProgramTestData;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

import static org.junit.jupiter.api.Assertions.*;

// all test are repeated as the program run in muti thread
// one time run might cause coincident pass
// TODO: load language part only
class WenyanProgramBasicTest extends WenyanProgramTestHelper {

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

}
