package indi.wenyan.judou.runtime;

import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.exec_interface.structure.IHandleContext;
import indi.wenyan.judou.runtime.utils.TestPlatform;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanThrowException;
import indi.wenyan.judou.utils.LanguageManager;
import indi.wenyan.judou.utils.LoggerManager;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WenyanProgramImplTest {

    private IWenyanPlatform platform = new TestPlatform();
    private WenyanProgramImpl program = new WenyanProgramImpl(platform);

    static {
        LoggerManager.registerLogger(LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME));
        LanguageManager.registerLanguageProvider(s -> s);
    }

    private abstract class TestRunner implements IBytecodeRunner, IThreadHolder<WenyanProgramImpl.PCB> {
        @Getter
        @Setter
        WenyanProgramImpl.PCB thread;
        int pc = 0;
        int totalPc;

        TestRunner(int totalPc) {
            this.totalPc = totalPc;
        }

        @Override
        public void run(int step) throws WenyanThrowException {
            for (int i = 0; i < step; i++) {
                program.consumeStep(this, 1);
                pc++;
                if (pc > totalPc) {
                    program.die(this);
                    return;
                }
                bytecodeRun();
            }
            program.yield(this);
        }

        abstract void bytecodeRun();
    }

    @Test
    void run() throws WenyanException, ExecutionException, InterruptedException {
        program.create(new TestRunner(100) {
            @Override
            void bytecodeRun() {
            }
        });
        int tickCnt = 0;
        while (program.isRunning()) {
            tickCnt++;
            program.step(1000);
            platform.handle(IHandleContext.NONE);
            Thread.sleep(20);
        }
        assertEquals(1, tickCnt);

        program.create(new TestRunner(100) {
            @Override
            void bytecodeRun() {}
        });
        tickCnt = 0;
        while (program.isRunning()) {
            tickCnt ++;
            program.step(10);
            platform.handle(IHandleContext.NONE);
            Thread.sleep(20);
        }
        assertEquals(11, tickCnt);
    }

    @Test
    void isRunning() {
    }

    @Test
    void block() {
    }

    @Test
    void unblock() {
    }

    @Test
    void yield() {
    }

    @Test
    void die() {
    }

    @Test
    void dieWithException() {
    }

    @Test
    void stop() {
    }
}