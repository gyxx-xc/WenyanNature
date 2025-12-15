package indi.wenyan.content.block.additional_module.paper;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter.exec_interface.handler.HandlerPackageBuilder;
import indi.wenyan.interpreter.structure.values.primitive.WenyanDouble;
import indi.wenyan.interpreter.structure.values.warper.WenyanVec3;
import indi.wenyan.interpreter.utils.WenyanSymbol;
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
    private final String basePackageName = WenyanSymbol.var("EntityModule");

    @Getter
    private final HandlerPackageBuilder.RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler(WenyanSymbol.var("EntityModule.inspectRange"), request -> {
                    assert getLevel() != null;
                    Vec3 start = request.args().getFirst().as(WenyanVec3.TYPE).value();
                    Vec3 end = request.args().getLast().as(WenyanVec3.TYPE).value();
                    List<Entity> entities = getLevel().getEntities((Entity) null,
                            new AABB(start, end), EntitySelector.NO_SPECTATORS);
                    // convert to WenyanList[WenyanEntity, WenyanEntity, WenyanEntity]
                    return WenyanValues.of(entities.stream().map(WenyanValues::of).toList());
                })
            .handler(WenyanSymbol.var("EntityModule.nearby"), request -> {
                    assert getLevel() != null;
                    double radius = request.args().getFirst().as(WenyanDouble.TYPE).value();
                    BlockPos pos = blockPos();
                    List<Entity> entities = getLevel().getEntities((Entity) null,
                            new AABB(pos).inflate(radius), EntitySelector.NO_SPECTATORS);
                    // convert to WenyanList[WenyanEntity, WenyanEntity, WenyanEntity]
                    return WenyanValues.of(entities.stream().map(WenyanValues::of).toList());
                })
            .handler(WenyanSymbol.var("EntityModule.lineOfSight"), request -> {
                    var origin = request.args().getFirst().as(WenyanVec3.TYPE).value();
                    var look = request.args().getLast().as(WenyanVec3.TYPE).value();
                    assert getLevel() != null;
                    var result = getLevel().clip(new ClipContext(origin, look,
                            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity) null));
                    return WenyanValues.of(getLevel().getBlockState(result.getBlockPos()));
                })
            .build();

    public EntityModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.ENTITY_MODULE_ENTITY.get(), pos, blockState);
    }
}
