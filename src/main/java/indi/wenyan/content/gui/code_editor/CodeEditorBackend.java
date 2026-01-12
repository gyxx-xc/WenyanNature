package indi.wenyan.content.gui.code_editor;

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
    @Getter
    private final StringBuilder content;
    @Getter
    private final List<CodeField.Placeholder> placeholders = new ArrayList<>();
    @Getter @Setter
    private int cursor = 0;
    @Getter @Setter
    private int selectCursor = 0;
    @Getter @Setter
    private boolean selecting = false;
    @Getter
    private final List<PackageSnippetWidget.PackageSnippet> packages;
    @Getter @Setter
    private List<SnippetSet> curSnippets = Snippets.STMT_CONTEXT;
    private final CodeEditorScreen screen;

    private final Consumer<String> saving;

    public CodeEditorBackend(String content, Consumer<String> saving, List<PackageSnippetWidget.PackageSnippet> packages, CodeEditorScreen screen) {
        this.content = new StringBuilder(StringUtil.truncateStringIfNecessary(content, CodeEditorScreen.CHARACTER_LIMIT, false));
        this.packages = packages;
        this.saving = saving;
        this.screen = screen;
    }

    public String getString() {
        return content.toString();
    }

    public void save() {
        saving.accept(content.toString());
    }


    public void insertSnippet(SnippetSet.Snippet s) {
        screen.getTextFieldWidget().getTextField().insertSnippet(s);
    }
}
