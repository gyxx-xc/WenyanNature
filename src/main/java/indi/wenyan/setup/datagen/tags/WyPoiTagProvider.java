package indi.wenyan.setup.datagen.tags;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.setup.definitions.WenyanVillageTrade;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.tags.PoiTypeTags;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class WyPoiTagProvider extends PoiTypeTagsProvider {
    public WyPoiTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, WenyanProgramming.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider registries) {
        this.tag(PoiTypeTags.ACQUIRABLE_JOB_SITE)
                .add(
                        WenyanVillageTrade.WRITING_POI.getKey()
                );
    }
}
