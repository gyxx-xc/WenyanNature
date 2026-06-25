package indi.wenyan.setup.datagen.language;

import indi.wenyan.setup.definitions.WenyanItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.List;

import static indi.wenyan.content.recipe.answering.checker.CheckerEnum.*;
import static indi.wenyan.judou.api.language.JudouExceptionText.*;
import static indi.wenyan.judou.api.language.JudouTypeText.*;
import static indi.wenyan.setup.language.ConfigText.*;
import static indi.wenyan.setup.language.ExceptionText.*;
import static indi.wenyan.setup.language.FunctionMetaText.*;
import static indi.wenyan.setup.language.GuiText.*;
import static indi.wenyan.setup.language.TypeText.*;

/// Provider for generating English language translations during data generation.
/// Contains all English translations used in the mod, keeping transliteration
/// for item names.
public class EnglishLanguageProvider extends LanguageProvider {

    /// Constructs a new English language provider.
    ///
    /// @param output The pack output for language file generation
    /// @param modid  The mod ID
    /// @param locale The locale code (en\_us)
    public EnglishLanguageProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
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
        add(WenyanItems.ARCANE_INK.get(), "玄武墨 (Lv.5)");
        add(WenyanItems.CELESTIAL_INK.get(), "冥土墨 (Lv.6)");

        forTiered(this::addBlockAndItem, WenyanItems.HAND_RUNNER.getItemsSorted(),
                "基础符", "一阶符", "二阶符", "三阶符", "四阶符", "五阶符", "六阶符");
        forTiered(this::add, WenyanItems.THROW_RUNNER.getItemsSorted(),
                "基础投符", "一阶投符", "二阶投符", "三阶投符", "四阶投符", "五阶投符", "六阶投符");

        add(WenyanItems.THROW_MODULE.get(), "投符令");

        addBlockAndItem(WenyanItems.BIT_MODULE_BLOCK_ITEM.get(), "位元符");
        addBlockAndItem(WenyanItems.MATH_MODULE_BLOCK_ITEM.get(), "數符");
        addBlockAndItem(WenyanItems.VEC3_MODULE_BLOCK_ITEM.get(), "向量符");
        addBlockAndItem(WenyanItems.RANDOM_MODULE_BLOCK_ITEM.get(), "熵符");
        addBlockAndItem(WenyanItems.STRING_MODULE_BLOCK_ITEM.get(), "字串符");
        addBlockAndItem(WenyanItems.COLLECTION_MODULE_BLOCK_ITEM.get(), "集符");
        addBlockAndItem(WenyanItems.ITEM_MODULE_BLOCK_ITEM.get(), "物品符");
        addBlockAndItem(WenyanItems.BLOCK_MODULE_BLOCK_ITEM.get(), "方塊符");
        addBlockAndItem(WenyanItems.ENTITY_MODULE_BLOCK_ITEM.get(), "實體符");
        addBlockAndItem(WenyanItems.INFORMATION_MODULE_BLOCK_ITEM.get(), "天下情報符");
        addBlockAndItem(WenyanItems.EXPLOSION_MODULE_BLOCK_ITEM.get(), "爆裂符");
        addBlockAndItem(WenyanItems.BLOCKING_QUEUE_MODULE_BLOCK_ITEM.get(), "阻塞隊列符");
        addBlockAndItem(WenyanItems.PISTON_MODULE_BLOCK_ITEM.get(), "推動符");

        add(WenyanItems.FLOAT_NOTE.get(), "浮签");

        addBlockAndItem(WenyanItems.CRAFTING_BLOCK_ITEM.get(), "創石");
        addBlockAndItem(WenyanItems.PEDESTAL_BLOCK_ITEM.get(), "基石");
        addBlockAndItem(WenyanItems.WRITING_BLOCK_ITEM.get(), "刻印台");
        addBlockAndItem(WenyanItems.LOGIC_FURNACE_BLOCK_ITEM.get(), "炉烘有天");
        addBlockAndItem(WenyanItems.POWER_BLOCK_ITEM.get(), "算核");
        addBlockAndItem(WenyanItems.CREATIVE_POWER_BLOCK_ITEM.get(), "創核石");
        addBlockAndItem(WenyanItems.FORMATION_CORE_MODULE_BLOCK_ITEM.get(), "阵眼");
        addBlockAndItem(WenyanItems.SCREEN_MODULE_BLOCK_ITEM.get(), "螢幕石");
        addBlockAndItem(WenyanItems.LOCK_MODULE_BLOCK_ITEM.get(), "信號量石");

