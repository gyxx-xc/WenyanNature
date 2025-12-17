package indi.wenyan.content.block.power;

import indi.wenyan.content.block.additional_module.IModulerBlock;
import indi.wenyan.setup.Registration;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PowerBlock extends Block implements IModulerBlock {
    public static final String ID = "power_block";
    public static final Properties PROPERTIES = Properties.of();

    public PowerBlock() {
        super(PROPERTIES);
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return Registration.POWER_BLOCK_ENTITY.get();
    }
}
