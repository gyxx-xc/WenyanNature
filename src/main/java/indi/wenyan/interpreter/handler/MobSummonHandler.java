package indi.wenyan.interpreter.handler;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.content.entity.HandlerEntity;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanValue;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MobSummonHandler {
    public static final WenyanValue.Type[] ARGS_TYPE =
            {WenyanValue.Type.INT, WenyanValue.Type.INT, WenyanValue.Type.INT};    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed"));
    private static final SimpleCommandExceptionType ERROR_DUPLICATE_UUID = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed.uuid"));
    private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(Component.translatable("commands.summon.invalidPosition"));
    private final HandRunnerEntity entity;
    private final Player holder;

    public MobSummonHandler(HandRunnerEntity entity, Player holder) {
        super();
        this.entity = entity;
        this.holder = holder;
    }
    @Override
    public WenyanValue handle(WenyanValue[] wenyan_args) throws WenyanException.WenyanThrowException {
        Object[] args = getArgs(wenyan_args, ARGS_TYPE);
        args[0] = Math.max(-10, Math.min(10, (int) args[0]));
        args[1] = Math.max(-10, Math.min(10, (int) args[1]));
        args[2] = Math.max(-10, Math.min(10, (int) args[2]));
        BlockPos blockPos = pos.offset((int) args[0], (int) args[1], (int) args[2]);

        HandlerEntity.levelRun(holder.level(), (level) -> createEntity( pos)
    }


    public static Entity createEntity(CommandSourceStack source, Holder.Reference<EntityType<?>> type, Vec3 pos, CompoundTag tag, boolean randomizeProperties) throws CommandSyntaxException {
        BlockPos blockpos = BlockPos.containing(pos);
        if (!Level.isInSpawnableBounds(blockpos)) {
            throw INVALID_POSITION.create();
        } else {
            CompoundTag compoundtag = tag.copy();
            compoundtag.putString("id", type.key().location().toString());
            ServerLevel serverlevel = source.getLevel();
            Entity entity = EntityType.loadEntityRecursive(compoundtag, serverlevel, (p_138828_) -> {
                p_138828_.moveTo(pos.x, pos.y, pos.z, p_138828_.getYRot(), p_138828_.getXRot());
                return p_138828_;
            });
            if (entity == null) {
                throw ERROR_FAILED.create();
            } else {
                if (randomizeProperties && entity instanceof Mob) {
                    ((Mob) entity).finalizeSpawn(source.getLevel(), source.getLevel().getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.COMMAND, (SpawnGroupData) null);
                }

                if (!serverlevel.tryAddFreshEntityWithPassengers(entity)) {
                    throw ERROR_DUPLICATE_UUID.create();
                } else {
                    return entity;
                }
            }
        }
    }

    private static int spawnEntity(CommandSourceStack source, Holder.Reference<EntityType<?>> type, Vec3
            pos, CompoundTag tag, boolean randomizeProperties) throws CommandSyntaxException {
        Entity entity = createEntity(source, type, pos, tag, randomizeProperties);
        source.sendSuccess(() -> {
            return Component.translatable("commands.summon.success", new Object[]{entity.getDisplayName()});
        }, true);
        return 1;
    }
}
