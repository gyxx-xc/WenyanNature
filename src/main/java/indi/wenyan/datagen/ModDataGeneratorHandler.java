package indi.wenyan.datagen;

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

        // 语言文件
//        event.getGenerator().addProvider(
//                event.includeClient(),
//                (DataProvider.Factory<ModLanguageProvider>) pOutput -> new ModLanguageProvider(pOutput,ExampleMod.MODID,"en_us")
//        );
        // 物品模型
        event.getGenerator().addProvider(
                event.includeClient(),
                (DataProvider.Factory<ModItemModelProvider>) pOutput -> new ModItemModelProvider(pOutput,WenyanNature.MODID,efh)
        );
        // 方块state
//        event.getGenerator().addProvider(
//                event.includeClient(),
//                (DataProvider.Factory<ModBlockStateProvider>) pOutput -> new ModBlockStateProvider(pOutput,ExampleMod.MODID,efh)
//        );

    }
}

