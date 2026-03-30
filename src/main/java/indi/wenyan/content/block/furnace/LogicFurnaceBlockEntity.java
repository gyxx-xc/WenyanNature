package indi.wenyan.content.block.furnace;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.utils.function.WenyanValues;
import indi.wenyan.judou.utils.language.JudouExceptionText;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.language.ExceptionText;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LogicFurnaceBlockEntity extends AbstractModuleEntity {
    @Getter
    private final ItemStacksResourceHandler input = new ItemStacksResourceHandler(1) {
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            super.onContentsChanged(index, previousContents);
            updateBlock();

            resetProgress = true;
        }
    };

    @Getter
    private final ItemStacksResourceHandler output = new ItemStacksResourceHandler(1) {
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            super.onContentsChanged(index, previousContents);
            updateBlock();
        }
    };

    @Getter
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int i) {
            return i == 0 ? progress : maxProgress;
        }

        @Override
        public void set(int i, int i1) {
            if (i == 0) {
                progress = i1;
            } else {
                maxProgress = i1;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    @Getter
    private final String basePackageName = WenyanSymbol.LOGIC_FURNACE;

    private boolean resetProgress = false;
    private ResourceKey<Recipe<?>> lastRecipe;

    public Optional<RecipeHolder<? extends AbstractCookingRecipe>> getRecipeFor(SingleRecipeInput input, ServerLevel level) {
        RecipeManager recipeManager = level.recipeAccess();
        var smeltResult = recipeManager.getRecipeFor(RecipeType.SMELTING, input, level, this.lastRecipe);
        if (smeltResult.isPresent()) {
            var unpackedResult = smeltResult.get();
            this.lastRecipe = unpackedResult.id();
            return Optional.of(unpackedResult);
        }

        var blastResult = recipeManager.getRecipeFor(RecipeType.BLASTING, input, level, this.lastRecipe);
        if (blastResult.isPresent()) {
            var unpackedResult = blastResult.get();
            this.lastRecipe = unpackedResult.id();
            return Optional.of(unpackedResult);
        }

        var campfireResult = recipeManager.getRecipeFor(RecipeType.CAMPFIRE_COOKING, input, level, this.lastRecipe);
        if (campfireResult.isPresent()) {
            var unpackedResult = campfireResult.get();
            this.lastRecipe = unpackedResult.id();
            return Optional.of(unpackedResult);
        }

        return Optional.empty();
    }

    private int progress;
    private int maxProgress;

    @Getter
    private final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler(WenyanSymbol.FURNACE_BURN, request -> {
                if (!request.args().isEmpty())
                    throw new WenyanException(JudouExceptionText.ArgsNumWrong.string(0, request.args().size()));
                if (progress == 0) {
                    throw new WenyanException(ExceptionText.NoRecipeFound.string());
                }
                if (++progress >= maxProgress) {
                    processOutput();
                }
                return WenyanNull.NULL;
            })
            .handler(WenyanSymbol.FURNACE_DOUBLE_BURN, request -> {
                if (!request.args().isEmpty())
                    throw new WenyanException(JudouExceptionText.ArgsNumWrong.string(0, request.args().size()));
                if (progress == 0) {
                    throw new WenyanException(ExceptionText.NoRecipeFound.string());
                }
                if (progress == maxProgress) {
                    processOutput();
                } else if (progress > maxProgress) {
                    ResourceHandlerUtil.extractFirst(input, _ -> true, Integer.MAX_VALUE, null);
                } else {
                    // check int overflow
                    if (progress >= Integer.MAX_VALUE / 2) {
                        throw new WenyanException(JudouExceptionText.IntegerOverflow.string());
                    }
                    progress *= 2;
                }
                return WenyanNull.NULL;
            })
            .handler(WenyanSymbol.FURNACE_GET_PROGRESS, request -> {
                if (!request.args().isEmpty())
                    throw new WenyanException(JudouExceptionText.ArgsNumWrong.string(0, request.args().size()));
                if (progress == 0) {
                    throw new WenyanException(ExceptionText.NoRecipeFound.string());
                }
                return WenyanValues.of(progress);
            })
            .handler(WenyanSymbol.FURNACE_GET_MAX_PROGRESS, request -> {
                if (!request.args().isEmpty())
                    throw new WenyanException(JudouExceptionText.ArgsNumWrong.string(0, request.args().size()));
                if (maxProgress == 0) {
                    throw new WenyanException(ExceptionText.NoRecipeFound.string());
                }
                return WenyanValues.of(maxProgress);
            })
            .build();

    public LogicFurnaceBlockEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.LOGIC_FURNACE_ENTITY.get(), pos, blockState);
    }

    public void tick(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        super.tick(level, pos, state);
        if (!resetProgress)
            return;
        resetProgress = false;
        SingleRecipeInput input = new SingleRecipeInput(getInput().getResource(0).toStack());
        RecipeHolder<? extends AbstractCookingRecipe> recipe = getRecipeFor(input, level).orElse(null);
        if (recipe != null) {
            int cookingTotalTime = recipe.value().cookingTime();
            int count = getInput().getAmountAsInt(0);
            double reduceTime = 1 - Math.max(count / 64, 1) * 0.5;
            maxProgress = random.nextInt((int) (cookingTotalTime * count * reduceTime), cookingTotalTime * count);
            progress = 1;
        } else {
            maxProgress = 0;
            progress = 0;
        }
    }

    @Override
    protected void saveData(ValueOutput output) {
        input.serialize(output);
        this.output.serialize(output);
    }

    @Override
    protected void loadData(ValueInput input) {
        this.input.deserialize(input);
        this.output.deserialize(input);
    }

    private void updateBlock() {
        assert getLevel() != null;
        getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
        setChanged();
    }

    private void processOutput() {
        SingleRecipeInput input = new SingleRecipeInput(getInput().getResource(0).toStack());
        RecipeHolder<? extends AbstractCookingRecipe> recipe = getRecipeFor(input, (ServerLevel) level).orElse(null);
        if (recipe != null) {
            ItemStack item = recipe.value().assemble(input);
            int batchSize = getInput().getAmountAsInt(0);
            item.setCount(item.count() * batchSize);
            var remain = ItemUtil.insertItemReturnRemaining(output, item, true, null);
            if (remain.isEmpty()) {
                ItemUtil.insertItemReturnRemaining(output, item, false, null);
                ResourceHandlerUtil.extractFirst(getInput(), _ -> true, batchSize, null);
            }
        }
    }
}
