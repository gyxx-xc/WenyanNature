package indi.wenyan.setup.datagen;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.setup.datagen.Language.WenyanLanguageProviderFactory;
import indi.wenyan.setup.datagen.model.ModBlockStateProvider;
import indi.wenyan.setup.datagen.model.ModItemModelProvider;
import indi.wenyan.setup.datagen.model.SubedModelProvider;
import indi.wenyan.setup.datagen.recipe.CheckerRecipeProvider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.registries.RegistryPatchGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * Central handler for data generation.
 * Registers all data providers to be executed during data generation.
 */
@EventBusSubscriber(modid = WenyanProgramming.MODID)
enum ModDataGeneratorHandler {
    ;

    /**
     * Event handler for gathering data providers.
     * Registers all necessary providers for mod assets and data.
     *
     * @param event The gather data event
     */
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        var generator = event.getGenerator().getVanillaPack(true);
        generator.addProvider(WenyanLanguageProviderFactory.create("zh_cn"));
        generator.addProvider(WenyanLanguageProviderFactory.create("en_us"));
        generator.addProvider(SubedModelProvider.of(
                ModBlockStateProvider::new,
                ModItemModelProvider::new));
        generator.addProvider(ModParticleDescriptionProvider::new);
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Server event) {
        var registries = RegistryPatchGenerator.createLookup(event.getLookupProvider(), new RegistrySetBuilder())
                .thenApply(RegistrySetBuilder.PatchedRegistries::full);
        var generator = event.getGenerator().getVanillaPack(true);
        generator.addProvider(output -> new CheckerRecipeProvider.Runner(output, registries));
    }
}
