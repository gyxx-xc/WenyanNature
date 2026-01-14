package indi.wenyan.content.gui.code_editor;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.StringUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// the following content will be saved within one gui open (across window resize and init() in screen)
// but will be removed when screen is closed
@OnlyIn(Dist.CLIENT)
public class CodeEditorBackend {
    private final PersistentData persistentData;
    @Getter
    private final List<CodeField.Placeholder> placeholders = new ArrayList<>();
    @Getter
    private int cursor = 0;
    @Getter
    private int selectCursor = 0;

    @Getter
    @Setter
    private List<SnippetSet> curSnippets = Snippets.STMT_CONTEXT;

    @Setter
    private Runnable cursorListener = () -> {
    };
    @Setter
    private Runnable valueListener = () -> {
    };

    private final Consumer<String> saving;

    public CodeEditorBackend(PersistentData data, Consumer<String> saving) {
        persistentData = data;
        this.saving = saving;
    }

    public void save() {
        saving.accept(persistentData.content.toString());
    }


    public String getContent() {
        return persistentData.content.toString();
    }

    public StringBuilder getTitle() {
        return persistentData.title;
    }

    public List<PackageSnippetWidget.PackageSnippet> getPackages() {
        return persistentData.packages;
    }

    // following part need to communicated
    public void insertText(String text) {
        if (!text.isEmpty() || selectCursor != cursor) {
            String filteredText = StringUtil.filterText(text.replace("\t", "    "), true);
            String string = StringUtil.truncateStringIfNecessary(filteredText,
                    CodeEditorScreen.CHARACTER_LIMIT - persistentData.content.length(), false);
            int beginIndex = Math.min(selectCursor, cursor);
            int endIndex = Math.max(selectCursor, cursor);
            persistentData.content.replace(beginIndex, endIndex, string);

            int lengthChanged = string.length() - (endIndex - beginIndex);
            for (int i = 0; i < placeholders.size(); i++) {
                var placeholder = placeholders.get(i);
                // NOTE: a equal here means if any text of cursor is changed, the placeholder will be removed
                if (placeholder.index() >= beginIndex) {
                    if (placeholder.index() <= endIndex) {
                        placeholders.remove(placeholder);
                        i--;
                    } else placeholders.set(i, new CodeField.Placeholder(
                            placeholder.context(), placeholder.index() + lengthChanged));
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

    @Data
    public static class PersistentData {
        final StringBuilder content;
        final StringBuilder title;
        List<PackageSnippetWidget.PackageSnippet> packages;
        String output;

        public PersistentData(String content, String title, List<PackageSnippetWidget.PackageSnippet> packages) {
            this.content = new StringBuilder(StringUtil.truncateStringIfNecessary(content, CodeEditorScreen.CHARACTER_LIMIT, false));
            this.title = new StringBuilder(StringUtil.truncateStringIfNecessary(title, CodeEditorScreen.TITLE_LENGTH_LIMIT, false));
            this.packages = packages;
        }
    }
}
