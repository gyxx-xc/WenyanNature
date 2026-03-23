package indi.wenyan.interpreter_impl.value;

import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.judou.exec_interface.handler.WenyanInlineJavacall;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanObject;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.IWenyanWarperValue;
import indi.wenyan.judou.utils.WenyanValues;
import indi.wenyan.judou.utils.language.JudouExceptionText;
import indi.wenyan.setup.language.ExceptionText;
import indi.wenyan.setup.language.TypeText;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public record WenyanBlock(BlockState value) implements IWenyanWarperValue<BlockState>, IWenyanObject {
    public static final WenyanType<WenyanBlock> TYPE = new WenyanType<>(TypeText.Block.string(), WenyanBlock.class);

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    public IWenyanValue getAttribute(String name) throws WenyanException {
        return switch (name) {
            case WenyanSymbol.BLOCK_NAME -> WenyanValues.of(value.getBlock().getName().toString());
            case WenyanSymbol.BLOCK_SAME_ITEM -> new WenyanInlineJavacall((_, args) -> {
                if (args.getFirst().as(WenyanCapabilitySlot.TYPE).getStack().getItem() instanceof BlockItem blockItem)
                    return WenyanValues.of(value.is(blockItem.getBlock()));
                else
                    throw new WenyanException(ExceptionText.NeedBlockItem.string());
            });
            case WenyanSymbol.BLOCK_SAME_BLOCK -> new WenyanInlineJavacall((_, args) ->
                    WenyanValues.of(value.is(args.getFirst().as(WenyanBlock.TYPE).value.getBlock()))
            );
            case WenyanSymbol.BLOCK_HAS_ENTITY -> WenyanValues.of(value.hasBlockEntity());
            case WenyanSymbol.BLOCK_DIRECTION -> getConnectedDirection(value);
            default -> throw new WenyanException(JudouExceptionText.NoAttribute.string(name));
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof WenyanBlock(BlockState that))
            return value.equals(that);
        return false;
    }

    public static WenyanVec3 getConnectedDirection(BlockState state) throws WenyanException {
        try {
            return new WenyanVec3(switch (state.getValue(FaceAttachedHorizontalDirectionalBlock.FACE)) {
                case CEILING -> Direction.DOWN.getUnitVec3();
                case FLOOR -> Direction.UP.getUnitVec3();
                default -> state.getValue(HorizontalDirectionalBlock.FACING).getUnitVec3();
            });
        } catch (IllegalArgumentException e) {
            throw new WenyanException(ExceptionText.NoConnectDirection.string());
        }
    }

    @Override
    public @NotNull String toString() {
        return value.getBlock().getName().getString();
    }
}
