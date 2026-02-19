package indi.wenyan.test.content.additional_module.block;

import indi.wenyan.content.block.additional_module.block.LockModuleBlock;
import indi.wenyan.setup.Registration;
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
        test.registerGameTestTemplate(()-> StructureTemplateBuilder
                .withSize(1, 1, 1)
                .set(0, 0, 0, Registration.LOCK_MODULE_BLOCK.get().defaultBlockState())
        );

        test.onGameTest(helper -> {
            helper.succeedIf(() -> {
                helper.assertBlockProperty(new BlockPos(0, 1, 0),
                        LockModuleBlock.LOCK_STATE, false);
            });
        });
    }
}
