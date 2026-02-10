package indi.wenyan.setup.datagen;

import indi.wenyan.setup.Registration;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

/**
 * Provider for generating English language translations during data generation.
 * Contains all English translations used in the mod, keeping transliteration for item names.
 */
public class EnglishLanguageProvider extends LanguageProvider {

    /**
     * Constructs a new English language provider.
     * @param output The pack output for language file generation
     * @param modid The mod ID
     * @param locale The locale code (en_us)
     */
    public EnglishLanguageProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        // Keep transliteration for item names
        add(Registration.HAND_RUNNER_0.get(),"Shou Pao");
        add(Registration.FLOAT_NOTE.get(), "Fu Qian");
        add(Registration.BAMBOO_PAPER.get(),"Zhu Zhi");
        add(Registration.CLOUD_PAPER.get(),"Yun Zhi");
        add(Registration.STAR_PAPER.get(),"Xing Zhi");
        add(Registration.FROST_PAPER.get(),"Shuang Zhi");
        add(Registration.PHOENIX_PAPER.get(),"Feng Zhi");
        add(Registration.DRAGON_PAPER.get(),"Long Zhi");
        add(Registration.ARCANE_INK.get(),"Xuan Mo");
        add(Registration.BAMBOO_INK.get(),"Zhu Mo");
        add(Registration.CELESTIAL_INK.get(),"Tian Mo");
        add(Registration.CINNABAR_INK.get(),"Dan Mo");
        add(Registration.LUNAR_INK.get(),"Yue Mo");
        add(Registration.STARLIGHT_INK.get(),"Xing Mo");

        // TODO: rename needed
        add(Registration.BIT_MODULE_BLOCK.get(), "位符");
        add(Registration.COLLECTION_MODULE_BLOCK.get(), "集符");
        add(Registration.MATH_MODULE_BLOCK.get(), "數符");
        add(Registration.RANDOM_MODULE_BLOCK.get(), "隨符");
        add(Registration.STRING_MODULE_BLOCK.get(), "字串符");
        add(Registration.VEC3_MODULE_BLOCK.get(), "向量符");
        add(Registration.BLOCK_MODULE_BLOCK.get(), "方塊符");
        add(Registration.COMMUNICATE_MODULE_BLOCK.get(), "通訊符");
        add(Registration.ENTITY_MODULE_BLOCK.get(), "實體符");
        add(Registration.EXPLOSION_MODULE_BLOCK.get(), "爆裂符");
        add(Registration.ITEM_MODULE_BLOCK.get(), "物品符");
        add(Registration.INFORMATION_MODULE_BLOCK.get(), "天下情報符");

        add(Registration.PRINT_INVENTORY_MODULE.get(), "印符");

        add(Registration.SCREEN_MODULE_BLOCK.get(), "螢幕石");
        add(Registration.SEMAPHORE_MODULE_BLOCK.get(), "信號量石");

        add(Registration.EQUIPABLE_RUNNER_ITEM.get(), "可裝符");

        add(Registration.CRAFTING_BLOCK.get(), "創石");
        add(Registration.PEDESTAL_BLOCK.get(), "石石");

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

        add("error.wenyan_programming.set_value_to_non_left_value", "謬：非左值不可設也");
        add("error.wenyan_programming.import_package_not_found", "謬：未尋之籍%s");

        add("type.wenyan_programming.int", "數");
        add("type.wenyan_programming.double", "分數");
        add("type.wenyan_programming.bool", "爻");
        add("type.wenyan_programming.string", "言");
        add("type.wenyan_programming.list", "列");
        add("error.wenyan_programming.already_run", "Already Running");

        add("type.wenyan_programming.null", "空無");
        add("type.wenyan_programming.function", "術");
        add("type.wenyan_programming.object", "物");
        add("type.wenyan_programming.object_type", "類");

        add("title.wenyan_programming.create_tab", "I Have a Technique");

        add("config.wenyan_programming.main.title", "I Have a Technique Configuration Panel");
        add("config.wenyan_programming.general.title", "General");
        add("config.wenyan_programming.general.test", "Test");

        add("config.wenyan_programming.performance.title", "Performance Settings");
        add("config.wenyan_programming.performance.thread_limit", "Thread Limit");
        add("config.wenyan_programming.performance.thread_limit.description", "Maximum number of threads that can execute charms simultaneously.");

        add("config.wenyan_programming.advanced.title", "Advanced Settings");
        add("config.wenyan_programming.advanced.debug_mode", "Debug Mode");
        add("config.wenyan_programming.advanced.debug_mode.description", "Enable this option to output debug information to the console.");

        add("code.wenyan_programming.bracket", "「%s」");
        add("gui.wenyan.hold_shift", "(Hold Shift to display details)");

        add("book.wenyan_programming.shuo_wen.name", "說文");
        add("book.wenyan_programming.shuo_wen.landing_text", "编程者，制机之令也。机铁无知，唯识原语。乃作典言，上合人意，下译机识，若算经然。");
    }
}
