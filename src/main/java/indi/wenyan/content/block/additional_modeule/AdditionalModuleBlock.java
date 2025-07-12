package indi.wenyan.content.block.additional_modeule;

import indi.wenyan.content.handler.IExecCallHandler;
import indi.wenyan.content.handler.IJavacallHandler;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanNull;
import indi.wenyan.interpreter.utils.IWenyanExecutor;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.setup.Registration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AdditionalModuleBlock extends Block implements EntityBlock {
    public static final Properties PROPERTIES = Properties.of();

    public AdditionalModuleBlock() {
        super(PROPERTIES);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return Registration.ADDITIONAL_MODULE_BLOCK.get().newBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T>
    getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return (level1, pos, state1, entity) -> {
            if (blockEntityType == Registration.ADDITIONAL_MODULE_ENTITY.get())
                ((AdditionalModuleEntity) entity).execQueue.handle();
        };
    }

    public static class AdditionalModuleEntity extends BlockEntity implements IWenyanExecutor {
        public final ExecQueue execQueue = new ExecQueue();

        public AdditionalModuleEntity(BlockPos pos, BlockState blockState) {
            super(Registration.ADDITIONAL_MODULE_ENTITY.get(), pos, blockState);
        }
        @Override
        public WenyanRuntime getPackage() {
            return WenyanPackageBuilder.create()
                    .function("「a」", new IExecCallHandler() {
                        @Override
                        public IWenyanValue handle(JavacallContext context) {
                            assert level != null;
                            Entity e = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
                            e.moveTo(getBlockPos().getCenter());
                            level.addFreshEntity(e);
                            return WenyanNull.NULL;
                        }

                        @Override
                        public Optional<IWenyanExecutor> getExecutor() {
                            return Optional.of(AdditionalModuleEntity.this);
                        }
                    })
                    .build();
        }

        @Override
        public String packageName() {
            return "「「a」」";
        }

        @Override
        public ExecQueue getExecQueue() {
            return execQueue;
        }
    }
}
