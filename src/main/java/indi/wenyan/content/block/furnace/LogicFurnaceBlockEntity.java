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
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LogicFurnaceBlockEntity extends AbstractModuleEntity {
    @Getter
    private final ItemStacksResourceHandler input = new ItemStacksResourceHandler(1) {
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            super.onContentsChanged(index, previousContents);
            updateBlock();
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

    private int progress;
    private int maxProgress;

    @Getter
    private final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler(WenyanSymbol.FURNACE_BURN, request -> {
                if (!request.args().isEmpty())
                    throw new WenyanException(JudouExceptionText.ArgsNumWrong.string(0, request.args().size()));
                if (++progress >= maxProgress) {
                    processOutput();
                }
                return WenyanNull.NULL;
            })
            .handler(WenyanSymbol.FURNACE_DOUBLE_BURN, request -> {
                if (!request.args().isEmpty())
                    throw new WenyanException(JudouExceptionText.ArgsNumWrong.string(0, request.args().size()));
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
                return WenyanValues.of(progress);
            })
            .handler(WenyanSymbol.FURNACE_GET_MAX_PROGRESS, request -> {
                if (!request.args().isEmpty())
                    throw new WenyanException(JudouExceptionText.ArgsNumWrong.string(0, request.args().size()));
                return WenyanValues.of(maxProgress);
            })
            .build();

    public LogicFurnaceBlockEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.LOGIC_FURNACE_ENTITY.get(), pos, blockState);
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

    public void updateBlock() {
        assert getLevel() != null;
        getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
        setChanged();
    }

    private void processOutput() {
        if (output.getResource(0).isEmpty()) {
//                        output.set(0, input.getResource(0), input.getAmountAsLong(0));
//                        input.set(0, ItemResource.of(ItemStack.EMPTY), 0);
            progress = 0;
        }
    }
}
