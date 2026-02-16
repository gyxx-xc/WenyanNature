package indi.wenyan.judou.runtime;

import indi.wenyan.judou.exec_interface.structure.IHandleContext;
import indi.wenyan.judou.runtime.function_impl.WenyanProgramImpl;
import indi.wenyan.judou.runtime.test_utils.TestPlatform;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanThrowException;
import indi.wenyan.judou.utils.LanguageManager;
import indi.wenyan.judou.utils.LoggerManager;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class WenyanProgramImplTest {

    private TestPlatform platform;
    private WenyanProgramImpl program;

    static {
        LoggerManager.registerLogger(LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME));
        LanguageManager.registerLanguageProvider(s -> s);
    }

    private static abstract class TestRunner implements IThreadHolder<WenyanProgramImpl.PCB> {
        @Getter
        @Setter
        WenyanProgramImpl.PCB thread;
        int pc = 0;
        int totalPc;

        TestRunner(int totalPc) {
            this.totalPc = totalPc;
        }

        @Override
        public void run(int step) {
            try {
                for (int i = 0; i < step; i++) {
                    if (pc++ >= totalPc) {
                        program().die(this);
                        return;
                    }
                    program().consumeStep(this, 1);
                    bytecodeRun();
                }
                program().yield(this);
            } catch (WenyanThrowException e) {
                try {
                    program().getPlatform().handleError(e.getMessage());
                    program().die(this);
                } catch (WenyanException ex) {
                    program().getPlatform().handleError(ex.getMessage());
                }
            } catch (ReturnException ignore) {
                return;
            }
        }

        @Override
        public void pause() {
        }

        protected void bytecodeRun() throws WenyanException, ReturnException {
        }

        static class ReturnException extends Exception {
        }
    }

    private int runUntilDone(int step) throws InterruptedException {
        int tickCnt = 0;
        while (program.isRunning()) {
            tickCnt++;
            program.step(step);
            platform.handle(IHandleContext.NONE);
            Thread.sleep(20);
        }
        return tickCnt;
    }

    @BeforeEach
    void setUp() {
        platform = new TestPlatform();
        program = new WenyanProgramImpl(platform);
    }

    @Test
    void run() throws WenyanException, ExecutionException, InterruptedException {
        program.create(new TestRunner(100) {
        });
        assertEquals(10, runUntilDone(10));
        assertNull(platform.error);
    }

    @Nested
    class TestStep {
        @Test
        void step_singleThread_succcess() throws WenyanException, InterruptedException {
            program.create(new TestRunner(10) {
            });
            assertEquals(1, runUntilDone(1000));
            assertNull(platform.error);
        }

        @Test
        void step_largeThread_success() throws WenyanException, InterruptedException {
            program.create(new TestRunner(5000) {
            });
            program.step(5000);
            Thread.sleep(20);
            assertFalse(program.isRunning());
            assertNull(platform.error);
        }

        @Test
        void step_mutiThread_succcess() throws WenyanException, InterruptedException {
            // test multi-thread
            program.create(new TestRunner(5000) {
            });
            program.create(new TestRunner(5000) {
            });
            program.create(new TestRunner(5000) {
            });
            program.step(15000);
            Thread.sleep(20);
            assertFalse(program.isRunning());
            assertNull(platform.error);
        }

        @Test
        void step_negative_throwError() {
            assertThrows(IllegalArgumentException.class, () -> program.step(-1));
            assertThrows(IllegalArgumentException.class, () -> program.step(0));
        }

        @Test
        void step_emptyThread_noBehavior() {
            // test step with empty program
            for (int i = 0; i < 100; i++)
                program.step(1000);
            assertNull(platform.error);
        }

        @Test
        void step_slowLongThread_warningAndStop() throws WenyanException, InterruptedException {
            program.create(new TestRunner(5000) {
                @Override
                protected void bytecodeRun() throws WenyanException, ReturnException {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new WenyanException(e.getMessage());
                    }
                }
            });
            program.step(1000);
            Thread.sleep(10);
            assertFalse(program.isRunning());
        }

        @Test
        void step_slowShortThread_warningAndStop() throws WenyanException, InterruptedException {
            program.create(new TestRunner(1) {
                @Override
                protected void bytecodeRun() throws WenyanException, ReturnException {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new WenyanException(e.getMessage());
                    }
                }
            });
            program.step(1000);
            Thread.sleep(20);
            assertFalse(program.isRunning());
        }

        @Test
        void step_fastTick_warning() throws WenyanException, InterruptedException {
            program.create(new TestRunner(2) {
                @Override
                protected void bytecodeRun() throws WenyanException, ReturnException {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new WenyanException(e.getMessage());
                    }
                }
            });
            program.step(1000);
            Thread.sleep(1);
            program.step(1000);
            Thread.sleep(1);
            program.step(1000);
            Thread.sleep(20);
            assertFalse(program.isRunning());
        }
    }

    @Test
    void isRunning() throws WenyanException, InterruptedException {
        assertFalse(program.isRunning());
        program.create(new TestRunner(10) {
        });
        assertTrue(program.isRunning());
        program.step(1000);
        Thread.sleep(100);
        assertFalse(program.isRunning());
        program.create(new TestRunner(10) {
        });
        assertTrue(program.isRunning());
        program.stop();
        assertFalse(program.isRunning());
    }

    @Nested
    class TestBlock {
        @Test
        void block_singleThread_success() throws WenyanException, InterruptedException {
            var runner = new TestRunner(1) {
                @Override
                protected void bytecodeRun() throws WenyanException, ReturnException {
                    program().block(this);
                    throw new ReturnException();
                }
            };
            program.create(runner);
            program.step(10);
            Thread.sleep(20);
            assertTrue(program.isRunning());
            assertTrue(program.isIdle());
            program.unblock(runner);
            program.step(10); // assert no logger output
            Thread.sleep(20);
            assertFalse(program.isRunning());
        }

        @Test
        void block_multiThread_success() throws WenyanException, InterruptedException {
            var runner1 = new TestRunner(1) {
                @Override
                protected void bytecodeRun() throws WenyanException, ReturnException {
                    program().block(this);
                    throw new ReturnException();
                }
            };
            var runner2 = new TestRunner(1) {
                @Override
                protected void bytecodeRun() throws WenyanException, ReturnException {
                    program().block(this);
                    throw new ReturnException();
                }
            };
            program.create(runner1);
            program.create(runner2);
            program.step(10);
            Thread.sleep(20);
            assertEquals(1, runner1.pc);
            assertEquals(1, runner2.pc);
            assertTrue(program.isRunning());
            assertTrue(program.isIdle());
            program.unblock(runner1);
            program.step(10);
            Thread.sleep(20);
            assertEquals(2, runner1.pc);
            assertEquals(1, runner2.pc);
            assertTrue(program.isRunning());
            program.unblock(runner2);
            program.step(10);
            Thread.sleep(20);
            assertEquals(2, runner1.pc);
            assertEquals(2, runner2.pc);
            assertFalse(program.isRunning());
        }

        @Test
        void block_redundentCall_fail() throws WenyanException, InterruptedException {
            var runner = new TestRunner(1) {
                @Override
                protected void bytecodeRun() throws WenyanException, ReturnException {
                    program().block(this);
                    program().block(this);
                    throw new ReturnException();
                }
            };
            program.create(runner);
            program.step(10);
            Thread.sleep(20);
            assertFalse(program.isRunning());
            assertNotNull(platform.error);
        }
    }

    @Nested
    class TestUnblock {
        @Test
        void unblock_normal_success() throws WenyanException, InterruptedException {
            var runner = new TestRunner(2) {
                @Override
                protected void bytecodeRun() throws WenyanException, ReturnException {
                    program().block(this);
                    throw new ReturnException();
                }
            };
            program.create(runner);
            program.step(10);
            Thread.sleep(20);
            assertTrue(program.isRunning());
            assertTrue(program.isIdle());
            assertEquals(1, runner.pc);
            program.unblock(runner);
            program.step(10);
            Thread.sleep(20);
            assertTrue(program.isRunning());
            assertTrue(program.isIdle());
            assertEquals(2, runner.pc);
            program.unblock(runner);
            program.step(10);
            Thread.sleep(20);
            assertFalse(program.isRunning());
        }

        @Test
        void unblock_runningThread_fail() throws WenyanException, InterruptedException {
            var runner = new TestRunner(1) {
                @Override
                protected void bytecodeRun() throws WenyanException, ReturnException {
                    program().unblock(this);
                }
            };
            program.create(runner);
            program.step(10);
            Thread.sleep(20);
            assertFalse(program.isRunning());
            assertNotNull(platform.error);
        }
    }

    @Nested
    class TestYield {
        @Test
        void yield_normal_success() throws InterruptedException, WenyanException {
            var runner = new TestRunner(10) {
                @Override
                protected void bytecodeRun() throws WenyanException, ReturnException {
                    program().yield(this);
                    throw new ReturnException();
                }
            };
            program.create(runner);
            program.step(5);
            Thread.sleep(20);
            assertEquals(6, runner.pc);
            program.step(15);
            Thread.sleep(20);
            assertEquals(11, runner.pc);
        }

        @Test
        void yield_mutiThreaad_success() throws WenyanException, InterruptedException {
            var runner = new TestRunner(10) {
                @Override
                protected void bytecodeRun() throws WenyanException, ReturnException {
                    program().yield(this);
                    throw new ReturnException();
                }
            };
            var runner1 = new TestRunner(100) {
            };
            program.create(runner);
            program.create(runner1);
            program.step(5);
            Thread.sleep(20);
            assertEquals(1, runner.pc);
            assertEquals(5, runner1.pc);
            program.step(150);
            Thread.sleep(20);
            assertEquals(11, runner.pc);
            assertEquals(101, runner1.pc);
        }

        @Test
        void yield_blocked_fail() throws InterruptedException, WenyanException {
            var runner = new TestRunner(10) {
                @Override
                protected void bytecodeRun() throws WenyanException, ReturnException {
                    program().block(this);
                    throw new ReturnException();
                }
            };
            program.create(runner);
            program.step(5);
            Thread.sleep(20);
            assertThrows(WenyanException.WenyanUnreachedException.class, () -> program.yield(runner));
            program.stop();
        }
    }

    @Nested
    class TestDie {
        @Test
        void die_normal_success() throws InterruptedException, WenyanException {
            var runner = new TestRunner(10) {
                @Override
                protected void bytecodeRun() throws WenyanException, ReturnException {
                    if (pc > 6) {
                        program().die(this);
                        throw new ReturnException();
                    }
                }
            };
            program.create(runner);
            assertEquals(4, runUntilDone(2));
            assertEquals(7, runner.pc);
        }

        @Test
        void die_dyingThreaad_error() throws InterruptedException, WenyanException {
            var runner = new TestRunner(10) {
                @Override
                protected void bytecodeRun() throws WenyanException, ReturnException {
                    if (pc > 6) {
                        program().die(this);
                        throw new ReturnException();
                    }
                }
            };
            program.create(runner);
            assertEquals(4, runUntilDone(2));
            assertThrows(WenyanException.WenyanUnreachedException.class, () -> program.die(runner));
        }
    }

    @Test
    void stop() throws WenyanException, InterruptedException, NoSuchFieldException, IllegalAccessException {
        program.create(new TestRunner(10){});
        program.step(5);
        Thread.sleep(20);
        program.stop();

        Class obj = program.getClass();
        Field execField = obj.getDeclaredField("executor");
        execField.setAccessible(true);
        ExecutorService exec = (ExecutorService) execField.get(program);
        assertTrue(exec.awaitTermination(100, TimeUnit.MICROSECONDS));
    }

    @Test
    void testPCBEquals() {
        var runner = new TestRunner(0) {
        };
        var pcb1 = new WenyanProgramImpl.PCB(runner, program);
        var pcb2 = new WenyanProgramImpl.PCB(runner, program);
        assertEquals(pcb1, pcb1);
        assertEquals(pcb1.hashCode(), pcb1.hashCode());
        assertNotEquals(pcb1, pcb2);
        assertNotEquals(pcb1.hashCode(), pcb2.hashCode());
        pcb1 = new WenyanProgramImpl.PCB(null, null);
        pcb2 = new WenyanProgramImpl.PCB(null, null);
        pcb1.setWatchdog(null);
        pcb2.setWatchdog(null);
        assertNotEquals(pcb1, pcb2);
        assertNotEquals(pcb1.hashCode(), pcb2.hashCode());
        pcb2 = pcb1;
        assertEquals(pcb1, pcb2);
        assertEquals(pcb1.hashCode(), pcb2.hashCode());
        pcb1 = new WenyanProgramImpl.PCB(null, null);
        assertNotEquals(pcb1, pcb2);
        assertNotEquals(pcb1.hashCode(), pcb2.hashCode());
    }
}