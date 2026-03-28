package indi.wenyan.setup.datagen.Language;

import indi.wenyan.setup.definitions.WenyanItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.List;

import static indi.wenyan.judou.utils.language.JudouExceptionText.*;
import static indi.wenyan.judou.utils.language.JudouTypeText.*;
import static indi.wenyan.setup.language.ConfigText.*;
import static indi.wenyan.setup.language.ExceptionText.*;
import static indi.wenyan.setup.language.GuiText.*;
import static indi.wenyan.setup.language.TypeText.*;

/**
 * Provider for generating Chinese language translations during data generation.
 * Contains all Chinese translations used in the mod.
 */
public class ChineseLanguageProvider extends LanguageProvider {

    /**
     * Constructs a new Chinese language provider.
     *
     * @param output The pack output for language file generation
     * @param modid  The mod ID
     * @param locale The locale code (zh_cn)
     */
    public ChineseLanguageProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        add(WenyanItems.FLOAT_NOTE.get(), "浮签");
        add(WenyanItems.BAMBOO_PAPER.get(), "松竹纸 (Lv.1)");
        add(WenyanItems.CLOUD_PAPER.get(), "云篆纸 (Lv.2)");
        add(WenyanItems.STARLIGHT_PAPER.get(), "星辉纸 (Lv.3)");
        add(WenyanItems.FROST_PAPER.get(), "霜华纸 (Lv.4)");
        add(WenyanItems.PHOENIX_PAPER.get(), "凤羽纸 (Lv.5)");
        add(WenyanItems.DRAGON_PAPER.get(), "龙鳞纸 (Lv.6)");

        add(WenyanItems.BAMBOO_INK.get(), "松清墨 (Lv.1)");
        add(WenyanItems.CINNABAR_INK.get(), "朱砂墨 (Lv.2)");
        add(WenyanItems.STARLIGHT_INK.get(), "星光墨 (Lv.3)");
        add(WenyanItems.LUNAR_INK.get(), "月华墨 (Lv.4)");
        add(WenyanItems.CELESTIAL_INK.get(), "冥土墨 (Lv.5)");
        add(WenyanItems.ARCANE_INK.get(), "玄武墨 (Lv.6)");

        forTiered(this::addBlockAndItem, WenyanItems.HAND_RUNNER.getItemsSorted(),
                "一阶符", "二阶符", "三阶符", "四阶符", "五阶符", "六阶符", "七阶符");
        forTiered(this::add, WenyanItems.THROW_RUNNER.getItemsSorted(),
                "一阶投符", "二阶投符", "三阶投符", "四阶投符", "五阶投符", "六阶投符", "七阶投符");

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
        addBlockAndItem(WenyanItems.BLOCKING_QUEUE_MODULE_BLOCK_ITEM.get(), "阻塞隊列符");

        addBlockAndItem(WenyanItems.SCREEN_MODULE_BLOCK_ITEM.get(), "螢幕石");
        addBlockAndItem(WenyanItems.LOCK_MODULE_BLOCK_ITEM.get(), "信號量石");

        addBlockAndItem(WenyanItems.CRAFTING_BLOCK_ITEM.get(), "創石");
        addBlockAndItem(WenyanItems.PEDESTAL_BLOCK_ITEM.get(), "基石");
        addBlockAndItem(WenyanItems.WRITING_BLOCK_ITEM.get(), "刻印台");
        addBlockAndItem(WenyanItems.POWER_BLOCK_ITEM.get(), "算核");
        addBlockAndItem(WenyanItems.FORMATION_CORE_MODULE_BLOCK_ITEM.get(), "阵眼");

        add(WenyanItems.PRINT_INVENTORY_MODULE.get(), "印符");
        add(WenyanItems.EQUIPABLE_RUNNER_ITEM.get(), "可戴符");

