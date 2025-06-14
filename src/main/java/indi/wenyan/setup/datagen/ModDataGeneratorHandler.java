package indi.wenyan.setup.datagen;

import indi.wenyan.WenyanNature;
import net.minecraft.data.DataProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = WenyanNature.MODID,bus = EventBusSubscriber.Bus.MOD)
public class ModDataGeneratorHandler {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        ExistingFileHelper efh = event.getExistingFileHelper();
        var generator = event.getGenerator();
        generator.addProvider(
                event.includeClient(),
                (DataProvider.Factory<ChineseLanguageProvider>) pOutput ->
                        new ChineseLanguageProvider(pOutput,WenyanNature.MODID,"zh_cn"));
        generator.addProvider(
                event.includeClient(),
                (DataProvider.Factory<EnglishLanguageProvider>) pOutput ->
                        new EnglishLanguageProvider(pOutput,WenyanNature.MODID,"en_us"));
        generator.addProvider(
                event.includeClient(),
                (DataProvider.Factory<ModItemModelProvider>) pOutput ->
                        new ModItemModelProvider(pOutput,WenyanNature.MODID,efh));
        generator.addProvider(
                event.includeClient(),
                (DataProvider.Factory<ModBlockStateProvider>) pOutput ->
                        new ModBlockStateProvider(pOutput,WenyanNature.MODID,efh));
        generator.addProvider(
                event.includeClient(),
                (DataProvider.Factory<CheckerRecipeProvider>) pOutput ->
                        new CheckerRecipeProvider(pOutput,event.getLookupProvider())
        );
    }
}

