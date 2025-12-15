package indi.wenyan.content.item.additional_module;

import indi.wenyan.interpreter.exec_interface.IWenyanDevice;
import indi.wenyan.interpreter.exec_interface.handler.HandlerPackageBuilder;
import indi.wenyan.interpreter.exec_interface.structure.ItemContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public class PrintInventoryModule extends Item implements IWenyanDevice {
    public static final String ID = "print_inventory_module";

    @Getter
    public final String packageName = "「a」";

    @Getter
    private final HandlerPackageBuilder.RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler("「a」", (HandlerPackageBuilder.HandlerReturnFunction) (context, request) -> {
                if (!(context instanceof ItemContext itemContext)) {
                    throw new WenyanException("unreached");
                }
                itemContext.player().displayClientMessage(
                        Component.literal(request.args().getFirst().as(WenyanString.TYPE).value()),
                        true
                );
                return WenyanNull.NULL;
            })
            .build();

    public PrintInventoryModule(Properties properties) {
        super(properties);
    }
}
