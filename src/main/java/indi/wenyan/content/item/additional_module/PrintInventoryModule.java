package indi.wenyan.content.item.additional_module;

import indi.wenyan.interpreter.exec_interface.handler.HandlerPackageBuilder;
import lombok.Getter;

public class PrintInventoryModule extends AbstractInventoryModule {
    public static final String ID = "print_inventory_module";

    @Getter
    public final String packageName = "「a」";

//    @Getter
//    private final WenyanPackage execPackage = HandlerPackageBuilder.create()
//            .handler("「a」", new ThisCallHandler() {
//                @Override
//                public @NotNull IWenyanValue handleContext(@NotNull IHandleContext context, @NotNull JavacallRequest request)
//                        throws WenyanException.WenyanTypeException {
//                    if (!(context instanceof ItemContext itemContext)) {
//                        throw new WenyanException("unreached");
//                    }
//                    itemContext.player().displayClientMessage(
//                            Component.literal(request.args().getFirst().as(WenyanString.TYPE).value()),
//                            true
//                    );
//                    return WenyanNull.NULL;
//                }
//            })
//            .build();

    public PrintInventoryModule(Properties properties) {
        super(properties);
    }

    @Override
    public HandlerPackageBuilder.RawHandlerPackage getExecPackage() {
        return null;
    }
}
