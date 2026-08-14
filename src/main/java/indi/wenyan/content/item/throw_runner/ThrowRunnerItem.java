package indi.wenyan.content.item.throw_runner;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.content.entity.ThrowRunnerEntity;
import indi.wenyan.content.item.ITooltipAppendable;
import indi.wenyan.setup.definitions.RunnerTier;
import indi.wenyan.setup.definitions.WyRegistration;
import indi.wenyan.setup.language.GuiText;
import lombok.Getter;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ThrowRunnerItem extends Item implements ProjectileItem, ITooltipAppendable {
    public static final String ID = "throw_runner";

    @Getter
    private final RunnerTier tier;

    public ThrowRunnerItem(RunnerTier tier, Properties properties) {
        super(properties.useCooldown(1.0f).component(WyRegistration.FU_DATA.get(), FuContainerComponent.EMPTY));
        this.tier = tier;
        DispenserBlock.registerProjectileBehavior(this);
    }

    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (player.isCreative()) {
            if (level instanceof ServerLevel serverLevel)
                Projectile.spawnProjectileFromRotation(throwRunnerEntityFromTier(),
                        serverLevel, itemStack, player, 0.5F, 0.1F, 5.0F);
            itemStack.consume(1, player);
        } else
            player.sendSystemMessage(Component.literal("请使用展馆内的投掷器"));
        return InteractionResult.SUCCESS;
    }

    private Projectile.@NonNull ProjectileFactory<ThrowRunnerEntity> throwRunnerEntityFromTier() {
        return (s, p, l) ->
                new ThrowRunnerEntity(s, p, l, tier);
    }

    @Override
    public List<Component> getTooltip(TooltipFlag flags, ItemStack itemStack, TooltipContext context, @Nullable Player entity) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(GuiText.ThrowTooltip.text());
        var modules = itemStack.getOrDefault(WyRegistration.FU_DATA, FuContainerComponent.EMPTY).createOne();
        if (!modules.isEmpty()) {
            tooltip.add(GuiText.ThrowModule.text());
            tooltip.addAll(modules.stream().map(ItemStack::getDisplayName).toList());
        }
        return tooltip;
    }

    @Override
    public Projectile asProjectile(Level level, Position position, ItemStack itemStack, Direction direction) {
        return new ThrowRunnerEntity(level, position, itemStack, tier);
    }

    @Override
    public DispenseConfig createDispenseConfig() {
        return DispenseConfig.builder()
                .power(0.3f)
                .uncertainty(20.0f)
                .build();
    }
}
