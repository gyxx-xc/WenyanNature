package indi.wenyan.content.checker;

import indi.wenyan.judou.structure.WenyanException;
import net.minecraft.util.RandomSource;

/**
 * Factory for creating different types of crafting answer checkers.
 * Contains constants for available checker types.
 */
public enum CheckerFactory {
    ;
    public static final String PLUS_CHECKER = "plus";
    public static final String ECHO_CHECKER = "echo";
    public static final String LABYRINTH_CHECKER = "labyrinth";
    public static final String PRINT_CHECKER = "print";
    public static final String HAND_RUNNER_1_CHECKER = "hand_runner_1";
    public static final String CINNABAR_INK_CHECKER = "cinnabar_ink";
    public static final String CLOUD_PAPER_CHECKER = "cloud_paper";
    public static final String STARLIGHT_INK_CHECKER = "starlight_ink";
    public static final String HAND_RUNNER_2_CHECKER = "hand_runner_2";

    /**
     * Creates a new crafting answer checker of the specified type.
     *
     * @param name   the type of checker to create
     * @param random a random source for the checker
     * @return the created checker, or null if the type is unknown
     */
    public static CraftingAnswerChecker produce(String name, RandomSource random) throws WenyanException {
        return switch (name) {
            case PLUS_CHECKER -> new PlusChecker(random);
            case ECHO_CHECKER -> new EchoChecker(random);
            case LABYRINTH_CHECKER -> new LabyrinthChecker(random);
            case PRINT_CHECKER -> new PrintChecker(random);
            case HAND_RUNNER_1_CHECKER -> new HandRunner1Checker(random);
            case CINNABAR_INK_CHECKER -> new CinnabarInkChecker(random);
            case CLOUD_PAPER_CHECKER -> new CloudPaperChecker(random);
            case HAND_RUNNER_2_CHECKER -> new HandRunner2Checker(random);
            default -> throw new WenyanException("Unknown checker type: " + name);
        };
    }
}
