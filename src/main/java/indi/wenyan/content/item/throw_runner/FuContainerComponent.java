package indi.wenyan.content.item.throw_runner;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import com.mojang.serialization.Codec;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FuContainerComponent implements TooltipProvider {
    public static final String ID = "fu_data";
    public static final FuContainerComponent EMPTY = new FuContainerComponent(List.of());

    @Getter(AccessLevel.PRIVATE)
    private final List<ItemStackTemplate> items;

    @Override
    public void addToTooltip(Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag, DataComponentGetter dataComponentGetter) {
    }

    public static final Codec<FuContainerComponent> CODEC = ItemStackTemplate.CODEC
            .sizeLimitedListOf(256)
            .xmap(FuContainerComponent::new, FuContainerComponent::getItems);
    public static final StreamCodec<RegistryFriendlyByteBuf, FuContainerComponent> STREAM_CODEC = ItemStackTemplate.STREAM_CODEC
            .apply(ByteBufCodecs.list(256))
            .map(FuContainerComponent::new, c -> c.items);

    private FuContainerComponent(List<ItemStackTemplate> items) {
        if (items.size() > 256) {
            throw new IllegalArgumentException("Got " + items.size() + " items, but maximum is 256");
        } else {
            this.items = items;
        }
    }

    public List<ItemStack> createOne() {
        return items.stream().map(ItemStackTemplate::create).toList();
    }

    public static FuContainerComponent fromItemStack(List<ItemStack> stacks) {
        return new FuContainerComponent(stacks.stream()
                .filter(itemStack -> !itemStack.isEmpty())
                .map(ItemStackTemplate::fromNonEmptyStack).toList());
    }
}
