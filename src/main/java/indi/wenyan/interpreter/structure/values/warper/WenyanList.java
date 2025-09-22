package indi.wenyan.interpreter.structure.values.warper;

import indi.wenyan.content.handler.WenyanBuiltinFunction;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanObject;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.IWenyanWarperValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanBoolean;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.utils.WenyanDataParser;
import indi.wenyan.interpreter.utils.WenyanValues;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A Wenyan array list implementation that wraps a Java List.
 * <p>
 * to make the list element mutable, use the left value as the element
 * to make the list immutable, use a immutable list as the value
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
     * @throws WenyanException.WenyanThrowException if the index is out of bounds
     */
    public IWenyanValue get(int index) throws WenyanException.WenyanThrowException {
        try {
            return value.get(index - 1);
        } catch (IndexOutOfBoundsException e) {
            throw new WenyanException.WenyanDataException(e.getMessage());
        }
    }

    @Override
    public void setVariable(String name, IWenyanValue value) {
        throw new WenyanException(Component.translatable("error.wenyan_programming.variable_not_found_").getString() + name);
    }

    @Override
    public IWenyanValue getAttribute(String name) {
        return switch (name) {
            case WenyanDataParser.ARRAY_GET_ID -> new WenyanBuiltinFunction((self, args) -> {
                if (args.size() != 1)
                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.number_of_arguments_does_not_match").getString());
                return self.as(TYPE).get(args.getFirst().as(WenyanInteger.TYPE).value());
            });
            case WenyanDataParser.ITER_ID -> new WenyanBuiltinFunction((self, args) -> {
                if (!args.isEmpty())
                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.number_of_arguments_does_not_match").getString());
                return new WenyanIterator(self.as(TYPE).value.iterator());
            });
            case WenyanDataParser.LONG_ID -> WenyanValues.of(value.size());
            case "「移除」" -> new WenyanBuiltinFunction((self, args) -> {
                if (args.size() != 1)
                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.number_of_arguments_does_not_match").getString());
                return WenyanValues.of(self.as(TYPE).value.remove(args.getFirst()));
            });
            case "「包含」" -> new WenyanBuiltinFunction((self, args) -> {
                if (args.size() != 1)
                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.number_of_arguments_does_not_match").getString());
                return WenyanValues.of(self.as(TYPE).value.contains(args.getFirst()));
            });
            case "「清空」" -> new WenyanBuiltinFunction((self, args) -> {
                if (!args.isEmpty())
                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.number_of_arguments_does_not_match").getString());
                self.as(TYPE).value.clear();
                return self;
            });
            case "「非空」" -> new WenyanBuiltinFunction((self, args) -> {
                if (!args.isEmpty())
                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.number_of_arguments_does_not_match").getString());
                return WenyanValues.of(!self.as(TYPE).value.isEmpty());
            });
            case "「索引」" -> new WenyanBuiltinFunction((self, args) -> {
                if (args.size() != 1)
                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.number_of_arguments_does_not_match").getString());
                return WenyanValues.of(self.as(TYPE).value.indexOf(args.getFirst()) + 1); // 1-based index
            });
            case "「子列」" -> new WenyanBuiltinFunction((self, args) -> {
                if (args.size() != 2)
                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.number_of_arguments_does_not_match").getString());
                int fromIndex = args.getFirst().as(WenyanInteger.TYPE).value() - 1; // 1-based to 0-based
                int toIndex = args.get(1).as(WenyanInteger.TYPE).value(); // 1-based
                return WenyanValues.of(self.as(TYPE).value.subList(fromIndex, toIndex));
            });
            default -> throw new WenyanException(Component.translatable("error.wenyan_programming.variable_not_found_").getString() + name);
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
}
