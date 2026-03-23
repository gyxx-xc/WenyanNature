package indi.wenyan.test.content.additional_module.block;

import indi.wenyan.content.block.runner.RunnerBlock;
import indi.wenyan.content.block.runner.RunnerBlockEntity;
import indi.wenyan.setup.definitions.RunnerTier;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.test.utils.RunnerTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.GameTest;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;
import org.jspecify.annotations.NonNull;

@ForEachTest(groups = "content.block.formation_module")
public class FormationCoreModuleBlockTest {
    @GameTest
    @TestHolder(description = "Tests that the formation module works correctly.")
    public static void formationModuleStartTest(final @NonNull DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 6, 1)
                .set(0, 0, 0, WenyanBlocks.FORMATION_CORE_MODULE_BLOCK.get().defaultBlockState())
                .set(0, 1, 0, WenyanBlocks.RUNNER_BLOCK.getBlock(RunnerTier.RUNNER_3).defaultBlockState())
                .set(0, 2, 0, Blocks.STONE.defaultBlockState())
                .set(0, 3, 0, WenyanBlocks.RUNNER_BLOCK.getBlock(RunnerTier.RUNNER_3).defaultBlockState())
                .set(0, 4, 0, Blocks.STONE.defaultBlockState())
                .set(0, 5, 0, WenyanBlocks.RUNNER_BLOCK.getBlock(RunnerTier.RUNNER_3).defaultBlockState())
        );

        test.onGameTest(RunnerTestHelper.class, helper -> {
            var runner1 = helper.getBlockEntity(0, 1, 0, RunnerBlockEntity.class);
            var runner2 = helper.getBlockEntity(0, 3, 0, RunnerBlockEntity.class);
            var runner3 = helper.getBlockEntity(0, 5, 0, RunnerBlockEntity.class);
            runner2.setPlatformName("「a1」");
            runner2.setCode("待十");
            runner3.setPlatformName("「b1」");
            runner3.setCode("待五");
            helper.startSequence()
                    .thenExecute(() -> runner1.newThread("施「啓」以「「a1」」以「「b1」」"))
                    .thenIdle(2)
                    .thenExecute(() -> {
                        helper.assertTrue(runner2.isRunning(), "runner2 is not running.");
                        helper.assertTrue(runner3.isRunning(), "runner3 is not running.");
                    })
                    .thenIdle(7)
                    .thenExecute(() -> {
                        helper.assertBlockState(BlockPos.containing(0,1,0),
                                state -> state.getValue(RunnerBlock.RUNNING_STATE) == RunnerBlock.RunningState.NOT_RUNNING,
                                blockState -> Component.literal(blockState + " is running."));
                        helper.assertBlockState(BlockPos.containing(0,3,0),
                                state -> state.getValue(RunnerBlock.RUNNING_STATE) == RunnerBlock.RunningState.IDLE,
                                blockState -> Component.literal(blockState + " is not idle."));
                        helper.assertBlockState(BlockPos.containing(0,5,0),
                                state -> state.getValue(RunnerBlock.RUNNING_STATE) == RunnerBlock.RunningState.NOT_RUNNING,
                                blockState -> Component.literal(blockState + " is running."));
                    })
                    .thenIdle(6)
                    .thenExecute(() -> {
                        helper.assertBlockState(BlockPos.containing(0,1,0),
                                state -> state.getValue(RunnerBlock.RUNNING_STATE) == RunnerBlock.RunningState.NOT_RUNNING,
                                blockState -> Component.literal(blockState + " is running."));
                        helper.assertBlockState(BlockPos.containing(0,3,0),
                                state -> state.getValue(RunnerBlock.RUNNING_STATE) == RunnerBlock.RunningState.NOT_RUNNING,
                                blockState -> Component.literal(blockState + " is running."));
                        helper.assertBlockState(BlockPos.containing(0,5,0),
                                state -> state.getValue(RunnerBlock.RUNNING_STATE) == RunnerBlock.RunningState.NOT_RUNNING,
                                blockState -> Component.literal(blockState + " is running."));
                    })
                    .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Tests that the formation module works correctly.")
    public static void formationModuleJoinTest(final @NonNull DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 6, 1)
                .set(0, 0, 0, WenyanBlocks.FORMATION_CORE_MODULE_BLOCK.get().defaultBlockState())
                .set(0, 1, 0, WenyanBlocks.RUNNER_BLOCK.getBlock(RunnerTier.RUNNER_3).defaultBlockState())
                .set(0, 2, 0, Blocks.STONE.defaultBlockState())
                .set(0, 3, 0, WenyanBlocks.RUNNER_BLOCK.getBlock(RunnerTier.RUNNER_3).defaultBlockState())
                .set(0, 4, 0, Blocks.STONE.defaultBlockState())
                .set(0, 5, 0, WenyanBlocks.RUNNER_BLOCK.getBlock(RunnerTier.RUNNER_3).defaultBlockState())
        );

        test.onGameTest(RunnerTestHelper.class, helper -> {
            var runner1 = helper.getBlockEntity(0, 1, 0, RunnerBlockEntity.class);
            var runner2 = helper.getBlockEntity(0, 3, 0, RunnerBlockEntity.class);
            var runner3 = helper.getBlockEntity(0, 5, 0, RunnerBlockEntity.class);
            runner2.setPlatformName("「a」");
            runner2.setCode("待十");
            runner3.setPlatformName("「b」");
            runner3.setCode("待五");
            helper.startSequence()
                    .thenExecute(() -> runner1.newThread("施「啓」以「「a」」以「「b」」施「歸」"))
                    .thenIdle(2)
                    .thenExecute(() -> {
                        helper.assertTrue(runner2.isRunning(), "runner2 is not running.");
                        helper.assertTrue(runner3.isRunning(), "runner3 is not running.");
                    })
                    .thenIdle(7)
                    .thenExecute(() -> {
                        helper.assertBlockState(BlockPos.containing(0,1,0),
                                state -> state.getValue(RunnerBlock.RUNNING_STATE) == RunnerBlock.RunningState.IDLE,
                                blockState -> Component.literal(blockState + " is not idle."));
                        helper.assertBlockState(BlockPos.containing(0,3,0),
                                state -> state.getValue(RunnerBlock.RUNNING_STATE) == RunnerBlock.RunningState.IDLE,
                                blockState -> Component.literal(blockState + " is not idle."));
                        helper.assertBlockState(BlockPos.containing(0,5,0),
                                state -> state.getValue(RunnerBlock.RUNNING_STATE) == RunnerBlock.RunningState.NOT_RUNNING,
                                blockState -> Component.literal(blockState + " is running."));
                    })
                    .thenIdle(6)
                    .thenExecute(() -> {
                        helper.assertBlockState(BlockPos.containing(0,1,0),
                                state -> state.getValue(RunnerBlock.RUNNING_STATE) == RunnerBlock.RunningState.NOT_RUNNING,
                                blockState -> Component.literal(blockState + " is running."));
                        helper.assertBlockState(BlockPos.containing(0,3,0),
                                state -> state.getValue(RunnerBlock.RUNNING_STATE) == RunnerBlock.RunningState.NOT_RUNNING,
                                blockState -> Component.literal(blockState + " is running."));
                        helper.assertBlockState(BlockPos.containing(0,5,0),
                                state -> state.getValue(RunnerBlock.RUNNING_STATE) == RunnerBlock.RunningState.NOT_RUNNING,
                                blockState -> Component.literal(blockState + " is running."));
                    })
                    .thenSucceed();
        });
    }
}
