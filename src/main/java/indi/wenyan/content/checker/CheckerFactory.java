package indi.wenyan.content.checker;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanThrowException;
import net.minecraft.util.RandomSource;

/**
 * Factory for creating different types of crafting answer checkers.
 * Contains constants for available checker types.
 */
public enum CheckerFactory {;
    public static final String PLUS_CHECKER = "plus";
    public static final String ECHO_CHECKER = "echo";
    public static final String LABYRINTH_CHECKER = "labyrinth";

    /**
     * Creates a new crafting answer checker of the specified type.
     *
     * @param name the type of checker to create
     * @param random a random source for the checker
     * @return the created checker, or null if the type is unknown
     */
    public static CraftingAnswerChecker produce(String name, RandomSource random) throws WenyanThrowException {
        return switch (name) {
            case PLUS_CHECKER -> new PlusChecker(random);
            case ECHO_CHECKER -> new EchoChecker(random);
            case LABYRINTH_CHECKER -> new LabyrinthChecker(random);
            default -> throw new WenyanException("Unknown checker type: " + name);
        };
    }
}
