package indi.wenyan.content.item.additional_module;

import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.utils.IWenyanBlockDevice;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import lombok.Getter;
import net.minecraft.world.phys.Vec3;

public class PrintInventoryModule extends AbstractInventoryModule implements IWenyanBlockDevice {
    @Getter
    public final String packageName = "a";

    @Getter
    private final WenyanPackage execPackage = WenyanPackageBuilder.create().build();

    public PrintInventoryModule(Properties properties) {
        super(properties);
    }

    @Override
    public Vec3 getPosition() {
        return null;
    }
}
