package indi.wenyan.interpreter.utils;

import indi.wenyan.content.handler.*;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.*;
import indi.wenyan.interpreter.structure.values.primitive.WenyanBoolean;
import indi.wenyan.interpreter.structure.values.primitive.WenyanDouble;
import indi.wenyan.interpreter.structure.values.primitive.WenyanNull;
import indi.wenyan.interpreter.structure.values.warper.WenyanArrayList;
import indi.wenyan.interpreter.structure.values.warper.WenyanVec3Object;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum WenyanPackages {;

    // these string for candy visitor
    public static final String AND_ID = "且";
    public static final String OR_ID = "或";
    public static final String MOD_ID = "模";

    public static final WenyanRuntime WENYAN_BASIC_PACKAGES = WenyanPackageBuilder.create()
            .function("加", WenyanPackageBuilder.reduceWith(IWenyanValue::add))
            .function(new String[]{"減","减"}, WenyanPackageBuilder.reduceWith(IWenyanValue::sub))
            .function("乘", WenyanPackageBuilder.reduceWith(IWenyanValue::mul))
            .function("除", WenyanPackageBuilder.reduceWith(IWenyanValue::div))

            .function(new String[]{"變","变"}, (self, args) -> args.getFirst().as(WenyanBoolean.TYPE).not())

            .function(new String[]{"銜","衔"}, (self, args) -> {
                if (args.size() <= 1)
                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.number_of_arguments_does_not_match").getString());
                WenyanArrayList value = args.getFirst().as(WenyanArrayList.TYPE);
                for (IWenyanValue v : args.subList(1, args.size())) {
                    value.concat(v.as(WenyanArrayList.TYPE));
                }
                return value;
            })
            .function("充", (self, args) -> {
                if (args.size() <= 1)
                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.number_of_arguments_does_not_match").getString());
                WenyanArrayList value = args.getFirst().as(WenyanArrayList.TYPE);
                args.subList(1, args.size()).forEach(value::add);
                return value;
            })

            // 模, 且, 或
            .function("模", WenyanPackageBuilder.reduceWith(IWenyanValue::mod))
            .function("且", WenyanPackageBuilder.boolBinaryOperation(Boolean::logicalAnd))
            .function("或", WenyanPackageBuilder.boolBinaryOperation(Boolean::logicalOr))

            .function(new String [] {"不等於","不等于"}, WenyanPackageBuilder.compareOperation((a, b) -> !IWenyanValue.equals(a, b)))
            .function(new String [] {"不大於","不大于"}, WenyanPackageBuilder.compareOperation((a, b) -> IWenyanValue.compareTo(a, b) <= 0))
            .function(new String [] {"不小於","不小于"}, WenyanPackageBuilder.compareOperation((a, b) -> IWenyanValue.compareTo(a, b) >= 0))
            .function(new String [] {"等於","等于"}, WenyanPackageBuilder.compareOperation((value, other) -> IWenyanValue.equals(value, other)))
            .function(new String [] {"大於","大于"}, WenyanPackageBuilder.compareOperation((a, b) -> IWenyanValue.compareTo(a, b) > 0))
            .function(new String[] {"小於","小于"}, WenyanPackageBuilder.compareOperation((a, b) -> IWenyanValue.compareTo(a, b) < 0))

            .function(new String[] {"書","书"}, new OutputHandler())
            .function("「」", (self, args) -> WenyanNull.NULL)
            .build();

    public static final WenyanRuntime MATH_PACKAGES = WenyanPackageBuilder.create()
            .constant("「圓周率」", new WenyanDouble(Math.PI))
            .constant("「倍圓周率」", new WenyanDouble(Math.TAU))
            .constant("「半圓周率」", new WenyanDouble(Math.PI / 2))
            .constant("「四分圓周率」", new WenyanDouble(Math.PI / 4))
            .constant("「自然常數」", new WenyanDouble( Math.E))
            .constant("「歐拉常數」", new WenyanDouble(0.5772156649))
            .constant("「黃金分割數」", new WenyanDouble(1.6180339887))
            .constant("「二之平方根」", new WenyanDouble(1.4142135623730951))
            .constant("「二之對數」", new WenyanDouble(Math.log(2)))
            .constant("「十之對數」", new WenyanDouble(Math.log(10)))
            .doubleFunction("「正弦」", args -> StrictMath.sin(args.getFirst()))
            .doubleFunction("「餘弦」", args -> StrictMath.cos(args.getFirst()))
            .doubleFunction("「反正弦」", args -> StrictMath.asin(args.getFirst()))
            .doubleFunction("「反餘弦」", args -> StrictMath.acos(args.getFirst()))
            .doubleFunction("「正切」", args -> StrictMath.tan(args.getFirst()))
            .doubleFunction("「反正切」", args -> StrictMath.atan(args.getFirst()))
            .doubleFunction("「勾股求角」", args -> StrictMath.atan2(args.getFirst(), args.get(1)))
            .doubleFunction("「勾股求弦」", args -> StrictMath.hypot(args.getFirst(), args.get(1)))
            .doubleFunction("「對數」", args -> StrictMath.log(args.getFirst()))
            .doubleFunction("「指數」", args -> StrictMath.exp(args.getFirst()))
            .doubleFunction("「冪」", args -> StrictMath.pow(args.getFirst(), args.get(1)))
            .doubleFunction("「平方根」", args -> Math.sqrt(args.getFirst()))
            .doubleFunction("「絕對」", args -> Math.abs(args.getFirst()))
            .doubleFunction("「取頂」", args -> Math.ceil(args.getFirst()))
            .doubleFunction("「取底」", args -> Math.floor(args.getFirst()))
            .doubleFunction("「取整」", args -> (double) Math.round(args.getFirst()))
            .doubleFunction("「正負」", args -> Math.signum(args.getFirst()))
            .build();

    public static final WenyanRuntime BIT_PACKAGES = WenyanPackageBuilder.create()
            .intFunction("「左移」", args -> args.getFirst()<<args.get(1))
            .intFunction("「右移」", args -> args.getFirst()>>args.get(1))
            .intFunction("「補零右移」", args -> args.getFirst()>>>args.get(1))
            .intFunction("「位與」", args -> args.getFirst()&args.get(1))
            .intFunction("「位或」", args -> args.getFirst()|args.get(1))
            .intFunction("「異或」", args -> args.getFirst()^args.get(1))
            .intFunction("「與非」", args -> ~(args.getFirst()&args.get(1)))
            .intFunction("「位變」", args -> ~args.getFirst())
            .build();

    public static final WenyanRuntime RANDOM_PACKAGES = WenyanPackageBuilder.create()
            .intFunction("「占數」", (args) -> {
                var random = Objects.requireNonNull(Minecraft.getInstance().level).getRandom();
                return switch (args.size()) {
                    case 0 -> random.nextInt();
                    case 1 -> random.nextInt(args.getFirst());
                    case 2 -> random.nextInt(args.get(0), args.get(1));
                    default -> throw new WenyanException(""); // TODO
                };
            })
            .doubleFunction("「占分」", args -> Objects.requireNonNull(Minecraft.getInstance().level).getRandom().nextDouble())
            .doubleFunction("「占偏」", args -> Objects.requireNonNull(Minecraft.getInstance().level).getRandom().triangle(args.getFirst(), args.get(1)))
            .function("「占爻」", (self, args) -> new WenyanBoolean(Objects.requireNonNull(Minecraft.getInstance().level).getRandom().nextBoolean()))
            .build();

    public static final WenyanRuntime HAND_ENVIRONMENT = WenyanPackageBuilder.create()
            .environment(WENYAN_BASIC_PACKAGES)
            .function("「射」", new BulletHandler(), BulletHandler.ARGS_TYPE)
            .function("「移」", new MoveHandler(), MoveHandler.ARGS_TYPE)
            .function("「爆」", new ExplosionHandler())
            .function("「雷」", new ThunderHandler())
            .object("「方位」", WenyanVec3Object.OBJECT_TYPE)
            .function("「己方位」", new SelfPositionHandler())
            .build();

    public static final WenyanRuntime BLOCK_ENVIRONMENT = WenyanPackageBuilder.create()
            .environment(WENYAN_BASIC_PACKAGES)
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
            .build();

    public static final WenyanRuntime CRAFTING_BASE_ENVIRONMENT = WenyanPackageBuilder.create()
            .environment(WENYAN_BASIC_PACKAGES)
            .build();

    public static final Map<String, WenyanRuntime> PACKAGES = new HashMap<>(){{
        put("「「算經」」", MATH_PACKAGES);
        put("「「位經」」", BIT_PACKAGES);
        put("「「易經」」", RANDOM_PACKAGES);
    }};
}
