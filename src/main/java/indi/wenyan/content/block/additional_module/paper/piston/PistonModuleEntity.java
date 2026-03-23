package indi.wenyan.content.block.additional_module.paper.piston;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.interpreter_impl.value.WenyanVec3;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.exec_interface.structure.BaseHandleableRequest;
import indi.wenyan.judou.exec_interface.structure.IArgsRequest;
import indi.wenyan.judou.exec_interface.structure.IHandleContext;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.language.ExceptionText;
import indi.wenyan.setup.network.client.PistonMovePacket;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

import static net.minecraft.world.level.block.DirectionalBlock.FACING;

public class PistonModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = WenyanSymbol.PISTON;

    @Getter
    private final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler(WenyanSymbol.PISTON_PUSH, () -> new PistonRequest(true))
            .handler(WenyanSymbol.PISTON_PULL, () -> new PistonRequest(false))
            .build();

    public PistonModuleEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.PISTON_MODULE_ENTITY.get(), pos, blockState);
    }

    public static boolean triggleMoveBlock(@NonNull Level level, boolean extending, Direction direction, BlockPos armPos) {
        if (!extending) {
            Direction opposite = direction.getOpposite();
            BlockState state1 = WenyanBlocks.DECORATIVE_PISTON_HEAD_BLOCK.get().defaultBlockState()
                    .setValue(FACING, direction)
                    .setValue(DecorativePistonHead.TYPE, PistonType.STICKY);
            BlockState blockState = Blocks.MOVING_PISTON.defaultBlockState()
                    .setValue(MovingPistonBlock.FACING, direction)
                    .setValue(MovingPistonBlock.TYPE, PistonType.STICKY);
            level.setBlock(armPos.relative(opposite), blockState, 324);
            level.setBlockEntity(MovingPistonBlock.newMovingBlockEntity(armPos.relative(opposite), blockState, state1, opposite, true, false));
        }

        if (!extending && level.getBlockState(armPos).is(Blocks.PISTON_HEAD)) {
            level.setBlock(armPos, Blocks.AIR.defaultBlockState(), 276);
        }

        SinglePistonStructureResolver resolver = new SinglePistonStructureResolver(level, armPos, direction, extending);
        if (!resolver.resolve()) {
            return false;
        } else {
            Map<BlockPos, BlockState> deleteAfterMove = Maps.newHashMap();
            List<BlockPos> toPush = resolver.getToPush();
            List<BlockState> toPushShapes = Lists.newArrayList();

            for (BlockPos pos : toPush) {
                BlockState state = level.getBlockState(pos);
                toPushShapes.add(state);
                deleteAfterMove.put(pos, state);
            }

            List<BlockPos> toDestroy = resolver.getToDestroy();
            BlockState[] toUpdate = new BlockState[toPush.size() + toDestroy.size()];
            Direction pushDirection = extending ? direction : direction.getOpposite();
            int updateIndex = 0;

            for (int i = toDestroy.size() - 1; i >= 0; --i) {
                BlockPos pos = toDestroy.get(i);
                BlockState state = level.getBlockState(pos);
                BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
                Block.dropResources(state, level, pos, blockEntity);
                if (!state.is(BlockTags.FIRE) && level.isClientSide()) {
                    level.levelEvent(2001, pos, Block.getId(state));
                }

                state.onDestroyedByPushReaction(level, pos, direction, level.getFluidState(pos));
                toUpdate[updateIndex++] = state;
            }

            for (int i = toPush.size() - 1; i >= 0; --i) {
                BlockPos pos = toPush.get(i);
                BlockState blockState = level.getBlockState(pos);
                pos = pos.relative(pushDirection);
                deleteAfterMove.remove(pos);
                BlockState actualState = Blocks.MOVING_PISTON.defaultBlockState().setValue(FACING, direction);
                level.setBlock(pos, actualState, 324);
                level.setBlockEntity(MovingPistonBlock.newMovingBlockEntity(pos, actualState, toPushShapes.get(i), direction, extending, false));
                toUpdate[updateIndex++] = blockState;
            }

            if (extending) {
                BlockState state = WenyanBlocks.DECORATIVE_PISTON_HEAD_BLOCK.get().defaultBlockState()
                        .setValue(FACING, direction)
                        .setValue(DecorativePistonHead.TYPE, PistonType.DEFAULT);
                BlockState blockState = Blocks.MOVING_PISTON.defaultBlockState()
                        .setValue(MovingPistonBlock.FACING, direction)
                        .setValue(MovingPistonBlock.TYPE, PistonType.DEFAULT);
                deleteAfterMove.remove(armPos);
                level.setBlock(armPos, blockState, 324);
                level.setBlockEntity(MovingPistonBlock.newMovingBlockEntity(armPos, blockState, state, direction, true, true));
            }

            BlockState air = Blocks.AIR.defaultBlockState();

            for (BlockPos pos : deleteAfterMove.keySet()) {
                level.setBlock(pos, air, 82);
            }

            for (Map.Entry<BlockPos, BlockState> entry : deleteAfterMove.entrySet()) {
                BlockPos pos = entry.getKey();
                BlockState oldState = entry.getValue();
                oldState.updateIndirectNeighbourShapes(level, pos, 2);
                air.updateNeighbourShapes(level, pos, 2);
                air.updateIndirectNeighbourShapes(level, pos, 2);
            }

            Orientation orientation = ExperimentalRedstoneUtils.initialOrientation(level, resolver.getPushDirection(), null);
            updateIndex = 0;

            for (int i = toDestroy.size() - 1; i >= 0; --i) {
                BlockState state = toUpdate[updateIndex++];
                BlockPos pos = toDestroy.get(i);
                if (level instanceof ServerLevel serverLevel) {
                    state.affectNeighborsAfterRemoval(serverLevel, pos, false);
                }

                state.updateIndirectNeighbourShapes(level, pos, 2);
                level.updateNeighborsAt(pos, state.getBlock(), orientation);
            }

            for (int i = toPush.size() - 1; i >= 0; --i) {
                level.updateNeighborsAt(toPush.get(i), toUpdate[updateIndex++].getBlock(), orientation);
            }

            if (extending) {
                level.updateNeighborsAt(armPos, Blocks.PISTON_HEAD, orientation);
            }

            return true;
        }
    }

    public class PistonRequest implements BaseHandleableRequest.IRawRequest {
        private int life = 0;
        private final boolean extending;

        public PistonRequest(boolean extending) {
            this.extending = extending;
        }

        @Override
        public boolean handle(IHandleContext context, IArgsRequest request) throws WenyanException {
            life++;
            if (life == 1) {
                // TODO: rename vars and redefine error handling
                var pos = request.args().getFirst().as(WenyanVec3.TYPE).value();
                var direction = request.args().get(1).as(WenyanVec3.TYPE).value();
                Direction nearest = Direction.getNearest((int) direction.x, (int) direction.y, (int) direction.z, null);
                if (nearest == null) {
                    throw new WenyanException(ExceptionText.InvaildDirection.string());
                }
                assert level != null;
                BlockPos blockPos = getBlockPos().offset((int) pos.x, (int) pos.y, (int) pos.z);
                if (!extending) {
                    blockPos = blockPos.relative(nearest);
                    nearest = nearest.getOpposite();
                }
                if (!level.getBlockState(blockPos.relative(nearest.getOpposite())).isEmpty()) {
                    throw new WenyanException(ExceptionText.FailedToPlacePiston.string());
                }

                boolean success = triggleMoveBlock(level, extending, nearest, blockPos);
                if (!success) {
                    throw new WenyanException(ExceptionText.FailedToMoveBlock.string());
                }
                if (level instanceof ServerLevel sl)
                    PacketDistributor.sendToPlayersTrackingChunk(sl, ChunkPos.containing(blockPos),
                            new PistonMovePacket(blockPos, nearest, extending));
                return false;
            } else {
                if (life <= 3) return false;
                request.thread().getCurrentRuntime().pushReturnValue(WenyanNull.NULL);
                request.thread().unblock();
                return true;
            }
        }
    }
}
