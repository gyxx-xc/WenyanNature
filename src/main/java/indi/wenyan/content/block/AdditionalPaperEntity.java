package indi.wenyan.content.block;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class AdditionalPaperEntity extends BlockEntity {
    // change to WP someday
//    public WenyanProgram program;

    public List<String> pages;

    public AdditionalPaperEntity(BlockPos pos, BlockState blockState) {
        super(Registration.RUNNER_BLOCK_ENTITY.get(), pos, blockState);
        pages = new ArrayList<>();
        pages.add("書一");
    }

//    public WenyanRuntime provide_package(){
//        return WenyanPackageBuilder.create()
//                .function("a", this::a)
//                .build();
//    }

    private IWenyanValue a(JavacallContext context) {
        return new WenyanString(pages.getFirst());
    }
}
