package indi.wenyan.setup.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

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
     * @param modId The mod ID
     * @param existingFileHelper Helper for accessing existing files
     */
    public ModSpriteProvider(PackOutput output,
                             CompletableFuture<HolderLookup.Provider> lookupProvider,
                             String modId, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void gather() {
        // Currently no sprite sources to gather
    }
}
