package indi.wenyan.judou.structure.values.primitive;

import indi.wenyan.judou.exec_interface.handler.WenyanInlineJavacall;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanObject;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.IWenyanWarperValue;
import indi.wenyan.judou.utils.WenyanDataParser;
import indi.wenyan.judou.utils.WenyanValues;
import indi.wenyan.judou.utils.language.JudouExceptionText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
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
            throw new WenyanException.WenyanDataException(JudouExceptionText.IndexOutOfBounds.string());
        return value.get(index - 1);
    }

    @Override
    public IWenyanValue getAttribute(String name) throws WenyanException {
        return switch (name) {
            case WenyanDataParser.ARRAY_GET_ID -> new WenyanInlineJavacall((self, args) -> {
                if (args.size() != 1)
                    throw new WenyanException.WenyanVarException(JudouExceptionText.ArgsNumWrong.string(1, args.size()));
                return self.as(TYPE).get(args.getFirst().as(WenyanInteger.TYPE).value());
            });
            case WenyanDataParser.ITER_ID -> new WenyanInlineJavacall((self, args) -> {
                if (!args.isEmpty())
                    throw new WenyanException.WenyanVarException(JudouExceptionText.ArgsNumWrong.string(1, args.size()));
                return new WenyanIterator(self.as(TYPE).value.iterator());
            });
            case WenyanDataParser.LONG_ID -> WenyanValues.of(value.size());
            case "「移除」" -> new WenyanInlineJavacall((self, args) -> {
                if (args.size() != 1)
                    throw new WenyanException.WenyanVarException(JudouExceptionText.ArgsNumWrong.string(1, args.size()));
                return WenyanValues.of(self.as(TYPE).value.remove(args.getFirst()));
            });
            case "「包含」" -> new WenyanInlineJavacall((self, args) -> {
                if (args.size() != 1)
                    throw new WenyanException.WenyanVarException(JudouExceptionText.ArgsNumWrong.string(1, args.size()));
                return WenyanValues.of(self.as(TYPE).value.contains(args.getFirst()));
            });
            case "「清空」" -> new WenyanInlineJavacall((self, args) -> {
                if (!args.isEmpty())
                    throw new WenyanException.WenyanVarException(JudouExceptionText.ArgsNumWrong.string(0, args.size()));
                self.as(TYPE).value.clear();
                return self;
            });
            case "「非空」" -> new WenyanInlineJavacall((self, args) -> {
                if (!args.isEmpty())
                    throw new WenyanException.WenyanVarException(JudouExceptionText.ArgsNumWrong.string(0, args.size()));
                return WenyanValues.of(!self.as(TYPE).value.isEmpty());
            });
            case "「索引」" -> new WenyanInlineJavacall((self, args) -> {
                if (args.size() != 1)
                    throw new WenyanException.WenyanVarException(JudouExceptionText.ArgsNumWrong.string(1, args.size()));
                return WenyanValues.of(self.as(TYPE).value.indexOf(args.getFirst()) + 1); // 1-based index
            });
            case "「子列」" -> new WenyanInlineJavacall((self, args) -> {
                if (args.size() != 2)
                    throw new WenyanException.WenyanVarException(JudouExceptionText.ArgsNumWrong.string(2, args.size()));
                int fromIndex = args.getFirst().as(WenyanInteger.TYPE).value() - 1; // 1-based to 0-based
                int toIndex = args.get(1).as(WenyanInteger.TYPE).value(); // 1-based
                return WenyanValues.of(self.as(TYPE).value.subList(fromIndex, toIndex));
            });
            default ->
                    throw new WenyanException(JudouExceptionText.NoAttribute.string(name));
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
            int size = value.size();
            if (size != list.value.size()) return Boolean.FALSE;
            for (int i = 0; i < size; i++) {
                try {
                    if (!IWenyanValue.equals(value.get(i), list.value.get(i))) return Boolean.FALSE;
                } catch (WenyanException ignore) {
                    // unreachable
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        }).orElse(Boolean.FALSE).booleanValue();
    }

    /**
     * Wrapper for an iterator of Wenyan values.
     * Used for iteration operations in the language.
     */
    public static final class WenyanIterator
            implements IWenyanWarperValue<Iterator<IWenyanValue>> {
        public static final WenyanType<WenyanIterator> TYPE = new WenyanType<>("iterator", WenyanIterator.class);
        private final Iterator<IWenyanValue> value;

        WenyanIterator(Iterator<IWenyanValue> value) {
            this.value = value;
        }

        @Override
        public WenyanType<?> type() {
            return TYPE;
        }

        @Override
        public Iterator<IWenyanValue> value() {
            return value;
        }
    }
}
