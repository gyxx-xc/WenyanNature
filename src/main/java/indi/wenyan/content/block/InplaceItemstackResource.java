package indi.wenyan.content.block;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStackResourceHandler;
import org.jspecify.annotations.NonNull;

@Accessors(fluent = true)
public class InplaceItemstackResource {
    @Getter private final ResourceHandler<ItemResource> handler = createItemHandler();
    @Getter private ItemStack item = ItemStack.EMPTY;
    private final Runnable onUpdate;

    public InplaceItemstackResource(Runnable onUpdate) {
        this.onUpdate = onUpdate;
    }

    /// this method intend to be called only when loadData. For other cases, use handler instead
    public void replaceItem(ItemStack item) {
        this.item = item;
    }

    private ResourceHandler<ItemResource> createItemHandler() {
        return new ItemStackResourceHandler() {
            @Override
            protected @NonNull ItemStack getStack() {
                return item;
            }

            @Override
            protected void setStack(@NonNull ItemStack stack) {
                item = stack;
                onUpdate.run();
            }
        };
    }
}