        add(NotFindFu.getTranslationKey(), "謬：不識此符");
        add(CantStart.getTranslationKey(), "謬：不可始%s");
        add(LockHoldAlready.getTranslationKey(), "謬：線程已持鎖");
        add(LockNotHold.getTranslationKey(), "謬：線程未持鎖");
        add(NeedBlockItem.getTranslationKey(), "謬：參數需方塊物");
        add(NeedItemCapability.getTranslationKey(), "謬：需持物");
        add(ArgsNeedWeather.getTranslationKey(), "謬：參數須為「「晴」」「「雨」」「「雷」」");
        add(InvalidDirection.getTranslationKey(), "謬：無效之方塊向");
        add(FailedToPlacePiston.getTranslationKey(), "謬：置活塞敗");
        add(FailedToMoveBlock.getTranslationKey(), "謬：移方塊敗");
        add(DeviceRemoved.getTranslationKey(), "謬：器已除");
        add(ImportNotFound.getTranslationKey(), "謬：未尋之籍%s");
        add(NoConnectDirection.getTranslationKey(), "謬：無連向");
        add(AlreadyRun.getTranslationKey(), "已在運行");
        add(PackageAlreadyRegistered.getTranslationKey(), "謬：已有此包名%s");
        add(NoRecipeFound.getTranslationKey(), "謬：未尋配方");
        add(OutOfRange.getTranslationKey(), "錯：超出範圍");

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
        add(LineError.getTranslationKey(), "謬：行%d:%d %s");
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

        add(HoldShift.getTranslationKey(), "按住§fShift§r以顯示詳情");
        add(PressRight.getTranslationKey(), "长按§f右键§r打开文档");
        add(NarrateEditBox.getTranslationKey(), "编辑");
        add(NarrateSnippet.getTranslationKey(), "代码片段");
        add(FloatNoteName.getTranslationKey(), "浮点笔记");
        add(Done.getTranslationKey(), "完成");
        add(Lock.getTranslationKey(), "鎖定");
        add(CreativeTabTitle.getTranslationKey(), "吾有一術");
        add(EnterToInput.getTranslationKey(), "按Enter以输入");
        add(FuNamePrompt.getTranslationKey(), "符名：");
        add(ThrowTooltip.getTranslationKey(), "§o§f右键§r§8使用，在§f刻印台§r§8编辑");
        add(ThrowModule.getTranslationKey(), "包含：");
        add(BlockRunnerTooltip.getTranslationKey(), "§o§f右键§r§8使用，§o§fShift+右键§r§8编辑");
        add(FurnaceTitle.getTranslationKey(), "熔炉");
        add(JeiAnswerTitle.getTranslationKey(), "解题");
        add(AiPromptLabel.getTranslationKey(), "指示大儒之事：");
        add(AiGenerateButton.getTranslationKey(), "✨制符");
        add(AiGenerating.getTranslationKey(), "大儒制符中...");
        add(AiError.getTranslationKey(), "大儒曰謬");
        add(LlmPanelToggle.getTranslationKey(), "✨大儒");
        add(LlmPanelBack.getTranslationKey(), "◄输出");

