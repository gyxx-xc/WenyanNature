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

         //语言文件 (zh_cn)
        event.getGenerator().addProvider(
                event.includeClient(),
                (DataProvider.Factory<ChineseLanguageProvider>) pOutput ->
                        new ChineseLanguageProvider(pOutput,WenyanNature.MODID,"zh_cn")
        );
        event.getGenerator().addProvider(
                event.includeClient(),
                (DataProvider.Factory<EnglishLanguageProvider>) pOutput ->
                        new EnglishLanguageProvider(pOutput,WenyanNature.MODID,"en_us")
        );

        // 物品模型
        event.getGenerator().addProvider(
                event.includeClient(),
                (DataProvider.Factory<ModItemModelProvider>) pOutput ->
                        new ModItemModelProvider(pOutput,WenyanNature.MODID,efh)
        );

        event.getGenerator().addProvider(
                event.includeClient(),
                (DataProvider.Factory<ModBlockStateProvider>) pOutput ->
                        new ModBlockStateProvider(pOutput,WenyanNature.MODID,efh)
        );

    }
}

