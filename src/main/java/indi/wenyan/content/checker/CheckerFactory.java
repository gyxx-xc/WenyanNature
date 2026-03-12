package indi.wenyan.content.checker;

import indi.wenyan.content.checker.checker.CraftingAnswerChecker;
import indi.wenyan.content.checker.checker.EchoChecker;
import indi.wenyan.content.checker.checker.challenge.Ex1Checker;
import indi.wenyan.content.checker.checker.handrunner.*;
import indi.wenyan.content.checker.checker.ink.*;
import indi.wenyan.content.checker.checker.paper.*;
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
    public static final String STARLIGHT_PAPER_CHECKER = "starlight_paper";
    public static final String LUNAR_INK_CHECKER = "lunar_ink";
    public static final String HAND_RUNNER_3_CHECKER = "hand_runner_3";
    public static final String FROST_PAPER_CHECKER = "frost_paper";
    public static final String ARCANE_INK_CHECKER = "arcane_ink";
    public static final String HAND_RUNNER_4_CHECKER = "hand_runner_4";
    public static final String PHOENIX_PAPER_CHECKER = "phoenix_paper";
    public static final String HAND_RUNNER_5_CHECKER = "hand_runner_5";
    public static final String CELESTIAL_INK_CHECKER = "celestial_ink";
    public static final String DRAGON_PAPER = "dragon_paper";
    public static final String HAND_RUNNER_6_CHECKER = "hand_runner_6";

    /**
     * Creates a new crafting answer checker of the specified type.
     *
     * @param name   the type of checker to create
     * @param random a random source for the checker
     * @return the created checker, or null if the type is unknown
     */
    public static CraftingAnswerChecker produce(String name, RandomSource random) throws WenyanException {
        return switch (name) {
            case PLUS_CHECKER -> new BambooPaperChecker(random);
            case ECHO_CHECKER -> new EchoChecker(random);
            case LABYRINTH_CHECKER -> new HandRunner7Checker(random);
            case PRINT_CHECKER -> new BambooInkChecker(random);
            case HAND_RUNNER_1_CHECKER -> new HandRunner1Checker(random);
            case CINNABAR_INK_CHECKER -> new CinnabarInkChecker(random);
            case CLOUD_PAPER_CHECKER -> new CloudPaperChecker(random);
            case HAND_RUNNER_2_CHECKER -> new HandRunner2Checker(random);
            case STARLIGHT_INK_CHECKER -> new StarlightInkChecker(random);
            case STARLIGHT_PAPER_CHECKER -> new Ex1Checker(random);
            case HAND_RUNNER_3_CHECKER -> new HandRunner3Checker(random);
            case LUNAR_INK_CHECKER -> new LunarInkChecker(random);
            case FROST_PAPER_CHECKER -> new FrostPaperChecker(random);
            case HAND_RUNNER_4_CHECKER -> new HandRunner4Checker(random);
            case ARCANE_INK_CHECKER -> new ArcaneInkChecker(random);
            case PHOENIX_PAPER_CHECKER -> new PhoenixPaperChecker(random);
            case HAND_RUNNER_5_CHECKER -> new HandRunner5Checker(random);
            case CELESTIAL_INK_CHECKER -> new CelestialInkChecker(random);
            case DRAGON_PAPER -> new DragonPaperChecker(random);
            case HAND_RUNNER_6_CHECKER -> new HandRunner6Checker(random);
            default -> throw new WenyanException("Unknown checker type: " + name);
        };
    }
}
