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
    @Getter @Setter
    private int cursor = 0;
    @Getter @Setter
    private int selectCursor = 0;
    @Getter @Setter
    private boolean selecting = false;
    @Getter @Setter
    private List<SnippetSet> curSnippets = Snippets.STMT_CONTEXT;
    private final CodeEditorScreen screen;

    private final Consumer<String> saving;

    public CodeEditorBackend(PersistentData data, Consumer<String> saving, CodeEditorScreen screen) {
        persistentData = data;
        this.saving = saving;
        this.screen = screen;
    }

    public String getString() {
        return persistentData.content.toString();
    }

    public void save() {
        saving.accept(persistentData.content.toString());
    }


    public void insertSnippet(SnippetSet.Snippet s) {
        screen.getTextFieldWidget().getTextField().insertSnippet(s);
    }

    public StringBuilder getContent() {
        return persistentData.content;
    }

    public StringBuilder getTitle() {
        return persistentData.title;
    }

    public List<PackageSnippetWidget.PackageSnippet> getPackages() {
        return persistentData.packages;
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
