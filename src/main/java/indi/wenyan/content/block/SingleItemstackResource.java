package indi.wenyan.content.block;

import lombok.experimental.Accessors;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

@Accessors(fluent = true)
public class SingleItemstackResource extends ItemStacksResourceHandler {
    private final Runnable onUpdate;

    public SingleItemstackResource(Runnable onUpdate) {
        super(1);
        this.onUpdate = onUpdate;
    }
}
