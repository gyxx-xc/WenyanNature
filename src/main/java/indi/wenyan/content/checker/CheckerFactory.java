package indi.wenyan.content.checker;

import net.minecraft.util.RandomSource;

public class CheckerFactory {
    public static final String PLUS_CHECKER = "plus";
    public static final String ECHO_CHECKER = "echo";

    public static CraftingAnswerChecker produce(String name, RandomSource random) {
        return switch (name) {
            case PLUS_CHECKER -> new PlusChecker(random);
            case ECHO_CHECKER -> new EchoChecker(random);
            default -> null;
        };
    }
}
