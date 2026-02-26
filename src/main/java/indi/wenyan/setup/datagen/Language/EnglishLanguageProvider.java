package indi.wenyan.setup.datagen.Language;

import indi.wenyan.setup.definitions.WenyanItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.common.data.LanguageProvider;

/**
 * Provider for generating English language translations during data generation.
 * Contains all English translations used in the mod, keeping transliteration for item names.
 */
public class EnglishLanguageProvider extends LanguageProvider {

    /**
     * Constructs a new English language provider.
     *
     * @param output The pack output for language file generation
     * @param modid  The mod ID
     * @param locale The locale code (en_us)
     */
    public EnglishLanguageProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        add(WenyanItems.HAND_RUNNER_0.get(), "一阶符");
        add(WenyanItems.HAND_RUNNER_1.get(), "二阶符");
        add(WenyanItems.HAND_RUNNER_2.get(), "三阶符");
        add(WenyanItems.HAND_RUNNER_3.get(), "四阶符");

        add(WenyanItems.FLOAT_NOTE.get(), "浮签");
        add(WenyanItems.BAMBOO_PAPER.get(), "松竹纸 (Lv.1)");
        add(WenyanItems.CLOUD_PAPER.get(), "云篆纸 (Lv.2)");
        add(WenyanItems.STAR_PAPER.get(), "星辉纸 (Lv.3)");
        add(WenyanItems.FROST_PAPER.get(), "霜华纸 (Lv.4)");
        add(WenyanItems.PHOENIX_PAPER.get(), "凤羽纸 (Lv.5)");
        add(WenyanItems.DRAGON_PAPER.get(), "龙鳞纸 (Lv.6)");

        add(WenyanItems.BAMBOO_INK.get(), "松清墨 (Lv.1)");
        add(WenyanItems.CINNABAR_INK.get(), "朱砂墨 (Lv.2)");
        add(WenyanItems.STARLIGHT_INK.get(), "星光墨 (Lv.3)");
        add(WenyanItems.LUNAR_INK.get(), "月华墨 (Lv.4)");
        add(WenyanItems.CELESTIAL_INK.get(), "冥土墨 (Lv.5)");
        add(WenyanItems.ARCANE_INK.get(), "玄武墨 (Lv.6)");

        addBlockAndItem(WenyanItems.BIT_MODULE_BLOCK_ITEM.get(), "位元符");
        addBlockAndItem(WenyanItems.COLLECTION_MODULE_BLOCK_ITEM.get(), "集符");
        addBlockAndItem(WenyanItems.MATH_MODULE_BLOCK_ITEM.get(), "數符");
        addBlockAndItem(WenyanItems.RANDOM_MODULE_BLOCK_ITEM.get(), "熵符");
        addBlockAndItem(WenyanItems.STRING_MODULE_BLOCK_ITEM.get(), "字串符");
        addBlockAndItem(WenyanItems.VEC3_MODULE_BLOCK_ITEM.get(), "向量符");
        addBlockAndItem(WenyanItems.BLOCK_MODULE_BLOCK_ITEM.get(), "方塊符");
        addBlockAndItem(WenyanItems.COMMUNICATE_MODULE_BLOCK_ITEM.get(), "通訊符");
        addBlockAndItem(WenyanItems.ENTITY_MODULE_BLOCK_ITEM.get(), "實體符");
        addBlockAndItem(WenyanItems.EXPLOSION_MODULE_BLOCK_ITEM.get(), "爆裂符");
        addBlockAndItem(WenyanItems.ITEM_MODULE_BLOCK_ITEM.get(), "物品符");
        addBlockAndItem(WenyanItems.INFORMATION_MODULE_BLOCK_ITEM.get(), "天下情報符");


        addBlockAndItem(WenyanItems.SCREEN_MODULE_BLOCK_ITEM.get(), "螢幕石");
        addBlockAndItem(WenyanItems.LOCK_MODULE_BLOCK_ITEM.get(), "信號量石");

        addBlockAndItem(WenyanItems.CRAFTING_BLOCK_ITEM.get(), "創石");
        addBlockAndItem(WenyanItems.PEDESTAL_BLOCK_ITEM.get(), "基石");
        addBlockAndItem(WenyanItems.POWER_BLOCK_ITEM.get(), "算核");

        add(WenyanItems.PRINT_INVENTORY_MODULE.get(), "印符");
        add(WenyanItems.EQUIPABLE_RUNNER_ITEM.get(), "可戴符");

        add("error.wenyan_programming.variables_not_match", "謬：參數非同於冊");
        add("error.wenyan_programming.cannot_assign_to_constant", "謬：常數者無變也");
        add("error.wenyan_programming.unknown_operator", "謬：不識此算子");
        add("error.wenyan_programming.unknown_preposition", "謬：不識此介詞");
        add("error.wenyan_programming.function_name_does_not_match", "謬：函名非相符");
        add("error.wenyan_programming.last_result_is_null", "謬：末次為空");
        add("error.wenyan_programming.not_implemented", "謬：not implemented");

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

        add("gui.wenyan.lock","锁定");

        add("title.wenyan_programming.create_tab", "吾有一術");

        add("config.wenyan_programming.main.title", "吾有一術 控制面板");
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

    private void addBlockAndItem(BlockItem blockItem, String name) {
        add(blockItem.getDescriptionId(), name);
        add(blockItem.getBlock(), name);
    }
}
