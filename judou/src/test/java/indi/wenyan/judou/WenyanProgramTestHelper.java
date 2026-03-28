package indi.wenyan.judou;

import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.exec_interface.structure.IHandleContext;
import indi.wenyan.judou.runtime.IWenyanProgram;
import indi.wenyan.judou.runtime.function_impl.RunnerCreater;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.runtime.function_impl.WenyanProgramImpl;
import indi.wenyan.judou.structure.WenyanCompileException;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.test_utils.TestPlatform;
import indi.wenyan.judou.utils.ConfigManager;
import indi.wenyan.judou.utils.IConfigProvider;
import indi.wenyan.judou.utils.LoggerManager;
import indi.wenyan.judou.utils.WenyanValues;
import indi.wenyan.judou.utils.language.LanguageManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.provider.Arguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WenyanProgramTestHelper {

    @BeforeAll
    static void init() {
        try {
            LanguageManager.registerLanguageProvider(s -> s);
            LoggerManager.registerLogger(LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME));
            ConfigManager.registerConfigProvider(new IConfigProvider() {
                @Override
                public int getMaxThread() {
                    return 10;
                }

                @Override
                public int getMaxSlice() {
                    return 1000;
                }

                @Override
                public int getWatchdogTimeout() {
                    return 10;
                }

                @Override
                public int getResultMaxSize() {
                    return 64;
                }
            });
        } catch (IllegalStateException _) {
        }
    }


    protected static Arguments resultArgs(String code, Object... output) {
        return Arguments.of(code, output);
    }

    protected static IWenyanValue wenyanValueFromObject(Object o) {
        return switch (o) {
            case null -> WenyanNull.NULL;
            case Integer i -> WenyanValues.of(i);
            case Long l -> WenyanValues.of(l);
            case Boolean b -> WenyanValues.of(b);
            case Float f -> WenyanValues.of(f);
            case Double d -> WenyanValues.of(d);
            case String s -> WenyanValues.of(s);
            case List<?> l ->
                    WenyanValues.of(l.stream().map(WenyanProgramTestHelper::wenyanValueFromObject).toList());
            default -> throw new IllegalArgumentException("unsupported type: " + o.getClass());
        };
    }

    protected void assertResult(String code, Object... output) throws WenyanException {
        TestPlatform testPlatform = new TestPlatform();
        assertDoesNotThrow(() -> createAndRun(code, testPlatform));
        assertNull(testPlatform.error);
        assertEquals(output.length, testPlatform.output.size(), Arrays.toString(output) + testPlatform.output);
        for (int i = 0; i < output.length; i++) {
            assertTrue(IWenyanValue.equals(WenyanProgramTestHelper.wenyanValueFromObject(output[i]), testPlatform.output.get(i)),
                    Arrays.toString(output) + " and " + testPlatform.output.toString() + " differ at " + i + "\n" +
                            output[i] + " and " + testPlatform.output.get(i));
        }
    }

    protected void assertCompileError(String code) {
        TestPlatform testPlatform = new TestPlatform();
        assertThrows(WenyanCompileException.class, () -> createAndRun(code, testPlatform));
    }

    protected void assertRuntimeError(String code) {
        TestPlatform testPlatform = new TestPlatform();
        assertDoesNotThrow(() -> createAndRun(code, testPlatform));
        assertNotNull(testPlatform.error, code);
    }

    protected void createAndRun(String code, IWenyanPlatform testPlatform) throws WenyanException, InterruptedException {
        IWenyanProgram<WenyanProgramImpl.PCB> wenyanProgram = new WenyanProgramImpl(testPlatform, 8000);
        wenyanProgram.create(RunnerCreater.newRunner(WenyanFrame.ofCode(code), testPlatform.initEnvironment()));
        while (wenyanProgram.isRunning()) {
            wenyanProgram.step();
            testPlatform.handle(IHandleContext.NONE);
            //noinspection BusyWait
            Thread.sleep(20);
        }
    }
}
