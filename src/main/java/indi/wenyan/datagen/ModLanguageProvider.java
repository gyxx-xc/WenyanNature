package indi.wenyan.datagen;

import indi.wenyan.item.WenyanHandRunner;
import indi.wenyan.setup.Registration;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {


    public ModLanguageProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }


    @Override
    protected void addTranslations() {
        this.add(Registration.HAND_RUNNER.get(),"符咒");
//        this.add(ModBlocks.RUBY_BLOCK.get(),"Ruby Block");

        //WenyanControlVistor

        //WenyanExprVistor
//        this.add("object.examplemod.example_object","Example Object");
        this.add("error.wenyan_nature.number_of_variables_does_not_match_number_of_values","謬：參數非同於冊");
        this.add("error.wenyan_nature.cannot_assign_to_constant","謬：定值非也");
        this.add("error.wenyan_nature.unknown_operator","謬：不識此算子");
        this.add("error.wenyan_nature.unknown_preposition","謬：不識此介詞");
        this.add("error.wenyan_nature.function_name_does_not_match","謬：函名不相符");

        //WenyanDataVistor
        this.add("error.wenyan_nature.unknown_data_type","謬：不識此參類");
        this.add("error.wenyan_nature.last_result_is_null","謬：末次結果為空");
        this.add("error.wenyan_nature.variable_not_found:_","謬：變數未尋得");

        //WenyanMainVistor
        this.add("error.wenyan_nature.not_implemented","謬：尚未實行");

        //Registration
        this.add("Wen Yan Nature","吾有一術");


    }
}

