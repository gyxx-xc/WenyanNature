package indi.wenyan.test.content.additional_module.block;

import indi.wenyan.content.block.additional_module.block.LockModuleBlock;
import indi.wenyan.content.block.runner.RunnerBlockEntity;
import indi.wenyan.setup.definitions.WYRegistration;
import indi.wenyan.test.utils.RunnerTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

@ForEachTest(groups = "content.block.lock_module")
public class LockModuleBlockTest {
    @GameTest
    @TestHolder(description = "Tests that the lock module works correctly.")
    public static void lockModuleTest(final DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 2, 1)
                .set(0, 0, 0, WYRegistration.LOCK_MODULE_BLOCK.get().defaultBlockState())
                .set(0, 1, 0, WYRegistration.RUNNER_BLOCK.get().defaultBlockState())
        );

        test.onGameTest(RunnerTestHelper.class, helper -> {
            final BlockPos lockPos = BlockPos.ZERO.offset(0, 1, 0);
            final RunnerBlockEntity runner1 = helper.getBlockEntity(
                    BlockPos.ZERO.offset(0, 2, 0));
            helper.startSequence()
                    .thenExecute(() -> {
                        helper.assertBlockProperty(lockPos,
                                LockModuleBlock.LOCK_STATE, false);
                        runner1.newThread("施「獲取」為是五遍書一云云施「釋放」");
                        runner1.newThread("施「獲取」為是五遍書二云云施「釋放」");
                    })
                    .thenIdle(10) // wait for sometime before check for better check
                    .thenWaitUntil(() -> {
                        helper.assertBlockProperty(lockPos,
                                LockModuleBlock.LOCK_STATE, false);
                        helper.assertOutput(runner1, "output",
                                "一", "一", "一", "一", "一", "二", "二", "二", "二", "二");
                    })
                    .thenSucceed();
        });
    }
}
