package indi.wenyan.content.item.additional_module;

import indi.wenyan.interpreter.structure.JavacallRequest;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import indi.wenyan.interpreter.utils.IHandleContext;
import indi.wenyan.interpreter.utils.ItemContext;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import lombok.Getter;
import net.minecraft.network.chat.Component;

public class PrintInventoryModule extends AbstractInventoryModule {
    public static final String ID = "print_inventory_module";

    @Getter
    public final String packageName = "「a」";

    @Getter
    private final WenyanPackage execPackage = WenyanPackageBuilder.create()
            .function("「a」", new ThisCallHandler() {
                @Override
                public IWenyanValue handleContext(IHandleContext context, JavacallRequest request) throws WenyanException.WenyanTypeException {
                    if (!(context instanceof ItemContext itemContext)) {
                        throw new WenyanException("unreached");
                    }
                    itemContext.player().displayClientMessage(
                            Component.literal(request.args().getFirst().as(WenyanString.TYPE).value()),
                            true
                    );
                    return WenyanNull.NULL;
                }
            })
            .build();

    public PrintInventoryModule(Properties properties) {
        super(properties);
    }
}
