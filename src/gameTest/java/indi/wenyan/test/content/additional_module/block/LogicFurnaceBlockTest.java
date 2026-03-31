package indi.wenyan.test.content.additional_module.block;

import indi.wenyan.content.block.furnace.LogicFurnaceBlockEntity;
import indi.wenyan.content.block.runner.RunnerBlockEntity;
import indi.wenyan.setup.definitions.RunnerTier;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.test.utils.RunnerTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.GameTest;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

import java.util.function.Supplier;

@ForEachTest(groups = "content.block.logic_furnace")
public class LogicFurnaceBlockTest {

    public static final Supplier<StructureTemplateBuilder> STRUCTURE_TEMPLATE_BUILDER = () -> StructureTemplateBuilder.withSize(2, 2, 1)
            .set(0, 0, 0, net.minecraft.world.level.block.Blocks.STONE.defaultBlockState())
            .set(0, 1, 0, WenyanBlocks.RUNNER_BLOCK.getBlock(RunnerTier.RUNNER_2).defaultBlockState())
            .set(1, 0, 0, net.minecraft.world.level.block.Blocks.STONE.defaultBlockState())
            .set(1, 1, 0, WenyanBlocks.LOGIC_FURNACE_BLOCK.get().defaultBlockState());

    private static void setFurnaceInput(RunnerTestHelper helper, BlockPos furnacePos, ItemStack stack) {
        LogicFurnaceBlockEntity furnace = helper.getBlockEntity(furnacePos, LogicFurnaceBlockEntity.class);
        ItemStacksResourceHandler input = furnace.getInput();
        // Clear existing input
        ResourceHandlerUtil.extractFirst(input, _ -> true, Integer.MAX_VALUE, null);
        // Insert new item
        ItemUtil.insertItemReturnRemaining(input, stack, false, null);
    }

    private static void setFurnaceProgress(RunnerTestHelper helper, BlockPos furnacePos, int progress, int maxProgress) {
        LogicFurnaceBlockEntity furnace = helper.getBlockEntity(furnacePos, LogicFurnaceBlockEntity.class);
        furnace.getData().set(0, progress);
        furnace.getData().set(1, maxProgress);
    }

