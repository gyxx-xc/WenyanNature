package indi.wenyan.content.block.additional_module.paper;

import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.WenyanMinecraftValues;
import indi.wenyan.interpreter_impl.value.WenyanBlock;
import indi.wenyan.interpreter_impl.value.WenyanCapabilitySlot;
import indi.wenyan.interpreter_impl.value.WenyanVec3;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.utils.WenyanSymbol;
import indi.wenyan.judou.utils.WenyanValues;
import indi.wenyan.setup.Registration;
import indi.wenyan.setup.network.BlockPosRangePacket;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class BlockModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = WenyanSymbol.var("BlockModule");

    @Getter
    private int continueCount = 0;

    @Getter
    private RenderRange renderRange;
    public record RenderRange(Vec3 start, Vec3 end, boolean found) {}

    @Getter
    private final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler(WenyanSymbol.var("BlockModule.search"), request -> {
                Vec3 s = request.args().get(0).as(WenyanVec3.TYPE).value();
                BlockPos start = new BlockPos((int) s.x, (int) s.y, (int) s.z);
                Vec3 e = request.args().get(1).as(WenyanVec3.TYPE).value();
                BlockPos end = new BlockPos((int) e.x, (int) e.y, (int) e.z);
                boolean found = false;
                var compare = request.args().get(2);
                if (compare.is(WenyanCapabilitySlot.TYPE)) {
                    Item item = compare.as(WenyanCapabilitySlot.TYPE).getStack().getItem();
                    if (item instanceof BlockItem blockItem) {
                        // search from start to end
                        var target = blockItem.getBlock();
                        assert level != null;
                        for (var pos : BlockPos.betweenClosed(start.offset(blockPos()),
                                end.offset(blockPos()))) {
                            if (level.getBlockState(pos).is(target)) {
                                found = true;
                                break;
                            }
                        }
                    } else {
                        throw new WenyanException("參數必須是方塊物品");
                    }
                } else if (compare.is(WenyanBlock.TYPE)) {
                    BlockState target = compare.as(WenyanBlock.TYPE).value();
                    assert level != null;
                    for (var pos : BlockPos.betweenClosed(start.offset(blockPos()),
                            end.offset(blockPos()))) {
                        if (level.getBlockState(pos).is(target.getBlock())) {
                            found = true;
                            break;
                        }
                    }
                } else {
                    throw new WenyanException("參數必須是方塊物品");
                }

                if (level instanceof ServerLevel serverLevel)
                    PacketDistributor.sendToPlayersTrackingChunk(serverLevel,
                            new ChunkPos(blockPos()),
                            new BlockPosRangePacket(blockPos(), start, end, found));
                return WenyanValues.of(found);
            })
            .handler(WenyanSymbol.var("BlockModule.get"), request -> {
                Vec3 p = request.args().getFirst().as(WenyanVec3.TYPE).value();
                BlockPos pos = new BlockPos((int) p.x, (int) p.y, (int) p.z);
                assert level != null;
                BlockState state = level.getBlockState(pos);
                return WenyanMinecraftValues.of(state);
            })
            .handler(WenyanSymbol.var("BlockModule.attach"), request -> {
                Direction attachedDirection = AbstractFuluBlock
                        .getConnectedDirection(getBlockState()).getOpposite();
                BlockPos pos = blockPos().relative(attachedDirection);
                assert level != null;
                BlockState state = level.getBlockState(pos);
                return WenyanMinecraftValues.of(state);
            })
            .build();

    public BlockModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.BLOCK_MODULE_ENTITY.get(), pos, blockState);
    }

    public void addRenderRange(Vec3 start, Vec3 end, boolean found) {
        renderRange = new RenderRange(start, end, found);
        continueCount = 20;
    }

    @Override
    public void tick(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
        super.tick(level, pos, state);
        if (continueCount > 0) {
            continueCount--;
        }
    }
}