        add(PLUS_CHECKER.getTranslationKey(), "已知「甲」「乙」求「甲」加「乙」");
        add(ECHO_CHECKER.getTranslationKey(), "已知「甲」输出「甲」");
        add(LABYRINTH_CHECKER.getTranslationKey(), "走出提供的迷宫");
        add(PRINT_CHECKER.getTranslationKey(), "输出「「吾有一術」」");
        add(HAND_RUNNER_1_CHECKER.getTranslationKey(), "這是HAND_RUNNER_1的題目描述，但是我也不知道題目是什麼，因爲題目的類名跟題目沒有一點關係");
        add(CINNABAR_INK_CHECKER.getTranslationKey(), "這是CINNABAR INK的題目描述，但是我也不知道題目是什麼，因爲題目的類名跟題目沒有一點關係");
        add(CLOUD_PAPER_CHECKER.getTranslationKey(), "這是CLOUD PAPER的題目描述，但是我也不知道題目是什麼，因爲題目的類名跟題目沒有一點關係");
        add(HAND_RUNNER_2_CHECKER.getTranslationKey(), "這是HAND_RUNNER_2的題目描述，但是我也不知道題目是什麼，因爲題目的類名跟題目沒有一點關係");
        add(STARLIGHT_INK_CHECKER.getTranslationKey(), "這是STARLIGHT INK的題目描述，但是我也不知道題目是什麼，因爲題目的類名跟題目沒有一點關係");
        add(STARLIGHT_PAPER_CHECKER.getTranslationKey(), "這是STARLIGHT PAPER的題目描述，但是我也不知道題目是什麼，因爲題目的類名跟題目沒有一點關係");
        add(HAND_RUNNER_3_CHECKER.getTranslationKey(), "這是HAND_RUNNER_3的題目描述，但是我也不知道題目是什麼，因爲題目的類名跟題目沒有一點關係");
        add(LUNAR_INK_CHECKER.getTranslationKey(), "這是LUNAR INK的題目描述，但是我也不知道題目是什麼，因爲題目的類名跟題目沒有一點關係");
        add(FROST_PAPER_CHECKER.getTranslationKey(), "這是FROST PAPER的題目描述，但是我也不知道題目是什麼，因爲題目的類名跟題目沒有一點關係");
        add(HAND_RUNNER_4_CHECKER.getTranslationKey(), "這是HAND_RUNNER_4的題目描述，但是我也不知道題目是什麼，因爲題目的類名跟題目沒有一點關係");
        add(ARCANE_INK_CHECKER.getTranslationKey(), "這是ARCANE INK的題目描述，但是我也不知道題目是什麼，因爲題目的類名跟題目沒有一點關係");
        add(PHOENIX_PAPER_CHECKER.getTranslationKey(), "這是PHOENIX PAPER的題目描述，但是我也不知道題目是什麼，因爲題目的類名跟題目沒有一點關係");
        add(HAND_RUNNER_5_CHECKER.getTranslationKey(), "這是HAND_RUNNER_5的題目描述，但是我也不知道題目是什麼，因爲題目的類名跟題目沒有一點關係");
        add(CELESTIAL_INK_CHECKER.getTranslationKey(), "這是CELESTIAL INK的題目描述，但是我也不知道題目是什麼，因爲題目的類名跟題目沒有一點關係");
        add(HAND_RUNNER_6_CHECKER.getTranslationKey(), "這是HAND_RUNNER_6的題目描述，但是我也不知道題目是什麼，因爲題目的類名跟題目沒有一點關係");
        add(DRAGON_PAPER_CHECKER.getTranslationKey(), "這是DRAGON_PAPER的題目描述，但是我也不知道題目是什麼，因爲題目的類名跟題目沒有一點關係");

