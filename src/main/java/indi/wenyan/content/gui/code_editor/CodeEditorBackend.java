package indi.wenyan.content.gui.code_editor;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

// the following content will be saved within one gui open (across window resize and init() in screen)
// but will be removed when screen is closed
public class CodeEditorBackend {
    private final DoubleSidedData sidedData;
    // TODO: change to map[player, cursor] and save (longtime later)
    @Getter
    private int cursor = 0;
    @Getter
    private int selectCursor = 0;

    @Getter
    @Setter
    private List<SnippetSet> curSnippets = Snippets.STMT_CONTEXT;
    @Getter
    private final List<PackageSnippetWidget.PackageSnippet> packages;

    @Setter
    private Runnable cursorListener = () -> {
    };
    @Setter
    private Runnable valueListener = () -> {
    };
    @Setter
    private Consumer<String> outputListener = s -> {
    };

    private final CodeEditorBackendSynchronizer synchronizer;

    public CodeEditorBackend(List<PackageSnippetWidget.PackageSnippet> packages,
                             CodeEditorBackendSynchronizer synchronizer) {
        this.synchronizer = synchronizer;
        sidedData = new DoubleSidedData(synchronizer.getContent(), synchronizer.getTitle());
        setOutput(synchronizer.getOutput());
        this.packages = packages;
    }

    public void tick() {
        if (synchronizer.outputChanged()) {
            setOutput(synchronizer.getOutput());
        }
    }

    public void save() {
        synchronizer.sendContent(sidedData.content.toString());
        synchronizer.sendTitle(sidedData.title.toString());
    }

    public String getContent() {
        return sidedData.content.toString();
    }

    public List<CodeField.Placeholder> getPlaceholders() {
        return sidedData.placeholders;
    }

    // following part need to communicated
    public void insertText(String text) {
        if (!text.isEmpty() || selectCursor != cursor) {
            String filteredText = StringUtil.filterText(text.replace("\t", "    "), true);
            String string = StringUtil.truncateStringIfNecessary(filteredText,
                    CodeEditorScreen.CHARACTER_LIMIT - sidedData.content.length(), false);
            int beginIndex = Math.min(selectCursor, cursor);
            int endIndex = Math.max(selectCursor, cursor);
            sidedData.content.replace(beginIndex, endIndex, string);

            int lengthChanged = string.length() - (endIndex - beginIndex);
            for (var i = getPlaceholders().listIterator(); i.hasNext();) {
                var placeholder = i.next();
                // NOTE: a equal here means if any text of cursor is changed, the placeholder
                // will be removed
                if (placeholder.index() >= beginIndex) {
                    if (placeholder.index() <= endIndex)
                        i.remove();
                    else
                        i.set(new CodeField.Placeholder(placeholder.context(),
                                placeholder.index() + lengthChanged));
                }
            }

            cursor = beginIndex + string.length();
            selectCursor = cursor;

            valueListener.run();
            cursorListener.run();
        }
    }

    public void setCursor(int cursor) {
        if (this.cursor != cursor) {
            this.cursor = cursor;
            cursorListener.run();
        }
    }

    public void setSelectCursor(int selectCursor) {
        if (this.selectCursor != selectCursor) {
            this.selectCursor = selectCursor;
        }
    }

    public String getTitle() {
        return sidedData.getTitle().toString();
    }

    public void setTitle(String title) {
        sidedData.title.setLength(0);
        sidedData.title.append(StringUtil.truncateStringIfNecessary(title, CodeEditorScreen.TITLE_LENGTH_LIMIT, false));
    }

    public String getOutput() {
        return Objects.requireNonNullElse(sidedData.output, "");
    }

    public void setOutput(String output) {
        sidedData.output = output;
        outputListener.accept(output);
    }

    @Data
    public static class DoubleSidedData { // these data need to sync
        final StringBuilder content;
        final StringBuilder title;
        final List<CodeField.Placeholder> placeholders = new ArrayList<>();
        String output;

        public DoubleSidedData(String content, String title) {
            this.content = new StringBuilder(
                    StringUtil.truncateStringIfNecessary(content, CodeEditorScreen.CHARACTER_LIMIT, false));
            this.title = new StringBuilder(
                    StringUtil.truncateStringIfNecessary(title, CodeEditorScreen.TITLE_LENGTH_LIMIT, false));
        }
    }
}