        add(NotFindFu.getTranslationKey(), "謬：不識此符");
        add(CantStart.getTranslationKey(), "謬：不可始%s");
        add(LockHoldAlready.getTranslationKey(), "謬：線程已持鎖");
        add(LockNotHold.getTranslationKey(), "謬：線程未持鎖");
        add(NeedBlockItem.getTranslationKey(), "謬：參數需方塊物");
        add(NeedItemCapability.getTranslationKey(), "謬：需持物");
        add(ArgsNeedWeather.getTranslationKey(), "謬：參數須為「「晴」」「「雨」」「「雷」」");
        add(InvaildDirection.getTranslationKey(), "謬：無效之方塊向");
        add(FailedToPlacePiston.getTranslationKey(), "謬：置活塞敗");
        add(FailedToMoveBlock.getTranslationKey(), "謬：移方塊敗");
        add(DeviceRemoved.getTranslationKey(), "謬：器已除");
        add(ImportNotFound.getTranslationKey(), "謬：未尋之籍%s");
        add(NoConnectDirection.getTranslationKey(), "謬：無連向");
        add(AlreadyRun.getTranslationKey(), "已在運行");
        add(PackageAlreadtRegistered.getTranslationKey(), "謬：已有此包名%s");

        add(ArgsNumWrong.getTranslationKey(), "謬：參數數需%d得%d");
        add(ArgsNumWrongRange.getTranslationKey(), "謬：參數數需%d至%d得%d");
        add(NoAttribute.getTranslationKey(), "謬：無屬性%s");
        add(StackEmpty.getTranslationKey(), "謬：棧空");
        add(StackIndexOutOfBounds.getTranslationKey(), "謬：棧索引越界");
        add(RecursionDepthTooDeep.getTranslationKey(), "謬：遞歸深度過深");
        add(SetValueToNonLeftValue.getTranslationKey(), "謬：設值於非左值");
        add(InvalidArgumentType.getTranslationKey(), "謬：無效參數類");
        add(CannotCast.getTranslationKey(), "謬：不可轉%s為%s");
        add(InvalidDataType.getTranslationKey(), "謬：無效資料類");
        add(FunctionDoesNotHaveReferences.getTranslationKey(), "謬：術無引");
        add(CannotCreateObject.getTranslationKey(), "謬：不可造物");
        add(OperationNotSupported.getTranslationKey(), "謬：操作未支");
        add(IntegerOverflow.getTranslationKey(), "謬：整數溢");
        add(DivisionByZero.getTranslationKey(), "謬：除零");
        add(LineError.getTranslationKey(), "謬：行%d:%d %s\n伴%s");
        add(DebugInfoNotFound.getTranslationKey(), "謬：無除錯資訊於索引%d");
        add(VariableNameDuplicate.getTranslationKey(), "謬：變量名稱重複");
        add(UnknownOperator.getTranslationKey(), "謬：未知算子");
        add(UnknownPreposition.getTranslationKey(), "謬：未知介詞");
        add(FunctionNameDoesNotMatch.getTranslationKey(), "謬：術名不符");
        add(VerificationFailed.getTranslationKey(), "謬：驗證敗");
        add(TooManyVariables.getTranslationKey(), "謬：變數過多");
        add(VariablesNotPositive.getTranslationKey(), "謬：變數非正");
        add(VariablesNotMatch.getTranslationKey(), "謬：變數不符");
        add(InvalidNumber.getTranslationKey(), "謬：無效數");
        add(InvalidFloatNumber.getTranslationKey(), "謬：無效分數");
        add(InvalidBoolValue.getTranslationKey(), "謬：無效爻");
        add(UnexpectedCharacter.getTranslationKey(), "謬：意外字元");
        add(IndexOutOfBounds.getTranslationKey(), "謬：索引越界");
        add(TooManyThreads.getTranslationKey(), "謬：線程過多");
        add(RunningTooSlow.getTranslationKey(), "謬：運行過慢");
        add(Unreached.getTranslationKey(), "未知错误，请提交issue");

