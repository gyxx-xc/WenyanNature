package indi.wenyan.interpreter.structure.values.warper;

import indi.wenyan.content.handler.WenyanBuiltinFunction;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanObject;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.IWenyanWarperValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanBoolean;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

public record WenyanBlock(BlockState value) implements IWenyanWarperValue<BlockState>, IWenyanObject {
    public static final WenyanType<WenyanBlock> TYPE = new WenyanType<>("block", WenyanBlock.class);

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    public IWenyanValue getAttribute(String name) {
        return switch (name) {
            case "「名」" -> new WenyanString(value.getBlock().getName().toString());
            case "「同物」" -> new WenyanBuiltinFunction((self, args) -> {
                if (args.getFirst().as(WenyanCapabilitySlot.TYPE).getStack().getItem() instanceof BlockItem blockItem)
                    return new WenyanBoolean(value.is(blockItem.getBlock()));
                else
                    throw new WenyanException("參數必須是方塊物品");
            });
            case "「同塊」" -> new WenyanBuiltinFunction((self, args) ->
                    new WenyanBoolean(value.is(args.getFirst().as(WenyanBlock.TYPE).value.getBlock()))
            );
            case "「有實」" -> new WenyanBoolean(value.hasBlockEntity());
            case "「向」" -> getConnectedDirection(value);
            default -> throw new WenyanException("Unknown Block attribute: " + name);
        };
    }

    @Override
    public void setVariable(String name, IWenyanValue value) {
        throw new WenyanException("Cannot set attribute of block");
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

}
