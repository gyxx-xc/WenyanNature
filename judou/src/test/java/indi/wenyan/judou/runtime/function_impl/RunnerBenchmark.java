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
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class RunnerBenchmark {

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

    @Test
    public void test() {
        String code = """
                恆為是云云
                """;
        IThreadHolder<NoScheProgram.SimpleThread> runner = RunnerCreater.newRunner(WenyanFrame.ofCode(code), WenyanPackages.WENYAN_BASIC_PACKAGES);
        var prog = new NoScheProgram();
        runner.setThread(prog.getThread());
        runner.run(1_000_000_000);
    }
}
