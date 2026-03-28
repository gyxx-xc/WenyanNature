package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.runtime.IThreadHolder;
import indi.wenyan.judou.test_utils.NoScheProgram;
import indi.wenyan.judou.utils.function.WenyanPackages;

import java.io.IOException;

public class RunnerBenchmarkTest {

//    @Test
    @SuppressWarnings({"unused", "RedundantThrows"})
    public void benchmark() throws IOException {
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
        IThreadHolder<NoScheProgram.SimpleThread> runner = new WenyanSwitchInlineRunner<>(WenyanFrame.ofCode(code), WenyanPackages.WENYAN_BASIC_PACKAGES);
        var prog = new NoScheProgram();
        runner.setThread(prog.getThread());

//        AsyncProfiler profiler = AsyncProfiler.getInstance();
//        profiler.execute("start,jfr,event=cpu,file=%p.jfr");
        for (int i = 0; i < 10; i++)
            runner.run(1_000_000_000);
//        profiler.execute("stop");
    }
}
