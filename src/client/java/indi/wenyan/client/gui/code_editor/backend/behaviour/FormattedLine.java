package indi.wenyan.client.gui.code_editor.backend.behaviour;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FormattedLine implements FormattedText, IStringView {
    private final List<TokenText> tokens = new ArrayList<>();
    private int length = 0;
    private final int indexInCode;

    public FormattedLine(int indexInCode) {
        this.indexInCode = indexInCode;
    }

    /// for both splitter and renderer
    private CachedWidth cachedWidth = CachedWidth.NO_CACHE;

    public int length() {
        return length;
    }

    @Override
    public int beginIndex() {
        return indexInCode;
    }

    @Override
    public int endIndex() {
        return length == 0 ? indexInCode : indexInCode + length - 1;
    }

    /// return width before index (inclusive)
    public int getWidth(Font font, int index) {
        if (index < 0) return 0; // for render, which give -1
        if (index < cachedWidth.index) cachedWidth = CachedWidth.NO_CACHE;
        int curIndex = cachedWidth.index;
        int width = cachedWidth.width;
        for (int i = cachedWidth.tokenIndex + 1; i < tokens.size(); i++) {
            TokenText token = tokens.get(i);
            if (curIndex + token.text.length() > index) {
                cachedWidth = new CachedWidth(curIndex, i - 1, width);
                return width + font.width(new TokenText(
                        token.text.substring(0, index + 1 - curIndex),
                        token.style,
                        token.tokenType
                ));
            }
            curIndex += token.text.length();
            width += font.width(token);
        }

        cachedWidth = new CachedWidth(curIndex, tokens.size() - 1, width);
        return width;
    }


    private void add(TokenText token) {
        tokens.add(token);
        length += token.text.length();
    }

    public void add(String text, int tokenType) {
        add(new TokenText(text, tokenType));
    }

    public @Nullable FormattedLine cutLine(Font font, int codeWidth) {
        for (int i = 0; i < length(); i++) {
            if (getWidth(font, i) > codeWidth) {
                return cut(i, token -> needCutToken(font, codeWidth, token));
            }
        }
        return null;
    }

    private boolean needCutToken(Font font, int width, TokenText tokenText) {
        return font.width(tokenText) >= width * 0.3;
    }

    /// remain tokens before {@param index} (exclusive), return the rest with new FormattedLine
    private FormattedLine cut(int index, Function<TokenText, Boolean> cutTokenFunction) {
        int currentLength = 0;
        if (tokens.isEmpty()) {
            return new FormattedLine(indexInCode);
        }

        ListIterator<TokenText> iterator = tokens.listIterator();
        TokenText token = null;
        while (iterator.hasNext()) {
            token = iterator.next();
            if (currentLength + token.text.length() >= index) {
                break;
            }
            currentLength += token.text.length();
        }

        FormattedLine result;

        assert token != null;
        if (cutTokenFunction.apply(token)) {
            result = new FormattedLine(indexInCode + index);
            result.add(new TokenText(
                    token.text.substring(index - currentLength),
                    token.style,
                    token.tokenType
            ));
            iterator.set(new TokenText(
                    token.text.substring(0, index - currentLength),
                    token.style,
                    token.tokenType
            ));
            length -= token.text.length() - index + currentLength;
        } else {
            result = new FormattedLine(indexInCode + currentLength);
            result.add(token);
            iterator.remove();
            length -= token.text.length();
        }

        while (iterator.hasNext()) {
            TokenText next = iterator.next();
            result.add(next);
            iterator.remove();
            length -= next.text.length();
        }
        return result;
    }

    @Override
    public <T> Optional<T> visit(ContentConsumer<T> contentConsumer) {
        int size = tokens.size();
        for (int i = 0; i < size; i++) {
            TokenText token = tokens.get(i);
            String text = token.text;
            if (i == size - 1 && (text.endsWith("\n") || text.endsWith("\0"))) {
                text = text.substring(0, text.length() - 1);
            }
            Optional<T> result = contentConsumer.accept(text);
            if (result.isPresent()) {
                return result;
            }
        }

        return Optional.empty();
    }

    @Override
    public <T> Optional<T> visit(StyledContentConsumer<T> styledContentConsumer, Style style) {
        int size = tokens.size();
        for (int i = 0; i < size; i++) {
            TokenText token = tokens.get(i);
            String text = token.text;
            if (i == size - 1 && (text.endsWith("\n") || text.endsWith("\0"))) {
                text = text.substring(0, text.length() - 1);
            }
            Optional<T> result = styledContentConsumer.accept(token.style.applyTo(style), text);
            if (result.isPresent()) {
                return result;
            }
        }

        return Optional.empty();
    }

    /// store token {@param tokenIndex} ended at {@param index}, is with {@param width}
    private record CachedWidth(int index, int tokenIndex, int width) {
        public static final CachedWidth NO_CACHE = new CachedWidth(0, -1, 0);
    }

    private record TokenText(String text, Style style, int tokenType) implements FormattedText {

        public TokenText(String text, int tokenType) {
            this(text, CodeFormatter.styleFromTokenType(tokenType), tokenType);
        }

        @Override
        public <T> Optional<T> visit(ContentConsumer<T> contentConsumer) {
            return contentConsumer.accept(text);
        }

        @Override
        public <T> Optional<T> visit(StyledContentConsumer<T> styledContentConsumer, Style parentStyle) {
            return styledContentConsumer.accept(style.applyTo(parentStyle), text);
        }
    }
}
