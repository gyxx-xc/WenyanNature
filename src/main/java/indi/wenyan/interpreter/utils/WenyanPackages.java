package indi.wenyan.interpreter.utils;

import indi.wenyan.content.checker.CraftingAnswerChecker;
import indi.wenyan.content.handler.*;
import indi.wenyan.content.handler.feature_additions.entity_handler.EntityFinderHandler;
import indi.wenyan.content.handler.feature_additions.magic_handler.TeleportHandler;
import indi.wenyan.content.handler.feature_additions.string_handler.*;
import indi.wenyan.content.handler.feature_additions.string_util_handler.*;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.VehicleEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class WenyanPackages {
    private WenyanPackages(){}

    // these string for candy visitor
    public static final String AND_ID = "且";
    public static final String OR_ID = "或";
    public static final String MOD_ID = "模";

    public static final WenyanRuntime WENYAN_BASIC_PACKAGES = WenyanPackageBuilder.create()
            .function("加", WenyanPackageBuilder.reduceWith(WenyanNativeValue::add))
            .function(new String[]{"減","减"}, WenyanPackageBuilder.reduceWith(WenyanNativeValue::sub))
            .function("乘", WenyanPackageBuilder.reduceWith(WenyanNativeValue::mul))
            .function("除", WenyanPackageBuilder.reduceWith(WenyanNativeValue::div))
            .function(new String[]{"銜","衔"}, WenyanPackageBuilder.reduceWith(WenyanNativeValue::add))

            .function(new String[]{"變","变"}, args -> args.getFirst().not())
            .function("充", args -> {
                if (args.size() <= 1)
                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
                WenyanNativeValue value = args.getFirst().casting(WenyanType.LIST);
                WenyanArrayObject list = (WenyanArrayObject) value.getValue();
                for (int i = 1; i < args.size(); i++) {
                    list.add(WenyanNativeValue.varOf(args.get(i)));
                }
                return value;
            })

            // 模, 且, 或
            .function("模", WenyanPackageBuilder.reduceWith(WenyanNativeValue::mod))
            .function("且", WenyanPackageBuilder.boolBinaryOperation(Boolean::logicalAnd))
            .function("或", WenyanPackageBuilder.boolBinaryOperation(Boolean::logicalOr))

            .function(new String [] {"不等於","不等于"}, WenyanPackageBuilder.compareOperation((a, b) -> !a.equals(b)))
            .function(new String [] {"不大於","不大于"}, WenyanPackageBuilder.compareOperation((a, b) -> a.compareTo(b) <= 0))
            .function(new String [] {"不小於","不小于"}, WenyanPackageBuilder.compareOperation((a, b) -> a.compareTo(b) >= 0))
            .function(new String [] {"等於","等于"}, WenyanPackageBuilder.compareOperation(WenyanNativeValue::equals))
            .function(new String [] {"大於","大于"}, WenyanPackageBuilder.compareOperation((a, b) -> a.compareTo(b) > 0))
            .function(new String[] {"小於","小于"}, WenyanPackageBuilder.compareOperation((a, b) -> a.compareTo(b) < 0))

            .function("「」", args -> WenyanValue.NULL)
            .function("書", args -> {
                System.out.println(args);
                return WenyanValue.NULL;
            })
            .build();

    public static final WenyanRuntime MATH_PACKAGES = WenyanPackageBuilder.create()
            .constant("「圓周率」", WenyanType.DOUBLE, Math.PI)
            .constant("「倍圓周率」", WenyanType.DOUBLE, Math.TAU)
            .constant("「半圓周率」", WenyanType.DOUBLE, Math.PI / 2)
            .constant("「四分圓周率」", WenyanType.DOUBLE, Math.PI / 4)
            .constant("「自然常數」", WenyanType.DOUBLE, Math.E)
            .constant("「歐拉常數」", WenyanType.DOUBLE, 0.5772156649)
            .constant("「黃金分割數」", WenyanType.DOUBLE, 1.6180339887)
            .constant("「二之平方根」", WenyanType.DOUBLE, Math.sqrt(2))
            .constant("「二之對數」", WenyanType.DOUBLE, Math.log(2))
            .constant("「十之對數」", WenyanType.DOUBLE, Math.log(10))
            .function("「正弦」", args -> Math.sin((double)args[0]), WenyanType.DOUBLE)
            .function("「餘弦」", args -> Math.cos((double)args[0]), WenyanType.DOUBLE)
            .function("「反正弦」", args -> Math.asin((double)args[0]), WenyanType.DOUBLE)
            .function("「反餘弦」", args -> Math.acos((double)args[0]), WenyanType.DOUBLE)
            .function("「正切」", args -> Math.tan((double)args[0]), WenyanType.DOUBLE)
            .function("「反正切」", args -> Math.atan((double)args[0]), WenyanType.DOUBLE)
            .function("「勾股求角」", args -> Math.atan2((double)args[0], (double)args[1]), WenyanType.DOUBLE)
            .function("「勾股求弦」", args -> Math.hypot((double)args[0], (double)args[1]), WenyanType.DOUBLE)
            .function("「對數」", args -> Math.log((double)args[0]), WenyanType.DOUBLE)
            .function("「指數」", args -> Math.exp((double)args[0]), WenyanType.DOUBLE)
            .function("「冪」", args -> Math.pow((double)args[0], (double)args[1]), WenyanType.DOUBLE)
            .function("「平方根」", args -> Math.sqrt((double)args[0]), WenyanType.DOUBLE)
            .function("「絕對」", args -> Math.abs((double)args[0]), WenyanType.DOUBLE)
            .function("「取頂」", args -> Math.ceil((double)args[0]), WenyanType.DOUBLE)
            .function("「取底」", args -> Math.floor((double)args[0]), WenyanType.DOUBLE)
            .function("「取整」", args -> Math.round((double)args[0]), WenyanType.DOUBLE)
            .function("「正負」", args -> Math.signum((double)args[0]), WenyanType.DOUBLE)
            .build();

    public static final WenyanRuntime BIT_PACKAGES = WenyanPackageBuilder.create()
            .function("「左移」", args -> (int)args[0]<<(int)args[1], WenyanType.INT)
            .function("「右移」", args -> (int)args[0]>>(int)args[1], WenyanType.INT)
            .function("「補零右移」", args -> (int)args[0]>>>(int)args[1], WenyanType.INT)
            .function("「位與」", args -> (int)args[0]&(int)args[1], WenyanType.INT)
            .function("「位或」", args -> (int)args[0]|(int)args[1], WenyanType.INT)
            .function("「異或」", args -> (int)args[0]^(int)args[1], WenyanType.INT)
            .function("「與非」", args -> ~((int)args[0]&(int)args[1]), WenyanType.INT)
            .function("「位變」", args -> ~(int)args[0], WenyanType.INT)
            .build();

    public static final WenyanRuntime RANDOM_PACKAGES = WenyanPackageBuilder.create()
            .function("「占數」", args -> switch (args.length) {
                case 0 -> Objects.requireNonNull(Minecraft.getInstance().level).getRandom().nextInt();
                case 1 -> Objects.requireNonNull(Minecraft.getInstance().level).getRandom().nextInt((int)args[0]);
                case 2 -> Objects.requireNonNull(Minecraft.getInstance().level).getRandom().nextInt((int)args[0], (int)args[1]);
                default -> 0;
            }, WenyanType.INT)
            .function("「占分」", args -> Objects.requireNonNull(Minecraft.getInstance().level).getRandom().nextDouble(), WenyanType.DOUBLE)
            .function("「占偏」", args -> Objects.requireNonNull(Minecraft.getInstance().level).getRandom().triangle((double) args[0], (double) args[1]), WenyanType.DOUBLE)
            .function("「占爻」", args -> Objects.requireNonNull(Minecraft.getInstance().level).getRandom().nextBoolean(), WenyanType.BOOL)
            .build();

    public static final WenyanRuntime STRING_UTILS_PACKAGES = WenyanPackageBuilder.create()
            .function("「观字位」", new StringUtilIndexOfHandler())
            .function("「契首字」", new StringUtilStartWithHandler())
            .function("「合尾字」", new StringUtilEndWithHandler())
            .function("「蕴字象」", new StringUtilContainsHandler())
            .function("「截首爻」",new StringUtilSubStringStartHandler())
            .function("「截全爻」",new StringUtilSubStringStartAndEndHandler())
            .function("「易篆文」",new StringUtilReplaceHandler())
            .function("「升阳文」", new StringUtilToUpperCaseHandler())
            .function("「化阴文」", new StringUtilToLowerCaseHandler())
            .function("「去芜文」", new StringUtilTrimHandler())
            .function("「分文炁」", new StringUtilSplitHandler())
            .function("「合谶文」", new StringUtilMatchesHandler())
            .function("「验谶文」", new StringUtilValidateProphecyHandler())
            .function("「辨谶凶」", new StringUtilDiagnoseProphecyHandler())
            .function("「蕴谶骨」", new StringUtilContainsProphecyCharactersHandler())
            .function("「虚室文」", new StringUtilIsEmptyHandler())
            .build();

    public static final WenyanRuntime HAND_ENVIRONMENT = WenyanPackageBuilder.create()
                .environment(WENYAN_BASIC_PACKAGES)
                .function(new String[] {"書","书"}, new OutputHandler())
                .function("「射」", new BulletHandler(), BulletHandler.ARGS_TYPE)
                .function("「移」", new MoveHandler(), MoveHandler.ARGS_TYPE)
                .function("「爆」", new ExplosionHandler())
                .function("「雷」", new ThunderHandler())
                .object(WenyanObjectTypes.VECTOR3)
                .function("「己方位」", new SelfPositionHandler())
                .function("「密语」", new SetChatHideFilterHandler())
                .function("「阅简」", new GetSignAndLecternMsgHandler())
                .function("「题篆」", new SetSignAndLecternMsgHandler())
                .function("「赋名」", new SetEntityCustomNameHanlder())
                .function("「显名」", new GetEntityNameHandler())
                .function("「物之隐名」", new GetItemInternalNameHandler())
                .function("「大寻」", new EntityFinderHandler<>(Entity.class))
                .function("「器象」", new EntityFinderHandler<>(ItemEntity.class))
                .function("「灵察」", new EntityFinderHandler<>(LivingEntity.class))
                .function("「煞觅」", new EntityFinderHandler<>(Monster.class))
                .function("「气踪」", new EntityFinderHandler<>(Projectile.class))
                .function("「天工」", new EntityFinderHandler<>(VehicleEntity.class))
                .function("「真觅」", new EntityFinderHandler<>(Player.class))
                .function("「兽觅」", new EntityFinderHandler<>(Animal.class))

                .build();

    public static final WenyanRuntime BLOCK_ENVIRONMENT = WenyanPackageBuilder.create()
                .environment(WENYAN_BASIC_PACKAGES)
                .function(new String[] {"書","书"}, new NewOutputHandler())
                .function("「觸」", new TouchHandler(), TouchHandler.ARGS_TYPE)
//                .function("「放置」", new BlockPlaceHandler(holder,
//                        (BlockItem) Items.ACACIA_LOG.asItem()
//                        ,pos, block))
                .function("「移」", new BlockMoveHandler(), BlockMoveHandler.ARGS_TYPE)
                .function("「放」", new CommunicateHandler(), CommunicateHandler.ARG_TYPES)
                .function("「紅石量」", new RedstoneSignalHandler())
                .function("「己於上」", new SelfPositionBlockHandler(Direction.UP))
                .function("「己於下」", new SelfPositionBlockHandler(Direction.DOWN))
                .function("「己於東」", new SelfPositionBlockHandler(Direction.EAST))
                .function("「己於南」", new SelfPositionBlockHandler(Direction.SOUTH))
                .function("「己於西」", new SelfPositionBlockHandler(Direction.WEST))
                .function("「己於北」", new SelfPositionBlockHandler(Direction.NORTH))
                .function("「万言感」", new GetLastMessageHandler())
                .function("「感言」", new GetMyMessageHandler())
                .function("「阅简」", new GetSignAndLecternMsgHandler())
                .function("「题篆」", new SetSignAndLecternMsgHandler())
                .function("「赋名」", new SetEntityCustomNameHanlder())
                .function("「显名」", new GetEntityNameHandler())
                .function("「物之隐名」", new GetItemInternalNameHandler())
                .function("「瞬」", new TeleportHandler())
                .function("「大寻」", new EntityFinderHandler<>(Entity.class))
                .function("「器象」", new EntityFinderHandler<>(ItemEntity.class))
                .function("「灵察」", new EntityFinderHandler<>(LivingEntity.class))
                .function("「煞觅」", new EntityFinderHandler<>(Monster.class))
                .function("「气踪」", new EntityFinderHandler<>(Projectile.class))
                .function("「天工」", new EntityFinderHandler<>(VehicleEntity.class))
                .function("「真觅」", new EntityFinderHandler<>(Player.class))
                .function("「兽觅」", new EntityFinderHandler<>(Animal.class))
                .build();

    public static WenyanRuntime craftingEnvironment(CraftingAnswerChecker checker) {
        return WenyanPackageBuilder.create()
                .environment(WENYAN_BASIC_PACKAGES)
                .environment(checker.inputEnvironment())
                .function("書", args -> {
                    checker.accept(args);
                    return WenyanValue.NULL;
                })
                .build();
    }

    public static final Map<String, WenyanRuntime> PACKAGES = new HashMap<>(){{
        put("「「算經」」", MATH_PACKAGES);
        put("「「位經」」", BIT_PACKAGES);
        put("「「易經」」", RANDOM_PACKAGES);
        put("「「玄文库」」", STRING_UTILS_PACKAGES);
    }};

    public static class WenyanObjectTypes {
        public static final JavacallObjectType VECTOR3 = new JavacallObjectType(null, "「方位」",
                ConstructorBuilder.builder()
                        .var("「「上下」」")
                        .var("「「東西」」")
                        .var("「「南北」」")
                        .makeConstructor())
                .addStatic("「「零」」", Arrays.asList(new WenyanNativeValue(WenyanType.INT, 0, true),
                        new WenyanNativeValue(WenyanType.INT, 0, true),
                        new WenyanNativeValue(WenyanType.INT, 0, true))
                        .toArray(WenyanNativeValue[]::new));
    }
}
