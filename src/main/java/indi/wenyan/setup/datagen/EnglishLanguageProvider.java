package indi.wenyan.setup.datagen;

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
        this.add(Registration.BAMBOO_PAPER.get(),"松竹纸");
        this.add(Registration.CLOUD_PAPER.get(),"云篆纸");
        this.add(Registration.STAR_PAPER.get(),"星辉纸");
        this.add(Registration.FROST_PAPER.get(),"霜华纸");
        this.add(Registration.PHOENIX_PAPER.get(),"凤羽纸");
        this.add(Registration.DRAGON_PAPER.get(),"龙鳞纸");
        this.add(Registration.ARCANE_INK.get(),"玄武墨");
        this.add(Registration.BAMBOO_INK.get(),"松清墨");
        this.add(Registration.CELESTIAL_INK.get(),"冥土墨");
        this.add(Registration.CINNABAR_INK.get(),"朱砂墨");
        this.add(Registration.LUNAR_INK.get(),"月华墨");
        this.add(Registration.STARLIGHT_INK.get(),"星光墨");



        this.add("error.wenyan_nature.variables_not_match","謬：參數非同於冊");
        this.add("error.wenyan_nature.cannot_assign_to_constant", "謬：常數者無變也");
        this.add("error.wenyan_nature.unknown_operator","謬：不識此算子");
        this.add("error.wenyan_nature.unknown_preposition","謬：不識此介詞");
        this.add("error.wenyan_nature.function_name_does_not_match","謬：函名非相符");
        this.add("error.wenyan_nature.last_result_is_null","謬：末次為空");
        this.add("error.wenyan_nature.not_implemented","謬：not implemented");

        this.add("error.wenyan_nature.invalid_float_number", "謬：不識此分數");
        this.add("error.wenyan_nature.invalid_bool_value", "謬：不識此爻");
        this.add("error.wenyan_nature.invalid_data_type", "謬：不識此參類");
        this.add("error.wenyan_nature.unexpected_character", "謬：意外之字元");
        this.add("error.wenyan_nature.number_of_arguments_does_not_match", "謬：參數數目不合");
        this.add("error.wenyan_nature.cannot_create_empty_of_null", "謬：空或虛無所不能造也");

        this.add("error.wenyan_nature.variable_not_found_", "謬：未尋之變數");
        this.add("error.wenyan_nature.function_not_found_", "謬：未尋之術");

        this.add("error.wenyan_nature.invalid_number", "謬：不識此數");

        this.add("error.wenyan_nature.cannot_cast_", "謬：不可轉");
        this.add("error.wenyan_nature._to_", "為");
        this.add("error.wenyan_nature.type_cannot_be_added", "謬：類不可相加");
        this.add("error.wenyan_nature.type_cannot_be_subtracted", "謬：類不可相減");
        this.add("error.wenyan_nature.type_cannot_be_multiplied", "謬：類不可相乘");
        this.add("error.wenyan_nature.type_cannot_be_divided", "謬：類不可相除");
        this.add("error.wenyan_nature.type_cannot_be_mod", "謬：類不可相餘");
        this.add("error.wenyan_nature.type_cannot_be_compared", "謬：類不可相比");
        this.add("error.wenyan_nature.for_iter", "謬：不可迭代之物");
        this.add("error.wenyan_nature.for_num", "謬：不可數之物");
        this.add("error.wenyan_nature.too_many_variables", "謬：變數過多");

        this.add("type.wenyan_nature.int", "數");
        this.add("type.wenyan_nature.double", "分數");
        this.add("type.wenyan_nature.bool", "爻");
        this.add("type.wenyan_nature.string", "言");
        this.add("type.wenyan_nature.list", "列");

        this.add("error.wenyan_nature.already_run", "已在運行");
        this.add("type.wenyan_nature.null", "空無");
        this.add("type.wenyan_nature.function", "術");
        this.add("type.wenyan_nature.object", "物");

        this.add("title.wenyan_nature.create_tab", "WenyanNature");

        this.add("config.wenyan_nature.main.title", "WenyanNature Configuration Panel");
        this.add("config.wenyan_nature.general.title", "General Settings");
        this.add("config.wenyan_nature.general.test", "Test");

        this.add("config.wenyan_nature.performance.title", "Performance Settings");
        this.add("config.wenyan_nature.performance.thread_limit", "Thread Limit");
        this.add("config.wenyan_nature.performance.thread_limit.description", "Set the maximum number of threads for WenyanNature. A higher value may improve performance, but also cause instability.");

        this.add("config.wenyan_nature.advanced.title", "进阶设置");
        this.add("config.wenyan_nature.advanced.debug_mode", "调试模式");
        this.add("config.wenyan_nature.advanced.debug_mode.description", "启用此选项以在控制台中输出调试信息。");
    }
}