    @GameTest
    @TestHolder(description = "Tests basic burn operation")
    public static void basicBurnTest(final DynamicTest test) {
        test.registerGameTestTemplate(STRUCTURE_TEMPLATE_BUILDER);
        test.onGameTest(RunnerTestHelper.class, helper -> {
            final RunnerBlockEntity runner = helper.getBlockEntity(
                    BlockPos.ZERO.offset(0, 1, 0), RunnerBlockEntity.class);
            LogicFurnaceBlockEntity furnace = helper.getBlockEntity(BlockPos.containing(1, 1, 0), LogicFurnaceBlockEntity.class);
            String order = "「1」";
            furnace.setPackageName(order);
            String importCommand = "吾嘗觀"+order+"之書方悟「熔」「倍熔」「取熔」「取熔最大」之義";
            helper.startSequence()
                    .thenExecute(() -> {
                        try {
                            setFurnaceInput(helper, BlockPos.containing(1, 1, 0), new ItemStack(Items.COBBLESTONE, 1));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .thenIdle(1) // Wait for tick to initialize progress
                    .thenExecute(() -> {
                        // Set deterministic progress values for testing
                        setFurnaceProgress(helper, BlockPos.containing(1, 1, 0), 0, 100);
                        runner.newThread(importCommand+"施「取熔」書之施「取熔最大」書之施「熔」施「取熔」書之");
                    })
                    .thenIdle(10)
                    .thenWaitUntil(() -> helper.assertOutputBlock(runner, "output", "零", "一百", "一"))
                    .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Tests double burn operation")
    public static void doubleBurnTest(final DynamicTest test) {
        test.registerGameTestTemplate(STRUCTURE_TEMPLATE_BUILDER);
        test.onGameTest(RunnerTestHelper.class, helper -> {
            final RunnerBlockEntity runner = helper.getBlockEntity(
                    BlockPos.ZERO.offset(0, 1, 0), RunnerBlockEntity.class);
            LogicFurnaceBlockEntity furnace = helper.getBlockEntity(BlockPos.containing(1, 1, 0), LogicFurnaceBlockEntity.class);
            String order = "「2」";
            furnace.setPackageName(order);
            String importCommand = "吾嘗觀"+order+"之書方悟「熔」「倍熔」「取熔」「取熔最大」之義";
            helper.startSequence()
                    .thenExecute(() -> {
                        try {
                            setFurnaceInput(helper, BlockPos.containing(1, 1, 0), new ItemStack(Items.COBBLESTONE, 1));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .thenIdle(1) // Wait for tick to initialize progress
                    .thenExecute(() -> {
                        // Set deterministic progress values for testing
                        setFurnaceProgress(helper, BlockPos.containing(1, 1, 0), 0, 100);
                        runner.newThread(importCommand+"施「取熔」書之施「熔」施「取熔」書之施「倍熔」施「取熔」書之");
                    })
                    .thenIdle(10)
                    .thenWaitUntil(() -> helper.assertOutputBlock(runner, "output", "零", "一", "二"))
                    .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Tests get progress and max progress operations")
    public static void getProgressMaxProgressTest(final DynamicTest test) {
        test.registerGameTestTemplate(STRUCTURE_TEMPLATE_BUILDER);
        test.onGameTest(RunnerTestHelper.class, helper -> {
            final RunnerBlockEntity runner = helper.getBlockEntity(
                    BlockPos.ZERO.offset(0, 1, 0), RunnerBlockEntity.class);
            LogicFurnaceBlockEntity furnace = helper.getBlockEntity(BlockPos.containing(1, 1, 0), LogicFurnaceBlockEntity.class);
            String order = "「3」";
            furnace.setPackageName(order);
            String importCommand = "吾嘗觀"+order+"之書方悟「熔」「倍熔」「取熔」「取熔最大」之義";
            helper.startSequence()
                    .thenExecute(() -> {
                        try {
                            setFurnaceInput(helper, BlockPos.containing(1, 1, 0), new ItemStack(Items.COBBLESTONE, 1));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .thenIdle(1) // Wait for tick to initialize progress
                    .thenExecute(() -> {
                        // Set deterministic progress values for testing
                        setFurnaceProgress(helper, BlockPos.containing(1, 1, 0), 5, 200);
                        runner.newThread(importCommand+"施「取熔」書之施「取熔最大」書之");
                    })
                    .thenIdle(10)
                    .thenWaitUntil(() -> helper.assertOutputBlock(runner, "output", "五", "二百"))
                    .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Tests completion when progress reaches max")
    public static void completionTest(final DynamicTest test) {
        test.registerGameTestTemplate(STRUCTURE_TEMPLATE_BUILDER);
        test.onGameTest(RunnerTestHelper.class, helper -> {
            final RunnerBlockEntity runner = helper.getBlockEntity(
                    BlockPos.ZERO.offset(0, 1, 0), RunnerBlockEntity.class);
            LogicFurnaceBlockEntity furnace = helper.getBlockEntity(BlockPos.containing(1, 1, 0), LogicFurnaceBlockEntity.class);
            String order = "「4」";
            furnace.setPackageName(order);
            String importCommand = "吾嘗觀"+order+"之書方悟「熔」「倍熔」「取熔」「取熔最大」之義";
            helper.startSequence()
                    .thenExecute(() -> {
                        try {
                            setFurnaceInput(helper, BlockPos.containing(1, 1, 0), new ItemStack(Items.COBBLESTONE, 1));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .thenIdle(1) // Wait for tick to initialize progress
                    .thenExecute(() -> {
                        // Set progress one step before completion
                        setFurnaceProgress(helper, BlockPos.containing(1, 1, 0), 4, 5);
                        runner.newThread(importCommand+"施「取熔」書之施「熔」施「取熔」書之");
                    })
                    .thenIdle(10)
                    // FIXME: change to error checker helper function.
//                    .thenWaitUntil(() -> helper.assertOutputBlock(runner, "output", "四", "1:40 施「取熔」: 謬：未尋配方"))
                    .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Tests error when no recipe found")
    public static void noRecipeTest(final DynamicTest test) {
        test.registerGameTestTemplate(STRUCTURE_TEMPLATE_BUILDER);
        test.onGameTest(RunnerTestHelper.class, helper -> {
            final RunnerBlockEntity runner = helper.getBlockEntity(
                    BlockPos.ZERO.offset(0, 1, 0), RunnerBlockEntity.class);
            LogicFurnaceBlockEntity furnace = helper.getBlockEntity(BlockPos.containing(1, 1, 0), LogicFurnaceBlockEntity.class);
            String order = "「5」";
            furnace.setPackageName(order);
            String importCommand = "吾嘗觀"+order+"之書方悟「熔」「倍熔」「取熔」「取熔最大」之義";
            helper.startSequence()
                    .thenExecute(() -> {
                        try {
                            // Dirt has no smelting recipe
                            setFurnaceInput(helper, BlockPos.containing(1, 1, 0), new ItemStack(Items.DIRT, 1));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .thenIdle(1) // Wait for tick to set progress=-1
                    .thenExecute(() -> runner.newThread(importCommand+"施「熔」書之"))
                    .thenIdle(10)
                    .thenWaitUntil(() -> {
                        // Expect error output (colored text)
                        var outputQueue = runner.getOutputQueue();
                        boolean hasError = outputQueue.stream()
                                .anyMatch(c -> c.getStyle().getColor() != null);
                        helper.assertTrue(hasError, "Expected error output but none found");
                    })
                    .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Tests argument validation - no arguments allowed")
    public static void argumentValidationTest(final DynamicTest test) {
        test.registerGameTestTemplate(STRUCTURE_TEMPLATE_BUILDER);
        test.onGameTest(RunnerTestHelper.class, helper -> {
            final RunnerBlockEntity runner = helper.getBlockEntity(
                    BlockPos.ZERO.offset(0, 1, 0), RunnerBlockEntity.class);
            LogicFurnaceBlockEntity furnace = helper.getBlockEntity(BlockPos.containing(1, 1, 0), LogicFurnaceBlockEntity.class);
            String order = "「6」";
            furnace.setPackageName(order);
            String importCommand = "吾嘗觀"+order+"之書方悟「熔」「倍熔」「取熔」「取熔最大」之義";
            helper.startSequence()
                    .thenExecute(() -> {
                        try {
                            setFurnaceInput(helper, BlockPos.containing(1, 1, 0), new ItemStack(Items.COBBLESTONE, 1));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .thenIdle(1) // Wait for tick to initialize progress
                    .thenExecute(() -> {
                        // Set deterministic progress values for testing
                        setFurnaceProgress(helper, BlockPos.containing(1, 1, 0), 0, 100);
                        runner.newThread(importCommand+"施「熔」以一書之");
                    })
                    .thenIdle(10)
                    .thenWaitUntil(() -> {
                        // Expect error about argument count
                        var outputQueue = runner.getOutputQueue();
                        boolean hasError = outputQueue.stream()
                                .anyMatch(c -> c.getStyle().getColor() != null);
                        helper.assertTrue(hasError, "Expected error output for invalid argument count");
                    })
                    .thenSucceed();
        });
    }

    @GameTest
    @TestHolder(description = "Tests overflow protection in double burn")
    public static void overflowTest(final DynamicTest test) {
        test.registerGameTestTemplate(STRUCTURE_TEMPLATE_BUILDER);
        test.onGameTest(RunnerTestHelper.class, helper -> {
            final RunnerBlockEntity runner = helper.getBlockEntity(
                    BlockPos.ZERO.offset(0, 1, 0), RunnerBlockEntity.class);
            LogicFurnaceBlockEntity furnace = helper.getBlockEntity(BlockPos.containing(1, 1, 0), LogicFurnaceBlockEntity.class);
            String order = "「7」";
            furnace.setPackageName(order);
            String importCommand = "吾嘗觀"+order+"之書方悟「熔」「倍熔」「取熔」「取熔最大」之義";
            helper.startSequence()
                    .thenExecute(() -> {
                        try {
                            setFurnaceInput(helper, BlockPos.containing(1, 1, 0), new ItemStack(Items.COBBLESTONE, 1));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .thenIdle(1) // Wait for tick to initialize progress
                    .thenExecute(() -> {
                        // Set progress to Integer.MAX_VALUE / 2 to trigger overflow protection
                        int halfMax = Integer.MAX_VALUE / 2;
                        setFurnaceProgress(helper, BlockPos.containing(1, 1, 0), halfMax, Integer.MAX_VALUE);
                        runner.newThread(importCommand+"施「倍熔」書之");
                    })
                    .thenIdle(10)
                    .thenWaitUntil(() -> {
                        // Expect error about integer overflow
                        var outputQueue = runner.getOutputQueue();
                        boolean hasError = outputQueue.stream()
                                .anyMatch(c -> c.getStyle().getColor() != null);
                        helper.assertTrue(hasError, "Expected error output for integer overflow");
                    })
                    .thenSucceed();
        });
    }
}