package indi.wenyan.setup.datagen;

import indi.wenyan.WenyanProgramming;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * Central handler for data generation.
 * Registers all data providers to be executed during data generation.
 */
@EventBusSubscriber(modid = WenyanProgramming.MODID)
public final class ModDataGeneratorHandler {

    /**
     * Event handler for gathering data providers.
     * Registers all necessary providers for mod assets and data.
     * @param event The gather data event
     */
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
//        generator.addProvider(
//                event.includeClient(),
//                (DataProvider.Factory<ModItemModelProvider>) pOutput ->
//                        new ModItemModelProvider(pOutput, WenyanProgramming.MODID,efh));
//        generator.addProvider(
//                event.includeClient(),
//                (DataProvider.Factory<CheckerRecipeProvider>) pOutput ->
//                        new CheckerRecipeProvider(pOutput,event.getLookupProvider()));
        event.createProvider(ModParticleDescriptionProvider::new);
        event.createProvider(ChineseLanguageProvider::new);
        event.createProvider(EnglishLanguageProvider::new);
        event.createProvider(ModSpriteProvider::new);
        event.createProvider(ModBlockStateProvider::new);
    }
}
