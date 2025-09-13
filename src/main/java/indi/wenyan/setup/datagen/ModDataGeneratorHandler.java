package indi.wenyan.setup.datagen;

import indi.wenyan.WenyanProgramming;
import net.minecraft.data.DataProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * Central handler for data generation.
 * Registers all data providers to be executed during data generation.
 */
@EventBusSubscriber(modid = WenyanProgramming.MODID,bus = EventBusSubscriber.Bus.MOD)
public final class ModDataGeneratorHandler {

    /**
     * Event handler for gathering data providers.
     * Registers all necessary providers for mod assets and data.
     * @param event The gather data event
     */
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        ExistingFileHelper efh = event.getExistingFileHelper();
        var generator = event.getGenerator();
        generator.addProvider(
                event.includeClient(),
                (DataProvider.Factory<ChineseLanguageProvider>) pOutput ->
                        new ChineseLanguageProvider(pOutput, WenyanProgramming.MODID,"zh_cn"));
        generator.addProvider(
                event.includeClient(),
                (DataProvider.Factory<EnglishLanguageProvider>) pOutput ->
                        new EnglishLanguageProvider(pOutput, WenyanProgramming.MODID,"en_us"));
        generator.addProvider(
                event.includeClient(),
                (DataProvider.Factory<ModBlockStateProvider>) pOutput ->
                        new ModBlockStateProvider(pOutput, WenyanProgramming.MODID,efh));
        generator.addProvider(
                event.includeClient(),
                (DataProvider.Factory<ModItemModelProvider>) pOutput ->
                        new ModItemModelProvider(pOutput, WenyanProgramming.MODID,efh));
        generator.addProvider(
                event.includeClient(),
                (DataProvider.Factory<ModSpriteProvider>) pOutput ->
                        new ModSpriteProvider(pOutput, event.getLookupProvider(),
                                WenyanProgramming.MODID, efh));
        generator.addProvider(
                event.includeClient(),
                (DataProvider.Factory<CheckerRecipeProvider>) pOutput ->
                        new CheckerRecipeProvider(pOutput,event.getLookupProvider()));
        generator.addProvider(
                event.includeClient(),
                (DataProvider.Factory<ModParticleDescriptionProvider>) pOutput ->
                        new ModParticleDescriptionProvider(pOutput, efh));
    }
}
