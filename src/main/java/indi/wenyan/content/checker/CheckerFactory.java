package indi.wenyan.content.checker;

import net.minecraft.util.RandomSource;

public class CheckerFactory {
    public static CraftingAnswerChecker produce(String name, RandomSource random) {
        return switch (name) {
            case "plus" -> new PlusChecker(random);
            case "echo" -> new EchoChecker(random);
            default -> null;
        };
    }
}
