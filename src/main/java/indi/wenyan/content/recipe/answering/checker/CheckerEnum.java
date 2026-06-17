package indi.wenyan.content.recipe.answering.checker;

import indi.wenyan.content.recipe.answering.checker.wyquestion.EchoChecker;
import indi.wenyan.content.recipe.answering.checker.wyquestion.challenge.Ex1Checker;
import indi.wenyan.content.recipe.answering.checker.wyquestion.handrunner.*;
import indi.wenyan.content.recipe.answering.checker.wyquestion.ink.*;
import indi.wenyan.content.recipe.answering.checker.wyquestion.paper.*;
import indi.wenyan.setup.language.ILocalizationEnum;
import net.minecraft.util.RandomSource;

import java.util.function.Function;

/// Factory for creating different types of crafting answer checkers.
/// Contains constants for available checker types.
public enum CheckerEnum implements ILocalizationEnum {
    PLUS_CHECKER(BambooPaperChecker::new),
    ECHO_CHECKER(EchoChecker::new),
    LABYRINTH_CHECKER(HandRunner7Checker::new),
    PRINT_CHECKER(BambooInkChecker::new),
    HAND_RUNNER_1_CHECKER(HandRunner1Checker::new),
    CINNABAR_INK_CHECKER(CinnabarInkChecker::new),
    CLOUD_PAPER_CHECKER(CloudPaperChecker::new),
    HAND_RUNNER_2_CHECKER(HandRunner2Checker::new),
    STARLIGHT_INK_CHECKER(StarlightInkChecker::new),
    STARLIGHT_PAPER_CHECKER(Ex1Checker::new),
    HAND_RUNNER_3_CHECKER(HandRunner3Checker::new),
    LUNAR_INK_CHECKER(LunarInkChecker::new),
    FROST_PAPER_CHECKER(FrostPaperChecker::new),
    HAND_RUNNER_4_CHECKER(HandRunner4Checker::new),
    ARCANE_INK_CHECKER(ArcaneInkChecker::new),
    PHOENIX_PAPER_CHECKER(PhoenixPaperChecker::new),
    HAND_RUNNER_5_CHECKER(HandRunner5Checker::new),
    CELESTIAL_INK_CHECKER(CelestialInkChecker::new),
    DRAGON_PAPER_CHECKER(DragonPaperChecker::new),
    HAND_RUNNER_6_CHECKER(HandRunner6Checker::new);

    private final Function<RandomSource, CraftingAnswerChecker> checkerProducer;

    CheckerEnum(Function<RandomSource, CraftingAnswerChecker> checkerProducer) {
        this.checkerProducer = checkerProducer;
    }

    public CraftingAnswerChecker produce(RandomSource random) {
        return checkerProducer.apply(random);
    }

    @Override
    public String getTranslationKey() {
        return "question.wenyan_programming." + name().toLowerCase();
    }
}
