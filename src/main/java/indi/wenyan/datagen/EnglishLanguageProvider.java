package indi.wenyan.datagen;

import indi.wenyan.setup.Registration;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class EnglishLanguageProvider extends LanguageProvider {
    public EnglishLanguageProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        this.add(Registration.HAND_RUNNER.get(),"符咒");

        this.add("error.wenyan_nature.variables_not_match","謬：參數非同於冊");
        this.add("error.wenyan_nature.cannot_assign_to_constant","謬：定值非也");
        this.add("error.wenyan_nature.unknown_operator","謬：不識此算子");
        this.add("error.wenyan_nature.unknown_preposition","謬：不識此介詞");
        this.add("error.wenyan_nature.function_name_does_not_match","謬：函名不相符");
        this.add("error.wenyan_nature.unknown_data_type","謬：不識此參類");
        this.add("error.wenyan_nature.last_result_is_null","謬：末次結果為空");
        this.add("error.wenyan_nature.variable_not_found:_","謬：變數未尋得");
        this.add("error.wenyan_nature.not_implemented","謬：尚未實行");
        this.add("type.wenyan_nature.null", "空無");
        this.add("type.wenyan_nature.function", "術");
        this.add("type.wenyan_nature.object", "物");

        this.add("error.wenyan_nature.invalid_float_number", "謬：非有效浮點數");
        this.add("error.wenyan_nature.invalid_bool_value", "謬：非有效布爾值");
        this.add("error.wenyan_nature.invalid_data_type", "謬：非有效數據型別");
        this.add("error.wenyan_nature.unexpected_character", "謬：有意外之字元");
        this.add("error.wenyan_nature.number_of_arguments_does_not_match", "謬：參數數目不合");
        this.add("error.wenyan_nature.cannot_create_empty_of_null", "謬：不能創造空或虛無");
    }
}
