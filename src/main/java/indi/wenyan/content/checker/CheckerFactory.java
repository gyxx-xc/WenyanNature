package indi.wenyan.content.checker;

import net.minecraft.util.RandomSource;

public enum CheckerFactory {;
    public static final String PLUS_CHECKER = "plus";
    public static final String ECHO_CHECKER = "echo";
    public static final String LABYRINTH_CHECKER = "labyrinth";

    public static CraftingAnswerChecker produce(String name, RandomSource random) {
        return switch (name) {
            case PLUS_CHECKER -> new PlusChecker(random);
            case ECHO_CHECKER -> new EchoChecker(random);
            case LABYRINTH_CHECKER -> new LabyrinthChecker(random);
            default -> null;
        };
    }
}
