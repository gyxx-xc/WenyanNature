package indi.wenyan.interpreter.utils;

import indi.wenyan.entity.HandRunnerEntity;
import indi.wenyan.handler.BulletHandler;
import indi.wenyan.handler.OutputHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

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

    public static WenyanFunctionEnvironment handEnvironment(Player holder, HandRunnerEntity runner) {
        return WenyanPackageBuilder.create()
                .environment(WENYAN_BASIC_PACKAGES)
                .function("書", new OutputHandler(holder))
                .function("「射」", new BulletHandler(holder.level(), runner), BulletHandler.ARGS_TYPE)
                .build();
    }

    public static WenyanFunctionEnvironment blockEnvironment(Player holder) {
        return WenyanPackageBuilder.create()
                .environment(WENYAN_BASIC_PACKAGES)
                .function("書", new OutputHandler(holder))
                .build();
    }

    public static final Map<String, WenyanFunctionEnvironment> PACKAGES = new HashMap<>();
}
