package indi.wenyan.setup.datagen;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.setup.datagen.Language.WenyanLanguageProviderFactory;
import indi.wenyan.setup.datagen.loot.WenyanLootTableProvider;
import indi.wenyan.setup.datagen.model.ModBlockStateProvider;
import indi.wenyan.setup.datagen.model.ModItemModelProvider;
import indi.wenyan.setup.datagen.model.SubedModelProvider;
import indi.wenyan.setup.datagen.recipe.CheckerRecipeProvider;
import indi.wenyan.setup.datagen.tags.WyItemTagProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * Central handler for data generation.
 * Registers all data providers to be executed during data generation.
 */
@EventBusSubscriber(modid = WenyanProgramming.MODID)
public enum ModDataGeneratorHandler {
    ;

    /**
     * Event handler for gathering data providers.
     * Registers all necessary providers for mod assets and data.
     *
     * @param event The gather data event
     */
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        var registries = event.getLookupProvider();
        var generator = event.getGenerator().getVanillaPack(true);
        generator.addProvider(WenyanLanguageProviderFactory.create("zh_cn"));
        generator.addProvider(WenyanLanguageProviderFactory.create("en_us"));
        generator.addProvider(SubedModelProvider.of(
                ModBlockStateProvider::new,
                ModItemModelProvider::new));
        generator.addProvider(ModParticleDescriptionProvider::new);
        generator.addProvider(output -> new CheckerRecipeProvider.Runner(output, registries));
        generator.addProvider(packOutput -> new WyItemTagProvider(packOutput, registries));
        generator.addProvider(p -> new WenyanLootTableProvider(p, registries));
    }

    @SubscribeEvent
    public static void gatherDataServer(GatherDataEvent.Server event) {
        // seen still has bug, and useless btw
    }
}
