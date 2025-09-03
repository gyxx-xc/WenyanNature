package indi.wenyan.setup.datagen;

import indi.wenyan.WenyanProgramming;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.data.SpriteSourceProvider;

import java.util.concurrent.CompletableFuture;

/**
 * Provider for generating sprite sources during data generation.
 * Defines sprite atlases and their contents.
 */
public class ModSpriteProvider extends SpriteSourceProvider {

    /**
     * Constructs a new sprite source provider.
     * @param output The pack output for sprite source generation
     * @param lookupProvider Future providing registry lookups
     */
    public ModSpriteProvider(PackOutput output,
                             CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, WenyanProgramming.MODID);
    }

    @Override
    protected void gather() {
        // Currently no sprite sources to gather
    }
}
