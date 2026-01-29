package indi.wenyan.content.gui.code_editor;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;

// the following content will be saved within one gui open (across window resize and init() in screen)
// but will be removed when screen is closed
public class CodeEditorBackend {
    private final DoubleSidedData sidedData;
    // PLAN: change to map[player, cursor] and save
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
    private Consumer<List<Component>> outputListener = s -> {
    };

    private final CodeEditorBackendSynchronizer synchronizer;

    public CodeEditorBackend(List<PackageSnippetWidget.PackageSnippet> packages,
                             CodeEditorBackendSynchronizer synchronizer) {
        this.synchronizer = synchronizer;
        sidedData = new DoubleSidedData(synchronizer.getContent(), synchronizer.getTitle());
        this.packages = packages;
    }

    public void tick() {
        var newOutput = synchronizer.newOutput();
        if (!newOutput.isEmpty()) {
            addOutput(newOutput);
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
            for (var iter = getPlaceholders().listIterator(); iter.hasNext();) {
                var placeholder = iter.next();
                // NOTE: a equal here means if any text of cursor is changed, the placeholder
                //   will be removed
                if (placeholder.index() >= beginIndex) {
                    if (placeholder.index() <= endIndex) {
                        iter.remove();
                    } else {
                        iter.set(new CodeField.Placeholder(placeholder.context(),
                                placeholder.index() + lengthChanged));
                    }
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

    public Deque<Component> getOutput() {
        return sidedData.output;
    }

    public void addOutput(List<Component> newOutput) {
        outputListener.accept(newOutput);
        sidedData.output.addAll(newOutput);
    }

    @Data
    public static class DoubleSidedData { // these data need to sync
        final StringBuilder content;
        final StringBuilder title;
        final List<CodeField.Placeholder> placeholders = new ArrayList<>();
        final Deque<Component> output = new ArrayDeque<>();

        public DoubleSidedData(String content, String title) {
            this.content = new StringBuilder(
                    StringUtil.truncateStringIfNecessary(content, CodeEditorScreen.CHARACTER_LIMIT, false));
            this.title = new StringBuilder(
                    StringUtil.truncateStringIfNecessary(title, CodeEditorScreen.TITLE_LENGTH_LIMIT, false));
        }
    }
}