        add(BlockingQueueModulePut.getTranslationKey(), "将元素加入队列（队列满时阻塞）");
        add(BlockingQueueModuleTake.getTranslationKey(), "从队列取出元素（队列空时阻塞）");
        add(BlockingQueueModuleOffer.getTranslationKey(), "尝试将元素加入队列（不阻塞）");
        add(BlockingQueueModulePoll.getTranslationKey(), "尝试从队列取出元素（不阻塞）");
        add(BlockingQueueModulePeek.getTranslationKey(), "查看队列头部元素但不取出");
        add(BlockingQueueModuleSize.getTranslationKey(), "获取队列当前大小");
        add(BlockingQueueModuleClear.getTranslationKey(), "清空队列并唤醒等待的生产者");
        add(BlockModuleSearch.getTranslationKey(), "在指定范围内搜索是否存在匹配的方块");
        add(BlockModuleGet.getTranslationKey(), "获取指定位置的方块状态");
        add(BlockModuleAttach.getTranslationKey(), "获取与此符相邻的方块");
        add(Print.getTranslationKey(), "打印一行");
        add(EntityModuleInspectRange.getTranslationKey(), "检测指定区域内的所有实体");
        add(EntityModuleNearby.getTranslationKey(), "检测符咒周围指定半径内的所有实体");
        add(EntityModuleLineOfSight.getTranslationKey(), "检测从起点到终点的视线上的方块（射线追踪）");
        add(ExplosionModuleLightning.getTranslationKey(), "在符咒位置召唤闪电");
        add(ExplosionModuleExplode.getTranslationKey(), "在符咒位置产生爆炸（半径 3.0）");
        add(ExplosionModuleIgnite.getTranslationKey(), "点燃指定位置的方块");
        add(ExplosionModuleFireball.getTranslationKey(), "发射一个大火球");
        add(CoreStart.getTranslationKey(), "启动指定名称的符");
        add(CoreJoin.getTranslationKey(), "等待所有已启动的可戴符执行完毕");
        add(CoreStatus.getTranslationKey(), "查询指定可戴符的运行状态");
        add(CoreExec.getTranslationKey(), "在指定符上执行函数");
        add(ItemModuleTransfer.getTranslationKey(), "将物品传输到相邻的物品处理器");
        add(ItemModuleRead.getTranslationKey(), "读取相邻物品处理器指定槽位的物品");
        add(SemaphoreModuleAcquire.getTranslationKey(), "获取锁");
        add(SemaphoreModuleRelease.getTranslationKey(), "释放锁");
        add(FurnaceBurn.getTranslationKey(), "逐次增加一点进度");
        add(FurnaceDoubleBurn.getTranslationKey(), "将当前进度翻倍");
        add(FurnaceGetProgress.getTranslationKey(), "获取当前进度值");
        add(FurnaceGetMaxProgress.getTranslationKey(), "获取烧炼所需的总进度值");
        add(PistonPush.getTranslationKey(), "推动指定位置的方块结构");
        add(PistonPull.getTranslationKey(), "拉动指定位置的方块结构");
        add(WorldModuleSignalStrength.getTranslationKey(), "检测周围最强的红石信号强度");
        add(WorldModuleEmitSignal.getTranslationKey(), "输出指定强度的红石信号");
        add(WorldModuleChangeWeather.getTranslationKey(), "改变天气");

