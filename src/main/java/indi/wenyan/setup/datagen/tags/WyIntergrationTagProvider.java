package indi.wenyan.setup.datagen.tags;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.setup.definitions.WenyanBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class WyIntergrationTagProvider extends BlockTagsProvider {

    public WyIntergrationTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, WenyanProgramming.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {
        // prevent carry-on shift click
        WenyanBlocks.RUNNER_BLOCK.getBlocks().forEach(block -> tag(Tags.Blocks.RELOCATION_NOT_SUPPORTED).add(block));
        tag(Tags.Blocks.RELOCATION_NOT_SUPPORTED).add(WenyanBlocks.WRITING_BLOCK.get());
    }
}
