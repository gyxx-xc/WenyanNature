package indi.wenyan.content.block.furnace;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.judou.api.exec.request.IArgsRequest;
import indi.wenyan.judou.api.exec.structure.RawHandlerPackage;
import indi.wenyan.judou.api.language.JudouExceptionText;
import indi.wenyan.judou.api.utils.WenyanValues;
import indi.wenyan.judou.api.values.WenyanNull;
import indi.wenyan.judou.api.values.exception.WenyanException;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.language.ExceptionText;
import indi.wenyan.setup.language.FunctionMetaText;
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
    private final ItemStacksResourceHandler output = new ItemStacksResourceHandler(1) {
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            super.onContentsChanged(index, previousContents);
            updateBlock();
        }
    };
    @Getter
    private final String basePackageName = WenyanSymbol.LOGIC_FURNACE;
    private boolean resetProgress = false;
    @Getter
    private final ItemStacksResourceHandler input = new ItemStacksResourceHandler(1) {
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            super.onContentsChanged(index, previousContents);
            updateBlock();

            resetProgress = true;
        }
    };
    private ResourceKey<Recipe<?>> lastRecipe;
    private int progress = -1;
    private int maxProgress = -1;
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
    private final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .description(FunctionMetaText.FurnaceBurn.string())
            .handler(WenyanSymbol.FURNACE_BURN, request -> {
                checkArgsAndRecipe(request);
                progress = Math.min(progress + 1, maxProgress);
                if (progress == maxProgress)
                    processOutput();
                return WenyanNull.NULL;
            })
            .description(FunctionMetaText.FurnaceDoubleBurn.string())
            .handler(WenyanSymbol.FURNACE_DOUBLE_BURN, request -> {
                checkArgsAndRecipe(request);
                // check int overflow
                if (progress >= Integer.MAX_VALUE / 2) {
                    throw new WenyanException(JudouExceptionText.IntegerOverflow.string());
                }
                progress *= 2;

                if (progress == maxProgress) {
                    processOutput();
                } else if (progress > maxProgress) {
                    ResourceHandlerUtil.extractFirst(input, _ -> true, Integer.MAX_VALUE, null);
                }
                return WenyanNull.NULL;
            })
            .description(FunctionMetaText.FurnaceGetProgress.string())
            .handler(WenyanSymbol.FURNACE_GET_PROGRESS, request -> {
                checkArgsAndRecipe(request);
                return WenyanValues.of(progress);
            })
            .description(FunctionMetaText.FurnaceGetMaxProgress.string())
            .handler(WenyanSymbol.FURNACE_GET_MAX_PROGRESS, request -> {
                checkArgsAndRecipe(request);
                return WenyanValues.of(maxProgress);
            })
            .build();

    public LogicFurnaceBlockEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.LOGIC_FURNACE_ENTITY.get(), pos, blockState);
    }

    private Optional<RecipeHolder<? extends AbstractCookingRecipe>> getRecipeFor(SingleRecipeInput input, ServerLevel level) {
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
            double reduceTime = Math.min(count / 64, 1) * 0.5;
            maxProgress = cookingTotalTime * count - random.nextInt((int) (cookingTotalTime * count * reduceTime) + 1);
            progress = 0;
        } else {
            maxProgress = -1;
            progress = -1;
        }
    }

    @Override
    protected void saveData(ValueOutput output) {
        var inputChild = output.child("input");
        this.input.serialize(inputChild);
        var outputChild = output.child("output");
        this.output.serialize(outputChild);
    }

    @Override
    protected void loadData(ValueInput input) {
        var inputChild = input.childOrEmpty("input");
        this.input.deserialize(inputChild);
        var outputChild = input.childOrEmpty("output");
        this.output.deserialize(outputChild);
    }

    private void updateBlock() {
        assert getLevel() != null;
        getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
        setChanged();
    }

    private void checkArgsAndRecipe(IArgsRequest request) throws WenyanException {
        if (!request.args().isEmpty())
            throw new WenyanException(JudouExceptionText.ArgsNumWrong.string(0, request.args().size()));
        if (progress < 0) {
            throw new WenyanException(ExceptionText.NoRecipeFound.string());
        }
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
