package indi.wenyan.client.gui.code_editor.llm;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LlmSessionTest {

    @Test
    void testCancelledRequestDoesNotCallBack() throws InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        BlockingClient client = new BlockingClient();
        LlmSession session = newTestSession(client, executor);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger errorCount = new AtomicInteger();

        session.generateCode("写一段代码", "", ignored -> successCount.incrementAndGet(), ignored -> errorCount.incrementAndGet());
        assertTrue(client.awaitStarted());

        session.clearHistory();
        client.release();
        Thread.sleep(100);

        assertEquals(0, successCount.get());
        assertEquals(0, errorCount.get());
        executor.shutdownNow();
    }

    @Test
    void testNextRequestSucceedsAfterCancel() throws InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        BlockingThenSuccessClient client = new BlockingThenSuccessClient();
        LlmSession session = newTestSession(client, executor);
        AtomicReference<String> result = new AtomicReference<>();
        CountDownLatch success = new CountDownLatch(1);

        session.generateCode("旧请求", "", ignored -> {
        }, ignored -> {
        });
        assertTrue(client.awaitStarted());
        session.clearHistory();
        client.release();

        session.generateCode("新请求", "", content -> {
            result.set(content);
            success.countDown();
        }, ignored -> {
        });

        assertTrue(success.await(2, TimeUnit.SECONDS));
        assertEquals("第二次结果", result.get());
        executor.shutdownNow();
    }

    @Test
    void testReasoningTierUsesReasoningModelSettings() throws InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CapturingClient client = new CapturingClient();
        LlmSession session = new LlmSession(client, executor, Runnable::run,
                (provider, modelTier, onError) -> Optional.of(new LlmSession.RequestSettings(
                        "key",
                        "https://example.invalid",
                        modelTier == LlmSession.ModelTier.REASONING ? "reasoning-model" : "normal-model")));
        CountDownLatch success = new CountDownLatch(1);

        session.toggleModelTier();
        session.generateCode("生成", "", ignored -> success.countDown(), ignored -> {
        });

        assertTrue(success.await(2, TimeUnit.SECONDS));
        assertEquals("reasoning-model", client.model.get());
        executor.shutdownNow();
    }

    private static LlmSession newTestSession(ILlmClient client, ExecutorService executor) {
        return new LlmSession(client, executor, Runnable::run,
                (provider, modelTier, onError) -> Optional.of(new LlmSession.RequestSettings("key", "https://example.invalid", "model")));
    }

    private static class BlockingClient implements ILlmClient {

        private final CountDownLatch started = new CountDownLatch(1);
        private final CountDownLatch release = new CountDownLatch(1);

        @Override
        public LlmResponse request(LlmRequest request) throws LlmException {
            started.countDown();
            try {
                release.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new LlmException("interrupted", e);
            }
            return new LlmResponse("旧请求结果");
        }

        boolean awaitStarted() throws InterruptedException {
            return started.await(2, TimeUnit.SECONDS);
        }

        void release() {
            release.countDown();
        }
    }

    private static class BlockingThenSuccessClient implements ILlmClient {

        private final AtomicInteger calls = new AtomicInteger();
        private final CountDownLatch firstStarted = new CountDownLatch(1);
        private final CountDownLatch releaseFirst = new CountDownLatch(1);

        @Override
        public LlmResponse request(LlmRequest request) throws LlmException {
            if (calls.incrementAndGet() == 1) {
                firstStarted.countDown();
                try {
                    releaseFirst.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new LlmException("interrupted", e);
                }
                return new LlmResponse("旧请求结果");
            }
            return new LlmResponse("第二次结果");
        }

        boolean awaitStarted() throws InterruptedException {
            return firstStarted.await(2, TimeUnit.SECONDS);
        }

        void release() {
            releaseFirst.countDown();
        }
    }

    private static class CapturingClient implements ILlmClient {

        private final AtomicReference<String> model = new AtomicReference<>();

        @Override
        public LlmResponse request(LlmRequest request) {
            model.set(request.model());
            return new LlmResponse("结果");
        }
    }
}
