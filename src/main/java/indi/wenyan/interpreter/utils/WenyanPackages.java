package indi.wenyan.interpreter.utils;

import indi.wenyan.entity.HandRunnerEntity;
import indi.wenyan.handler.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class WenyanPackages {
    public static final WenyanFunctionEnvironment WENYAN_BASIC_PACKAGES = WenyanPackageBuilder.create()
            .function("加", args -> {
                if (args.length <= 1)
                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
                WenyanValue value = args[0];
                for (int i = 1; i < args.length; i++) {
                    value = value.add(args[i]);
                }
                return value;
            })
            .function("減", args -> {
                if (args.length <= 1)
                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
                WenyanValue value = args[0];
                for (int i = 1; i < args.length; i++) {
                    value = value.sub(args[i]);
                }
                return value;
            })
            .function("乘", args -> {
                if (args.length <= 1)
                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
                WenyanValue value = args[0];
                for (int i = 1; i < args.length; i++) {
                    value = value.mul(args[i]);
                }
                return value;
            })
            .function("除", args -> {
                if (args.length <= 1)
                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
                WenyanValue value = args[0];
                for (int i = 1; i < args.length; i++) {
                    value = value.div(args[i]);
                }
                return value;
            })
            .function("變", args -> args[0].not())
            .function("銜", args -> {
                if (args.length <= 1)
                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
                WenyanValue value = args[0];
                for (int i = 1; i < args.length; i++) {
                    value = value.add(args[i]);
                }
                return value;
            })
            .function("充", args -> {
                if (args.length <= 1)
                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
                WenyanValue value = args[0];
                for (int i = 1; i < args.length; i++) {
                    value = value.append(args[i]);
                }
                return value;
            })
            .build();

    public static final WenyanFunctionEnvironment MATH_PACKAGES = WenyanPackageBuilder.create()
            .constant("「圓周率」", WenyanValue.Type.DOUBLE, Math.PI)
            .constant("「倍圓周率」", WenyanValue.Type.DOUBLE, Math.TAU)
            .constant("「半圓周率」", WenyanValue.Type.DOUBLE, Math.PI / 2)
            .constant("「四分圓周率」", WenyanValue.Type.DOUBLE, Math.PI / 4)
            .constant("「自然常數」", WenyanValue.Type.DOUBLE, Math.E)
            .constant("「歐拉常數」", WenyanValue.Type.DOUBLE, 0.5772156649)
            .constant("「黃金分割數」", WenyanValue.Type.DOUBLE, 1.6180339887)
            .constant("「二之平方根」", WenyanValue.Type.DOUBLE, Math.sqrt(2))
            .constant("「二之對數」", WenyanValue.Type.DOUBLE, Math.log(2))
            .constant("「十之對數」", WenyanValue.Type.DOUBLE, Math.log(10))
            .function("「正弦」", args -> Math.sin((double)args[0]), WenyanValue.Type.DOUBLE)
            .function("「餘弦」", args -> Math.cos((double)args[0]), WenyanValue.Type.DOUBLE)
            .function("「反正弦」", args -> Math.asin((double)args[0]), WenyanValue.Type.DOUBLE)
            .function("「反餘弦」", args -> Math.acos((double)args[0]), WenyanValue.Type.DOUBLE)
            .function("「正切」", args -> Math.tan((double)args[0]), WenyanValue.Type.DOUBLE)
            .function("「反正切」", args -> Math.atan((double)args[0]), WenyanValue.Type.DOUBLE)
            .function("「勾股求角」", args -> Math.atan2((double)args[0], (double)args[1]), WenyanValue.Type.DOUBLE)
            .function("「勾股求弦」", args -> Math.hypot((double)args[0], (double)args[1]), WenyanValue.Type.DOUBLE)
            .function("「對數」", args -> Math.log((double)args[0]), WenyanValue.Type.DOUBLE)
            .function("「指數」", args -> Math.exp((double)args[0]), WenyanValue.Type.DOUBLE)
            .function("「冪」", args -> Math.pow((double)args[0], (double)args[1]), WenyanValue.Type.DOUBLE)
            .function("「平方根」", args -> Math.sqrt((double)args[0]), WenyanValue.Type.DOUBLE)
            .function("「絕對」", args -> Math.abs((double)args[0]), WenyanValue.Type.DOUBLE)
            .function("「取頂」", args -> Math.ceil((double)args[0]), WenyanValue.Type.DOUBLE)
            .function("「取底」", args -> Math.floor((double)args[0]), WenyanValue.Type.DOUBLE)
            .function("「取整」", args -> Math.round((double)args[0]), WenyanValue.Type.DOUBLE)
            .function("「正負」", args -> Math.signum((double)args[0]), WenyanValue.Type.DOUBLE)
            .build();

    public static WenyanFunctionEnvironment handEnvironment(Player holder, HandRunnerEntity runner) {
        return WenyanPackageBuilder.create()
                .environment(WENYAN_BASIC_PACKAGES)
                .function("書", new OutputHandler(holder))
                .function("「射」", new BulletHandler(runner.level(), runner), BulletHandler.ARGS_TYPE)
                .function("「移」", new MoveHandler(runner), MoveHandler.ARGS_TYPE)
                .function("「爆」", new ExplosionHandler(runner, holder))
                .function("「雷」", new ThunderHandler(runner, holder))
                .build();
    }

    public static WenyanFunctionEnvironment blockEnvironment(BlockPos pos, BlockState block, Player holder) {
        return WenyanPackageBuilder.create()
                .environment(WENYAN_BASIC_PACKAGES)
                .function("書", new OutputHandler(holder))
                .function("「触」", new TouchHandler(holder.level(), pos, holder), TouchHandler.ARGS_TYPE)
                .build();
    }

    public static final Map<String, WenyanFunctionEnvironment> PACKAGES = new HashMap<>(){{
        put("「「算經」」", MATH_PACKAGES);
    }};
}
