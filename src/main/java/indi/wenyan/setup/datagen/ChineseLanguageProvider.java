package indi.wenyan.setup.datagen;

import indi.wenyan.setup.Registration;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ChineseLanguageProvider extends LanguageProvider {
    public ChineseLanguageProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        add(Registration.HAND_RUNNER.get(),"符咒");
        add(Registration.BAMBOO_PAPER.get(),"松竹纸");
        add(Registration.CLOUD_PAPER.get(),"云篆纸");
        add(Registration.STAR_PAPER.get(),"星辉纸");
        add(Registration.FROST_PAPER.get(),"霜华纸");
        add(Registration.PHOENIX_PAPER.get(),"凤羽纸");
        add(Registration.DRAGON_PAPER.get(),"龙鳞纸");
        add(Registration.ARCANE_INK.get(),"玄武墨");
        add(Registration.BAMBOO_INK.get(),"松清墨");
        add(Registration.CELESTIAL_INK.get(),"冥土墨");
        add(Registration.CINNABAR_INK.get(),"朱砂墨");
        add(Registration.LUNAR_INK.get(),"月华墨");
        add(Registration.STARLIGHT_INK.get(),"星光墨");


        add("error.wenyan_programming.variables_not_match","謬：參數非同於冊");
        add("error.wenyan_programming.cannot_assign_to_constant", "謬：常數者無變也");
        add("error.wenyan_programming.unknown_operator","謬：不識此算子");
        add("error.wenyan_programming.unknown_preposition","謬：不識此介詞");
        add("error.wenyan_programming.function_name_does_not_match","謬：函名非相符");
        add("error.wenyan_programming.last_result_is_null","謬：末次為空");
        add("error.wenyan_programming.not_implemented","謬：not implemented");

        add("error.wenyan_programming.invalid_float_number", "謬：不識此分數");
        add("error.wenyan_programming.invalid_bool_value", "謬：不識此爻");
        add("error.wenyan_programming.invalid_data_type", "謬：不識此參類");
        add("error.wenyan_programming.unexpected_character", "謬：意外之字元");
        add("error.wenyan_programming.number_of_arguments_does_not_match", "謬：參數數目不合");
        add("error.wenyan_programming.cannot_create_empty_of_null", "謬：空或虛無所不能造也");

        add("error.wenyan_programming.variable_not_found_", "謬：未尋之變數");
        add("error.wenyan_programming.function_not_found_", "謬：未尋之術");

        add("error.wenyan_programming.invalid_number", "謬：不識此數");

        add("error.wenyan_programming.cannot_cast_", "謬：不可轉");
        add("error.wenyan_programming._to_", "為");
        add("error.wenyan_programming.type_cannot_be_added", "謬：類不可相加");
        add("error.wenyan_programming.type_cannot_be_subtracted", "謬：類不可相減");
        add("error.wenyan_programming.type_cannot_be_multiplied", "謬：類不可相乘");
        add("error.wenyan_programming.type_cannot_be_divided", "謬：類不可相除");
        add("error.wenyan_programming.type_cannot_be_mod", "謬：類不可相餘");
        add("error.wenyan_programming.type_cannot_be_compared", "謬：類不可相比");

        add("error.wenyan_programming.for_iter", "謬：不可迭代之物");
        add("error.wenyan_programming.for_num", "謬：不可數之物");
        add("error.wenyan_programming.too_many_variables", "謬：變數過多");

        add("type.wenyan_programming.int", "數");
        add("type.wenyan_programming.double", "分數");
        add("type.wenyan_programming.bool", "爻");
        add("type.wenyan_programming.string", "言");
        add("type.wenyan_programming.list", "列");
        add("error.wenyan_programming.already_run", "已在運行");

        add("type.wenyan_programming.null", "空無");
        add("type.wenyan_programming.function", "術");
        add("type.wenyan_programming.object", "物");
        add("type.wenyan_programming.object_type", "類");

        add("title.wenyan_programming.create_tab", "吾有一術");

        add("config.wenyan_programming.main.title", "吾有一术 参数板");
        add("config.wenyan_programming.general.title", "通用");
        add("config.wenyan_programming.general.test", "测试");

        add("config.wenyan_programming.performance.title", "性能设置");
        add("config.wenyan_programming.performance.thread_limit", "线程限制");
        add("config.wenyan_programming.performance.thread_limit.description", "同时能够执行符咒的最大线程数。");

        add("config.wenyan_programming.advanced.title", "进阶设置");
        add("config.wenyan_programming.advanced.debug_mode", "调试模式");
        add("config.wenyan_programming.advanced.debug_mode.description", "启用此选项以在控制台中输出调试信息。");

    }
}
