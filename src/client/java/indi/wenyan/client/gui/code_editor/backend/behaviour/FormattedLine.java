package indi.wenyan.client.gui.code_editor.backend.behaviour;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

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
    @Nullable private CachedWidth cachedWidth = null;

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

    public int getWidth(Font font, int index) {
        int curIndex = 0;
        int width = 0;
        for (TokenText token : tokens) {
            curIndex += token.text.length();
            if (curIndex > index) {
                return width + font.width(new TokenText(
                        token.text.substring(0, index + 1 - curIndex + token.text.length()),
                        token.style,
                        token.tokenType
                ));
            }
            width += font.width(token);
        }

        // unreached
        return width;
    }


    private void add(TokenText token) {
        tokens.add(token);
        length += token.text.length();
    }

    public void add(String text, int tokenType) {
        add(new TokenText(text, tokenType));
    }

    /// remain tokens before {@param index}, return the rest with new FormattedLine
    public FormattedLine cut(int index) {
        int currentLength = 0;
        if (tokens.isEmpty()) {
            return new FormattedLine(indexInCode);
        }

        Iterator<TokenText> iterator = tokens.iterator();
        TokenText token = null;
        while (iterator.hasNext()) {
            token = iterator.next();
            if (currentLength + token.text.length() >= index) {
                break;
            }
            currentLength += token.text.length();
        }

        FormattedLine result = new FormattedLine(indexInCode + currentLength);

        assert token != null;
        result.add(token);
        iterator.remove();
        length -= token.text.length();

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

    private record CachedWidth(int index, int tokenIndex, int width) {
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
