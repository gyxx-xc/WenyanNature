package indi.wenyan.content.item;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.content.entity.ThrowRunnerEntity;
import indi.wenyan.setup.definitions.RunnerTier;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ThrowRunnerItem extends Item {
    public static final String ID = "throw_runner";

    @Getter
    private final RunnerTier tier;

    public ThrowRunnerItem(RunnerTier tier, Properties properties) {
        super(properties.useCooldown(1.0f));
        this.tier = tier;
    }

    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (level instanceof ServerLevel serverLevel) {
            Projectile.spawnProjectileFromRotation(throwRunnerEntityFromTier(),
                    serverLevel, itemStack, player, 0.5F, 0.1F, 5.0F);
        }

        itemStack.consume(1, player);
        return InteractionResult.SUCCESS;
    }

    private Projectile.@NonNull ProjectileFactory<ThrowRunnerEntity> throwRunnerEntityFromTier() {
        return (s, p, l) ->
                new ThrowRunnerEntity(s, p, l, tier);
    }
}
