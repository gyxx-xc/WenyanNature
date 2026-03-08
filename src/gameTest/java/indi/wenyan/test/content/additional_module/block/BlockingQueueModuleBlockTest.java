package indi.wenyan.test.content.additional_module.block;

import indi.wenyan.content.block.additional_module.paper.BlockingQueueModuleEntity;
import indi.wenyan.content.block.runner.RunnerBlockEntity;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.test.utils.RunnerTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.GameTest;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

import java.util.function.Supplier;

@ForEachTest(groups = "content.block.blocking_queue")
public class BlockingQueueModuleBlockTest {

    public static final Supplier<StructureTemplateBuilder> STRUCTURE_TEMPLATE_BUILDER = () -> StructureTemplateBuilder.withSize(2, 2, 1)
            .set(0, 0, 0, Blocks.STONE.defaultBlockState())
            .set(0, 1, 0, WenyanBlocks.RUNNER_BLOCK_2.get().defaultBlockState())
            .set(1, 0, 0, Blocks.STONE.defaultBlockState())
            .set(1, 1, 0, WenyanBlocks.BLOCKING_QUEUE_MODULE_BLOCK.get().defaultBlockState());

    @GameTest
    @TestHolder(description = "Tests basic put and take operations")
    public static void basicPutTakeTest(final DynamicTest test) {
        test.registerGameTestTemplate(STRUCTURE_TEMPLATE_BUILDER);

        test.onGameTest(RunnerTestHelper.class, helper -> {
            final RunnerBlockEntity runner = helper.getBlockEntity(
                    BlockPos.ZERO.offset(0, 1, 0), RunnerBlockEntity.class);
            BlockingQueueModuleEntity device = helper.getBlockEntity(BlockPos.containing(1, 1, 0), BlockingQueueModuleEntity.class);
            String order = "「1」";
            device.setPackageName(order);
            String importCommand = "吾嘗觀"+order+"之書方悟「長」「試取」「入」「試入」「窺」「取」「清空」之義";
            helper.startSequence()
                    .thenExecute(() -> runner.newThread(importCommand+"施「入」以一施「取」書之"))
                    .thenIdle(10)
                    .thenWaitUntil(() -> helper.assertOutput(runner, "output", "一"))
                    .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Tests offer and poll operations (non-blocking)")
    public static void offerPollTest(final DynamicTest test) {
        test.registerGameTestTemplate(STRUCTURE_TEMPLATE_BUILDER);
        test.onGameTest(RunnerTestHelper.class, helper -> {
            final RunnerBlockEntity runner = helper.getBlockEntity(
                    BlockPos.ZERO.offset(0, 1, 0), RunnerBlockEntity.class);
            BlockingQueueModuleEntity device = helper.getBlockEntity(BlockPos.containing(1, 1, 0), BlockingQueueModuleEntity.class);
            String order = "「2」";
            device.setPackageName(order);
            String importCommand = "吾嘗觀"+order+"之書方悟「長」「試取」「入」「試入」「窺」「取」「清空」之義";
            helper.startSequence()
                    .thenExecute(() -> {
                        // Test offer returns true when successful
                        runner.newThread(importCommand+"施「試入」以一書之施「試取」書之夫「「null」」施「試取」書之");
                    })
                    .thenIdle(10)
                    .thenWaitUntil(() -> helper.assertOutput(runner, "output", "陽", "一", "null"))
                    .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Tests peek operation")
    public static void peekTest(final DynamicTest test) {
        test.registerGameTestTemplate(STRUCTURE_TEMPLATE_BUILDER);
        test.onGameTest(RunnerTestHelper.class, helper -> {
            final RunnerBlockEntity runner = helper.getBlockEntity(
                    BlockPos.ZERO.offset(0, 1, 0), RunnerBlockEntity.class);
            BlockingQueueModuleEntity device = helper.getBlockEntity(BlockPos.containing(1, 1, 0), BlockingQueueModuleEntity.class);
            String order = "「3」";
            device.setPackageName(order);
            String importCommand = "吾嘗觀"+order+"之書方悟「長」「試取」「入」「試入」「窺」「取」「清空」之義";
            helper.startSequence()
                    .thenExecute(() -> {
                        // Put item then peek multiple times
                        runner.newThread(importCommand+"施「入」以一施「窺」書之噫施「窺」書之噫施「取」夫「「null」」施「窺」書之");
                    })
                    .thenIdle(10)
                    .thenWaitUntil(() -> helper.assertOutput(runner, "output", "一", "一", "null"))
                    .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Tests size operation")
    public static void sizeTest(final DynamicTest test) {
        test.registerGameTestTemplate(STRUCTURE_TEMPLATE_BUILDER);
        test.onGameTest(RunnerTestHelper.class, helper -> {
            final RunnerBlockEntity runner = helper.getBlockEntity(
                    BlockPos.ZERO.offset(0, 1, 0), RunnerBlockEntity.class);
            BlockingQueueModuleEntity device = helper.getBlockEntity(BlockPos.containing(1, 1, 0), BlockingQueueModuleEntity.class);
            String order = "「4」";
            device.setPackageName(order);
            String importCommand = "吾嘗觀"+order+"之書方悟「長」「試取」「入」「試入」「窺」「取」「清空」之義";
            helper.startSequence()
                    .thenExecute(() -> {
                        // Test size at different states
                        runner.newThread(importCommand+"施「長」書之施「入」以一施「入」以二噫施「長」書之施「取」噫施「長」書之");
                    })
                    .thenIdle(10)
                    .thenWaitUntil(() -> helper.assertOutput(runner, "output", "零", "二", "一"))
                    .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Tests clear operation")
    public static void clearTest(final DynamicTest test) {
        test.registerGameTestTemplate(STRUCTURE_TEMPLATE_BUILDER);
        test.onGameTest(RunnerTestHelper.class, helper -> {
            final RunnerBlockEntity runner = helper.getBlockEntity(
                    BlockPos.ZERO.offset(0, 1, 0), RunnerBlockEntity.class);
            BlockingQueueModuleEntity device = helper.getBlockEntity(BlockPos.containing(1, 1, 0), BlockingQueueModuleEntity.class);
            String order = "「5」";
            device.setPackageName(order);
            String importCommand = "吾嘗觀"+order+"之書方悟「長」「試取」「入」「試入」「窺」「取」「清空」之義";
            helper.startSequence()
                    .thenExecute(() -> {
                        // Put items then clear
                        runner.newThread(importCommand+"施「入」以一施「入」以二施「長」書之施「清空」施「長」書之");
                    })
                    .thenIdle(10)
                    .thenWaitUntil(() -> helper.assertOutput(runner, "output", "二", "零"))
                    .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Tests blocking behavior when queue is full")
    public static void blockingPutWhenFullTest(final DynamicTest test) {
        test.registerGameTestTemplate(STRUCTURE_TEMPLATE_BUILDER);
        test.onGameTest(RunnerTestHelper.class, helper -> {
            final RunnerBlockEntity runner1 = helper.getBlockEntity(
                    BlockPos.ZERO.offset(0, 1, 0), RunnerBlockEntity.class);
            BlockingQueueModuleEntity device = helper.getBlockEntity(BlockPos.containing(1, 1, 0), BlockingQueueModuleEntity.class);
            String order = "「6」";
            device.setPackageName(order);
            String importCommand = "吾嘗觀"+order+"之書方悟「長」「試取」「入」「試入」「窺」「取」「清空」之義";
            helper.startSequence()
                    .thenExecute(() -> {
                        // Fill the queue (capacity=10) and then block on put
                        // Thread 1: fill queue and block
                        runner1.newThread(importCommand+"為是一十遍施「入」以一云云書一施「入」以一書三");
                        // Thread 2: consume one item to unblock thread 1
                        runner1.newThread(importCommand+"待十書二施「取」");
                    })
                    .thenIdle(20)
                    .thenWaitUntil(() -> {
                        // Both threads should complete
                        helper.assertOutput(runner1, "output", "一", "二", "三");
                    })
                    .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Tests blocking behavior when queue is empty")
    public static void blockingTakeWhenEmptyTest(final DynamicTest test) {
        test.registerGameTestTemplate(STRUCTURE_TEMPLATE_BUILDER);
        test.onGameTest(RunnerTestHelper.class, helper -> {
            final RunnerBlockEntity runner1 = helper.getBlockEntity(
                    BlockPos.ZERO.offset(0, 1, 0), RunnerBlockEntity.class);
            BlockingQueueModuleEntity device = helper.getBlockEntity(BlockPos.containing(1, 1, 0), BlockingQueueModuleEntity.class);
            String order = "「7」";
            device.setPackageName(order);
            String importCommand = "吾嘗觀"+order+"之書方悟「長」「試取」「入」「試入」「窺」「取」「清空」之義";
            helper.startSequence()
                    .thenExecute(() -> {
                        // Thread 1: try to take from empty queue (will block)
                        runner1.newThread(importCommand+"施「取」書二");
                        // Thread 2: put item to unblock thread 1
                        runner1.newThread(importCommand+"待三書一施「入」以一");
                    })
                    .thenIdle(20)
                    .thenWaitUntil(() -> {
                        // Both threads should complete
                        helper.assertOutput(runner1, "output", "一", "二");
                    })
                    .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Tests producer-consumer pattern with multiple threads")
    public static void producerConsumerPatternTest(final DynamicTest test) {
        test.registerGameTestTemplate(STRUCTURE_TEMPLATE_BUILDER);
        test.onGameTest(RunnerTestHelper.class, helper -> {
            final RunnerBlockEntity producer1 = helper.getBlockEntity(
                    BlockPos.ZERO.offset(0, 1, 0), RunnerBlockEntity.class);
            BlockingQueueModuleEntity device = helper.getBlockEntity(BlockPos.containing(1, 1, 0), BlockingQueueModuleEntity.class);
            String order = "「8」";
            device.setPackageName(order);
            String importCommand = "吾嘗觀"+order+"之書方悟「長」「試取」「入」「試入」「窺」「取」「清空」之義";
            helper.startSequence()
                    .thenExecute(() -> {
                        // Two producers, one consumer
                        // Producer 1: produce 5 items
                        producer1.newThread(importCommand+"為是五遍施「入」以一云云書一");
                        // Producer 2: produce 5 items
                        producer1.newThread(importCommand+"為是五遍施「入」以一云云書一");
                        // Consumer: consume 10 items
                        producer1.newThread(importCommand+"為是一十遍施「取」云云書一");
                    })
                    .thenIdle(30)
                    .thenWaitUntil(() -> helper.assertOutput(producer1, "output", "一", "一", "一"))
                    .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Tests FIFO ordering of the queue")
    public static void fifoOrderTest(final DynamicTest test) {
        test.registerGameTestTemplate(STRUCTURE_TEMPLATE_BUILDER);
        test.onGameTest(RunnerTestHelper.class, helper -> {
            final RunnerBlockEntity runner = helper.getBlockEntity(
                    BlockPos.ZERO.offset(0, 1, 0), RunnerBlockEntity.class);
            BlockingQueueModuleEntity device = helper.getBlockEntity(BlockPos.containing(1, 1, 0), BlockingQueueModuleEntity.class);
            String order = "「9」";
            device.setPackageName(order);
            String importCommand = "吾嘗觀"+order+"之書方悟「長」「試取」「入」「試入」「窺」「取」「清空」之義";
            helper.startSequence()
                    .thenExecute(() -> {
                        // Put items in order and take them out
                        runner.newThread(importCommand+"施「入」以一施「入」以二施「入」以三施「取」書之施「取」書之施「取」書之");
                    })
                    .thenIdle(10)
                    .thenWaitUntil(() -> helper.assertOutput(runner, "output", "一", "二", "三"))
                    .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Tests offer returns false when queue is full")
    public static void offerWhenFullTest(final DynamicTest test) {
        test.registerGameTestTemplate(STRUCTURE_TEMPLATE_BUILDER);
        test.onGameTest(RunnerTestHelper.class, helper -> {
            final RunnerBlockEntity runner = helper.getBlockEntity(
                    BlockPos.ZERO.offset(0, 1, 0), RunnerBlockEntity.class);
            BlockingQueueModuleEntity device = helper.getBlockEntity(BlockPos.containing(1, 1, 0), BlockingQueueModuleEntity.class);
            String order = "「10」";
            device.setPackageName(order);
            String importCommand = "吾嘗觀"+order+"之書方悟「長」「試取」「入」「試入」「窺」「取」「清空」之義";
            helper.startSequence()
                    .thenExecute(() -> {
                        // Fill queue and test offer returns false
                        runner.newThread(importCommand+"為是一十遍施「入」以一云云施「試入」書之");
                    })
                    .thenIdle(10)
                    .thenWaitUntil(() -> helper.assertOutput(runner, "output", "陰"))
                    .thenSucceed();
        });
    }
}