        add(RunningState.getTranslationKey(), "運行狀態");
        add(CheckerObject.getTranslationKey(), "檢查物");
        add(Checker7Map.getTranslationKey(), "地圖");
        add(Position.getTranslationKey(), "位置");
        add(PositionType.getTranslationKey(), "位置類");
        add(Block.getTranslationKey(), "方塊");
        add(Itemslot.getTranslationKey(), "物品槽");
        add(Player.getTranslationKey(), "玩家");
        add(Vec3.getTranslationKey(), "向量");
        add(Vec3ObjectType.getTranslationKey(), "向量類");
        add(Entity.getTranslationKey(), "實體");

        add(JavacallHandler.getTranslationKey(), "异術");
        add(Comparable.getTranslationKey(), "可比較");
        add(Function.getTranslationKey(), "術");
        add(Number.getTranslationKey(), "數");
        add(Object.getTranslationKey(), "物");
        add(ObjectType.getTranslationKey(), "類");
        add(Null.getTranslationKey(), "空無");
        add(Package.getTranslationKey(), "包");
        add(BuiltinAsyncFunction.getTranslationKey(), "內建異術");
        add(BuiltinFunction.getTranslationKey(), "內建術");
        add(BuiltinFuture.getTranslationKey(), "內建異程");
        add(DictObject.getTranslationKey(), "字典物");
        add(Bool.getTranslationKey(), "爻");
        add(Double.getTranslationKey(), "分數");
        add(Int.getTranslationKey(), "整數");
        add(List.getTranslationKey(), "列");
        add(Iterator.getTranslationKey(), "迭代器");
        add(String.getTranslationKey(), "言");

        add(HoldShift.getTranslationKey(), "（按住Shift以顯示詳情）");
        add(NarrateEditBox.getTranslationKey(), "编辑");
        add(NarrateSnippet.getTranslationKey(), "代码片段");
        add(FloatNoteName.getTranslationKey(), "浮点笔记");
        add(Done.getTranslationKey(), "完成");
        add(Lock.getTranslationKey(), "鎖定");
        add(CreativeTabTitle.getTranslationKey(), "吾有一術");
        add(EnterToInput.getTranslationKey(), "按Enter以输入");
        add(FuNamePrompt.getTranslationKey(), "符名：");

        add(Judou.getTranslationKey(), "句读（程序运行）");
        add(SliceStep.getTranslationKey(), "线程切换长度");
        add(MaxThread.getTranslationKey(), "最大线程数");
        add(WatchdogTimeout.getTranslationKey(), "超时倍率");
        add(ResultMaxSize.getTranslationKey(), "结果堆栈最大长度");
        add(InGame.getTranslationKey(), "游戏（物品与世界）");
        add(FormationRange.getTranslationKey(), "阵眼范围");
        add(PedestalRange.getTranslationKey(), "基石范围");
        add(RunnerRange.getTranslationKey(), "符範圍");
        add(Duration.getTranslationKey(), "算核消散游戏刻");
        add(Lifetime.getTranslationKey(), "投符持續時間");
        add(MaxRecursionDepth.getTranslationKey(), "最大递归深度");
        add(UseLegancyRunner.getTranslationKey(), "使用旧式符");


        add("book.wenyan_programming.shuo_wen.name", "說文");
        add("book.wenyan_programming.shuo_wen.landing_text", "编程者，制机之令也。机铁无知，唯识原语。乃作典言，上合人意，下译机识，若算经然。");
    }

    private void addBlockAndItem(BlockItem blockItem, String name) {
        add(blockItem.getDescriptionId(), name);
        add(blockItem.getBlock(), name);
    }

    private <T> void forTiered(NamingFunction<T> function, List<T> items, String... names) {
        for (int i = 0; i < items.size(); i++) {
            function.register(items.get(i), names[i]);
        }
    }

    @FunctionalInterface
    private interface NamingFunction<T> {
        void register(T item, String name);
    }
}
