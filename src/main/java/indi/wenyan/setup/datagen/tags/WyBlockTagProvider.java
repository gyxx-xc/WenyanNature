package indi.wenyan.setup.datagen.tags;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.definitions.WyRegistration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class WyBlockTagProvider extends BlockTagsProvider {

    public WyBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, WenyanProgramming.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {
        WenyanBlocks.RUNNER_BLOCK.getBlocks().forEach(block -> {
            tag(WyRegistration.RUNNABLE_BLOCK).add(block);
            // prevent carry-on shift click
            tag(Tags.Blocks.RELOCATION_NOT_SUPPORTED).add(block);
        });
        // prevent carry-on shift click
        tag(Tags.Blocks.RELOCATION_NOT_SUPPORTED).add(WenyanBlocks.WRITING_BLOCK.get());
    }
}
