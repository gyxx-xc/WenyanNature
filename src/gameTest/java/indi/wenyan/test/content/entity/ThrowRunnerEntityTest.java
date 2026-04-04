package indi.wenyan.test.content.entity;

import indi.wenyan.content.entity.ThrowRunnerEntity;
import indi.wenyan.setup.config.WenyanConfig;
import indi.wenyan.setup.definitions.RunnerTier;
import indi.wenyan.setup.definitions.WenyanEntities;
import indi.wenyan.setup.definitions.WenyanItems;
import indi.wenyan.setup.definitions.WyRegistration;
import indi.wenyan.test.utils.RunnerTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.item.ItemStack;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.GameTest;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

import java.util.function.Supplier;

@ForEachTest(groups = "content.entity.throw_runner")
public class ThrowRunnerEntityTest {

    public static final Supplier<StructureTemplateBuilder> STRUCTURE_TEMPLATE_BUILDER = () -> StructureTemplateBuilder.withSize(3, 3, 3);

    @GameTest(timeoutTicks = 200)
    @TestHolder(description = "Tests that a thrown runner entity discards after its lifetime")
    public static void lifetimeTest(final DynamicTest test) {
        test.registerGameTestTemplate(STRUCTURE_TEMPLATE_BUILDER);
        test.onGameTest(RunnerTestHelper.class, helper -> {
            ItemStack stack = new ItemStack(WenyanItems.THROW_RUNNER.getItem(RunnerTier.RUNNER_0));
            stack.set(WyRegistration.PROGRAM_CODE_DATA.get(), "");
            Position pos = helper.absolutePos(new BlockPos(0, 2, 0)).getCenter();
            ThrowRunnerEntity entity = new ThrowRunnerEntity(helper.getLevel(), pos, stack, RunnerTier.RUNNER_0);
            int lifetime = WenyanConfig.getThrowEntityLifetime();

            helper.startSequence()
                    .thenExecute(() -> {
                        helper.getLevel().addFreshEntity(entity);
                        entity.setPos(helper.getBounds().getCenter());
                    })
                    .thenWaitUntil(() -> helper.assertEntityPresent(WenyanEntities.THROW_RUNNER_ENTITY.get()))
                    .thenIdle(lifetime + 5) // wait for lifetime plus some buffer
                    .thenWaitUntil(() -> helper.assertEntityNotPresent(WenyanEntities.THROW_RUNNER_ENTITY.get()))
                    .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Tests that a thrown runner with valid code runs without immediate discard")
    public static void validCodeTest(final DynamicTest test) {
        test.registerGameTestTemplate(STRUCTURE_TEMPLATE_BUILDER);
        test.onGameTest(RunnerTestHelper.class, helper -> {
            ItemStack stack = new ItemStack(WenyanItems.THROW_RUNNER.getItem(RunnerTier.RUNNER_0));
            stack.set(WyRegistration.PROGRAM_CODE_DATA.get(), "待二二");
            Position pos = helper.absolutePos(new BlockPos(0, 2, 0)).getCenter();
            ThrowRunnerEntity entity = new ThrowRunnerEntity(helper.getLevel(), pos, stack, RunnerTier.RUNNER_0);

            helper.startSequence()
                    .thenExecute(() -> {
                        helper.getLevel().addFreshEntity(entity);
                        entity.setPos(helper.getBounds().getCenter());
                    })
                    .thenWaitUntil(() -> helper.assertEntityPresent(WenyanEntities.THROW_RUNNER_ENTITY.get()))
                    .thenIdle(10) // wait some ticks, ensure still present
                    .thenWaitUntil(() -> helper.assertEntityPresent(WenyanEntities.THROW_RUNNER_ENTITY.get()))
                    .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Tests that a thrown runner with invalid code discards quickly")
    public static void invalidCodeTest(final DynamicTest test) {
        test.registerGameTestTemplate(STRUCTURE_TEMPLATE_BUILDER);
        test.onGameTest(RunnerTestHelper.class, helper -> {
            ItemStack stack = new ItemStack(WenyanItems.THROW_RUNNER.getItem(RunnerTier.RUNNER_0));
            stack.set(WyRegistration.PROGRAM_CODE_DATA.get(), "「"); // invalid syntax
            Position pos = helper.absolutePos(new BlockPos(0, 2, 0)).getCenter();
            ThrowRunnerEntity entity = new ThrowRunnerEntity(helper.getLevel(), pos, stack, RunnerTier.RUNNER_0);

            helper.startSequence()
                    .thenExecute(() -> {
                        helper.getLevel().addFreshEntity(entity);
                        entity.setPos(helper.getBounds().getCenter());
                    })
                    .thenWaitUntil(() -> helper.assertEntityPresent(WenyanEntities.THROW_RUNNER_ENTITY.get()))
                    .thenIdle(5) // wait a few ticks, should discard
                    .thenWaitUntil(() -> helper.assertEntityNotPresent(WenyanEntities.THROW_RUNNER_ENTITY.get()))
                    .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Tests that a thrown runner slows down over time")
    public static void movementDecayTest(final DynamicTest test) {
        test.registerGameTestTemplate(STRUCTURE_TEMPLATE_BUILDER);
        test.onGameTest(RunnerTestHelper.class, helper -> {
            ItemStack stack = new ItemStack(WenyanItems.THROW_RUNNER.getItem(RunnerTier.RUNNER_0));
            stack.set(WyRegistration.PROGRAM_CODE_DATA.get(), "");
            Position pos = helper.absolutePos(new BlockPos(0, 2, 0)).getCenter();
            ThrowRunnerEntity entity = new ThrowRunnerEntity(helper.getLevel(), pos, stack, RunnerTier.RUNNER_0);
            entity.setDeltaMovement(new net.minecraft.world.phys.Vec3(1.0, 0.0, 0.0));

            helper.startSequence()
                    .thenExecute(() -> {
                        helper.getLevel().addFreshEntity(entity);
                        entity.setPos(helper.getBounds().getCenter());
                    })
                    .thenWaitUntil(() -> helper.assertEntityPresent(WenyanEntities.THROW_RUNNER_ENTITY.get()))
                    .thenIdle(2)
                    .thenWaitUntil(() -> {
                        net.minecraft.world.phys.Vec3 movement = entity.getDeltaMovement();
                        helper.assertTrue(movement.length() < 1.0, "Speed should decay");
                    })
                    .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Tests that a thrown runner has no gravity")
    public static void noGravityTest(final DynamicTest test) {
        test.registerGameTestTemplate(STRUCTURE_TEMPLATE_BUILDER);
        test.onGameTest(RunnerTestHelper.class, helper -> {
            ItemStack stack = new ItemStack(WenyanItems.THROW_RUNNER.getItem(RunnerTier.RUNNER_0));
            stack.set(WyRegistration.PROGRAM_CODE_DATA.get(), "");
            Position pos = helper.absolutePos(new BlockPos(0, 2, 0)).getCenter();
            ThrowRunnerEntity entity = new ThrowRunnerEntity(helper.getLevel(), pos, stack, RunnerTier.RUNNER_0);

            helper.startSequence()
                    .thenExecute(() -> {
                        helper.getLevel().addFreshEntity(entity);
                        entity.setPos(helper.getBounds().getCenter());
                    })
                    .thenWaitUntil(() -> helper.assertEntityPresent(WenyanEntities.THROW_RUNNER_ENTITY.get()))
                    .thenExecute(() -> helper.assertTrue(entity.isNoGravity(), "Throw runner should have no gravity"))
                    .thenSucceed();
        });
    }
}
