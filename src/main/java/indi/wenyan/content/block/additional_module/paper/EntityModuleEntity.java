package indi.wenyan.content.block.additional_module.paper;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.primitive.WenyanDouble;
import indi.wenyan.interpreter.structure.values.warper.WenyanVec3;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.interpreter.utils.WenyanValues;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class EntityModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = "「实」";

    @Getter
    private final WenyanPackage execPackage = WenyanPackageBuilder.create()
            .function("「察域之实」", new ThisCallHandler() { // "察域之实" means "Inspect entities within a range"
                @Override
                public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
                    assert getLevel() != null;
                    Vec3 start = context.args().getFirst().as(WenyanVec3.TYPE).value();
                    Vec3 end = context.args().getLast().as(WenyanVec3.TYPE).value();
                    List<Entity> entities = getLevel().getEntities((Entity) null,
                            new AABB(start, end), EntitySelector.NO_SPECTATORS);
                    // convert to WenyanList[WenyanEntity, WenyanEntity, WenyanEntity]
                    return WenyanValues.of(entities.stream().map(WenyanValues::of).toList());
                }
            })
            .function("「近域之实」", new ThisCallHandler() { // "近域之实" means "Entities near a point"
                @Override
                public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
                    assert getLevel() != null;
                    double radius = context.args().getFirst().as(WenyanDouble.TYPE).value();
                    BlockPos pos = getBlockPos();
                    List<Entity> entities = getLevel().getEntities((Entity) null,
                            new AABB(pos).inflate(radius), EntitySelector.NO_SPECTATORS);
                    // convert to WenyanList[WenyanEntity, WenyanEntity, WenyanEntity]
                    return WenyanValues.of(entities.stream().map(WenyanValues::of).toList());
                }
            })
            .function("「实之视」", new ThisCallHandler() { // "视域之实" means "Entities in line of sight"
                @Override
                public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
                    var origin = context.args().getFirst().as(WenyanVec3.TYPE).value();
                    var look = context.args().getLast().as(WenyanVec3.TYPE).value();
                    assert getLevel() != null;
                    var result = getLevel().clip(new ClipContext(origin, look,
                            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity) null));
                    return WenyanValues.of(getLevel().getBlockState(result.getBlockPos()));
                }
            })
            .build();

    public EntityModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.ENTITY_MODULE_ENTITY.get(), pos, blockState);
    }
}