        add(BitModuleLeftShift.getTranslationKey(), "将第一个数向左移动第二个数指定的位数");
        add(BitModuleRightShift.getTranslationKey(), "将第一个数向右移动第二个数指定的位数（保留符号位）");
        add(BitModuleZeroFillRightShift.getTranslationKey(), "将第一个数向右移动第二个数指定的位数（高位补零）");
        add(BitModuleBitAnd.getTranslationKey(), "对两个数进行按位与运算");
        add(BitModuleBitOr.getTranslationKey(), "对两个数进行按位或运算");
        add(BitModuleBitXor.getTranslationKey(), "对两个数进行按位异或运算");
        add(BitModuleBitNand.getTranslationKey(), "对两个数进行与非运算");
        add(BitModuleBitNot.getTranslationKey(), "对数进行按位取反运算");
        add(CollectionModuleDisjoint.getTranslationKey(), "判断两个列表是否没有共同元素");
        add(CollectionModuleIntersection.getTranslationKey(), "求两个列表的交集");
        add(CollectionModuleDifference.getTranslationKey(), "求第一个列表相对于第二个列表的差集");
        add(CollectionModuleReverse.getTranslationKey(), "反转列表中的元素顺序");
        add(CollectionModuleSort.getTranslationKey(), "对列表进行排序（要求元素可比较）");
        add(CollectionModuleContains.getTranslationKey(), "判断列表是否包含指定元素");
        add(CollectionModuleMax.getTranslationKey(), "求列表中的最大元素");
        add(CollectionModuleMin.getTranslationKey(), "求列表中的最小元素");
        add(MathModulePI.getTranslationKey(), "圆周率 π (3.14159...)");
        add(MathModuleTAU.getTranslationKey(), "2π (6.28318...)");
        add(MathModuleHalfPi.getTranslationKey(), "π/2 (1.57079...)");
        add(MathModuleQuarterPi.getTranslationKey(), "π/4 (0.78539...)");
        add(MathModuleE.getTranslationKey(), "自然常数 e (2.71828...)");
        add(MathModuleEuler.getTranslationKey(), "欧拉常数 (0.57721...)");
        add(MathModuleGoldenRatio.getTranslationKey(), "黄金分割比 (1.61803...)");
        add(MathModuleSqrt2.getTranslationKey(), "√2 (1.41421...)");
        add(MathModuleLog2.getTranslationKey(), "ln(2) (0.69314...)");
        add(MathModuleLog10.getTranslationKey(), "ln(10) (2.30258...)");
        add(MathModuleSin.getTranslationKey(), "求正弦值");
        add(MathModuleCos.getTranslationKey(), "求余弦值");
        add(MathModuleAsin.getTranslationKey(), "求反正弦值");
        add(MathModuleAcos.getTranslationKey(), "求反余弦值");
        add(MathModuleTan.getTranslationKey(), "求正切值");
        add(MathModuleAtan.getTranslationKey(), "求反正切值");
        add(MathModuleAtan2.getTranslationKey(), "已知直角三角形的两条边，求角度");
        add(MathModuleHypot.getTranslationKey(), "已知直角三角形的两条边，求斜边长度");
        add(MathModuleLog.getTranslationKey(), "求自然对数");
        add(MathModuleExp.getTranslationKey(), "求 e 的指数");
        add(MathModulePow.getTranslationKey(), "求幂次方");
        add(MathModuleSqrt.getTranslationKey(), "求平方根");
        add(MathModuleAbs.getTranslationKey(), "求绝对值");
        add(MathModuleCeil.getTranslationKey(), "向上取整");
        add(MathModuleFloor.getTranslationKey(), "向下取整");
        add(MathModuleRound.getTranslationKey(), "四舍五入取整");
        add(MathModuleSignum.getTranslationKey(), "求符号函数值");
        add(RandomModuleNextInt.getTranslationKey(), "生成随机整数。无参数：生成任意整数。一个参数：生成 0 到指定数之间的整数。两个参数：生成指定范围内的整数。");
        add(RandomModuleNextDouble.getTranslationKey(), "生成 0.0 到 1.0 之间的随机分数");
        add(RandomModuleNextTriangle.getTranslationKey(), "生成三角形分布的随机数");
        add(RandomModuleNextBoolean.getTranslationKey(), "生成随机爻（布尔值）");
        add(StringModuleLength.getTranslationKey(), "求字符串的长度");
        add(StringModuleCharAt.getTranslationKey(), "从字符串中取出指定位置的字符");
        add(StringModuleIndexOf.getTranslationKey(), "在第一个字符串中查找第二个字符串的位置");
        add(StringModuleSplit.getTranslationKey(), "用第二个字符串作为分隔符，分割第一个字符串");
        add(StringModuleReplace.getTranslationKey(), "将第一个字符串中的第二个字符串替换为第三个字符串");
        add(StringModuleReverse.getTranslationKey(), "反转字符串");
        add(StringModuleTrim.getTranslationKey(), "去除字符串首尾的空白字符");
        add(StringModuleContains.getTranslationKey(), "判断第一个字符串是否包含第二个字符串");
        add(StringModuleStartsWith.getTranslationKey(), "判断第一个字符串是否以第二个字符串开头");
        add(StringModuleEndsWith.getTranslationKey(), "判断第一个字符串是否以第二个字符串结尾");

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
        add(UseLegacyRunner.getTranslationKey(), "使用旧式符");
        add(UseTraditionalConversion.getTranslationKey(), "使用繁體轉換");
        add(SymbolConversion.getTranslationKey(), "变量名转换");

        add("book.wenyan_programming.shuo_wen.name", "說文");
        add("book.wenyan_programming.shuo_wen.landing_text", "编程者，制机之令也。机铁无知，唯识原语。乃作典言，上合人意，下译机识，若算经然。");
    }

    private <T> void forTiered(NamingFunction<T> function, List<T> items, String... names) {
        for (int i = 0; i < items.size(); i++) {
            function.register(items.get(i), names[i]);
        }
    }

    private void addBlockAndItem(BlockItem blockItem, String name) {
        add(blockItem.getDescriptionId(), name);
        add(blockItem.getBlock(), name);
    }

    @FunctionalInterface
    private interface NamingFunction<T> {
        void register(T item, String name);
    }
}
