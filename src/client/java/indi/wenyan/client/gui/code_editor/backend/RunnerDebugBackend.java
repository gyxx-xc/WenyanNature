package indi.wenyan.client.gui.code_editor.backend;

import indi.wenyan.client.gui.code_editor.backend.behaviour.CodeField;
import indi.wenyan.client.gui.code_editor.backend.behaviour.SnippetSet;
import indi.wenyan.client.gui.code_editor.backend.behaviour.generated_Snippets;
import indi.wenyan.client.gui.code_editor.backend.interfaces.BaseTickBackend;
import indi.wenyan.client.gui.code_editor.backend.interfaces.CodeEditBackend;
import indi.wenyan.client.gui.code_editor.backend.interfaces.OutputBackend;
import indi.wenyan.client.gui.code_editor.backend.interfaces.RunnerDebugSynchronizer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.Component;

import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;

// the following content will be saved within one gui open (across window resize and init() in screen)
// but will be removed when screen is closed
public class RunnerDebugBackend implements BaseTickBackend, CodeEditBackend, OutputBackend {
    @Getter
    private int cursor = 0;
    @Getter
    private int selectCursor = 0;

    @Getter
    @Setter
    private List<SnippetSet> curSnippets = generated_Snippets.STMT_CONTEXT;
    @Getter private final String content;

    @Setter
    private Runnable cursorListener = () -> {
    };
    @Setter
    private Consumer<Deque<Component>> outputListener = _ -> {
    };
    private Deque<Component> output;

    private final RunnerDebugSynchronizer synchronizer;

    public RunnerDebugBackend(RunnerDebugSynchronizer synchronizer, String code, Deque<Component> output) {
        this.synchronizer = synchronizer;
        content = code;
        this.output = output;
    }

    @Override
    public void tick() {
        this.selectCursor = synchronizer.getContextStart();
        this.cursor = synchronizer.getContextEnd();
        cursorListener.run();
        if (synchronizer.isOutputChanged()) {
            output = synchronizer.getOutput();
            outputListener.accept(output);
        }
    }

    @Override
    public void save() {
    }

    @Override
    public List<CodeField.Placeholder> getPlaceholders() {
        return List.of();
    }

    // following part need to communicated
    @Override
    public void insertText(String text) {
    }

    @Override
    public void setCursor(int cursor) {
    }

    @Override
    public void setSelectCursor(int selectCursor) {
    }

    @Override
    public void setValueListener(Runnable runnable) {
    }

    @Override
    public Deque<Component> getOutput() {
        return output;
    }
}
