package indi.wenyan.content.checker.checker.ink;

import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.content.checker.ink.BambooInkChecker;
import indi.wenyan.content.checker.checker.test_utils.MockRandomSource;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class BambooInkCheckerTest {

    @Test
    void testCorrectAnswer() throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .build();

        IAnsweringChecker checker = new BambooInkChecker(random);
        checker.init();

        checker.accept(WenyanValues.of("吾有一術"));
        Assertions.assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult());
    }

    @ParameterizedTest
    @ValueSource(strings = { "其他", "吾有二術", "Hello", "123", "", "\n", "//", "\n\n\n\n\n", "%"})
    void testWrongAnswer(String wrongAnswer) throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .build();

        IAnsweringChecker checker = new BambooInkChecker(random);
        checker.init();

        checker.accept(WenyanValues.of(wrongAnswer));
        assertEquals(IAnsweringChecker.ResultStatus.WRONG_ANSWER, checker.getResult());
    }

    @Test
    void testInitialization() throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .build();

        IAnsweringChecker checker = new BambooInkChecker(random);
        checker.init();

        assertNotNull(checker.getArgs(), "Arguments should not be null after initialization");
        assertEquals(IAnsweringChecker.ResultStatus.RUNNING, checker.getResult(),
                "Result status should be RUNNING after initialization");
    }

    @Test
    void testResultStatusTransition() throws WenyanException {
        RandomSource random = MockRandomSource.InputBuilder.create()
                .build();

        BambooInkChecker checker = new BambooInkChecker(random);

        assertNull(checker.getResult(), "Should be null before init");

        checker.init();
        assertEquals(IAnsweringChecker.ResultStatus.RUNNING, checker.getResult(),
                "Should be RUNNING after init");

        checker.accept(WenyanValues.of("吾有一術"));
        assertEquals(IAnsweringChecker.ResultStatus.ANSWER_CORRECT, checker.getResult(),
                "Should be ANSWER_CORRECT after correct answer");
    }
}