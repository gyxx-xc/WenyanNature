package indi.wenyan.test.content.additional_module.block;

import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.test.utils.RunnerTestHelper;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.GameTest;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;
import org.jspecify.annotations.NonNull;

@ForEachTest(groups = "content.block.formation_module")
public class FormationCoreModuleBlockTest {
    @GameTest
    @TestHolder(description = "Tests that the lock module works correctly.")
    public static void lockModuleTest(final @NonNull DynamicTest test) {
        test.registerGameTestTemplate(() -> StructureTemplateBuilder.withSize(1, 2, 1)
                .set(0, 0, 0, WenyanBlocks.FORMATION_CORE_MODULE_BLOCK.get().defaultBlockState())
                .set(0, 1, 0, WenyanBlocks.RUNNER_BLOCK_3.get().defaultBlockState())
        );

        test.onGameTest(RunnerTestHelper.class, helper -> {
        });
    }

}
