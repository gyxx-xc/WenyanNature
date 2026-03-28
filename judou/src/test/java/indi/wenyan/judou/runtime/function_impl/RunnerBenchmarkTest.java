package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.runtime.IThreadHolder;
import indi.wenyan.judou.test_utils.NoScheProgram;
import indi.wenyan.judou.utils.ConfigManager;
import indi.wenyan.judou.utils.IConfigProvider;
import indi.wenyan.judou.utils.LoggerManager;
import indi.wenyan.judou.utils.WenyanPackages;
import indi.wenyan.judou.utils.language.ILanguageProvider;
import indi.wenyan.judou.utils.language.LanguageManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class RunnerBenchmarkTest {

    @BeforeAll
    static void init() {
        try {
            LanguageManager.registerLanguageProvider(new ILanguageProvider() {
                @Override
                public String getTranslation(String key) {
                    return key;
                }

                @Override
                public String getTranslation(String key, Object... args) {
                    return key + " " + Arrays.toString(args);
                }
            });
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

    @RepeatedTest(10)
    public void test() throws IOException {
        String code = """
                吾有一術。名之曰「a 」。
                欲行是術。必先得一數。曰「b 」。乃行是術曰。
                若「b 」大於一千者乃歸空無云云
                為是四遍
                加「b 」以一
                施「a 」以其云云
                是謂「a 」之術也
                
                施「a 」以零
                """;
        IThreadHolder<NoScheProgram.SimpleThread> runner = new WenyanRunner<>(WenyanFrame.ofCode(code), WenyanPackages.WENYAN_BASIC_PACKAGES);
        var prog = new NoScheProgram();
        runner.setThread(prog.getThread());
//        AsyncProfiler profiler = AsyncProfiler.getInstance();
//        profiler.execute("start,jfr,event=cpu,file=%p.jfr");
        runner.run(1_000_000_000);
//        profiler.execute("stop");
    }
}
