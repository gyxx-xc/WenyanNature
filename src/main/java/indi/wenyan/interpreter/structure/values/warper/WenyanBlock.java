package indi.wenyan.interpreter.structure.values.warper;

import indi.wenyan.content.handler.WenyanInlineJavacall;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanObject;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.IWenyanWarperValue;
import indi.wenyan.interpreter.utils.WenyanValues;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public record WenyanBlock(BlockState value) implements IWenyanWarperValue<BlockState>, IWenyanObject {
    public static final WenyanType<WenyanBlock> TYPE = new WenyanType<>("block", WenyanBlock.class);

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    public IWenyanValue getAttribute(String name) {
        return switch (name) {
            case "「名」" -> WenyanValues.of(value.getBlock().getName().toString());
            case "「同物」" -> new WenyanInlineJavacall((self, args) -> {
                if (args.getFirst().as(WenyanCapabilitySlot.TYPE).getStack().getItem() instanceof BlockItem blockItem)
                    return WenyanValues.of(value.is(blockItem.getBlock()));
                else
                    throw new WenyanException("參數必須是方塊物品");
            });
            case "「同塊」" -> new WenyanInlineJavacall((self, args) ->
                    WenyanValues.of(value.is(args.getFirst().as(WenyanBlock.TYPE).value.getBlock()))
            );
            case "「有實」" -> WenyanValues.of(value.hasBlockEntity());
            case "「向」" -> getConnectedDirection(value);
            default -> throw new WenyanException("Unknown Block attribute: " + name);
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof WenyanBlock(BlockState that))
            return value.equals(that);
        return false;
    }

    public static WenyanVec3 getConnectedDirection(BlockState state) {
        try {
            return new WenyanVec3(switch (state.getValue(FaceAttachedHorizontalDirectionalBlock.FACE)) {
                case CEILING -> Direction.DOWN.getNormal();
                case FLOOR -> Direction.UP.getNormal();
                default -> state.getValue(HorizontalDirectionalBlock.FACING).getNormal();
            });
        } catch (IllegalArgumentException e) {
            throw new WenyanException("此方塊無法取得面向");
        }
    }

    @Override
    public @NotNull String toString() {
        return value.getBlock().getName().getString();
    }
}
