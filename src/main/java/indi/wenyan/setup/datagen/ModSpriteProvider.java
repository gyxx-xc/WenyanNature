package indi.wenyan.setup.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

import java.util.concurrent.CompletableFuture;

public class ModSpriteProvider extends SpriteSourceProvider {
    public ModSpriteProvider(PackOutput output,
                             CompletableFuture<HolderLookup.Provider> lookupProvider,
                             String modId, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void gather() {

    }
}
