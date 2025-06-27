package indi.wenyan.content.block;

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
        super(Registration.BLOCK_RUNNER.get(), pos, blockState);
        pages = new ArrayList<>();
        pages.add("書一");
    }
}
