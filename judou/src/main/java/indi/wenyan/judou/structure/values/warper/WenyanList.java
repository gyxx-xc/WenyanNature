package indi.wenyan.judou.structure.values.warper;

import indi.wenyan.judou.exec_interface.handler.WenyanInlineJavacall;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanObject;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.IWenyanWarperValue;
import indi.wenyan.judou.structure.values.primitive.WenyanBoolean;
import indi.wenyan.judou.structure.values.primitive.WenyanInteger;
import indi.wenyan.judou.utils.LanguageManager;
import indi.wenyan.judou.utils.WenyanDataParser;
import indi.wenyan.judou.utils.WenyanValues;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A Wenyan array list implementation that wraps a Java List.
 * <p>
 * to make the list element mutable, use the left value as the element
 * to make the list immutable, use an immutable list as the value
 */
public record WenyanList(List<IWenyanValue> value)
        implements IWenyanWarperValue<List<IWenyanValue>>, IWenyanObject {
    public static final WenyanType<WenyanList> TYPE = new WenyanType<>("list", WenyanList.class);

    public WenyanList() {
        this(new ArrayList<>());
    }

    /**
     * Concatenates another list to this list.
     *
     * @param other the list to concatenate
     * @return this list after concatenation
     */
    @SuppressWarnings("UnusedReturnValue")
    public WenyanList concat(WenyanList other) {
        value.addAll(other.value);
        return this;
    }

    /**
     * Adds a value to this list.
     *
     * @param wenyanValue the value to add
     */
    public void add(IWenyanValue wenyanValue) {
        value.add(wenyanValue);
    }

    /**
     * Gets a value from this list at the specified index.
     * Note: Wenyan uses 1-based indexing.
     *
     * @param index the 1-based index
     * @return the value at the specified index
     * @throws WenyanException if the index is out of bounds
     */
    public IWenyanValue get(int index) throws WenyanException {
        if (index < 1 || index > value.size())
            throw new WenyanException.WenyanDataException(LanguageManager.getTranslation("error.wenyan_programming.index_out_of_bounds"));
        return value.get(index - 1);
    }

    @Override
    public IWenyanValue getAttribute(String name) throws WenyanException {
        return switch (name) {
            case WenyanDataParser.ARRAY_GET_ID -> new WenyanInlineJavacall((self, args) -> {
                if (args.size() != 1)
                    throw new WenyanException.WenyanVarException(LanguageManager.getTranslation("error.wenyan_programming.number_of_arguments_does_not_match"));
                return self.as(TYPE).get(args.getFirst().as(WenyanInteger.TYPE).value());
            });
            case WenyanDataParser.ITER_ID -> new WenyanInlineJavacall((self, args) -> {
                if (!args.isEmpty())
                    throw new WenyanException.WenyanVarException(LanguageManager.getTranslation("error.wenyan_programming.number_of_arguments_does_not_match"));
                return new WenyanIterator(self.as(TYPE).value.iterator());
            });
            case WenyanDataParser.LONG_ID -> WenyanValues.of(value.size());
            case "「移除」" -> new WenyanInlineJavacall((self, args) -> {
                if (args.size() != 1)
                    throw new WenyanException.WenyanVarException(LanguageManager.getTranslation("error.wenyan_programming.number_of_arguments_does_not_match"));
                return WenyanValues.of(self.as(TYPE).value.remove(args.getFirst()));
            });
            case "「包含」" -> new WenyanInlineJavacall((self, args) -> {
                if (args.size() != 1)
                    throw new WenyanException.WenyanVarException(LanguageManager.getTranslation("error.wenyan_programming.number_of_arguments_does_not_match"));
                return WenyanValues.of(self.as(TYPE).value.contains(args.getFirst()));
            });
            case "「清空」" -> new WenyanInlineJavacall((self, args) -> {
                if (!args.isEmpty())
                    throw new WenyanException.WenyanVarException(LanguageManager.getTranslation("error.wenyan_programming.number_of_arguments_does_not_match"));
                self.as(TYPE).value.clear();
                return self;
            });
            case "「非空」" -> new WenyanInlineJavacall((self, args) -> {
                if (!args.isEmpty())
                    throw new WenyanException.WenyanVarException(LanguageManager.getTranslation("error.wenyan_programming.number_of_arguments_does_not_match"));
                return WenyanValues.of(!self.as(TYPE).value.isEmpty());
            });
            case "「索引」" -> new WenyanInlineJavacall((self, args) -> {
                if (args.size() != 1)
                    throw new WenyanException.WenyanVarException(LanguageManager.getTranslation("error.wenyan_programming.number_of_arguments_does_not_match"));
                return WenyanValues.of(self.as(TYPE).value.indexOf(args.getFirst()) + 1); // 1-based index
            });
            case "「子列」" -> new WenyanInlineJavacall((self, args) -> {
                if (args.size() != 2)
                    throw new WenyanException.WenyanVarException(LanguageManager.getTranslation("error.wenyan_programming.number_of_arguments_does_not_match"));
                int fromIndex = args.getFirst().as(WenyanInteger.TYPE).value() - 1; // 1-based to 0-based
                int toIndex = args.get(1).as(WenyanInteger.TYPE).value(); // 1-based
                return WenyanValues.of(self.as(TYPE).value.subList(fromIndex, toIndex));
            });
            default ->
                    throw new WenyanException(LanguageManager.getTranslation("error.wenyan_programming.variable_not_found_") + name);
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <T extends IWenyanValue> T casting(WenyanType<T> type) {
        if (type == WenyanBoolean.TYPE) {
            return (T) WenyanValues.of(!value.isEmpty());
        }
        return null;
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    public @NotNull String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IWenyanValue otherValue)) return false;
        return otherValue.tryAs(TYPE).map(list -> {
            if (value.size() != list.value.size()) return false;
            for (int i = 0; i < value.size(); i++) {
                try {
                    if (IWenyanValue.equals(value.get(i), list.value.get(i))) return false;
                } catch (WenyanException ignore) {
                    return false;
                }
            }
            return true;
        }).orElse(false);
    }
}
