package indi.wenyan.setup.datagen;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.setup.datagen.Language.WenyanLanguageProviderFactory;
import indi.wenyan.setup.datagen.model.ModItemModelProvider;
import indi.wenyan.setup.datagen.model.SubedModelProvider;
import indi.wenyan.setup.datagen.recipe.CheckerRecipeProvider;
import net.minecraft.data.DataProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;

/**
 * Central handler for data generation.
 * Registers all data providers to be executed during data generation.
 */
@EventBusSubscriber(modid = WenyanProgramming.MODID)
enum ModDataGeneratorHandler {;

    /**
     * Event handler for gathering data providers.
     * Registers all necessary providers for mod assets and data.
     * @param event The gather data event
     */
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event){
        var generator = event.getGenerator().getVanillaPack(true);
        generator.addProvider(WenyanLanguageProviderFactory.create("zh_cn"));
        generator.addProvider(WenyanLanguageProviderFactory.create("en_us"));
//        generator.addProvider(
//                (DataProvider.Factory<ModBlockStateProvider>) pOutput ->
//                        new ModBlockStateProvider(pOutput, WenyanProgramming.MODID));
        generator.addProvider(SubedModelProvider.of(ModItemModelProvider::new));
        generator.addProvider(ModParticleDescriptionProvider::new);
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Server event){
        var generator = event.getGenerator().getVanillaPack(true);
        generator.addProvider(
                pOutput -> new CheckerRecipeProvider(pOutput,event.getLookupProvider()));
        generator.addProvider(
                (DataProvider.Factory<AdvancementProvider>) pOutput ->
                        new AdvancementProvider(pOutput, event.getLookupProvider(), efh,
                                List.of((registries, saver, existingFileHelper) -> {
                                    var provider = new indi.wenyan.setup.datagen.AdvancementProvider();
                                    provider.generate(registries, saver, existingFileHelper);
                                })));
    }
}
