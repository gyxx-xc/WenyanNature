package indi.wenyan.content.checker;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanObject;
import indi.wenyan.judou.structure.values.IWenyanValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.RandomSource;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for checkers that validate answers in crafting recipes.
 * Manages the checking process and result status.
 */
public abstract class CraftingAnswerChecker implements IAnsweringChecker {
    /** Random source for generating test cases */
    protected final RandomSource random;

    @Getter @Setter(AccessLevel.PROTECTED)
    private ResultStatus result;
    @Getter
    private CheckerWenyanObject args = null;

    /**
     * Creates a new checker with the specified random source.
     *
     * @param random the random source for test cases
     */
    protected CraftingAnswerChecker(RandomSource random) {
        this.random = random;
    }

    @Override
    public void init() {
        args = new CheckerWenyanObject();
        result = ResultStatus.RUNNING;
    }

    /**
     * Sets a variable in the program environment using the default naming scheme.
     *
     * @param i the index of the variable (0-9)
     * @param value the value to set
     * @throws IllegalStateException if the program is not initialized
     */
    protected void setVariable(int i, IWenyanValue value) {
        if (args == null)
            throw new IllegalStateException("Program is not initialized");
        args.addAttribute(DEFAULT_INPUT_NAME[i], value);
    }

    /**
     * Sets a named variable in the program environment.
     *
     * @param name the name of the variable
     * @param value the value to set
     * @throws IllegalStateException if the program is not initialized
     */
    protected void setAttribute(String name, IWenyanValue value) {
        if (args == null)
            throw new IllegalStateException("Program is not initialized");
        args.addAttribute(name, value);
    }

    /** Default variable names used for inputs */
    private static final String[] DEFAULT_INPUT_NAME =
            {"「甲」", "「乙」", "「丙」", "「丁」", "「戊」", "「己」", "「庚」", "「辛」", "「壬」", "「癸」"};

    // TODO: RW Lock
    protected static class CheckerWenyanObject implements IWenyanObject {
        public static final WenyanType<CheckerWenyanObject> TYPE =
                new WenyanType<>("checker_object", CheckerWenyanObject.class);

        public final Map<String, IWenyanValue> attributes = new HashMap<>();

        protected void addAttribute(String name, IWenyanValue value) {
            attributes.put(name, value);
        }

        @Override
        public IWenyanValue getAttribute(String name) throws WenyanException {
             if (attributes.containsKey(name)) {
                 return attributes.get(name);
             }
             throw new WenyanException("Checker object has no such attribute: " + name);
        }

        @Override
        public WenyanType<?> type() {
            return TYPE;
        }
    }
}
