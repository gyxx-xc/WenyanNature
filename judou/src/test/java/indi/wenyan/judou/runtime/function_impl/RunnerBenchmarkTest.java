package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.api.compile.IWenyanBytecode;
import indi.wenyan.judou.api.compile.WenyanCompiler;
import indi.wenyan.judou.test_utils.NoScheProgram;
import indi.wenyan.judou.utils.WenyanPackages;

import java.io.IOException;

@SuppressWarnings("unused")
public class RunnerBenchmarkTest {

//    @Test
    @SuppressWarnings({"unused", "RedundantThrows", "CommentedOutCode"})
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
        IWenyanBytecode bytecode = new WenyanCompiler().compile(code).bytecode();
        IThreadHolder<NoScheProgram.SimpleThread> runner = new WenyanSwitchInlineRunner<>(WenyanFrame.ofCode(bytecode), WenyanPackages.WENYAN_BASIC_PACKAGES);
        var prog = new NoScheProgram();
        runner.setThread(prog.getThread());

//        AsyncProfiler profiler = AsyncProfiler.getInstance();
//        profiler.execute("start,jfr,event=cpu,file=%p.jfr");
        for (int i = 0; i < 10; i++)
            runner.run(1_000_000_000);
//        profiler.execute("stop");
    }
}
