package indi.wenyan.content.checker;

import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import net.minecraft.util.RandomSource;

/**
 * Base class for checkers that validate answers in crafting recipes.
 * Manages the checking process and result status.
 */
public abstract class CraftingAnswerChecker implements IAnsweringChecker {
    /** Random source for generating test cases */
    protected final RandomSource random;
    /** The Wenyan program being checked */
    protected WenyanProgram program;

    private Result result = Result.WRONG_ANSWER;

    /** Default variable names used for inputs */
    private static final String[] DEFAULT_INPUT_NAME =
            {"「甲」", "「乙」", "「丙」", "「丁」", "「戊」", "「己」", "「庚」", "「辛」", "「壬」", "「癸」"};

    /**
     * Sets the result status of the checker.
     *
     * @param result the new result status
     */
    protected void setStatus(Result result) {
        this.result = result;
    }

    @Override
    public Result getResult() {
        // TODO: if tle: return tle
        return result;
    }

    /**
     * Creates a new checker with the specified random source.
     *
     * @param random the random source for test cases
     */
    protected CraftingAnswerChecker(RandomSource random) {
        this.random = random;
    }

    /**
     * Processes a collection of values for checking.
     *
     * @param value the values to check
     * @throws WenyanException.WenyanCheckerError if a checking error occurs
     */
    public void accept(Iterable<IWenyanValue> value) throws WenyanException.WenyanCheckerError {
        for (var v : value)
            accept(v);
    }

    @Override
    public void init(WenyanProgram program) {
        this.program = program;
    }

    /**
     * Sets a variable in the program environment using the default naming scheme.
     *
     * @param i the index of the variable (0-9)
     * @param value the value to set
     * @throws IllegalStateException if the program is not initialized
     */
    protected void setVariable(int i, IWenyanValue value) {
        if (program == null)
            throw new IllegalStateException("Program is not initialized");
        program.baseEnvironment.setVariable(DEFAULT_INPUT_NAME[i], value);
    }

    /**
     * Sets a named variable in the program environment.
     *
     * @param name the name of the variable
     * @param value the value to set
     * @throws IllegalStateException if the program is not initialized
     */
    protected void setAttribute(String name, IWenyanValue value) {
        if (program == null)
            throw new IllegalStateException("Program is not initialized");
        program.baseEnvironment.setVariable(name, value);
    }
}
