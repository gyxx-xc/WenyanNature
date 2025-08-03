package indi.wenyan.content.block.additional_module;

import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.primitive.WenyanBoolean;
import indi.wenyan.interpreter.structure.values.warper.WenyanVec3;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.setup.Registration;
import indi.wenyan.setup.network.BlockPosRangePacket;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class BlockModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = "「塊」";

    @Getter
    private int continueCount = 0;

    @Getter
    private RenderRange renderRange;
    public record RenderRange(Vec3 start, Vec3 end, boolean found) {}

    @Getter
    private final WenyanPackage execPackage = WenyanPackageBuilder.create()
            .function("「識」", new ThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
                    Vec3 s = context.args().get(0).as(WenyanVec3.TYPE).value();
                    BlockPos start = new BlockPos((int) s.x, (int) s.y, (int) s.z);
                    Vec3 e = context.args().get(1).as(WenyanVec3.TYPE).value();
                    BlockPos end = new BlockPos((int) e.x, (int) e.y, (int) e.z);
                    boolean found = false;
                    // search from start to end
                    assert level != null;
                    for (var pos : BlockPos.betweenClosed(start.offset(getBlockPos()),
                            end.offset(getBlockPos()))) {
                        if (level.getBlockState(pos).is(Blocks.DIAMOND_ORE)) {
                            found = true;
                            break;
                        }
                    }

                    if (level instanceof ServerLevel serverLevel)
                        PacketDistributor.sendToPlayersTrackingChunk(serverLevel,
                                new ChunkPos(getBlockPos()),
                                new BlockPosRangePacket(getBlockPos(), start, end, found));
                    return new WenyanBoolean(found);
                }
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
    public void tick() {
        super.tick();
        if (continueCount > 0) {
            continueCount--;
        }
    }
}
