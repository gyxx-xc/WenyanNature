package indi.wenyan.content.item.ink;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import com.mojang.serialization.MapCodec;
import indi.wenyan.setup.definitions.WyRegistration;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BambooInk extends Item {
    public static final String ID = "bamboo_ink";

    public BambooInk(Properties properties) {
        super(properties.component(DataComponents.CONSUMABLE,
                Consumable.builder()
                        .consumeSeconds(0.2f)
                        .animation(ItemUseAnimation.NONE)
                        .soundAfterConsume(SoundEvents.ITEM_BREAK)
                        .onConsume(NoFireEffect.INSTANCE)
                        .build())
                .useCooldown(1));
    }

    public enum NoFireEffect implements ConsumeEffect {
        INSTANCE;

        public static final MapCodec<NoFireEffect> CODEC = MapCodec.unit(INSTANCE);
        public static final StreamCodec<RegistryFriendlyByteBuf, NoFireEffect> STREAM_CODEC = StreamCodec.unit(INSTANCE);

        public ConsumeEffect.Type<NoFireEffect> getType() {
            return WyRegistration.NO_FIRE_EFFECT.get();
        }

        public boolean apply(Level level, ItemStack stack, LivingEntity user) {
            user.clearFire();
            return true;
        }
    }
}
