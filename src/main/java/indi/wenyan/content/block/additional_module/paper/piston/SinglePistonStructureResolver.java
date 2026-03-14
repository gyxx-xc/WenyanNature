package indi.wenyan.content.block.additional_module.paper.piston;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

import java.util.List;

public class SinglePistonStructureResolver {
    public static final int MAX_PUSH_DEPTH = 1;
    private final Level level;
    private final boolean extending;
    private final BlockPos startPos;
    @Getter
    private final Direction pushDirection;
    @Getter
    private final List<BlockPos> toPush = Lists.newArrayList();
    @Getter
    private final List<BlockPos> toDestroy = Lists.newArrayList();
    private final Direction pistonDirection;

    public SinglePistonStructureResolver(Level level, BlockPos armPos, Direction direction, boolean extending) {
        this.level = level;
        this.pistonDirection = direction;
        this.extending = extending;
        if (extending) {
            this.pushDirection = direction;
            this.startPos = armPos;
        } else {
            this.pushDirection = direction.getOpposite();
            this.startPos = armPos.relative(direction, 1);
        }
    }

    public boolean resolve() {
        this.toPush.clear();
        this.toDestroy.clear();

        BlockState nextState = this.level.getBlockState(this.startPos);
        if (!PistonBaseBlock.isPushable(nextState, this.level, this.startPos, this.pushDirection, false, this.pistonDirection)) {
            if (this.extending && nextState.getPistonPushReaction() == PushReaction.DESTROY) {
                this.toDestroy.add(this.startPos);
                return true;
            } else {
                return false;
            }
        }


        if (this.addBlockLine(this.startPos, this.pushDirection)) {
            return false;
        }

        // for(:) cause CME
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < this.toPush.size(); ++i) {
            BlockPos pos = this.toPush.get(i);
            if (this.level.getBlockState(pos).isStickyBlock() && this.addBranchingBlocks(pos)) {
                return false;
            }
        }

        return true;
    }

    private boolean addBlockLine(BlockPos start, Direction direction) {
        BlockState nextState = this.level.getBlockState(start);
        if (nextState.isAir()) {
            return false;
        } else if (!PistonBaseBlock.isPushable(nextState, this.level, start, this.pushDirection, false, direction)) {
            return false;
        } else if (this.toPush.contains(start)) {
            return false;
        } else {
            int blockCount = 1;
            if (blockCount + this.toPush.size() > MAX_PUSH_DEPTH) {
                return true;
            } else {
                while (nextState.isStickyBlock()) {
                    BlockPos pos = start.relative(this.pushDirection.getOpposite(), blockCount);
                    BlockState oldState = nextState;
                    nextState = this.level.getBlockState(pos);
                    if (nextState.isAir() ||
                            !oldState.canStickTo(nextState) ||
                            !oldState.canStickTo(oldState) ||
                            !PistonBaseBlock.isPushable(nextState, this.level, pos, this.pushDirection,
                                    false, this.pushDirection.getOpposite())) {
                        break;
                    }

                    ++blockCount;
                    if (blockCount + this.toPush.size() > MAX_PUSH_DEPTH) {
                        return true;
                    }
                }

                int blocksAdded = 0;

                for (int i = blockCount - 1; i >= 0; --i) {
                    this.toPush.add(start.relative(this.pushDirection.getOpposite(), i));
                    ++blocksAdded;
                }

                int i = 1;

                while (true) {
                    BlockPos posx = start.relative(this.pushDirection, i);
                    int collisionPos = this.toPush.indexOf(posx);
                    if (collisionPos > -1) {
                        this.reorderListAtCollision(blocksAdded, collisionPos);

                        for (int j = 0; j <= collisionPos + blocksAdded; ++j) {
                            BlockPos blockPos = this.toPush.get(j);
                            if (this.level.getBlockState(blockPos).isStickyBlock() && this.addBranchingBlocks(blockPos)) {
                                return true;
                            }
                        }

                        return false;
                    }

                    nextState = this.level.getBlockState(posx);
                    if (nextState.isAir()) {
                        return false;
                    }

                    if (!PistonBaseBlock.isPushable(nextState, this.level, posx, this.pushDirection, true, this.pushDirection)) {
                        return true;
                    }

                    if (nextState.getPistonPushReaction() == PushReaction.DESTROY) {
                        this.toDestroy.add(posx);
                        return false;
                    }

                    if (!this.toPush.isEmpty()) {
                        return true;
                    }

                    this.toPush.add(posx);
                    ++blocksAdded;
                    ++i;
                }
            }
        }
    }

    private void reorderListAtCollision(int blocksAdded, int collisionPos) {
        List<BlockPos> head = Lists.newArrayList();
        List<BlockPos> lastLineAdded = Lists.newArrayList();
        List<BlockPos> collisionToLine = Lists.newArrayList();
        head.addAll(this.toPush.subList(0, collisionPos));
        lastLineAdded.addAll(this.toPush.subList(this.toPush.size() - blocksAdded, this.toPush.size()));
        collisionToLine.addAll(this.toPush.subList(collisionPos, this.toPush.size() - blocksAdded));
        this.toPush.clear();
        this.toPush.addAll(head);
        this.toPush.addAll(lastLineAdded);
        this.toPush.addAll(collisionToLine);
    }

    private boolean addBranchingBlocks(BlockPos fromPos) {
        BlockState fromState = this.level.getBlockState(fromPos);

        for (Direction direction : Direction.values()) {
            if (direction.getAxis() != this.pushDirection.getAxis()) {
                BlockPos neighbourPos = fromPos.relative(direction);
                BlockState neighbourState = this.level.getBlockState(neighbourPos);
                if (neighbourState.canStickTo(fromState) && fromState.canStickTo(neighbourState) && this.addBlockLine(neighbourPos, direction)) {
                    return true;
                }
            }
        }

        return false;
    }
}
